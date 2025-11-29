# AI标书智能创作平台 - 数据模型与API接口 - 🔌 API接口设计 - 认证与授权API

```typescript
// 认证API接口
interface AuthAPI {
  // 用户注册
  register(userData: RegisterRequest): Promise<APIResponse<UserResponse>>;

  // 用户登录
  login(credentials: LoginRequest): Promise<APIResponse<LoginResponse>>;

  // 刷新令牌
  refreshToken(refreshToken: string): Promise<APIResponse<TokenResponse>>;

  // 登出
  logout(): Promise<APIResponse<null>>;

  // 获取当前用户信息
  getCurrentUser(): Promise<APIResponse<UserResponse>>;

  // 更新用户信息
  updateProfile(userData: UpdateProfileRequest): Promise<APIResponse<UserResponse>>;

  // 修改密码
  changePassword(passwordData: ChangePasswordRequest): Promise<APIResponse<null>>;

  // 忘记密码
  forgotPassword(email: string): Promise<APIResponse<null>>;

  // 重置密码
  resetPassword(resetData: ResetPasswordRequest): Promise<APIResponse<null>>;

  // 验证邮箱
  verifyEmail(token: string): Promise<APIResponse<null>>;

  // 重新发送验证邮件
  resendVerificationEmail(): Promise<APIResponse<null>>;
}

// 注册请求
interface RegisterRequest {
  username: string;              // 用户名
  email: string;                 // 邮箱
  password: string;              // 密码
  full_name: string;             // 全名
  company_name: string;          // 公司名称
  phone?: string;                // 电话
  agree_terms: boolean;          // 同意条款
}

// 登录请求
interface LoginRequest {
  username: string;              // 用户名或邮箱
  password: string;              // 密码
  remember_me?: boolean;         // 记住我
  captcha?: string;              // 验证码
}

// 登录响应
interface LoginResponse {
  access_token: string;          // 访问令牌
  refresh_token: string;         // 刷新令牌
  token_type: string;            // 令牌类型
  expires_in: number;            // 过期时间
  user: UserResponse;            // 用户信息
}

// 令牌响应
interface TokenResponse {
  access_token: string;          // 访问令牌
  expires_in: number;            // 过期时间
  token_type: string;            // 令牌类型
}

// 更新资料请求
interface UpdateProfileRequest {
  full_name?: string;            // 全名
  avatar_url?: string;           // 头像
  phone?: string;                // 电话
  department?: string;           // 部门
  position?: string;             // 职位
  bio?: string;                  // 个人简介
  timezone?: string;             // 时区
  language?: string;             // 语言
  theme?: ThemeType;             // 主题
  notification_settings?: NotificationSettings; // 通知设置
}

// 修改密码请求
interface ChangePasswordRequest {
  current_password: string;      // 当前密码
  new_password: string;          // 新密码
  confirm_password: string;      // 确认密码
}

// 重置密码请求
interface ResetPasswordRequest {
  token: string;                 // 重置令牌
  new_password: string;          // 新密码
  confirm_password: string;      // 确认密码
}
```
