# AIBidComposer - 项目记忆文件

**项目**: AI标书智能创作平台（AIBidComposer）
**版本**: 1.2
**更新日期**: 2025-11-26
**项目阶段**: 架构设计完成，准备进入开发阶段

---

## 项目核心定位

AIBidComposer 是企业级 SaaS 平台，使用 AI 技术辅助企业快速创作投标文档。

**技术架构**: 混合后端微服务架构
- **Java Spring Boot** (端口 8080) - 数据维护、业务逻辑、CRUD
- **Python FastAPI** (端口 8001) - AI能力、大模型调用
- **React + Ant Design Pro** - 企业级前端

**主要分支**: master

---

## 技术栈核心

### 后端架构职责划分

```
前端 (React + Ant Design Pro)
    │
    ├─► Java Spring Boot (8080)      ├─► Python FastAPI (8001)
    │   - 用户认证授权               │   - GPT-4/Claude 调用
    │   - 组织项目管理               │   - 文档智能解析
    │   - 文档CRUD                   │   - RAG 内容生成
    │   - 模板管理                   │   - 向量化检索
    │   - 协作审批                   │   - 知识图谱
    │                                │
    └─► PostgreSQL + Redis + Elasticsearch
```

### 技术栈速查

**Java Spring Boot 服务** (数据维护):
- Java 17 LTS + Spring Boot 3.2.x
- Spring Data JPA + Spring Security 6.x
- PostgreSQL 14+ + Redis 7+
- Maven 3.9+

**Python FastAPI 服务** (AI能力):
- Python 3.11+ + FastAPI 0.104+
- LlamaIndex 0.9+ (主力RAG框架，80%任务)
- LangChain 0.1+ (备用，仅复杂Agent场景)
- OpenAI SDK 1.0+ + Anthropic SDK 0.7+
- Elasticsearch Python 8.11+

**前端**:
- React 18 + TypeScript 5.x
- Ant Design Pro 6.x + Umi 4.x

**基础设施**:
- Docker 24+ + Kubernetes 1.28+
- Nginx 1.25+ + MinIO

### 服务间通信
- REST API (Java ↔ Python)
- RabbitMQ (异步任务)
- Redis Pub/Sub (事件通知)

---

## 项目铁律（10条必须遵守）

> 🚨 **重要**: 每次工作都必须遵守以下铁律，违反将导致工作成果不合格。

### 1. 简体中文优先 🇨🇳
- ✅ 所有文档、注释、交流使用简体中文
- ✅ 技术术语可保留英文（如 Spring Boot、React）
- ❌ 禁止使用繁体中文或纯英文文档

### 2. 混合后端架构职责明确 ☕🐍
- ✅ 数据维护使用 Java Spring Boot
- ✅ AI能力使用 Python FastAPI
- ❌ 禁止在Java中调用大模型（应由Python服务处理）
- ❌ 禁止在Python中处理数据CRUD（应由Java服务处理）

### 3. 文档集中管理 📁
- ✅ 所有文档保存到 `docs/` 或现有文档目录
- ✅ 使用标准化子目录：01-指引/ 02-需求/ 03-架构/ 04-设计/ 05-实现/ 06-测试/ 07-交付/ 99-知识/
- ❌ 禁止文档散落在项目根目录

### 4. 文档元信息完整 📝
每个文档必须包含标准头部（7个字段）：
```yaml
---
文档类型: [需求/设计/实现/测试]文档
需求编号: REQ-YYYY-MM-NNN
创建日期: YYYY-MM-DD
创建者: [AI模型名称 / 开发者姓名]
最后更新: YYYY-MM-DD
更新者: [AI模型名称 / 开发者姓名]
状态: [草稿/评审中/已批准/已实现]
---
```

### 5. 修改历史可追溯 📜
每个文档包含修改历史表：
```markdown
## 修改历史
| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| YYYY-MM-DD | 1.0 | 模型名称/姓名 | 初始创建 |
```

### 6. 源码需求标注 🏷️
代码注释中添加需求编号：
```java
/**
 * 用户管理服务
 * 需求编号: REQ-2025-11-001
 */
@Service
public class UserService {
    // 需求编号: REQ-2025-11-001 - 用户查询功能
    public User findById(Long id) { ... }
}
```

### 7. 完整功能必须提交Git 💾
- ✅ 每个完整功能实现后立即提交
- ✅ 提交信息包含需求编号：
```bash
feat(用户模块): 实现用户管理功能

需求编号: REQ-2025-11-001
影响范围: 新增 UserService、UserController
验证结果: 单元测试通过

🤖 Generated with Claude Code
Co-Authored-By: Claude <noreply@anthropic.com>
```
- ❌ 禁止积累多个功能后一次性提交

### 8. 数据必须可追溯 📊
- ✅ 所有数字说明来源（如"基于 xxx.md 实际扫描"）
- ✅ 所有结论说明依据
- ❌ 禁止未经验证的量化声明
- ❌ 禁止模棱两可的表述（如"大概"、"应该"）

### 9. 诚实透明优先 🔍
- ✅ 不确定时明确说明
- ✅ 错误时立即承认并纠正
- ✅ 推测时明确标注
- ❌ 禁止主观猜测冒充客观事实

### 10. AI模型身份标识完整 🤖
- ✅ 使用完整模型标识：`claude-sonnet-4-5 (claude-sonnet-4-5-20250929)`
- ✅ Git提交包含 Co-Authored-By

---

## 代码规范要点

### Java 命名规范
```java
// 类名：大驼峰
public class UserService { }

// 方法名：小驼峰
public User findById(Long id) { }

// 常量：全大写+下划线
public static final int MAX_RETRY_COUNT = 3;

// 包名：全小写
package com.aibidcomposer.service;
```

### Spring Boot 分层规范
```java
// Controller 层
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController { }

// Service 层
@Service
@RequiredArgsConstructor
@Transactional
public class UserService { }

// Repository 层
@Repository
public interface UserRepository extends JpaRepository<User, Long> { }
```

**详细规范**: @docs/99-知识/Java代码规范.md (计划创建)

---

## 常用命令速查

### Maven 构建
```bash
mvn clean install      # 清理并构建
mvn test              # 运行单元测试
mvn verify            # 运行所有测试
mvn spring-boot:run   # 启动Java服务
```

### Python AI 服务
```bash
cd backend/fastapi-ai-service
uvicorn main:app --reload --port 8001
pytest                # 运行测试
```

### Docker 操作
```bash
docker-compose up -d     # 启动所有服务
docker-compose down      # 停止所有服务
docker-compose logs -f   # 查看日志
```

### 前端操作
```bash
cd frontend/react-app
npm run dev    # 启动开发服务器
npm test       # 运行测试
npm run build  # 构建生产版本
```

### Git 操作
```bash
git status               # 查看状态
git add .               # 添加所有修改
git commit              # 提交（使用规范格式）
git push origin master  # 推送到远程
```

---

## 检查清单

### 每次创建/修改文档前
- [ ] 使用简体中文编写
- [ ] 确认技术栈为混合后端架构（Java处理数据，Python处理AI）
- [ ] 保存到正确的文档目录（docs/01-07或99）
- [ ] 包含完整的元信息头部（7个字段）
- [ ] 包含修改历史表
- [ ] AI模型名称使用完整标识

### 每次修改代码前
- [ ] 明确需求编号
- [ ] 确认使用正确的技术栈（Java数据维护 / Python AI能力）
- [ ] 代码注释中标注需求编号
- [ ] 遵循命名规范和分层规范

### 每次完成功能后
- [ ] 运行测试验证（`mvn test` 或 `pytest`）
- [ ] 更新相关文档
- [ ] 提交 Git（包含需求编号和详细说明）
- [ ] 检查提交信息符合规范

### 质量检查
- [ ] 代码符合规范
- [ ] 测试覆盖率 >80%
- [ ] 文档已更新
- [ ] 无安全漏洞

---

## Git 提交规范

### 提交消息格式
```bash
<type>(<scope>): <subject>

<body>

<footer>
```

### 类型说明
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具

### 示例
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

---

## 项目目录结构

```
ai-contract/
├── docs/                    # 项目文档
│   ├── 01-指引/             # 项目指引、规范
│   ├── 02-需求/             # 需求文档
│   ├── 03-架构/             # 系统架构
│   ├── 04-设计/             # 详细设计
│   ├── 05-实现/             # 实现说明、任务计划
│   ├── 06-测试/             # 测试计划
│   ├── 07-交付/             # 部署文档
│   └── 99-知识/             # 知识库、FAQ
├── backend/
│   ├── spring-boot-service/ # Java Spring Boot 服务
│   └── fastapi-ai-service/  # Python FastAPI AI服务
├── frontend/
│   └── react-app/           # React 前端
├── deploy/
│   ├── docker/              # Docker 配置
│   └── k8s/                 # Kubernetes 配置
└── CLAUDE.md               # 本文档
```

---

## 关键文档索引

使用 @import 语法引用详细文档：

**架构文档**:
- @docs/03-架构/00-架构设计总览.md
- @docs/03-架构/02-数据库设计.md
- @docs/03-架构/03-API接口设计.md
- @docs/03-架构/05-AI能力层设计.md
- @docs/03-架构/06-部署架构设计.md

**实现文档**:
- @docs/05-实现/task-plan.md - 开发任务计划和进度跟踪

**需求文档**:
- @docs/02-需求/01-产品愿景总览.md - 产品愿景和战略定位
- @docs/02-需求/03-功能模块详解.md - 8大核心功能模块详解

---

## 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|---------|
| 1.2 | 2025-11-26 | 精简优化：删除冗余内容，聚焦项目特定规则，从841行优化到约350行 |
| 1.1 | 2025-11-26 | 架构调整：更新为混合后端架构（Java + Python），明确服务职责 |
| 1.0 | 2025-11-25 | 初始版本：创建项目记忆文件 |

### 版本1.2优化要点（2025-11-26）

根据 [Claude Code 官方最佳实践](https://code.claude.com/docs/en/memory)：

**删除内容**:
- 通用的 Claude Code 使用说明（非项目特定）
- 重复的章节（核心原则、项目特定规范已合并到铁律）
- 详细的代码示例（应放在单独的代码规范文档中）
- 冗长的工作流程图和测试示例

**优化策略**:
- ✅ 聚焦项目特定信息（混合后端架构是最大特点）
- ✅ 具体而简洁（保留核心规则，删除冗长示例）
- ✅ 使用 @import 引用详细文档（避免重复）
- ✅ 保持可扫描性（清晰的标题和项目符号）
- ✅ 从841行精简到约350行（减少58%）

---

## 快速提醒

**每次工作前必读**：

1. ✅ **混合后端架构**: Java处理数据，Python处理AI
2. ✅ **简体中文**: 所有文档和注释
3. ✅ **需求编号**: 代码和Git提交都要包含
4. ✅ **文档标准**: 元信息完整 + 修改历史
5. ✅ **立即提交**: 完整功能实现后马上提交Git

**技术栈速记**:
- Java 17 + Spring Boot 3.2 (数据维护)
- Python 3.11 + FastAPI + LlamaIndex (AI能力)
- React 18 + TypeScript + Ant Design Pro (前端)
- PostgreSQL + Elasticsearch + Redis (数据层)

**详细文档**: 使用 `/memory` 命令编辑本文档，查看 @docs/ 目录获取详细信息。

---

**本文档遵循自身定义的所有规范，可作为规范执行的参考示例。**
