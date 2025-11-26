# AI标书智能创作平台 - 开发任务计划

**文档编号**: IMP-001-001
**创建时间**: 2025-11-26
**最后修改**: 2025-11-26
**执行模型**: claude-sonnet-4-5-20250929
**文档状态**: 进行中
**项目阶段**: 架构设计完成，准备进入开发阶段

---

## 修改历史

| 日期 | 修改人/模型 | 修改概要 |
|------|------------|---------|
| 2025-11-26 | claude-sonnet-4-5 | 创建AI标书智能创作平台任务计划 |

---

## 概述

本文档记录 **AIBidComposer（AI标书智能创作平台）** 项目的开发任务计划和进度跟踪。

### 项目架构特点

本项目采用**混合后端微服务架构**：
- **Java Spring Boot 服务**（端口 8080）- 数据维护、业务逻辑、CRUD
- **Python FastAPI 服务**（端口 8001）- AI能力、大模型调用
- **React + Ant Design Pro 前端** - 企业级用户界面

### 任务组织原则

1. **按技术栈分类**：Java模块、Python AI模块、前端模块
2. **按功能模块分类**：对应8大核心功能模块
3. **按优先级分类**：P0-P3，确保核心功能优先交付

---

## 当前迭代：v1.0.0 MVP

**时间范围**: 2025-11-26 - 2026-02-28
**迭代目标**: 完成最小可用产品（MVP），实现核心AI标书生成流程

### MVP核心范围

✅ **必须包含的功能**：
- 用户认证和组织管理
- 招标文件智能解析
- 企业能力库基础管理
- 智能内容生成（核心AI功能）
- 标书模板管理
- 标书导出（PDF/Word）

⚠️ **暂不包含的功能**：
- 高级协作和审批流程
- 完整知识图谱
- 深度查重系统
- 多人实时协作

---

## 任务优先级分类

### P0 - 紧急重要（立即处理）
- [x] 项目架构设计文档完成
- [ ] 开发环境搭建和基础框架初始化

### P1 - 高优先级（第1-2月完成）
- [ ] **JAVA-001**: 用户认证授权模块
- [ ] **JAVA-002**: 组织和项目管理模块
- [ ] **AI-001**: 招标文件智能解析模块
- [ ] **AI-002**: 智能内容生成引擎
- [ ] **FRONT-001**: 基础框架和布局
- [ ] **FRONT-002**: 用户和项目管理界面

### P2 - 中优先级（第2-3月完成）
- [ ] **JAVA-003**: 模板管理模块
- [ ] **JAVA-004**: 文档管理模块
- [ ] **AI-003**: 企业能力库向量化
- [ ] **AI-004**: 智能匹配分析
- [ ] **FRONT-003**: 文档编辑和预览界面
- [ ] **FRONT-004**: AI助手交互界面

### P3 - 低优先级（v2.0规划）
- [ ] **JAVA-005**: 协作审批流程
- [ ] **JAVA-006**: 高级权限控制
- [ ] **AI-005**: 深度查重系统
- [ ] **AI-006**: 知识图谱构建
- [ ] **FRONT-005**: 多人实时协作
- [ ] **FRONT-006**: 高级数据分析看板

---

## 基础设施任务

### INFRA-001: 开发环境搭建
**负责人**: 开发团队
**开始时间**: 2025-11-26
**预计完成**: 2025-12-05
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/8 子任务)

#### 子任务
1. [ ] 本地开发环境配置
   - [ ] Java 17 + Maven 3.9 安装
   - [ ] Python 3.11 + Poetry/pip 环境
   - [ ] Node.js 18+ + pnpm/yarn 安装
   - [ ] Docker Desktop 安装和配置

2. [ ] 数据库环境搭建
   - [ ] PostgreSQL 14+ 本地实例
   - [ ] Redis 7+ 本地实例
   - [ ] Elasticsearch 8+ 本地实例
   - [ ] 数据库管理工具（DBeaver/DataGrip）

3. [ ] Java Spring Boot 项目初始化
   - [ ] Spring Initializr 生成基础项目
   - [ ] 配置 application.yml（dev/prod环境）
   - [ ] 集成 Spring Data JPA
   - [ ] 集成 Spring Security
   - [ ] 配置 Flyway 数据库迁移

4. [ ] Python FastAPI 项目初始化
   - [ ] FastAPI 项目结构创建
   - [ ] 配置 pyproject.toml 或 requirements.txt
   - [ ] 集成 LlamaIndex 0.9+
   - [ ] 集成 OpenAI SDK
   - [ ] 配置 Elasticsearch Python 客户端

5. [ ] React 前端项目初始化
   - [ ] Ant Design Pro 脚手架初始化
   - [ ] 配置 TypeScript 5.x
   - [ ] 集成 Umi 4.x 路由
   - [ ] 配置 ProComponents
   - [ ] API 客户端配置（axios/fetch）

6. [ ] Docker 容器化配置
   - [ ] 编写 Java 服务 Dockerfile
   - [ ] 编写 Python 服务 Dockerfile
   - [ ] 编写 React 前端 Dockerfile
   - [ ] 编写 docker-compose.yml

7. [ ] CI/CD 基础配置
   - [ ] GitHub Actions 或 GitLab CI 配置
   - [ ] 代码质量检查（SonarQube/ESLint）
   - [ ] 自动化测试配置

8. [ ] 文档和规范
   - [ ] API 文档生成配置（Swagger/OpenAPI）
   - [ ] 代码规范文档（Java/Python/TS）
   - [ ] Git 工作流文档

#### 技术实现要点
- 使用 Docker Compose 实现一键启动所有服务
- 配置环境变量管理（.env 文件）
- 确保所有服务能够相互通信

#### 相关文档
- 架构文档: `docs/03-架构/00-架构设计总览.md`
- 部署文档: `docs/03-架构/06-部署架构设计.md`

---

## Java Spring Boot 服务任务

### JAVA-001: 用户认证授权模块
**需求编号**: REQ-JAVA-001
**负责人**: Java 后端开发
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/6 子任务)

#### 子任务
1. [ ] 用户管理基础功能
   - [ ] User 实体设计（JPA Entity）
   - [ ] UserRepository 接口
   - [ ] UserService 业务逻辑
   - [ ] UserController REST API
   - [ ] 单元测试

2. [ ] Spring Security 集成
   - [ ] JWT Token 认证配置
   - [ ] SecurityConfig 配置类
   - [ ] UserDetailsService 实现
   - [ ] 密码加密（BCrypt）

3. [ ] 登录注册功能
   - [ ] 用户注册 API（/api/auth/register）
   - [ ] 用户登录 API（/api/auth/login）
   - [ ] Token 刷新 API（/api/auth/refresh）
   - [ ] 登出功能（Token 失效）

4. [ ] 权限控制
   - [ ] 角色管理（Role Entity）
   - [ ] 基于角色的访问控制（RBAC）
   - [ ] 权限注解（@PreAuthorize）
   - [ ] API 权限拦截器

5. [ ] 用户个人信息管理
   - [ ] 获取当前用户信息 API
   - [ ] 更新用户资料 API
   - [ ] 修改密码 API
   - [ ] 用户头像上传（MinIO集成）

6. [ ] 数据库设计和迁移
   - [ ] Flyway 迁移脚本：V1__create_user_tables.sql
   - [ ] users 表设计
   - [ ] roles 表设计
   - [ ] user_roles 关联表设计

#### 技术实现要点
- 使用 Spring Security 6.x + JWT
- 密码使用 BCryptPasswordEncoder
- Token 存储在 Redis（支持快速失效）
- 实现 RefreshToken 机制（7天有效期）

#### API 接口设计
```
POST   /api/auth/register      # 用户注册
POST   /api/auth/login         # 用户登录
POST   /api/auth/refresh       # 刷新Token
POST   /api/auth/logout        # 登出
GET    /api/users/me           # 获取当前用户
PUT    /api/users/me           # 更新用户资料
PUT    /api/users/me/password  # 修改密码
```

#### 遇到的问题
记录开发过程中遇到的问题和解决方案

#### 相关文档
- 架构文档: `docs/03-架构/02-数据库设计.md`
- API文档: `docs/03-架构/03-API接口设计.md`

---

### JAVA-002: 组织和项目管理模块
**需求编号**: REQ-JAVA-002
**负责人**: Java 后端开发
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

#### 子任务
1. [ ] 组织管理功能
   - [ ] Organization 实体设计
   - [ ] 组织CRUD操作
   - [ ] 组织成员管理
   - [ ] 组织权限控制

2. [ ] 项目管理功能
   - [ ] Project 实体设计
   - [ ] 项目CRUD操作
   - [ ] 项目阶段管理
   - [ ] 项目成员分配

3. [ ] 招标项目管理
   - [ ] BidProject 实体设计
   - [ ] 招标文件关联
   - [ ] 投标进度跟踪
   - [ ] 项目归档

4. [ ] 项目协作
   - [ ] 项目成员权限
   - [ ] 项目活动日志
   - [ ] 项目通知机制

5. [ ] 数据库设计
   - [ ] Flyway 脚本：V2__create_org_project_tables.sql
   - [ ] organizations 表
   - [ ] projects 表
   - [ ] bid_projects 表
   - [ ] project_members 表

---

### JAVA-003: 模板管理模块
**需求编号**: REQ-JAVA-003
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

#### 子任务
1. [ ] 模板基础管理
   - [ ] Template 实体设计
   - [ ] 模板CRUD操作
   - [ ] 模板分类管理
   - [ ] 模板版本控制

2. [ ] 模板内容管理
   - [ ] 模板结构定义（JSON Schema）
   - [ ] 模板变量管理
   - [ ] 模板片段管理
   - [ ] 模板预览功能

3. [ ] 模板共享
   - [ ] 公共模板库
   - [ ] 组织私有模板
   - [ ] 模板权限控制
   - [ ] 模板收藏和评分

4. [ ] 数据库设计
   - [ ] Flyway 脚本：V3__create_template_tables.sql
   - [ ] templates 表
   - [ ] template_versions 表
   - [ ] template_categories 表

---

### JAVA-004: 文档管理模块
**需求编号**: REQ-JAVA-004
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

#### 子任务
1. [ ] 文档基础管理
   - [ ] Document 实体设计
   - [ ] 文档CRUD操作
   - [ ] 文档状态管理
   - [ ] 文档关联管理

2. [ ] 文件存储
   - [ ] MinIO 集成
   - [ ] 文件上传下载
   - [ ] 文件预览
   - [ ] 文件版本管理

3. [ ] 文档版本控制
   - [ ] Git-like 版本系统
   - [ ] 版本比较
   - [ ] 版本回退
   - [ ] 版本分支管理

4. [ ] 文档导出
   - [ ] 导出为 PDF
   - [ ] 导出为 Word
   - [ ] 自定义导出格式
   - [ ] 批量导出

5. [ ] 数据库设计
   - [ ] Flyway 脚本：V4__create_document_tables.sql
   - [ ] documents 表
   - [ ] document_versions 表
   - [ ] document_files 表

---

## Python FastAPI AI 服务任务

### AI-001: 招标文件智能解析模块
**需求编号**: REQ-AI-001
**负责人**: Python AI 开发
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/6 子任务)

#### 子任务
1. [ ] 文档解析引擎
   - [ ] PDF 文档解析（PyPDF2/PDFPlumber）
   - [ ] Word 文档解析（python-docx）
   - [ ] Excel 表格解析（openpyxl）
   - [ ] 文档结构化提取

2. [ ] 关键信息提取
   - [ ] 使用 LlamaIndex 构建提取 Pipeline
   - [ ] GPT-4 调用（OpenAI SDK）
   - [ ] 项目基本信息提取
   - [ ] 技术要求提取
   - [ ] 商务条款提取
   - [ ] 评分标准提取

3. [ ] 需求分析引擎
   - [ ] 需求分类（强制/可选/优先）
   - [ ] 需求优先级分析
   - [ ] 需求复杂度评估
   - [ ] 需求依赖关系识别

4. [ ] 风险评估系统
   - [ ] 技术风险识别
   - [ ] 商务风险识别
   - [ ] 合规风险识别
   - [ ] 风险评分和建议

5. [ ] 向量化和检索
   - [ ] 文档内容向量化（OpenAI Embeddings）
   - [ ] Elasticsearch 向量存储
   - [ ] 语义搜索功能
   - [ ] 相似文档推荐

6. [ ] API 接口实现
   - [ ] POST /api/ai/parse-document
   - [ ] POST /api/ai/extract-requirements
   - [ ] POST /api/ai/analyze-risks
   - [ ] GET /api/ai/similar-documents

#### 技术实现要点
```python
from fastapi import FastAPI, UploadFile
from llama_index import VectorStoreIndex, ServiceContext
from llama_index.vector_stores import ElasticsearchStore
from llama_index.embeddings import OpenAIEmbedding
import openai

app = FastAPI()

@app.post("/api/ai/parse-document")
async def parse_document(file: UploadFile):
    # 1. 文档解析
    # 2. 内容提取
    # 3. 结构化输出
    pass

@app.post("/api/ai/extract-requirements")
async def extract_requirements(document_id: str):
    # 使用 LlamaIndex + GPT-4 提取需求
    pass
```

#### 相关文档
- AI设计文档: `docs/03-架构/05-AI能力层设计.md`
- API文档: `docs/03-架构/03-API接口设计.md`

---

### AI-002: 智能内容生成引擎
**需求编号**: REQ-AI-002
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

#### 子任务
1. [ ] RAG 系统构建
   - [ ] LlamaIndex 索引构建
   - [ ] 企业知识库向量化
   - [ ] 项目案例向量化
   - [ ] 检索增强生成 Pipeline

2. [ ] 智能生成引擎
   - [ ] 技术方案生成
   - [ ] 项目实施方案生成
   - [ ] 团队介绍生成
   - [ ] 案例引用生成

3. [ ] AI助手矩阵
   - [ ] 技术专家助手
   - [ ] 商务专家助手
   - [ ] 合规专家助手
   - [ ] 质量审查助手

4. [ ] 内容优化
   - [ ] 内容润色
   - [ ] 专业术语检查
   - [ ] 格式统一
   - [ ] 内容去重

5. [ ] API 接口实现
   - [ ] POST /api/ai/generate-content
   - [ ] POST /api/ai/optimize-content
   - [ ] POST /api/ai/expert-review
   - [ ] POST /api/ai/suggest-improvements

---

### AI-003: 企业能力库向量化
**需求编号**: REQ-AI-003
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

#### 子任务
1. [ ] 产品服务向量化
   - [ ] 产品信息向量化
   - [ ] 服务描述向量化
   - [ ] 技术能力向量化

2. [ ] 项目经验向量化
   - [ ] 历史项目案例向量化
   - [ ] 项目成果向量化
   - [ ] 客户评价向量化

3. [ ] 资质证书向量化
   - [ ] 企业资质向量化
   - [ ] 人员证书向量化
   - [ ] 专利技术向量化

4. [ ] 向量检索优化
   - [ ] Elasticsearch 索引优化
   - [ ] 混合检索（向量+关键词）
   - [ ] 检索结果重排序

---

### AI-004: 智能匹配分析
**需求编号**: REQ-AI-004
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/3 子任务)

#### 子任务
1. [ ] 需求匹配分析
   - [ ] 招标需求与企业能力匹配
   - [ ] 匹配度评分算法
   - [ ] 匹配可视化

2. [ ] 竞争优势分析
   - [ ] 优势识别
   - [ ] 差距分析
   - [ ] 改进建议

3. [ ] 智能推荐
   - [ ] 相似项目推荐
   - [ ] 案例推荐
   - [ ] 团队推荐

---

## React 前端任务

### FRONT-001: 基础框架和布局
**需求编号**: REQ-FRONT-001
**负责人**: 前端开发
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

#### 子任务
1. [ ] 项目脚手架搭建
   - [ ] Ant Design Pro 初始化
   - [ ] TypeScript 配置
   - [ ] Umi 路由配置
   - [ ] ESLint + Prettier 配置

2. [ ] 全局布局
   - [ ] ProLayout 配置
   - [ ] 顶部导航栏
   - [ ] 侧边菜单
   - [ ] 面包屑导航
   - [ ] 页脚

3. [ ] 主题配置
   - [ ] Ant Design 主题定制
   - [ ] 深色模式支持
   - [ ] 主题切换功能

4. [ ] API 客户端配置
   - [ ] axios 封装
   - [ ] 请求拦截器（添加 Token）
   - [ ] 响应拦截器（错误处理）
   - [ ] 双服务端点配置（Java + Python）

5. [ ] 全局状态管理
   - [ ] 用户状态管理
   - [ ] 组织和项目上下文
   - [ ] 权限状态管理

#### 技术实现要点
```typescript
// API 客户端配置
const javaClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 30000,
});

const pythonClient = axios.create({
  baseURL: 'http://localhost:8001',
  timeout: 60000,  // AI 接口可能较慢
});
```

---

### FRONT-002: 用户和项目管理界面
**需求编号**: REQ-FRONT-002
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

#### 子任务
1. [ ] 登录注册页面
   - [ ] 登录表单（ProForm）
   - [ ] 注册表单
   - [ ] 忘记密码流程
   - [ ] 第三方登录（预留）

2. [ ] 用户中心
   - [ ] 个人资料展示
   - [ ] 资料编辑表单
   - [ ] 密码修改
   - [ ] 头像上传

3. [ ] 组织管理
   - [ ] 组织列表页面（ProTable）
   - [ ] 组织详情页面
   - [ ] 组织创建/编辑表单
   - [ ] 组织成员管理

4. [ ] 项目管理
   - [ ] 项目看板视图
   - [ ] 项目列表视图
   - [ ] 项目详情页面
   - [ ] 项目创建/编辑表单

---

### FRONT-003: 文档编辑和预览界面
**需求编号**: REQ-FRONT-003
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

#### 子任务
1. [ ] 富文本编辑器集成
   - [ ] 选择编辑器（TinyMCE/Quill/Slate）
   - [ ] 工具栏定制
   - [ ] 图片上传
   - [ ] 表格支持

2. [ ] 文档大纲
   - [ ] 自动生成大纲
   - [ ] 大纲导航
   - [ ] 章节折叠展开

3. [ ] 文档预览
   - [ ] PDF 预览
   - [ ] Word 预览
   - [ ] 打印预览

4. [ ] 版本管理界面
   - [ ] 版本历史列表
   - [ ] 版本比较视图
   - [ ] 版本回退操作

5. [ ] 模板应用
   - [ ] 模板选择器
   - [ ] 模板预览
   - [ ] 应用模板到文档

---

### FRONT-004: AI助手交互界面
**需求编号**: REQ-FRONT-004
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

#### 子任务
1. [ ] 招标文件上传和解析
   - [ ] 文件上传组件（ProUpload）
   - [ ] 解析进度展示
   - [ ] 解析结果展示
   - [ ] 关键信息提取结果

2. [ ] AI内容生成界面
   - [ ] 生成配置面板
   - [ ] 生成进度展示
   - [ ] 生成结果展示
   - [ ] 结果编辑和接受

3. [ ] AI助手对话
   - [ ] 聊天界面
   - [ ] 消息列表
   - [ ] 输入框和发送
   - [ ] 打字动画效果

4. [ ] 智能推荐展示
   - [ ] 相似项目卡片
   - [ ] 案例推荐列表
   - [ ] 模板推荐
   - [ ] 一键应用推荐

---

## 进度跟踪

### 第1月计划（2025-12）
**本月目标**:
- ✅ 完成架构设计文档（已完成）
- [ ] 完成开发环境搭建（INFRA-001）
- [ ] 启动用户认证模块（JAVA-001）
- [ ] 启动招标解析模块（AI-001）
- [ ] 启动前端框架搭建（FRONT-001）

**已完成**:
- [x] 架构设计总览文档
- [x] 数据库设计文档
- [x] API接口设计文档
- [x] AI能力层设计文档
- [x] 部署架构设计文档

**进行中**:
- [ ] 无

**遇到的问题**:
- 待记录

---

### 第2月计划（2026-01）
**本月目标**:
- [ ] 完成用户认证模块（JAVA-001）
- [ ] 完成组织项目管理（JAVA-002）
- [ ] 完成招标解析模块（AI-001）
- [ ] 完成基础前端界面（FRONT-001, FRONT-002）

---

### 第3月计划（2026-02）
**本月目标**:
- [ ] 完成模板管理（JAVA-003）
- [ ] 完成文档管理（JAVA-004）
- [ ] 完成智能生成引擎（AI-002）
- [ ] 完成文档编辑界面（FRONT-003）
- [ ] 完成AI助手界面（FRONT-004）
- [ ] MVP 整体联调和测试

---

## 里程碑

### M1: 基础设施就绪（2025-12-15）
- [ ] INFRA-001: 开发环境搭建完成
- [ ] 所有服务能够成功启动
- [ ] 服务间通信测试通过

### M2: 用户和认证功能完成（2026-01-15）
- [ ] JAVA-001: 用户认证授权模块完成
- [ ] JAVA-002: 组织项目管理完成
- [ ] FRONT-001: 基础框架完成
- [ ] FRONT-002: 用户管理界面完成

### M3: 核心AI功能完成（2026-02-15）
- [ ] AI-001: 招标文件解析完成
- [ ] AI-002: 智能内容生成完成
- [ ] FRONT-004: AI助手界面完成

### M4: MVP 发布（2026-02-28）
- [ ] 所有 P1 任务完成
- [ ] 端到端功能测试通过
- [ ] 性能测试通过
- [ ] 安全测试通过
- [ ] MVP 部署到测试环境

---

## 统计数据

### 总体进度
- **总任务数**: 15
- **已完成**: 1 (6.7%)
- **进行中**: 0 (0%)
- **待开始**: 14 (93.3%)
- **已阻塞**: 0 (0%)

### 按技术栈统计
- **基础设施**: 0/1 (INFRA-001)
- **Java 模块**: 0/4 (JAVA-001 ~ JAVA-004)
- **Python AI 模块**: 0/4 (AI-001 ~ AI-004)
- **前端模块**: 0/4 (FRONT-001 ~ FRONT-004)
- **其他任务**: 0/2

### 按优先级统计
- **P0**: 1/1 (100%)
- **P1**: 0/6 (0%)
- **P2**: 0/6 (0%)
- **P3**: 0/6 (0%)

---

## 风险和问题

### 当前风险

#### 技术风险
1. **AI模型稳定性**
   - 描述: GPT-4 API 可能出现限流或服务不稳定
   - 影响: 高
   - 缓解措施: 实现重试机制，准备 Claude 作为备用模型

2. **向量检索性能**
   - 描述: Elasticsearch 向量检索性能可能不够
   - 影响: 中
   - 缓解措施: 优化索引配置，必要时使用专用向量数据库

#### 进度风险
1. **团队规模**
   - 描述: MVP 开发任务量较大，3个月时间紧张
   - 影响: 高
   - 缓解措施: 优先完成核心流程，非核心功能延后到 v2.0

---

## 使用说明

### 任务状态标记
- ⏸️ **待开始**: 任务已计划但未开始
- 🔄 **进行中**: 任务正在开发
- ✅ **已完成**: 任务已完成并通过验证
- ⚠️ **已阻塞**: 任务被阻塞，无法继续

### 更新频率
- **每日**: 更新进行中任务的进度
- **每周**: 回顾本周完成情况，规划下周任务
- **每月**: 回顾迭代进展，调整计划和优先级

### 与 TodoWrite 的配合
1. **选择任务**: 从此文档选择当前要做的任务
2. **创建会话任务**: 使用 `TodoWrite` 创建会话级任务清单，将任务拆分为更细粒度的子任务
3. **执行开发**: 按照 TodoWrite 任务清单逐步完成
4. **更新进度**: 完成后更新此文档的进度百分比和状态
5. **提交代码**: Git 提交代码和文档更新

### 示例工作流
```bash
# 1. 从 task-plan.md 选择任务
# 选择: JAVA-001 用户认证授权模块 - 子任务1: 用户管理基础功能

# 2. 在 Claude Code 中使用 TodoWrite 创建会话任务
# - 设计 User 实体类
# - 创建 UserRepository 接口
# - 实现 UserService 业务逻辑
# - 创建 UserController REST API
# - 编写单元测试

# 3. 完成开发并通过测试

# 4. 更新 task-plan.md
# - 标记子任务1为已完成 [x]
# - 更新完成进度: 16.7% (1/6 子任务)

# 5. 提交代码
git add .
git commit -m "feat: 实现用户管理基础功能"
```

---

## 附录

### 关键技术文档索引

#### 架构文档
- [架构设计总览](../03-架构/00-架构设计总览.md)
- [项目结构设计](../03-架构/01-项目结构设计.md)
- [数据库设计](../03-架构/02-数据库设计.md)
- [API接口设计](../03-架构/03-API接口设计.md)
- [AI能力层设计](../03-架构/05-AI能力层设计.md)
- [部署架构设计](../03-架构/06-部署架构设计.md)

#### 指引文档
- [CLAUDE.md](../../CLAUDE.md)
- [README](../01-指引/README.md)

#### 需求文档
- [产品愿景总览](../02-需求/01-产品愿景总览.md)
- [功能模块详解](../02-需求/03-功能模块详解.md)

### 常用命令速查

#### 开发环境启动
```bash
# 启动所有服务
docker-compose up -d

# 启动 Java 服务
cd backend/spring-boot-service
mvn spring-boot:run

# 启动 Python AI 服务
cd backend/fastapi-ai-service
uvicorn main:app --reload --port 8001

# 启动前端
cd frontend/react-app
npm run dev
```

#### 数据库迁移
```bash
# Java 服务（Flyway 自动执行）
mvn flyway:migrate

# 手动执行特定版本
mvn flyway:migrate -Dflyway.target=2
```

#### 测试
```bash
# Java 单元测试
mvn test

# Python 测试
pytest

# 前端测试
npm test
```
