# OpenAPI规范文件

**目录**: docs/04-设计/API接口设计/openapi/
**OpenAPI版本**: 3.0.3
**最后更新**: 2025-11-28

---

## 📋 文件说明

### 主文件
- **openapi.yaml** - 完整的API规范文档（引用各模块）

### 模块文件
- **auth.yaml** - 认证授权API ✅ 已完成
- **projects.yaml** - 项目管理API ⏸️ 待生成
- **documents.yaml** - 文档管理API ⏸️ 待生成
- **ai-services.yaml** - AI服务API ⏸️ 待生成
- **templates.yaml** - 模板管理API ⏸️ 待生成
- **capabilities.yaml** - 企业能力API ⏸️ 待生成
- **collaboration.yaml** - 协作API ⏸️ 待生成
- **export.yaml** - 导出API ⏸️ 待生成

---

## 🔧 使用方法

### 1. 在线预览

使用Swagger Editor在线预览：
```bash
# 访问 https://editor.swagger.io/
# 将 openapi.yaml 内容粘贴到编辑器中
```

### 2. 本地预览

使用Swagger UI本地预览：
```bash
# 安装依赖
npm install -g swagger-ui-watcher

# 启动预览服务
swagger-ui-watcher openapi.yaml
```

### 3. 生成API文档

使用Redoc生成静态文档：
```bash
# 安装依赖
npm install -g redoc-cli

# 生成HTML文档
redoc-cli bundle openapi.yaml -o api-docs.html
```

### 4. 生成客户端代码

使用OpenAPI Generator生成客户端代码：
```bash
# 安装 OpenAPI Generator
npm install -g @openapitools/openapi-generator-cli

# 生成TypeScript客户端
openapi-generator-cli generate -i openapi.yaml -g typescript-axios -o ./client

# 生成Java客户端
openapi-generator-cli generate -i openapi.yaml -g java -o ./java-client

# 生成Python客户端
openapi-generator-cli generate -i openapi.yaml -g python -o ./python-client
```

---

## 📝 规范说明

### 响应格式

所有API遵循统一的响应格式：

**成功响应**:
```json
{
  "success": true,
  "data": {...},
  "message": "操作成功",
  "timestamp": "2025-11-28T07:30:00Z"
}
```

**错误响应**:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "错误描述",
    "details": {...}
  },
  "timestamp": "2025-11-28T07:30:00Z"
}
```

**分页响应**:
```json
{
  "success": true,
  "data": {
    "items": [...],
    "total": 100,
    "page": 1,
    "pageSize": 20,
    "totalPages": 5
  },
  "timestamp": "2025-11-28T07:30:00Z"
}
```

### 认证方式

使用JWT Bearer Token：
```
Authorization: Bearer {access_token}
```

### 服务器地址

- 生产环境: `https://api.aibidcomposer.com/api/v1`
- 预发环境: `https://staging.aibidcomposer.com/api/v1`
- Java开发: `http://localhost:8080/api/v1`
- Python开发: `http://localhost:8001/api/v1`

---

## 🔄 生成进度

| 模块 | 状态 | 完成时间 | 备注 |
|------|------|----------|------|
| 主文件（openapi.yaml） | ✅ 完成 | 2025-11-28 | 引用各模块定义 |
| 认证授权（auth.yaml） | ✅ 完成 | 2025-11-28 | 包含5个端点 |
| 项目管理（projects.yaml） | ⏸️ 待生成 | - | 基于01-项目管理API.md |
| 文档管理（documents.yaml） | ⏸️ 待生成 | - | 基于02-文档管理API.md |
| AI服务（ai-services.yaml） | ⏸️ 待生成 | - | 基于03-AI服务API.md |
| 模板管理（templates.yaml） | ⏸️ 待生成 | - | 基于04-模板管理API.md |
| 企业能力（capabilities.yaml） | ⏸️ 待生成 | - | 基于05-企业能力API.md |
| 协作（collaboration.yaml） | ⏸️ 待生成 | - | 基于06-协作API.md |
| 导出（export.yaml） | ⏸️ 待生成 | - | 基于07-导出API.md |

---

## 📚 相关文档

- [API接口设计总览](../00-API接口设计总览.md)
- [API接口设计索引](../INDEX.md)
- [各模块API文档](../)

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-28 07:30 | 1.0 | claude-sonnet-4-5 | 创建OpenAPI规范文件和README |

---

**说明**:
- 当前已完成主文件和认证授权API的OpenAPI规范
- 其余7个模块的OpenAPI文件将基于对应的API文档逐步生成
- 可以使用上述工具预览和生成客户端代码
