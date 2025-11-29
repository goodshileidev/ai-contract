---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 👥 协作API

> **服务**: Java Spring Boot
> **端口**: 8080
> **协议**: WebSocket

### 1. WebSocket连接

```yaml
WebSocket /api/v1/collaboration/ws
Authorization: Bearer {access_token}

Connection URL:
  ws://api.example.com/api/v1/collaboration/ws?token={access_token}&document_id={document_id}

Message Types:
  - join: 加入协作会话
  - leave: 离开协作会话
  - edit: 编辑操作
  - cursor: 光标位置
  - selection: 选中内容
  - comment: 评论
  - awareness: 用户状态

Message Format:
  {
    "type": "string",
    "data": object,
    "timestamp": "datetime"
  }
```

### 2. 创建协作会话

```yaml
POST /api/v1/collaboration/sessions
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)

Response: 201 Created
  {
    "success": true,
    "data": {
      "session_id": "uuid",
      "session_key": "string",
      "ws_url": "string",
      "created_at": "datetime"
    }
  }
```
