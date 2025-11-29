---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 📁 项目管理API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 获取项目列表

```yaml
GET /api/v1/projects
Authorization: Bearer {access_token}

Query Parameters:
  page: integer (default: 1)
  pageSize: integer (default: 20, max: 100)
  status: string (draft|in_progress|review|submitted|won|lost|archived)
  priority: string (low|medium|high|urgent)
  search: string (搜索项目名称/编号)
  sortBy: string (created_at|updated_at|submission_deadline)
  sortOrder: string (asc|desc, default: desc)

Response: 200 OK
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": "uuid",
          "name": "string",
          "code": "string",
          "description": "string",
          "status": "string",
          "priority": "string",
          "budget_amount": number,
          "submission_deadline": "datetime",
          "win_probability": number,
          "created_by": {
            "id": "uuid",
            "name": "string"
          },
          "created_at": "datetime",
          "updated_at": "datetime"
        }
      ],
      "total": 100,
      "page": 1,
      "pageSize": 20,
      "totalPages": 5
    }
  }
```

### 2. 创建项目

```yaml
POST /api/v1/projects
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  name: string (required)
  code: string (optional, auto-generated if not provided)
  description: string (optional)
  bidding_type: string (government|enterprise|international|other)
  industry: string (optional)
  budget_amount: number (optional)
  start_date: date (optional)
  end_date: date (optional)
  submission_deadline: datetime (optional)
  priority: string (low|medium|high|urgent, default: medium)
  tags: string[] (optional)

Response: 201 Created
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "string",
      "code": "string",
      "status": "draft",
      "created_at": "datetime"
    },
    "message": "项目创建成功"
  }

Errors:
  - 400: INVALID_INPUT - 输入数据无效
  - 409: PROJECT_CODE_EXISTS - 项目编号已存在
```

### 3. 获取项目详情

```yaml
GET /api/v1/projects/{project_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "string",
      "code": "string",
      "description": "string",
      "organization_id": "uuid",
      "bidding_type": "string",
      "industry": "string",
      "budget_amount": number,
      "currency": "string",
      "start_date": "date",
      "end_date": "date",
      "submission_deadline": "datetime",
      "status": "string",
      "priority": "string",
      "win_probability": number,
      "tags": ["string"],
      "members": [
        {
          "user_id": "uuid",
          "username": "string",
          "full_name": "string",
          "role": "string"
        }
      ],
      "documents_count": number,
      "requirements_count": number,
      "created_by": {...},
      "created_at": "datetime",
      "updated_at": "datetime"
    }
  }

Errors:
  - 404: PROJECT_NOT_FOUND - 项目不存在
  - 403: ACCESS_DENIED - 无权访问
```

### 4. 更新项目

```yaml
PUT /api/v1/projects/{project_id}
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body: (所有字段可选)
  name: string
  description: string
  status: string
  priority: string
  budget_amount: number
  submission_deadline: datetime
  win_probability: number
  tags: string[]

Response: 200 OK
  {
    "success": true,
    "data": {...},
    "message": "项目更新成功"
  }

Errors:
  - 404: PROJECT_NOT_FOUND
  - 403: PERMISSION_DENIED
  - 400: INVALID_STATUS_TRANSITION
```

### 5. 删除项目

```yaml
DELETE /api/v1/projects/{project_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "message": "项目删除成功"
  }

Errors:
  - 404: PROJECT_NOT_FOUND
  - 403: PERMISSION_DENIED
  - 409: PROJECT_HAS_ACTIVE_DOCUMENTS - 项目有活跃文档，不能删除
```

### 6. 添加项目成员

```yaml
POST /api/v1/projects/{project_id}/members
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  user_id: string (required)
  role: string (owner|manager|member|viewer, default: member)
  permissions: string[] (optional)

Response: 201 Created
  {
    "success": true,
    "data": {
      "id": "uuid",
      "user": {...},
      "role": "string",
      "joined_at": "datetime"
    },
    "message": "成员添加成功"
  }

Errors:
  - 404: USER_NOT_FOUND
  - 409: MEMBER_ALREADY_EXISTS
  - 403: PERMISSION_DENIED
```
