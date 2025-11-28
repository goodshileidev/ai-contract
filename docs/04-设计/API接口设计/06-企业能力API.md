# 企业能力API

**文档类型**: 设计文档 - API接口设计
**模块编号**: 6
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

## 🏢 企业能力API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 获取产品服务列表

```yaml
GET /api/v1/capabilities/products
Authorization: Bearer {access_token}

Query Parameters:
  page: integer
  pageSize: integer
  category: string
  type: string (product|service|solution)
  search: string

Response: 200 OK (Paginated)
```

### 2. 添加产品服务

```yaml
POST /api/v1/capabilities/products
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  name: string (required)
  category: string
  type: string (product|service|solution)
  description: string
  features: string[]
  specifications: object
  advantages: string[]

Response: 201 Created
```

### 3. 获取项目案例列表

```yaml
GET /api/v1/capabilities/cases
Authorization: Bearer {access_token}

Query Parameters:
  page: integer
  pageSize: integer
  client_industry: string
  project_category: string
  search: string

Response: 200 OK (Paginated)
```

### 4. 添加项目案例

```yaml
POST /api/v1/capabilities/cases
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  project_name: string (required)
  client_name: string
  client_industry: string
  project_description: string
  achievements: string[]
  technologies_used: string[]

Response: 201 Created
```

---

## 相关API

请参考 [API接口设计总览](./INDEX.md) 查看所有相关API模块。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
