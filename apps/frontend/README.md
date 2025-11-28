# AI Frontend - React + Ant Design Pro

**AIBidComposer 前端应用** - 基于React 18 + TypeScript + Ant Design Pro的企业级前端应用。

## 📋 应用概述

本应用是AIBidComposer项目的**用户界面层**，提供：

- 🎨 **现代化UI** - 基于Ant Design Pro 6.x的企业级设计
- ⚡ **高性能** - Vite构建，快速开发体验
- 🔐 **安全认证** - JWT认证，权限控制
- 📱 **响应式设计** - 适配桌面端和移动端
- 🤖 **AI交互** - 智能助手，内容生成
- 📊 **数据可视化** - 项目看板，统计图表

**开发端口**: 5173
**技术栈**: React 18 + TypeScript 5 + Ant Design Pro 6 + Vite 5

## 🏗️ 架构说明

### 与后端服务的关系

```
┌─────────────────────────────────────────────────┐
│           Frontend (React + Ant Design Pro)     │
│                  Port: 5173                     │
└──────────┬────────────────────┬─────────────────┘
           │                    │
           ▼                    ▼
┌──────────────────┐   ┌──────────────────┐
│  Java Service    │   │  Python Service  │
│  (Port 8080)     │   │  (Port 8001)     │
│                  │   │                  │
│  - 用户认证      │   │  - AI文档解析    │
│  - 项目管理      │   │  - 内容生成      │
│  - 文档CRUD      │   │  - 智能匹配      │
│  - 模板管理      │   │  - 向量检索      │
└──────────────────┘   └──────────────────┘
```

**服务通信方式**:
- HTTP REST API - 同步请求
- WebSocket - 实时协作

## 📦 项目结构

```
frontend/
├── public/                  # 静态资源
├── src/
│   ├── api/                 # API调用（预留）
│   ├── components/          # 可复用组件
│   ├── layouts/             # 布局组件
│   │   ├── AuthLayout.tsx   # 认证布局
│   │   └── MainLayout.tsx   # 主应用布局
│   ├── pages/               # 页面组件
│   │   ├── auth/            # 认证页面
│   │   │   └── Login.tsx    # 登录页面
│   │   ├── projects/        # 项目管理页面
│   │   │   └── ProjectList.tsx
│   │   ├── Dashboard.tsx    # 工作台
│   │   └── NotFound.tsx     # 404页面
│   ├── services/            # 业务逻辑服务
│   │   ├── api.ts           # API客户端配置
│   │   └── auth.service.ts  # 认证服务
│   ├── stores/              # 状态管理
│   │   └── auth.ts          # 认证状态
│   ├── types/               # TypeScript类型定义
│   ├── utils/               # 工具函数
│   ├── App.tsx              # 应用根组件
│   ├── main.tsx             # 应用入口
│   └── index.css            # 全局样式
├── .env.example             # 环境变量示例
├── .env.development         # 开发环境变量
├── .eslintrc.cjs            # ESLint配置
├── .prettierrc              # Prettier配置
├── .gitignore
├── index.html               # HTML模板
├── vite.config.ts           # Vite配置
├── tsconfig.json            # TypeScript配置
├── package.json
├── Dockerfile               # 生产环境镜像
├── nginx.conf               # Nginx配置
└── README.md                # 本文档
```

## 🚀 快速开始

### 前置条件

- Node.js 18+
- npm 9+ 或 pnpm 8+

### 本地开发环境

1. **安装依赖**

```bash
cd apps/frontend

# 使用npm
npm install

# 或使用pnpm (推荐)
pnpm install
```

2. **配置环境变量**

```bash
# 复制环境变量示例文件
cp .env.example .env.local

# 编辑.env.local文件，填写必要的配置
vim .env.local
```

**必须配置的环境变量**:
- `VITE_JAVA_API_BASE_URL` - Java后端API地址
- `VITE_AI_API_BASE_URL` - Python AI API地址
- `VITE_WS_BASE_URL` - WebSocket地址

3. **启动开发服务器**

```bash
# 启动开发服务器（支持热重载）
npm run dev

# 或
pnpm dev
```

4. **访问应用**

打开浏览器访问: http://localhost:5173

### 生产环境构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### Docker部署

```bash
# 构建镜像
docker build -t aibidcomposer/frontend:latest .

# 运行容器
docker run -d \
  -p 80:80 \
  --name frontend \
  aibidcomposer/frontend:latest
```

## 🔧 核心功能

### 1. 用户认证

**页面**: `/login`

功能:
- 用户登录
- 记住我
- 忘记密码
- 用户注册（预留）

### 2. 工作台

**页面**: `/dashboard`

功能:
- 项目统计概览
- 最近项目
- 快捷操作

### 3. 项目管理

**页面**: `/projects`

功能:
- 项目列表展示（ProTable）
- 项目搜索和筛选
- 项目创建/编辑/删除
- 项目详情查看

### 4. 文档管理（待实现）

**页面**: `/documents`

功能:
- 标书文档列表
- 文档创建和编辑
- 文档版本管理
- 文档导出（PDF/Word）

### 5. AI助手（待实现）

**功能**:
- 招标文件智能解析
- AI内容生成
- 智能推荐
- 质量审核

## 📚 技术栈详解

### 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| React | 18.2+ | UI框架 |
| TypeScript | 5.3+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Ant Design | 5.12+ | UI组件库 |
| Ant Design Pro | 6.x | 企业级组件 |
| ProComponents | 2.8+ | Pro增强组件 |
| React Router | 6.20+ | 路由管理 |
| TanStack Query | 4.36+ | 服务端状态管理 |
| Zustand | 4.4+ | 客户端状态管理 |
| Axios | 1.6+ | HTTP客户端 |

### 开发依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| ESLint | 8.55+ | 代码检查 |
| Prettier | 3.1+ | 代码格式化 |
| Vitest | 1.0+ | 单元测试 |
| @testing-library/react | 14.1+ | React测试 |

### 状态管理策略

```typescript
// 服务端状态 - TanStack Query
import { useQuery, useMutation } from '@tanstack/react-query';

// 获取数据
const { data, isLoading } = useQuery({
  queryKey: ['projects'],
  queryFn: () => fetchProjects(),
});

// 提交数据
const mutation = useMutation({
  mutationFn: createProject,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['projects'] });
  },
});

// 客户端状态 - Zustand
import { create } from 'zustand';

const useAuthStore = create((set) => ({
  user: null,
  token: null,
  login: (user, token) => set({ user, token }),
  logout: () => set({ user: null, token: null }),
}));
```

## 🧪 测试

```bash
# 运行所有测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 运行测试UI
npm run test:ui

# 监听模式
npm run test:watch
```

## 📊 代码规范

### 代码检查

```bash
# 检查代码规范
npm run lint

# 自动修复
npm run lint:fix
```

### 代码格式化

```bash
# 格式化代码
npm run format

# 检查格式
npm run format:check
```

### 类型检查

```bash
# TypeScript类型检查
npm run type-check
```

## 🔐 环境变量

### 开发环境

```bash
# .env.development
VITE_JAVA_API_BASE_URL=http://localhost:8080
VITE_AI_API_BASE_URL=http://localhost:8001
VITE_WS_BASE_URL=ws://localhost:8080
VITE_DEBUG=true
```

### 生产环境

```bash
# .env.production
VITE_JAVA_API_BASE_URL=https://api.example.com
VITE_AI_API_BASE_URL=https://ai.example.com
VITE_WS_BASE_URL=wss://api.example.com
VITE_DEBUG=false
```

## 🛠️ 开发指南

### 添加新页面

1. 在 `src/pages/` 创建页面组件
2. 在 `src/App.tsx` 添加路由
3. 在 `src/layouts/MainLayout.tsx` 添加菜单项（如需要）

### 添加新的API服务

1. 在 `src/services/` 创建服务文件
2. 使用 `javaRequest` 或 `aiRequest` 封装API调用
3. 在组件中使用 `useQuery` 或 `useMutation`

### 状态管理

**服务端状态** (推荐使用 TanStack Query):
- 数据获取
- 缓存管理
- 自动重试
- 乐观更新

**客户端状态** (推荐使用 Zustand):
- 用户认证状态
- 主题设置
- UI状态

## 🤝 相关文档

- [项目总览](../../README.md)
- [Java服务文档](../backend-java/README.md)
- [Python AI服务文档](../backend-python/README.md)
- [架构设计](../../docs/03-架构/00-架构设计总览.md)
- [API接口设计](../../docs/03-架构/03-API接口设计.md)

## 📄 许可证

MIT

---

**需求编号**: REQ-FRONT-001
**创建时间**: 2025-11-26
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**维护团队**: AIBidComposer Team
