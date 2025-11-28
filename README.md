# AIBidComposer - AI标书智能创作平台

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0--MVP-green.svg)]()

## 项目概述

AIBidComposer（AI标书智能创作平台）是一款企业级SaaS平台，使用AI技术辅助企业快速创作高质量投标文档。

### 技术架构

本项目采用**Monorepo混合后端微服务架构**：

```
ai-contract/
├── apps/
│   ├── backend-java/        # Java Spring Boot 服务（端口 8080）
│   │                         # 负责：数据维护、业务逻辑、CRUD
│   ├── backend-python/       # Python FastAPI 服务（端口 8001）
│   │                         # 负责：AI能力、大模型调用、向量检索
│   └── frontend/             # React + Ant Design Pro 前端
│                             # 企业级用户界面
├── docs/                     # 项目文档
├── deploy/                   # 部署配置
└── scripts/                  # 构建和部署脚本
```

### 核心技术栈

#### 后端服务

**Java Spring Boot 服务**（数据维护）：
- Java 17 LTS + Spring Boot 3.2.x
- Spring Data JPA + Spring Security 6.x
- PostgreSQL 14+ + Redis 7+
- Maven 3.9+

**Python FastAPI 服务**（AI能力）：
- Python 3.11+ + FastAPI 0.104+
- LlamaIndex 0.9+（主力RAG框架）
- OpenAI SDK 1.0+ + Anthropic SDK 0.7+
- Elasticsearch 8+（向量检索）

#### 前端

- React 18 + TypeScript 5.x
- Ant Design Pro 6.x + Umi 4.x
- TanStack Query + Zustand

#### 基础设施

- Docker 24+ + Kubernetes 1.28+
- PostgreSQL 14+ / Redis 7+ / Elasticsearch 8+
- MinIO（对象存储）

## 快速开始

### 前置要求

- **Java**: JDK 17+
- **Python**: 3.11+
- **Node.js**: 18+
- **Docker**: 24+
- **PostgreSQL**: 14+
- **Redis**: 7+
- **Elasticsearch**: 8+

### 开发环境启动

#### 1. 克隆代码

```bash
git clone <repository-url>
cd ai-contract
```

#### 2. 环境变量配置

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填写实际配置
# - 数据库密码
# - Redis密码
# - OpenAI API Key
# - 其他密钥
```

#### 3. 使用Docker Compose启动所有服务

```bash
# 启动所有基础服务（数据库、缓存等）
docker-compose up -d postgres redis elasticsearch minio rabbitmq

# 等待服务就绪（约30秒）
```

#### 4. 启动后端服务

**Java Spring Boot 服务**：
```bash
cd apps/backend-java
mvn clean install
mvn spring-boot:run
# 访问：http://localhost:8080
# API文档：http://localhost:8080/swagger-ui.html
```

**Python FastAPI 服务**：
```bash
cd apps/backend-python
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8001
# 访问：http://localhost:8001
# API文档：http://localhost:8001/docs
```

#### 5. 启动前端

```bash
cd apps/frontend
npm install
npm run dev
# 访问：http://localhost:5173
```

### 完整Docker部署

```bash
# 构建所有服务镜像
./scripts/build-images.sh

# 启动所有服务
docker-compose up -d

# 检查服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 项目文档

详细文档请参考 `docs/` 目录：

### 指引文档
- [项目记忆文件（CLAUDE.md）](CLAUDE.md) - AI开发规范和项目铁律
- [README](docs/01-指引/README.md)

### 需求文档
- [产品愿景总览](docs/02-需求/01-产品愿景总览.md)
- [功能模块详解](docs/02-需求/03-功能模块详解.md)

### 架构文档
- [架构设计总览](docs/03-架构/00-架构设计总览.md)
- [数据库设计](docs/03-架构/02-数据库设计.md)
- [API接口设计](docs/03-架构/03-API接口设计.md)
- [AI能力层设计](docs/03-架构/05-AI能力层设计.md)
- [部署架构设计](docs/03-架构/06-部署架构设计.md)

### 实现文档
- [任务计划](docs/05-实现/task-plan.md) - 开发任务和进度跟踪

## 开发规范

### Git提交规范

```bash
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具

**示例**：
```bash
feat(用户模块): 实现用户管理功能

需求编号: REQ-2025-11-001
影响范围:
- 新增 UserService、UserController
- 新增用户管理相关 API

验证结果: 单元测试通过

🤖 Generated with Claude Code
Co-Authored-By: Claude <noreply@anthropic.com>
```

### 代码规范

- **Java**: 遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **Python**: 遵循 PEP 8
- **TypeScript**: 遵循 Airbnb TypeScript Style Guide

详细规范请参考：`docs/99-知识/Java代码规范.md`

## 项目结构

```
ai-contract/
├── apps/                      # 应用代码（Monorepo）
│   ├── backend-java/          # Java Spring Boot 服务
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/aibidcomposer/
│   │   │   │   │       ├── controller/
│   │   │   │   │       ├── service/
│   │   │   │   │       ├── repository/
│   │   │   │   │       ├── model/
│   │   │   │   │       └── config/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   │
│   ├── backend-python/        # Python FastAPI AI服务
│   │   ├── app/
│   │   │   ├── api/
│   │   │   ├── services/
│   │   │   │   └── ai/
│   │   │   ├── models/
│   │   │   ├── core/
│   │   │   └── main.py
│   │   ├── tests/
│   │   ├── requirements.txt
│   │   └── Dockerfile
│   │
│   └── frontend/              # React前端
│       ├── src/
│       │   ├── pages/
│       │   ├── components/
│       │   ├── services/
│       │   ├── models/
│       │   └── layouts/
│       ├── package.json
│       └── Dockerfile
│
├── docs/                      # 文档
│   ├── 01-指引/
│   ├── 02-需求/
│   ├── 03-架构/
│   ├── 04-设计/
│   ├── 05-实现/
│   ├── 06-测试/
│   ├── 07-交付/
│   └── 99-知识/
│
├── deploy/                    # 部署配置
│   ├── docker/
│   └── k8s/
│
├── scripts/                   # 脚本
│   ├── build-images.sh
│   └── deploy.sh
│
├── docker-compose.yml         # Docker Compose配置
├── .env.example               # 环境变量模板
├── .gitignore
├── CLAUDE.md                  # 项目记忆文件
└── README.md                  # 本文件
```

## API文档

### Java服务API（端口 8080）

负责核心业务逻辑：

- **认证授权**: `/api/v1/auth/*`
- **用户管理**: `/api/v1/users/*`
- **组织管理**: `/api/v1/organizations/*`
- **项目管理**: `/api/v1/projects/*`
- **文档管理**: `/api/v1/documents/*`
- **模板管理**: `/api/v1/templates/*`

访问API文档：
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Python AI服务API（端口 8001）

负责AI能力：

- **文档解析**: `/api/v1/ai/parse-document`
- **需求分析**: `/api/v1/ai/analyze-requirements`
- **智能匹配**: `/api/v1/ai/match-capabilities`
- **内容生成**: `/api/v1/ai/generate-content`
- **质量审核**: `/api/v1/ai/review-quality`

访问API文档：
- Swagger UI: http://localhost:8001/docs
- ReDoc: http://localhost:8001/redoc

## 测试

### Java服务测试

```bash
cd apps/backend-java
mvn test                    # 单元测试
mvn verify                  # 集成测试
mvn test jacoco:report      # 测试覆盖率报告
```

### Python服务测试

```bash
cd apps/backend-python
pytest                      # 运行所有测试
pytest --cov=app            # 测试覆盖率
pytest -v tests/            # 详细输出
```

### 前端测试

```bash
cd apps/frontend
npm test                    # 单元测试
npm run test:coverage       # 测试覆盖率
npm run e2e                 # E2E测试
```

## 监控和日志

### 应用监控

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000

### 日志查看

```bash
# Docker容器日志
docker-compose logs -f backend-java
docker-compose logs -f backend-python
docker-compose logs -f frontend

# Kubernetes日志
kubectl logs -f -n aibidcomposer -l app=backend-java
kubectl logs -f -n aibidcomposer -l app=backend-python
```

## 部署

### 开发环境

```bash
docker-compose up -d
```

### 生产环境

```bash
# 构建镜像
./scripts/build-images.sh latest

# 部署到Kubernetes
kubectl apply -f deploy/k8s/

# 检查部署状态
kubectl get pods -n aibidcomposer
```

详细部署文档：[部署架构设计](docs/03-架构/06-部署架构设计.md)

## 常见问题

### 1. Java服务启动失败

检查：
- JDK版本是否为17+
- PostgreSQL是否已启动
- 数据库连接配置是否正确

### 2. Python服务AI功能不可用

检查：
- 环境变量中的 `OPENAI_API_KEY` 是否配置
- Elasticsearch是否已启动
- 网络连接是否正常

### 3. 前端无法连接后端

检查：
- 后端服务是否已启动
- 环境变量中的API地址是否正确
- CORS配置是否正确

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

- **项目主页**: https://github.com/your-org/ai-contract
- **问题反馈**: https://github.com/your-org/ai-contract/issues
- **技术文档**: https://docs.aibidcomposer.com

---

**开发团队**: AIBidComposer Team
**最后更新**: 2025-11-26
**版本**: v1.0.0-MVP
