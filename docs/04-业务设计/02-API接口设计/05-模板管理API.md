# 模板管理API

**文档类型**: 设计文档 - API接口设计
**模块编号**: 5
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中
**服务**: Java Spring Boot (端口 8080)

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从03-API接口设计.md拆分独立模块文档 |

---

## 📋 模板管理API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 获取模板列表

```yaml
GET /api/v1/templates
Authorization: Bearer {access_token}

Query Parameters:
  page: integer
  pageSize: integer
  category: string
  industry: string
  scope: string (public|organization|private)
  search: string

Response: 200 OK (Paginated)
```

### 2. 获取模板详情

```yaml
GET /api/v1/templates/{template_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "string",
      "description": "string",
      "category": "string",
      "industry": "string",
      "content": {...},
      "structure": [...],
      "variables": [...],
      "usage_count": number,
      "rating": number
    }
  }
```

### 3. 创建模板

```yaml
POST /api/v1/templates
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  name: string (required)
  description: string
  category: string
  template_type: string
  content: object (required)
  structure: array
  variables: array

Response: 201 Created
```

---

## 相关API

请参考 [API接口设计总览](./INDEX.md) 查看所有相关API模块。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
