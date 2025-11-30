# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.5: 部署 - Docker Compose 配置（开发环境）

**docker-compose.yml**:
```yaml
# docker-compose.yml
# 需求编号: REQ-JAVA-002
# 组织管理功能 - 开发环境部署配置

version: '3.8'

services:
  # PostgreSQL 数据库
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
    networks:
      - aibidcomposer-network

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: aibidcomposer-redis
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD:-redis}
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
    networks:
      - aibidcomposer-network

  # Elasticsearch (向量存储)
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:9.2.1
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
    networks:
      - aibidcomposer-network

  # RabbitMQ 消息队列
  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: aibidcomposer-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER:-rabbitmq}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD:-rabbitmq}
    ports:
      - "5672:5672"
      - "15672:15672"  # 管理界面
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 30s
      timeout: 10s
      retries: 5
    networks:
      - aibidcomposer-network

  # MinIO 对象存储
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
    networks:
      - aibidcomposer-network

  # Java Spring Boot 服务
  backend-java:
    build:
      context: ./apps/backend-java
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
      JWT_SECRET: ${JWT_SECRET_KEY}
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
      - ./apps/backend-java/src:/app/src
      - ./apps/backend-java/target:/app/target
      - maven_cache:/root/.m2
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
    networks:
      - aibidcomposer-network

  # Python FastAPI AI 服务
  backend-python:
    build:
      context: ./apps/backend-python
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
      JAVA_SERVICE_URL: http://backend-java:8080
      JWT_SECRET_KEY: ${JWT_SECRET_KEY}
      JWT_ALGORITHM: HS256
    ports:
      - "8001:8001"
    depends_on:
      - redis
      - rabbitmq
      - elasticsearch
      - backend-java
    volumes:
      - ./apps/backend-python/app:/app/app
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8001/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 30s
    networks:
      - aibidcomposer-network
    command: uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload

  # Celery Worker (AI 异步任务)
  celery-worker:
    build:
      context: ./apps/backend-python
      dockerfile: Dockerfile
    container_name: aibidcomposer-celery-worker
    environment:
      REDIS_URL: redis://:${REDIS_PASSWORD:-redis}@redis:6379/0
      RABBITMQ_URL: amqp://${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq}@rabbitmq:5672/
      ELASTICSEARCH_URL: http://elasticsearch:9200
      ELASTICSEARCH_USER: elastic
      ELASTICSEARCH_PASSWORD: ${ELASTICSEARCH_PASSWORD:-elastic}
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      JAVA_SERVICE_URL: http://backend-java:8080
    depends_on:
      - redis
      - rabbitmq
      - elasticsearch
      - backend-java
    volumes:
      - ./apps/backend-python/app:/app/app
    networks:
      - aibidcomposer-network
    command: celery -A app.tasks.celery_app worker --loglevel=info -Q ai_tasks

  # Celery Beat (定时任务调度)
  celery-beat:
    build:
      context: ./apps/backend-python
      dockerfile: Dockerfile
    container_name: aibidcomposer-celery-beat
    environment:
      REDIS_URL: redis://:${REDIS_PASSWORD:-redis}@redis:6379/0
      RABBITMQ_URL: amqp://${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq}@rabbitmq:5672/
    depends_on:
      - redis
      - rabbitmq
    volumes:
      - ./apps/backend-python/app:/app/app
    networks:
      - aibidcomposer-network
    command: celery -A app.tasks.celery_app beat --loglevel=info

  # React 前端
  frontend:
    build:
      context: ./apps/frontend
      dockerfile: Dockerfile.dev
    container_name: aibidcomposer-frontend
    environment:
      VITE_API_BASE_URL: http://localhost:8080
      VITE_AI_API_BASE_URL: http://localhost:8001
      VITE_WS_BASE_URL: ws://localhost:8080
    ports:
      - "5173:5173"
    volumes:
      - ./apps/frontend/src:/app/src
      - ./apps/frontend/public:/app/public
      - node_modules:/app/node_modules
    depends_on:
      - backend-java
      - backend-python
    networks:
      - aibidcomposer-network
    command: npm run dev -- --host 0.0.0.0

volumes:
  postgres_data:
  redis_data:
  elasticsearch_data:
  rabbitmq_data:
  minio_data:
  maven_cache:
  node_modules:

networks:
  aibidcomposer-network:
    driver: bridge
```

**启动脚本 (scripts/start-dev.sh)**:
```bash
#!/bin/bash
# 需求编号: REQ-JAVA-002
# 开发环境一键启动脚本

set -e

echo "🚀 启动 AIBidComposer 开发环境..."

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker Desktop"
    exit 1
fi

# 检查 .env 文件
if [ ! -f .env ]; then
    echo "⚠️  .env 文件不存在，复制 .env.example 创建..."
    cp .env.example .env
    echo "✅ .env 文件已创建，请编辑配置后重新运行"
    exit 1
fi

# 停止已有容器
echo "🛑 停止已有容器..."
docker-compose down

# 构建镜像
echo "🔨 构建 Docker 镜像..."
docker-compose build

# 启动服务
echo "🌟 启动所有服务..."
docker-compose up -d

# 等待服务就绪
echo "⏳ 等待服务启动..."
sleep 10

# 检查健康状态
echo "🏥 检查服务健康状态..."
docker-compose ps

# 显示服务 URL
echo ""
echo "✅ 开发环境启动成功！"
echo ""
echo "📊 服务访问地址："
echo "  - 前端:          http://localhost:5173"
echo "  - Java API:      http://localhost:8080"
echo "  - Python AI API: http://localhost:8001"
echo "  - Swagger UI:    http://localhost:8080/swagger-ui.html"
echo "  - MinIO Console: http://localhost:9001"
echo "  - RabbitMQ UI:   http://localhost:15672"
echo ""
echo "📝 查看日志: docker-compose logs -f [service-name]"
echo "🛑 停止服务: docker-compose down"
echo ""
```
