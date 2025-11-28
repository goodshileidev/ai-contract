import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { message } from 'antd';
import { useAuthStore } from '@/stores/auth';

/**
 * API响应接口
 */
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  message?: string;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  timestamp: string;
}

/**
 * 分页响应接口
 */
export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/**
 * 创建Axios实例
 */
const createApiClient = (baseURL: string, timeout: number = 30000): AxiosInstance => {
  const client = axios.create({
    baseURL,
    timeout,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor - Add authentication token
  client.interceptors.request.use(
    (config) => {
      const { token } = useAuthStore.getState();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }

      // Log request in development
      if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_REQUEST_LOG === 'true') {
        console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, config.data);
      }

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor - Handle errors
  client.interceptors.response.use(
    (response: AxiosResponse<ApiResponse>) => {
      const { data } = response;

      // Log response in development
      if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_REQUEST_LOG === 'true') {
        console.log(`[API Response] ${response.config.url}`, data);
      }

      // Check if response indicates failure
      if (data.success === false) {
        const errorMessage = data.error?.message || data.message || '请求失败';
        message.error(errorMessage);
        return Promise.reject(new Error(errorMessage));
      }

      return response;
    },
    (error: AxiosError<ApiResponse>) => {
      // Handle network errors
      if (!error.response) {
        message.error('网络错误，请检查网络连接');
        return Promise.reject(error);
      }

      const { status, data } = error.response;

      // Handle different HTTP status codes
      switch (status) {
        case 401:
          // Unauthorized - clear auth and redirect to login
          message.error('登录已过期，请重新登录');
          useAuthStore.getState().logout();
          window.location.href = '/login';
          break;

        case 403:
          message.error('没有权限执行此操作');
          break;

        case 404:
          message.error('请求的资源不存在');
          break;

        case 429:
          message.error('请求过于频繁，请稍后再试');
          break;

        case 500:
        case 502:
        case 503:
        case 504:
          message.error('服务器错误，请稍后再试');
          break;

        default:
          const errorMessage = data?.error?.message || data?.message || '请求失败';
          message.error(errorMessage);
      }

      return Promise.reject(error);
    }
  );

  return client;
};

/**
 * Java Backend API Client (Port 8080)
 * 核心业务API - 用户、组织、项目、文档、模板等
 */
export const javaApiClient = createApiClient(
  import.meta.env.VITE_JAVA_API_BASE_URL || 'http://localhost:8080',
  30000
);

/**
 * Python AI API Client (Port 8001)
 * AI服务API - 文档解析、内容生成、智能匹配等
 */
export const aiApiClient = createApiClient(
  import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8001',
  60000 // AI operations may take longer
);

/**
 * Generic request wrapper
 */
export async function request<T = any>(
  client: AxiosInstance,
  config: AxiosRequestConfig
): Promise<T> {
  const response = await client.request<ApiResponse<T>>(config);
  return response.data.data as T;
}

/**
 * Java API request helper
 */
export async function javaRequest<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request<T>(javaApiClient, config);
}

/**
 * AI API request helper
 */
export async function aiRequest<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request<T>(aiApiClient, config);
}

export default javaApiClient;
