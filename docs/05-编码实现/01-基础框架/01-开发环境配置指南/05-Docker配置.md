# AI标书智能创作平台 - 开发环境配置指南 - 🐳️ Docker配置

### 多服务Docker配置
```yaml
# docker-compose.yml - 完整的Docker Compose配置
version: '3.8'

services:
  # 前端应用
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      - REACT_APP_API_URL=http://localhost:8000
      - REACT_APP_ENVIRONMENT=development
    volumes:
      - ./frontend:/app
      - /app/node_modules:/app/node_modules
    depends_on:
      - api
    networks:
      - aibidcomposer-network

  # 后端API服务
  api:
    build:
      context: ./server
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    environment:
      - DATABASE_URL=postgresql://aibidcomposer:password@postgres:5432/aibidcomposer
      - REDIS_URL=redis://redis:6379
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ZHIPUAI_API_KEY=${ZHIPUAI_API_KEY}
      - PINECONE_API_KEY=${PINECONE_API_KEY}
      - SECRET_KEY=${SECRET_KEY}
      - ENVIRONMENT=development
    volumes:
      - ./server:/app
      - ./logs:/app/logs
      - ./uploads:/app/uploads
    depends_on:
      - postgres
      - redis
    networks:
      - aibidcomposer-network

  # PostgreSQL数据库
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=aibidcomposer
      - POSTGRES_USER=aibidcomposer
      - POSTGRES_PASSWORD=secure_password
      - POSTGRES_HOST=postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./server/migrations:/docker-entrypoint-initdb.d
    ports:
      - "5432:5432"
    networks:
      - aibcomposer-network

  # Redis缓存
  redis:
    image: redis:7-alpine
    command: redis-server /usr/local/etc/redis/redis.conf
    volumes:
      - ./redis/redis.conf:/usr/local/etc/redis/redis.conf
      - redis_data:/data
    ports:
      - "6379:6379"
    networks:
      - aibcomposer-network

  # MinIO对象存储
  minio:
    image: minio/minio:latest
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
      MINIO_DEFAULT_BUCKETS=aibidcomposer-files
    volumes:
      - minio_data:/data
    ports:
      - "9000:9000"
      - "9001:9001"
    networks:
      - aibcomposer-network

  # Celery异步任务队列
  celery-worker:
    build:
      context: ./server
      dockerfile: Dockerfile.celery
    environment:
      - DATABASE_URL=postgresql://aibidcomposer:password@postgres:5432/aibidcomposer
      - REDIS_URL=redis://redis:6379
      - CELERY_BROKER_URL=redis://redis:6379/0
      - CELERY_RESULT_BACKEND=redis://redis:6379/0
    volumes:
      - ./server:/app
      - ./logs:/app/logs
      - ./uploads:/app/uploads
    depends_on:
      - postgres
      - redis
      - api
    networks:
      - aibcomposer-network

  # Celery监控
  celery-flower:
    build:
      context: ./server
      dockerfile: Dockerfile.celery-flower
    ports:
      - "5555:5555"
    environment:
      - CELERY_BROKER_URL=redis://redis:6379/0
      - FLOWER_DEBUG=1
    depends_on:
      - redis
      - api
    networks:
      - aibcomposer-network

  # Nginx反向代理
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
      - ./logs/nginx:/var/log/nginx
    depends_on:
      - frontend
      - api
    networks:
      - aibidcomposer-network

networks:
  aibicomposer-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
        gateway: 172.20.0.1
```

### Docker配置文件
```dockerfile
# Dockerfile - 前端Dockerfile
FROM node:18-alpine as builder

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./
RUN npm ci

# 安装依赖
RUN npm install

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 生产镜像
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 3000

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
```

```dockerfile
# Dockerfile - 后端Dockerfile
FROM python:3.11-slim

# 设置工作目录
WORKDIR /app

# 安装系统依赖
RUN apk add --no-cache \
    gcc \
    musl-dev \
    postgresql-dev \
    linux-headers \
    libffi-dev \
    openssl-dev \
    curl

# 创建非root用户
RUN addgroup -g appuser appuser
RUN adduser -D -G appuser appuser

# 复制requirements文件
COPY requirements.txt .

# 安装Python依赖
RUN pip install --no-cache --upgrade pip
RUN pip install --no-cache -r requirements.txt

# 复制源代码
COPY . .

# 设置环境变量
ENV PYTHONPATH=/app/.venv/bin
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

# 创建必要的目录
RUN mkdir -p /app/logs /app/uploads /app/static

# 设置权限
RUN chown -R appuser:appuser /app

# 切换到应用用户
USER appuser

# 健康检查
RUN curl -f http://localhost:8000/health || echo "API service not ready"

# 暴露端口
EXPOSE 8000

# 启动应用
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

```dockerfile
# Dockerfile.celery - Celery工作节点Dockerfile
FROM python:3.11-slim

# 设置工作目录
WORKDIR /app

# 安装系统依赖
RUN apk add --no-cache \
    gcc \
    musl-dev \
    postgresql-dev \
    linux-headers \
    libffi-dev \
    openssl-dev \
    curl

# 创建非root用户
RUN addgroup -g appuser appuser
RUN adduser -D -G appuser appuser

# 复制requirements文件
COPY requirements.txt .

# 安装Python依赖
RUN pip install --no-cache --upgrade pip
RUN pip install --no-cache -r requirements.txt

# 复制源代码
COPY . .

# 设置环境变量
ENV PYTHONPATH=/app/.venv/bin
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

# 创建必要的目录
RUN mkdir -p /app/logs /app/uploads

# 设置权限
RUN chown -R appuser:appuser /app

# 启动Celery Worker
CMD ["celery", "-A", "celery.celery:app", "--loglevel=info", "--concurrency=4"]
```
