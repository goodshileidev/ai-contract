---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 📄 文档管理API

> **服务**: Java Spring Boot (业务逻辑) + Python FastAPI (文件解析)
> **端口**: 8080 (Java), 8001 (Python)
> **说明**: 文档业务管理由Java服务提供，复杂的文档解析由Python服务提供

### 1. 解析招标文件

```yaml
POST /api/v1/documents/parse
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

Request Body:
  file: binary (required, max 50MB)
  project_id: string (required)

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing",
      "estimated_time": 30
    },
    "message": "文档解析任务已提交"
  }

Errors:
  - 400: FILE_TOO_LARGE - 文件过大
  - 400: UNSUPPORTED_FILE_TYPE - 不支持的文件类型
  - 404: PROJECT_NOT_FOUND
```

### 2. 获取解析结果

```yaml
GET /api/v1/documents/parse/{task_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "success",
      "result": {
        "document_id": "uuid",
        "file_name": "string",
        "parsed_content": {
          "project_info": {...},
          "requirements": [...],
          "evaluation_criteria": [...],
          "submission_info": {...}
        },
        "requirements_count": number,
        "parsed_at": "datetime"
      }
    }
  }

Status Values:
  - pending: 等待处理
  - processing: 处理中
  - success: 处理成功
  - failed: 处理失败

Errors:
  - 404: TASK_NOT_FOUND
```

### 3. 创建标书文档

```yaml
POST /api/v1/documents
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  project_id: string (required)
  template_id: string (optional)
  title: string (required)
  document_type: string (main|technical|commercial|qualification|other)

Response: 201 Created
  {
    "success": true,
    "data": {
      "id": "uuid",
      "project_id": "uuid",
      "title": "string",
      "version": "1.0",
      "status": "draft",
      "created_at": "datetime"
    },
    "message": "文档创建成功"
  }
```

### 4. 获取文档详情

```yaml
GET /api/v1/documents/{document_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "project_id": "uuid",
      "title": "string",
      "version": "string",
      "status": "string",
      "content": {
        "sections": [...]
      },
      "toc": [...],
      "word_count": number,
      "page_count": number,
      "last_edited_by": {...},
      "last_edited_at": "datetime",
      "locked_by": null,
      "created_at": "datetime",
      "updated_at": "datetime"
    }
  }

Errors:
  - 404: DOCUMENT_NOT_FOUND
  - 403: ACCESS_DENIED
```

### 5. 更新文档内容

```yaml
PUT /api/v1/documents/{document_id}
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  title: string (optional)
  content: object (optional)
  status: string (optional)

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "version": "string",
      "updated_at": "datetime"
    },
    "message": "文档更新成功"
  }

Errors:
  - 404: DOCUMENT_NOT_FOUND
  - 409: DOCUMENT_LOCKED - 文档被其他用户锁定
  - 403: PERMISSION_DENIED
```

### 6. 创建文档版本

```yaml
POST /api/v1/documents/{document_id}/versions
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  change_summary: string (required)
  change_type: string (major|minor|patch|draft, default: minor)

Response: 201 Created
  {
    "success": true,
    "data": {
      "id": "uuid",
      "version": "string",
      "version_number": number,
      "created_at": "datetime"
    },
    "message": "版本创建成功"
  }
```

### 7. 获取版本列表

```yaml
GET /api/v1/documents/{document_id}/versions
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": "uuid",
          "version": "string",
          "version_number": number,
          "change_summary": "string",
          "created_by": {...},
          "created_at": "datetime"
        }
      ]
    }
  }
```
