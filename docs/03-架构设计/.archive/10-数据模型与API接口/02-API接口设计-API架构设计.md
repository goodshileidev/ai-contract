# AI标书智能创作平台 - 数据模型与API接口 - 🔌 API接口设计 - API架构设计

```typescript
// API基础配置
interface APIConfig {
  baseURL: string;               // 基础URL
  version: string;               // API版本
  timeout: number;               // 超时时间
  retryAttempts: number;         // 重试次数
  retryDelay: number;            // 重试延迟
}

// 通用响应格式
interface APIResponse<T> {
  success: boolean;              // 是否成功
  data?: T;                      // 响应数据
  message?: string;              // 消息
  errors?: APIError[];           // 错误列表
  metadata?: ResponseMetadata;   // 元数据
}

// 响应元数据
interface ResponseMetadata {
  timestamp: string;             // 时间戳
  requestId: string;             // 请求ID
  version: string;               // API版本
  pagination?: PaginationInfo;   // 分页信息
  rateLimit?: RateLimitInfo;     // 限流信息
}

// 分页信息
interface PaginationInfo {
  page: number;                  // 当前页
  limit: number;                 // 每页数量
  total: number;                 // 总数
  totalPages: number;            // 总页数
  hasNext: boolean;              // 是否有下一页
  hasPrev: boolean;              // 是否有上一页
}

// 限流信息
interface RateLimitInfo {
  limit: number;                 // 限制
  remaining: number;             // 剩余
  reset: number;                 // 重置时间
}

// 错误信息
interface APIError {
  code: string;                  // 错误代码
  message: string;               // 错误消息
  field?: string;                // 字段
  details?: any;                 // 详细信息
}

// 请求参数
interface RequestParams {
  [key: string]: any;
}

// 查询参数
interface QueryParams extends RequestParams {
  page?: number;                 // 页码
  limit?: number;                // 每页数量
  sort?: string;                 // 排序
  order?: 'asc' | 'desc';       // 排序方向
  search?: string;               // 搜索关键词
  filter?: Record<string, any>;  // 过滤条件
  include?: string[];            // 包含字段
  exclude?: string[];            // 排除字段
}
```
