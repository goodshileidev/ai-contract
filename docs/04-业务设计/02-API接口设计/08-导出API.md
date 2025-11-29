# 导出API

**文档类型**: 设计文档 - API接口设计
**模块编号**: 8
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

## 📤 导出API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 导出为PDF

```yaml
POST /api/v1/export/pdf
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  options: {
    page_size: string (A4|A3|Letter, default: A4)
    orientation: string (portrait|landscape, default: portrait)
    include_toc: boolean (default: true)
    watermark: string (optional)
  }

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing"
    }
  }
```

### 2. 导出为Word

```yaml
POST /api/v1/export/word
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  options: {
    include_images: boolean (default: true)
    include_comments: boolean (default: false)
  }

Response: 202 Accepted
```

### 3. 获取导出任务状态

```yaml
GET /api/v1/export/tasks/{task_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "success",
      "download_url": "string",
      "file_size": number,
      "expires_at": "datetime"
    }
  }
```

---

## 相关API

请参考 [API接口设计总览](./INDEX.md) 查看所有相关API模块。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
