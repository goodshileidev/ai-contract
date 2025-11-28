import { javaRequest } from './api';
import type { User } from '@/stores/auth';

/**
 * 登录请求参数
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  user: User;
}

/**
 * 注册请求参数
 */
export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  full_name?: string;
  organization_name?: string;
}

/**
 * 认证服务
 */
export const authService = {
  /**
   * 用户登录
   */
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    return javaRequest<LoginResponse>({
      method: 'POST',
      url: '/api/v1/auth/login',
      data,
    });
  },

  /**
   * 用户注册
   */
  register: async (data: RegisterRequest): Promise<{ user_id: string }> => {
    return javaRequest<{ user_id: string }>({
      method: 'POST',
      url: '/api/v1/auth/register',
      data,
    });
  },

  /**
   * 用户登出
   */
  logout: async (): Promise<void> => {
    return javaRequest<void>({
      method: 'POST',
      url: '/api/v1/auth/logout',
    });
  },

  /**
   * 刷新Token
   */
  refreshToken: async (refreshToken: string): Promise<{ access_token: string }> => {
    return javaRequest<{ access_token: string }>({
      method: 'POST',
      url: '/api/v1/auth/refresh-token',
      headers: {
        Authorization: `Bearer ${refreshToken}`,
      },
    });
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser: async (): Promise<User> => {
    return javaRequest<User>({
      method: 'GET',
      url: '/api/v1/auth/me',
    });
  },

  /**
   * 更新用户资料
   */
  updateProfile: async (data: Partial<User>): Promise<User> => {
    return javaRequest<User>({
      method: 'PUT',
      url: '/api/v1/users/me',
      data,
    });
  },

  /**
   * 修改密码
   */
  changePassword: async (data: {
    old_password: string;
    new_password: string;
  }): Promise<void> => {
    return javaRequest<void>({
      method: 'PUT',
      url: '/api/v1/users/me/password',
      data,
    });
  },
};
