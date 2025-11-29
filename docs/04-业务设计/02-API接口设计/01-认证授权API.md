# 认证授权API

**文档类型**: 设计文档 - API接口设计
**模块编号**: 1
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

## 🔑 认证授权API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 用户注册

```yaml
POST /api/v1/auth/register
Content-Type: application/json

Request Body:
  email: string (required, email format)
  username: string (required, 3-50 chars)
  password: string (required, min 8 chars)
  full_name: string (optional)
  organization_name: string (optional)

Response: 201 Created
  {
    "success": true,
    "data": {
      "user_id": "uuid",
      "email": "string",
      "username": "string",
      "full_name": "string",
      "organization_id": "uuid",
      "created_at": "datetime"
    },
    "message": "注册成功"
  }

Errors:
  - 400: EMAIL_ALREADY_EXISTS - 邮箱已存在
  - 400: USERNAME_ALREADY_EXISTS - 用户名已存在
  - 400: INVALID_PASSWORD - 密码强度不足
```

### 2. 用户登录

```yaml
POST /api/v1/auth/login
Content-Type: application/json

Request Body:
  email: string (required)
  password: string (required)

Response: 200 OK
  {
    "success": true,
    "data": {
      "access_token": "jwt_token",
      "refresh_token": "jwt_token",
      "token_type": "Bearer",
      "expires_in": 3600,
      "user": {
        "id": "uuid",
        "email": "string",
        "username": "string",
        "full_name": "string",
        "organization_id": "uuid",
        "roles": ["string"]
      }
    },
    "message": "登录成功"
  }

Errors:
  - 401: INVALID_CREDENTIALS - 用户名或密码错误
  - 403: ACCOUNT_SUSPENDED - 账号已被暂停
  - 429: TOO_MANY_ATTEMPTS - 登录尝试次数过多
```

### 3. 刷新Token

```yaml
POST /api/v1/auth/refresh-token
Content-Type: application/json
Authorization: Bearer {refresh_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "access_token": "new_jwt_token",
      "expires_in": 3600
    }
  }

Errors:
  - 401: INVALID_REFRESH_TOKEN - 刷新Token无效
  - 401: REFRESH_TOKEN_EXPIRED - 刷新Token已过期
```

### 4. 用户登出

```yaml
POST /api/v1/auth/logout
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "message": "登出成功"
  }
```

### 5. 获取当前用户信息

```yaml
GET /api/v1/auth/me
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "email": "string",
      "username": "string",
      "full_name": "string",
      "avatar_url": "string",
      "organization": {
        "id": "uuid",
        "name": "string"
      },
      "roles": ["string"],
      "permissions": ["string"],
      "settings": {}
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
