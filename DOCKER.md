# AIBidComposer - Docker 部署指南

**更新时间**: 2025-11-26
**文档版本**: 1.0
**适用环境**: 开发环境 + 生产环境

---

## 目录

1. [快速开始](#快速开始)
2. [环境配置](#环境配置)
3. [服务架构](#服务架构)
4. [开发环境](#开发环境)
5. [生产环境](#生产环境)
6. [常用命令](#常用命令)
7. [数据管理](#数据管理)
8. [故障排查](#故障排查)

---

## 快速开始

### 前置要求

- **Docker**: 24.0+ （[安装指南](https://docs.docker.com/get-docker/)）
- **Docker Compose**: 2.0+ （Docker Desktop 自带）
- **硬件要求**:
  - CPU: 4核+
  - 内存: 8GB+ （建议 16GB）
  - 磁盘: 50GB+

### 一键启动开发环境

```bash
# 1. 克隆代码库
git clone https://github.com/your-org/ai-contract.git
cd ai-contract

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，填写必要的配置

# 3. 启动所有服务
chmod +x scripts/start-dev.sh
./scripts/start-dev.sh

# 或者手动启动
docker-compose up -d
```

### 一键启动生产环境

```bash
# 1. 配置生产环境变量
cp .env.example .env
# 填写生产环境的安全配置

# 2. 启动生产服务
chmod +x scripts/start-prod.sh
./scripts/start-prod.sh

# 或者手动启动
docker-compose -f docker-compose.prod.yml up -d
```

---

## 环境配置

### 环境变量说明

复制 `.env.example` 为 `.env` 并配置以下必需变量：

#### 数据库配置

```bash
# PostgreSQL（必需）
POSTGRES_PASSWORD=your_secure_postgres_password_change_me

# Elasticsearch（必需）
ELASTICSEARCH_PASSWORD=your_secure_elasticsearch_password_change_me

# Redis（必需）
REDIS_PASSWORD=your_secure_redis_password_change_me

# RabbitMQ（必需）
RABBITMQ_PASSWORD=your_secure_rabbitmq_password_change_me

# MinIO（必需）
MINIO_ROOT_PASSWORD=your_secure_minio_password_change_me
```

#### 应用配置

```bash
# JWT 密钥（必需，至少 32 字符）
JWT_SECRET=your_jwt_secret_key_min_32_characters_long_change_me

# 加密密钥（必需，至少 32 字符）
SECRET_KEY=your_secret_key_for_encryption_min_32_characters_change_me
```

#### AI 服务配置

```bash
# OpenAI API Key（必需）
OPENAI_API_KEY=sk-your-openai-api-key-here

# Anthropic Claude API Key（可选）
ANTHROPIC_API_KEY=your-anthropic-api-key-here

# Pinecone（可选，云端向量数据库）
PINECONE_API_KEY=your-pinecone-api-key-here
PINECONE_ENVIRONMENT=your-pinecone-environment
```

### 密码强度要求

⚠️ **生产环境密码规范**：

- **长度**: 至少 16 位
- **复杂度**: 包含大小写字母、数字、特殊字符
- **不要**: 使用默认密码、简单密码、重复密码

**生成强密码示例**:

```bash
# Linux/Mac
openssl rand -base64 32

# 或使用 Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

---

## 服务架构

### 服务列表

| 服务名 | 端口 | 用途 | 依赖 |
|--------|------|------|------|
| **postgres** | 5432 | 主数据库（PostgreSQL 15） | - |
| **elasticsearch** | 9200, 9300 | 向量检索 + 全文搜索 | - |
| **redis** | 6379 | 缓存 + 会话存储 | - |
| **rabbitmq** | 5672, 15672 | 消息队列 | - |
| **minio** | 9000, 9001 | 对象存储 | - |
| **backend-java** | 8080 | Java Spring Boot API | postgres, redis, rabbitmq |
| **backend-python** | 8001 | Python FastAPI AI 服务 | redis, rabbitmq, elasticsearch |
| **ai-worker** | - | Celery 异步任务 | redis, rabbitmq, elasticsearch |
| **celery-beat** | - | Celery 定时任务 | redis, rabbitmq |
| **frontend** | 5173 (dev) / 80 (prod) | React 前端 | backend-java, backend-python |

### 服务依赖关系

```
前端 (React)
  ├─► Java API (8080)
  └─► Python AI API (8001)

Java API
  ├─► PostgreSQL (数据持久化)
  ├─► Redis (缓存)
  ├─► RabbitMQ (消息队列)
  ├─► MinIO (文件存储)
  └─► Python AI API (AI 能力调用)

Python AI API
  ├─► PostgreSQL (只读查询)
  ├─► Elasticsearch (向量检索)
  ├─► Redis (缓存)
  ├─► RabbitMQ (消息队列)
  └─► OpenAI/Anthropic (LLM 调用)

AI Worker
  └─► 与 Python AI API 相同依赖
```

### 网络拓扑

```
Docker Network: aibidcomposer-network (bridge)
  ├─ postgres
  ├─ elasticsearch
  ├─ redis
  ├─ rabbitmq
  ├─ minio
  ├─ backend-java
  ├─ backend-python
  ├─ ai-worker
  ├─ celery-beat
  └─ frontend
```

---

## 开发环境

### 启动开发环境

```bash
# 方式1：使用脚本（推荐）
./scripts/start-dev.sh

# 方式2：手动启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend-java
docker-compose logs -f backend-python
```

### 开发环境特点

✅ **热重载支持**:
- Java: 源码挂载到 `/app/src`，支持 Spring Boot DevTools
- Python: 源码挂载到 `/app/app`，使用 `--reload` 模式
- React: 源码挂载，Vite HMR 自动刷新

✅ **调试工具**:
- React Query DevTools（前端）
- Swagger UI（Java API 文档）: http://localhost:8080/swagger-ui.html
- ReDoc（Python API 文档）: http://localhost:8001/docs
- RabbitMQ Management: http://localhost:15672
- MinIO Console: http://localhost:9001

✅ **数据持久化**:
- 所有数据存储在 Docker volumes
- 停止服务不会丢失数据
- `docker-compose down -v` 会删除所有数据

### 访问地址

- **前端**: http://localhost:5173
- **Java API**: http://localhost:8080
- **Python AI API**: http://localhost:8001
- **PostgreSQL**: localhost:5432
- **Elasticsearch**: http://localhost:9200
- **Redis**: localhost:6379
- **RabbitMQ Web**: http://localhost:15672 (guest/guest)
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin)

---

## 生产环境

### 启动生产环境

```bash
# 方式1：使用脚本（推荐）
./scripts/start-prod.sh

# 方式2：手动启动
docker-compose -f docker-compose.prod.yml up -d

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f
```

### 生产环境特点

🔒 **安全配置**:
- 数据库端口不暴露到主机
- 使用强密码（至少 16 位）
- JWT Token 过期时间限制
- Nginx 反向代理

⚡ **性能优化**:
- 多进程部署（Python 8 workers）
- 资源限制和健康检查
- Gzip 压缩
- 静态资源缓存
- 数据库连接池

📊 **监控和日志**:
- Docker 健康检查
- 应用健康端点
- 集中式日志收集
- 慢查询日志

### 生产环境访问

- **前端**: http://localhost (Nginx 反向代理)
- **API**: http://localhost/api
- **健康检查**: http://localhost/health

**注意**: 生产环境不暴露内部服务端口（PostgreSQL、Redis 等）

---

## 常用命令

### 启动和停止

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose stop

# 停止并删除容器（保留数据）
docker-compose down

# 停止并删除容器和数据卷（⚠️ 会删除所有数据）
docker-compose down -v

# 重启服务
docker-compose restart

# 重启特定服务
docker-compose restart backend-java
```

### 查看状态

```bash
# 查看所有服务状态
docker-compose ps

# 查看服务资源使用
docker stats

# 查看服务健康状态
docker-compose ps | grep healthy
```

### 日志管理

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend-java

# 查看最近 100 行日志
docker-compose logs --tail=100 backend-python

# 查看实时日志
docker-compose logs -f --tail=0
```

### 进入容器

```bash
# 进入 Java 容器
docker-compose exec backend-java bash

# 进入 Python 容器
docker-compose exec backend-python sh

# 进入 PostgreSQL 容器
docker-compose exec postgres psql -U postgres -d aibidcomposer

# 进入 Redis 容器
docker-compose exec redis redis-cli -a your_redis_password
```

### 重新构建

```bash
# 重新构建所有服务
docker-compose build

# 重新构建特定服务
docker-compose build backend-java

# 强制重新构建（不使用缓存）
docker-compose build --no-cache

# 构建并启动
docker-compose up -d --build
```

---

## 数据管理

### 数据卷位置

Docker volumes 存储位置：

- **Linux**: `/var/lib/docker/volumes/`
- **Mac**: `~/Library/Containers/com.docker.docker/Data/vms/0/`
- **Windows**: `\\wsl$\docker-desktop-data\version-pack-data\community\docker\volumes\`

### 数据卷列表

```bash
# 查看所有数据卷
docker volume ls | grep aibidcomposer

# 查看特定数据卷详情
docker volume inspect aibidcomposer_postgres_data
```

**数据卷**:
- `aibidcomposer_postgres_data` - PostgreSQL 数据
- `aibidcomposer_elasticsearch_data` - Elasticsearch 索引
- `aibidcomposer_redis_data` - Redis 持久化数据
- `aibidcomposer_rabbitmq_data` - RabbitMQ 队列数据
- `aibidcomposer_minio_data` - MinIO 对象存储

### 数据备份

```bash
# 备份 PostgreSQL
docker-compose exec postgres pg_dump -U postgres aibidcomposer > backup_$(date +%Y%m%d).sql

# 备份 MinIO 数据
docker run --rm \
  --volumes-from ac-minio \
  -v $(pwd)/backups:/backup \
  alpine \
  tar czf /backup/minio_$(date +%Y%m%d).tar.gz /data

# 备份所有数据卷
docker run --rm \
  -v aibidcomposer_postgres_data:/postgres:ro \
  -v aibidcomposer_minio_data:/minio:ro \
  -v $(pwd)/backups:/backup \
  alpine \
  tar czf /backup/full_backup_$(date +%Y%m%d).tar.gz /postgres /minio
```

### 数据恢复

```bash
# 恢复 PostgreSQL
cat backup_20251126.sql | docker-compose exec -T postgres psql -U postgres aibidcomposer

# 恢复 MinIO 数据
docker run --rm \
  --volumes-from ac-minio \
  -v $(pwd)/backups:/backup \
  alpine \
  tar xzf /backup/minio_20251126.tar.gz -C /
```

### 清理数据

```bash
# ⚠️ 删除所有容器和数据卷
docker-compose down -v

# 清理未使用的镜像和容器
docker system prune -a

# 清理所有未使用的数据卷（谨慎使用）
docker volume prune
```

---

## 故障排查

### 常见问题

#### 1. 服务无法启动

```bash
# 查看服务状态
docker-compose ps

# 查看详细日志
docker-compose logs backend-java

# 检查端口占用
lsof -i :8080
netstat -an | grep 8080
```

**解决方案**:
- 检查端口是否被占用
- 查看日志中的错误信息
- 确认环境变量配置正确

#### 2. 数据库连接失败

```bash
# 检查 PostgreSQL 是否运行
docker-compose ps postgres

# 查看 PostgreSQL 日志
docker-compose logs postgres

# 测试连接
docker-compose exec postgres psql -U postgres -d aibidcomposer -c "SELECT 1"
```

**解决方案**:
- 等待数据库完全启动（查看健康检查）
- 检查数据库密码配置
- 确认网络连接正常

#### 3. Elasticsearch 启动失败

```bash
# 查看 Elasticsearch 日志
docker-compose logs elasticsearch

# 检查内存设置
docker stats elasticsearch
```

**常见错误**:
- `max virtual memory areas vm.max_map_count [65530] is too low`

**解决方案**:

```bash
# Linux
sudo sysctl -w vm.max_map_count=262144

# Mac (Docker Desktop)
# Settings -> Resources -> Advanced -> Memory: 至少 4GB
```

#### 4. AI 服务调用失败

```bash
# 查看 Python AI 服务日志
docker-compose logs backend-python

# 检查 OpenAI API Key
docker-compose exec backend-python env | grep OPENAI_API_KEY
```

**解决方案**:
- 确认 API Key 配置正确
- 检查网络连接
- 查看 API 限流状态

#### 5. 内存不足

```bash
# 查看资源使用
docker stats

# 查看系统内存
free -h  # Linux
vm_stat  # Mac
```

**解决方案**:
- 增加 Docker Desktop 内存限制
- 减少服务数量（注释可选服务）
- 降低 Elasticsearch 内存配置

### 健康检查

```bash
# 检查所有服务健康状态
docker-compose ps | grep healthy

# 测试 API 端点
curl http://localhost:8080/actuator/health
curl http://localhost:8001/health

# 测试数据库连接
docker-compose exec postgres pg_isready

# 测试 Redis 连接
docker-compose exec redis redis-cli -a your_password ping

# 测试 Elasticsearch
curl -u elastic:your_password http://localhost:9200/_cluster/health
```

### 性能调优

#### Java 服务

```yaml
# docker-compose.yml 中添加
services:
  backend-java:
    environment:
      JAVA_OPTS: "-Xms512m -Xmx2g -XX:+UseG1GC"
```

#### Python 服务

```yaml
# docker-compose.yml 中调整
services:
  backend-python:
    environment:
      WORKERS: 8  # 根据 CPU 核心数调整
      WEB_CONCURRENCY: 4
```

#### PostgreSQL

```yaml
# docker-compose.yml 中添加
services:
  postgres:
    command:
      - "postgres"
      - "-c"
      - "max_connections=200"
      - "-c"
      - "shared_buffers=256MB"
      - "-c"
      - "effective_cache_size=1GB"
```

---

## 最佳实践

### 开发环境

✅ **推荐做法**:
1. 定期执行 `docker-compose pull` 更新基础镜像
2. 使用 `docker-compose logs -f` 实时查看日志
3. 定期清理未使用的镜像和容器
4. 使用环境变量分离配置
5. 代码修改后自动热重载（已配置）

❌ **不推荐**:
1. 不要在开发环境使用 `down -v`（会删除数据）
2. 不要修改 Docker volumes 数据
3. 不要在容器内安装额外软件

### 生产环境

✅ **推荐做法**:
1. 使用强密码和密钥
2. 定期备份数据
3. 监控服务健康状态
4. 使用 Nginx 反向代理
5. 配置日志轮转
6. 限制资源使用

❌ **不推荐**:
1. 不要使用默认密码
2. 不要暴露内部服务端口
3. 不要在生产环境启用调试模式
4. 不要忽略健康检查失败

---

## 附录

### 相关文档

- [主 README](./README.md) - 项目总览
- [架构设计](./docs/03-架构/00-架构设计总览.md)
- [部署架构](./docs/03-架构/06-部署架构设计.md)
- [开发指南](./CONTRIBUTING.md)

### 外部资源

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 参考](https://docs.docker.com/compose/compose-file/)
- [PostgreSQL 官方文档](https://www.postgresql.org/docs/)
- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)

---

**文档维护**: AIBidComposer Team
**最后更新**: 2025-11-26
**反馈建议**: 请提交 Issue 或 Pull Request
