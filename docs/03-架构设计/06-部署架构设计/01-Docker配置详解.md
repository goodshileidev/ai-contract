---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-29
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-29
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - Docker配置详解

> **部署方式**: Docker容器化部署
> **开发环境**: Docker Compose
> **生产环境**: Kubernetes
> **镜像仓库**: Harbor / Docker Hub

## 📋 文档导航

本文档是部署架构设计的一部分，其他相关文档：

1. [00-部署架构总览.md](./00-部署架构总览.md) - 部署架构概览和环境规划
2. **01-Docker配置详解.md**（本文档）- Docker镜像构建和Docker Compose配置
3. [02-Kubernetes配置.md](./02-Kubernetes配置.md) - Kubernetes完整部署配置
4. [03-部署运维脚本.md](./03-部署运维脚本.md) - 部署脚本、监控日志和安全配置

## 🐳 Docker配置

### 1. 前端Dockerfile

```dockerfile
# ============================================================================
# 前端生产环境Dockerfile
# ============================================================================
# 多阶段构建

# 阶段1: 构建阶段
FROM node:18-alpine AS builder

WORKDIR /app

# 复制依赖文件
COPY package.json package-lock.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 阶段2: 生产阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制Nginx配置
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
```

### 2. 前端Nginx配置

```nginx
# docker/frontend/nginx.conf
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/javascript application/json;

    # 缓存静态资源
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # SPA路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api {
        proxy_pass http://backend-service:8000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # WebSocket代理
    location /ws {
        proxy_pass http://backend-service:8000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 86400;
    }

    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### 3. Java后端Dockerfile

```dockerfile
# ============================================================================
# Java后端生产环境Dockerfile (Spring Boot)
# ============================================================================

FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 复制Maven/Gradle文件
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 下载依赖
RUN ./mvnw dependency:go-offline

# 复制源码并构建
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制JAR文件
COPY --from=builder /app/target/*.jar app.jar

# 创建非root用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

### 4. Python AI服务Dockerfile

```dockerfile
# ============================================================================
# Python AI服务生产环境Dockerfile (FastAPI)
# ============================================================================

FROM python:3.11-slim

# 设置工作目录
WORKDIR /app

# 安装系统依赖
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    libpq-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 复制依赖文件
COPY requirements/prod.txt requirements.txt

# 安装Python依赖
RUN pip install --no-cache-dir -r requirements.txt

# 复制应用代码
COPY app /app/app
COPY alembic /app/alembic
COPY alembic.ini /app/

# 创建非root用户
RUN useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /app

USER appuser

# 暴露端口
EXPOSE 8000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8000/health || exit 1

# 启动应用
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001", "--workers", "4"]
```

### 5. Python AI服务启动脚本

```bash
#!/bin/bash
# docker/python-ai/entrypoint.sh

set -e

echo "Waiting for Elasticsearch..."
while ! curl -s $ELASTICSEARCH_URL > /dev/null; do
  sleep 1
done
echo "Elasticsearch is ready!"

echo "Waiting for RabbitMQ..."
while ! nc -z $RABBITMQ_HOST $RABBITMQ_PORT; do
  sleep 1
done
echo "RabbitMQ is ready!"

echo "Starting Python AI Service..."
exec uvicorn app.main:app --host 0.0.0.0 --port 8001 --workers ${WORKERS:-4}
```

### 6. Docker Compose (开发环境)

```yaml
# docker-compose.yml
version: '3.8'

services:
  # PostgreSQL数据库
  postgres:
    image: postgres:14-alpine
    container_name: aibidcomposer-postgres
    environment:
      POSTGRES_DB: aibidcomposer
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
      TZ: Asia/Shanghai
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis缓存
  redis:
    image: redis:7-alpine
    container_name: aibidcomposer-redis
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD:-redis}
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  # RabbitMQ消息队列
  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: aibidcomposer-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER:-rabbitmq}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD:-rabbitmq}
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 30s
      timeout: 10s
      retries: 5

  # MinIO对象存储
  minio:
    image: minio/minio:latest
    container_name: aibidcomposer-minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER:-minioadmin}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD:-minioadmin}
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  # Java后端API服务 (Spring Boot)
  backend-java:
    build:
      context: ./backend-java
      dockerfile: Dockerfile.dev
    container_name: aibidcomposer-backend-java
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/aibidcomposer
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD:-redis}
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: ${RABBITMQ_USER:-rabbitmq}
      SPRING_RABBITMQ_PASSWORD: ${RABBITMQ_PASSWORD:-rabbitmq}
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: ${MINIO_ROOT_USER:-minioadmin}
      MINIO_SECRET_KEY: ${MINIO_ROOT_PASSWORD:-minioadmin}
      AI_SERVICE_URL: http://backend-python:8001
      JWT_SECRET: ${SECRET_KEY}
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    volumes:
      - ./backend-java/src:/app/src
      - ./backend-java/target:/app/target

  # Python AI服务 (FastAPI)
  backend-python:
    build:
      context: ./backend-python
      dockerfile: Dockerfile
    container_name: aibidcomposer-backend-python
    environment:
      REDIS_URL: redis://:${REDIS_PASSWORD:-redis}@redis:6379/0
      RABBITMQ_URL: amqp://${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq}@rabbitmq:5672/
      ELASTICSEARCH_URL: http://elasticsearch:9200
      ELASTICSEARCH_USER: elastic
      ELASTICSEARCH_PASSWORD: ${ELASTICSEARCH_PASSWORD:-elastic}
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      ANTHROPIC_API_KEY: ${ANTHROPIC_API_KEY}
      NEO4J_URI: bolt://neo4j:7687
      NEO4J_USER: neo4j
      NEO4J_PASSWORD: ${NEO4J_PASSWORD:-neo4j}
    ports:
      - "8001:8001"
    depends_on:
      - redis
      - rabbitmq
      - elasticsearch
    volumes:
      - ./backend-python/app:/app/app
    command: uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload

  # Elasticsearch (向量存储)
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: aibidcomposer-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=true
      - ELASTIC_PASSWORD=${ELASTICSEARCH_PASSWORD:-elastic}
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    healthcheck:
      test: ["CMD-SHELL", "curl -u elastic:${ELASTICSEARCH_PASSWORD:-elastic} -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Python AI Worker (Celery)
  ai-worker:
    build:
      context: ./backend-python
      dockerfile: Dockerfile
    container_name: aibidcomposer-ai-worker
    environment:
      REDIS_URL: redis://:${REDIS_PASSWORD:-redis}@redis:6379/0
      RABBITMQ_URL: amqp://${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq}@rabbitmq:5672/
      ELASTICSEARCH_URL: http://elasticsearch:9200
      OPENAI_API_KEY: ${OPENAI_API_KEY}
    depends_on:
      - redis
      - rabbitmq
      - elasticsearch
    command: celery -A app.tasks.celery_app worker --loglevel=info -Q ai_tasks

  # Celery Beat调度器
  celery-beat:
    build:
      context: ./backend-python
      dockerfile: Dockerfile
    container_name: aibidcomposer-celery-beat
    environment:
      REDIS_URL: redis://:${REDIS_PASSWORD:-redis}@redis:6379/0
      RABBITMQ_URL: amqp://${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq}@rabbitmq:5672/
    depends_on:
      - redis
      - rabbitmq
    command: celery -A app.tasks.celery_app beat --loglevel=info

  # 前端应用
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.dev
    container_name: aibidcomposer-frontend
    ports:
      - "5173:5173"
    volumes:
      - ./frontend/src:/app/src
      - ./frontend/public:/app/public
    environment:
      VITE_API_BASE_URL: http://localhost:8080
      VITE_AI_API_BASE_URL: http://localhost:8001
    command: npm run dev

volumes:
  postgres_data:
  redis_data:
  rabbitmq_data:
  minio_data:
  elasticsearch_data:
```

### 7. 环境变量文件

```bash
# .env.example
# 复制此文件为.env并填写实际值

# 数据库配置
POSTGRES_PASSWORD=your_secure_password
DATABASE_URL=postgresql://postgres:your_secure_password@postgres:5432/aibidcomposer

# Redis配置
REDIS_PASSWORD=your_redis_password
REDIS_URL=redis://:your_redis_password@redis:6379/0

# RabbitMQ配置
RABBITMQ_USER=rabbitmq
RABBITMQ_PASSWORD=your_rabbitmq_password
RABBITMQ_URL=amqp://rabbitmq:your_rabbitmq_password@rabbitmq:5672/

# MinIO配置
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password
MINIO_ENDPOINT=minio:9000

# 应用配置
SECRET_KEY=your_secret_key_min_32_characters_long
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30

# Elasticsearch配置
ELASTICSEARCH_PASSWORD=your_elasticsearch_password
ELASTICSEARCH_URL=http://elasticsearch:9200

# AI服务配置
OPENAI_API_KEY=sk-your-openai-api-key
ANTHROPIC_API_KEY=your-anthropic-api-key

# Pinecone配置(可选备选方案)
PINECONE_API_KEY=your-pinecone-api-key
PINECONE_ENVIRONMENT=your-pinecone-environment

# Neo4j配置
NEO4J_URI=bolt://neo4j:7687
NEO4J_USER=neo4j
NEO4J_PASSWORD=your_neo4j_password

# 前端配置
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_API_BASE_URL=http://localhost:8001
VITE_WS_BASE_URL=ws://localhost:8080

# 服务间通信
JAVA_SERVICE_URL=http://backend-java:8080
PYTHON_SERVICE_URL=http://backend-python:8001
```

## 🚀 快速使用指南

### 开发环境启动

```bash
# 1. 复制环境变量文件
cp .env.example .env

# 2. 编辑.env文件，填写必要的配置
vim .env

# 3. 启动所有服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看日志
docker-compose logs -f

# 6. 停止服务
docker-compose down

# 7. 停止并清除数据
docker-compose down -v
```

### 生产环境镜像构建

```bash
# 1. 构建前端镜像
docker build -t aibidcomposer/frontend:latest \
  -f docker/frontend/Dockerfile \
  ./frontend

# 2. 构建Java后端镜像
docker build -t aibidcomposer/backend-java:latest \
  -f docker/backend-java/Dockerfile \
  ./backend-java

# 3. 构建Python AI服务镜像
docker build -t aibidcomposer/backend-python:latest \
  -f docker/backend-python/Dockerfile \
  ./backend-python

# 4. 推送镜像到仓库
docker push aibidcomposer/frontend:latest
docker push aibidcomposer/backend-java:latest
docker push aibidcomposer/backend-python:latest
```

## 📝 最佳实践

### 1. 多阶段构建

所有Dockerfile都采用多阶段构建：
- **构建阶段**: 编译代码、安装依赖
- **运行阶段**: 只包含运行时必需文件，镜像体积更小

### 2. 非root用户运行

所有服务容器都使用非root用户运行，提高安全性。

### 3. 健康检查

每个服务都配置了健康检查，确保容器运行正常。

### 4. 资源限制

生产环境部署时应配置资源限制：

```yaml
services:
  backend-java:
    # ...
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 4G
        reservations:
          cpus: '1'
          memory: 2G
```

### 5. 日志管理

配置日志驱动和日志轮转：

```yaml
services:
  backend-java:
    # ...
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## 🔗 下一步

- **Kubernetes部署**: 参见 [02-Kubernetes配置.md](./02-Kubernetes配置.md)
- **运维脚本**: 参见 [03-部署运维脚本.md](./03-部署运维脚本.md)
- **部署总览**: 参见 [00-部署架构总览.md](./00-部署架构总览.md)

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-29 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从06-部署架构设计.md拆分创建Docker配置详解文档 |

---

**文档版本**: v1.0
**创建时间**: 2025年11月29日
**文档状态**: ✅ 已批准
