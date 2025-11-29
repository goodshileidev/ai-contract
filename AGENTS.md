# Repository Guidelines

## 项目结构与模块组织
仓库采用“前端 React + Ant Design Pro、Java Spring Boot 数据服务、Python FastAPI AI 能力”三层架构，分别位于 `frontend/react-app`、`backend/spring-boot-service` 与 `backend/fastapi-ai-service`。所有文档集中在 `docs/` 八大子目录（01-原则指引至99-知识积累），临时分析需按 `{描述}_YYYY-MM-DD-HHMM.md` 命名。向量检索、消息队列与缓存组件的配置位于 `deploy/` 及 `docker/`。

## 构建、测试与开发命令
- `mvn clean install` / `mvn test`：构建与验证 Java 侧 CRUD、审批、模板等能力。
- `cd backend/fastapi-ai-service && uvicorn main:app --reload --port 8001` 启动 AI 服务，`pytest` 执行 RAG 与多 Agent 单元测试。
- `cd frontend/react-app && npm run dev` 体验管理后台，`npm test` 运行前端用例，`npm run build` 生成发布产物。
- Docker 统一通过 `docker-compose up -d` 启动，日志使用 `docker-compose logs -f`。

## 编码风格与命名约定
Java 统一使用 Java 17、Spring Boot 3.2，控制层/服务层/仓储层分离，类名大驼峰、方法小驼峰、常量全大写。Python 依赖 FastAPI + LlamaIndex，模块 <300 行，强调类型注解与 Pydantic 模型。前端使用 TypeScript 5.x、函数式组件、Zustand 管理状态，所有展示文案走 i18n 资源，CSS 按 Ant Design 设计令完成。

## 测试指南
单元与契约测试是合并前的必备门槛：Java 以 JUnit + Spring Test 为主，Python 使用 Pytest + 自定义 RAG fixtures，前端沿用 Vitest + Testing Library。端到端验证由 Playwright 覆盖 AI 工作流关键路径，覆盖率门槛≥80%，所有新功能需先写失败用例再实现。测试命名遵循 `should_<行为>_when_<条件>`（Java/Python）与 `renders <行为> when <条件>`（前端）。

## 提交与合并规范
Git 信息遵循 `<type>(<scope>): <subject>`，正文注明需求编号、受影响模块与验证结果，如 `feat(招投标流程): 支持模板导入

需求编号: REQ-2025-11-003
验证结果: mvn test、pytest、npm test 全部通过`。Pull Request 必须附带变更摘要、截图（界面修改时）、日志或 SQL 迁移说明，并链接任务/需求。所有代码在提交前执行 Edit→Read→测试三步自检，确保 README 与 CLAUDE.md 同步更新。

## 安全与配置提示
密钥通过 `.env` 或 CI Secret 注入，Java 读取 `SPRING_CONFIG_LOCATION`，Python 走 `pydantic-settings`，前端在 `.env.local` 中设置 `VITE_API_BASE_URL`。生产模式需同时启用 PostgreSQL、Redis、Elasticsearch 与 RabbitMQ；若 AI 服务不可用，Java 不得直接调用 LLM，而是返回友好降级信息。
