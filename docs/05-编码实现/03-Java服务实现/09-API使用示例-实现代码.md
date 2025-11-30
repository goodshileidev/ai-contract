---
文档类型: 实现文档
需求编号: REQ-2025-11-010
创建日期: 2025-11-30 13:30
创建者: claude-opus-4-1-20250805
最后更新: 2025-11-30 13:30
更新者: claude-opus-4-1-20250805
状态: 草稿
---

# API使用示例 - JavaScript/TypeScript实现代码

本文档包含AI标书智能创作平台的API使用示例代码，包括JavaScript和TypeScript实现。

## 1. 初始化和配置

### 1.1 安装依赖

```bash
# 使用npm
npm install axios @types/axios
npm install form-data @types/form-data
npm install ws @types/ws

# 使用yarn
yarn add axios @types/axios
yarn add form-data @types/form-data
yarn add ws @types/ws
```

### 1.2 API客户端配置

```typescript
// config/api.config.ts
export interface ApiConfig {
  baseURL: string;
  timeout: number;
  headers: Record<string, string>;
  retry: {
    times: number;
    delay: number;
  };
}

export const apiConfig: ApiConfig = {
  baseURL: process.env.API_BASE_URL || 'https://api.aibidcomposer.com',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'X-API-Version': 'v1'
  },
  retry: {
    times: 3,
    delay: 1000
  }
};
```

## 2. API客户端实现

### 2.1 基础HTTP客户端

```typescript
// lib/http-client.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { apiConfig } from '../config/api.config';

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  timestamp: string;
  requestId?: string;
}

export class HttpClient {
  private client: AxiosInstance;
  private token: string | null = null;

  constructor(config: Partial<ApiConfig> = {}) {
    const mergedConfig = { ...apiConfig, ...config };

    this.client = axios.create({
      baseURL: mergedConfig.baseURL,
      timeout: mergedConfig.timeout,
      headers: mergedConfig.headers
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // 请求拦截器
    this.client.interceptors.request.use(
      (config) => {
        if (this.token) {
          config.headers['Authorization'] = `Bearer ${this.token}`;
        }

        // 添加请求ID
        config.headers['X-Request-ID'] = this.generateRequestId();

        // 打印请求日志
        console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
          params: config.params,
          data: config.data
        });

        return config;
      },
      (error) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
      }
    );

    // 响应拦截器
    this.client.interceptors.response.use(
      (response) => {
        console.log(`[API Response] ${response.config.url}`, {
          status: response.status,
          data: response.data
        });
        return response;
      },
      async (error: AxiosError) => {
        console.error(`[API Response Error] ${error.config?.url}`, {
          status: error.response?.status,
          data: error.response?.data
        });

        // Token过期处理
        if (error.response?.status === 401) {
          await this.handleTokenExpired();
        }

        // 重试机制
        if (this.shouldRetry(error)) {
          return this.retryRequest(error.config!);
        }

        return Promise.reject(error);
      }
    );
  }

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private shouldRetry(error: AxiosError): boolean {
    return error.response?.status === 503 || error.code === 'ECONNABORTED';
  }

  private async retryRequest(config: AxiosRequestConfig, retryCount = 0): Promise<AxiosResponse> {
    if (retryCount >= apiConfig.retry.times) {
      throw new Error('Max retry attempts reached');
    }

    await this.sleep(apiConfig.retry.delay * Math.pow(2, retryCount));

    return this.client.request({ ...config, _retry: retryCount + 1 } as any);
  }

  private sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private async handleTokenExpired(): Promise<void> {
    // 尝试刷新Token
    try {
      const refreshToken = localStorage.getItem('refresh_token');
      if (refreshToken) {
        const response = await this.post<{ access_token: string; refresh_token: string }>(
          '/auth/refresh',
          { refresh_token: refreshToken }
        );

        if (response.success && response.data) {
          this.setToken(response.data.access_token);
          localStorage.setItem('refresh_token', response.data.refresh_token);
        }
      }
    } catch (error) {
      // 刷新失败，清除认证信息
      this.clearAuth();
      window.location.href = '/login';
    }
  }

  public setToken(token: string): void {
    this.token = token;
    localStorage.setItem('access_token', token);
  }

  public clearAuth(): void {
    this.token = null;
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }

  // HTTP方法封装
  public async get<T>(url: string, params?: any): Promise<ApiResponse<T>> {
    const response = await this.client.get<ApiResponse<T>>(url, { params });
    return response.data;
  }

  public async post<T>(url: string, data?: any): Promise<ApiResponse<T>> {
    const response = await this.client.post<ApiResponse<T>>(url, data);
    return response.data;
  }

  public async put<T>(url: string, data?: any): Promise<ApiResponse<T>> {
    const response = await this.client.put<ApiResponse<T>>(url, data);
    return response.data;
  }

  public async delete<T>(url: string): Promise<ApiResponse<T>> {
    const response = await this.client.delete<ApiResponse<T>>(url);
    return response.data;
  }

  public async upload<T>(url: string, file: File, onProgress?: (progress: number) => void): Promise<ApiResponse<T>> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await this.client.post<ApiResponse<T>>(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(progress);
        }
      }
    });

    return response.data;
  }
}
```

### 2.2 API服务封装

```typescript
// services/auth.service.ts
import { HttpClient } from '../lib/http-client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  user: {
    id: string;
    name: string;
    email: string;
    role: string;
  };
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  organization?: string;
}

export class AuthService {
  constructor(private http: HttpClient) {}

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await this.http.post<LoginResponse>('/api/v1/auth/login', credentials);

    if (response.success && response.data) {
      // 保存Token
      this.http.setToken(response.data.access_token);
      localStorage.setItem('refresh_token', response.data.refresh_token);
      localStorage.setItem('user', JSON.stringify(response.data.user));

      return response.data;
    }

    throw new Error(response.error?.message || 'Login failed');
  }

  async register(data: RegisterRequest): Promise<void> {
    const response = await this.http.post('/api/v1/auth/register', data);

    if (!response.success) {
      throw new Error(response.error?.message || 'Registration failed');
    }
  }

  async logout(): Promise<void> {
    try {
      await this.http.post('/api/v1/auth/logout');
    } finally {
      this.http.clearAuth();
      localStorage.removeItem('user');
    }
  }

  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response = await this.http.post<LoginResponse>('/api/v1/auth/refresh', {
      refresh_token: refreshToken
    });

    if (response.success && response.data) {
      this.http.setToken(response.data.access_token);
      return response.data;
    }

    throw new Error('Token refresh failed');
  }

  async getCurrentUser() {
    const response = await this.http.get('/api/v1/auth/me');
    return response.data;
  }
}
```

## 3. 业务API使用示例

### 3.1 项目管理API

```typescript
// services/project.service.ts
export interface Project {
  id: string;
  name: string;
  description: string;
  status: 'draft' | 'in_progress' | 'completed';
  deadline: string;
  created_at: string;
  updated_at: string;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  deadline: string;
  team_members?: string[];
}

export interface ProjectListParams {
  page?: number;
  pageSize?: number;
  status?: string;
  search?: string;
  sort?: string;
}

export class ProjectService {
  constructor(private http: HttpClient) {}

  async getProjects(params?: ProjectListParams) {
    const response = await this.http.get<{
      items: Project[];
      total: number;
      page: number;
      pageSize: number;
    }>('/api/v1/projects', params);

    return response.data;
  }

  async getProject(id: string) {
    const response = await this.http.get<Project>(`/api/v1/projects/${id}`);
    return response.data;
  }

  async createProject(data: CreateProjectRequest) {
    const response = await this.http.post<Project>('/api/v1/projects', data);
    return response.data;
  }

  async updateProject(id: string, data: Partial<Project>) {
    const response = await this.http.put<Project>(`/api/v1/projects/${id}`, data);
    return response.data;
  }

  async deleteProject(id: string) {
    await this.http.delete(`/api/v1/projects/${id}`);
  }

  async addTeamMember(projectId: string, userId: string, role: string) {
    const response = await this.http.post(`/api/v1/projects/${projectId}/members`, {
      user_id: userId,
      role: role
    });
    return response.data;
  }
}
```

### 3.2 文档解析API

```typescript
// services/document.service.ts
export interface DocumentParseRequest {
  document_id: string;
  parse_options?: {
    extract_tables?: boolean;
    extract_images?: boolean;
    ocr_enabled?: boolean;
  };
}

export interface ParsedDocument {
  document_id: string;
  title: string;
  sections: DocumentSection[];
  requirements: Requirement[];
  tables: any[];
  metadata: Record<string, any>;
  parse_duration: number;
}

export interface DocumentSection {
  id: string;
  title: string;
  content: string;
  level: number;
  page_number: number;
}

export interface Requirement {
  id: string;
  type: 'technical' | 'business' | 'qualification';
  description: string;
  priority: 'high' | 'medium' | 'low';
  mandatory: boolean;
}

export class DocumentService {
  constructor(private http: HttpClient) {}

  async uploadDocument(file: File, onProgress?: (progress: number) => void) {
    const response = await this.http.upload<{ document_id: string }>(
      '/api/v1/documents/upload',
      file,
      onProgress
    );

    if (!response.success) {
      throw new Error('Document upload failed');
    }

    return response.data;
  }

  async parseDocument(request: DocumentParseRequest) {
    const response = await this.http.post<ParsedDocument>(
      '/api/v1/documents/parse',
      request
    );

    return response.data;
  }

  async getDocument(id: string) {
    const response = await this.http.get<ParsedDocument>(`/api/v1/documents/${id}`);
    return response.data;
  }

  async getDocumentRequirements(id: string) {
    const response = await this.http.get<Requirement[]>(
      `/api/v1/documents/${id}/requirements`
    );
    return response.data;
  }

  async updateDocument(id: string, data: any) {
    const response = await this.http.put(`/api/v1/documents/${id}`, data);
    return response.data;
  }
}
```

### 3.3 AI服务API

```typescript
// services/ai.service.ts
export interface AnalyzeRequirementsRequest {
  document_id: string;
  analysis_depth: 'quick' | 'standard' | 'comprehensive';
}

export interface GenerateContentRequest {
  project_id: string;
  requirements: string[];
  template_id?: string;
  options?: {
    style: 'formal' | 'technical' | 'business';
    length: 'concise' | 'standard' | 'detailed';
    language: 'zh-CN' | 'en-US';
  };
}

export interface QualityCheckRequest {
  document_id: string;
  check_type: 'full' | 'quick' | 'custom';
  check_items?: string[];
}

export interface QualityCheckResult {
  quality_score: number;
  plagiarism: {
    score: number;
    similarity_rate: number;
    similar_segments: any[];
  };
  compliance: {
    score: number;
    violations: any[];
    warnings: any[];
  };
  completeness: {
    score: number;
    missing_sections: string[];
    coverage_rate: number;
  };
  risk: {
    score: number;
    high_risks: any[];
    medium_risks: any[];
  };
  suggestions: string[];
}

export class AIService {
  constructor(private http: HttpClient) {}

  async analyzeRequirements(request: AnalyzeRequirementsRequest) {
    const response = await this.http.post(
      '/api/v1/ai/analyze-requirements',
      request
    );
    return response.data;
  }

  async matchCapabilities(projectId: string, requirements: string[]) {
    const response = await this.http.post('/api/v1/ai/match-capabilities', {
      project_id: projectId,
      requirements: requirements
    });
    return response.data;
  }

  async generateContent(request: GenerateContentRequest) {
    const response = await this.http.post('/api/v1/ai/generate-content', request);

    if (!response.success) {
      throw new Error('Content generation failed');
    }

    return response.data;
  }

  async checkQuality(request: QualityCheckRequest) {
    const response = await this.http.post<QualityCheckResult>(
      '/api/v1/ai/quality-check',
      request
    );
    return response.data;
  }

  async getTaskStatus(taskId: string) {
    const response = await this.http.get(`/api/v1/ai/tasks/${taskId}`);
    return response.data;
  }

  // 长轮询获取任务结果
  async waitForTask(taskId: string, maxWaitTime = 60000): Promise<any> {
    const startTime = Date.now();

    while (Date.now() - startTime < maxWaitTime) {
      const status = await this.getTaskStatus(taskId);

      if (status.state === 'completed') {
        return status.result;
      }

      if (status.state === 'failed') {
        throw new Error(status.error || 'Task failed');
      }

      // 等待2秒后重试
      await new Promise(resolve => setTimeout(resolve, 2000));
    }

    throw new Error('Task timeout');
  }
}
```

## 4. WebSocket实时协作

```typescript
// services/collaboration.service.ts
import WebSocket from 'ws';

export interface CollaborationMessage {
  type: 'cursor' | 'selection' | 'edit' | 'comment' | 'presence';
  user_id: string;
  document_id: string;
  data: any;
  timestamp: string;
}

export class CollaborationService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000;
  private listeners: Map<string, Set<Function>> = new Map();

  connect(sessionId: string, token: string): Promise<void> {
    return new Promise((resolve, reject) => {
      const wsUrl = `wss://api.aibidcomposer.com/api/v1/collaboration/connect?session=${sessionId}&token=${token}`;

      this.ws = new WebSocket(wsUrl);

      this.ws.on('open', () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        resolve();
      });

      this.ws.on('message', (data: string) => {
        try {
          const message = JSON.parse(data) as CollaborationMessage;
          this.handleMessage(message);
        } catch (error) {
          console.error('Failed to parse WebSocket message:', error);
        }
      });

      this.ws.on('close', () => {
        console.log('WebSocket disconnected');
        this.handleDisconnect();
      });

      this.ws.on('error', (error) => {
        console.error('WebSocket error:', error);
        reject(error);
      });
    });
  }

  private handleMessage(message: CollaborationMessage): void {
    const listeners = this.listeners.get(message.type);
    if (listeners) {
      listeners.forEach(listener => listener(message));
    }
  }

  private handleDisconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);

      console.log(`Reconnecting in ${delay}ms... (attempt ${this.reconnectAttempts})`);

      setTimeout(() => {
        // Reconnect logic here
      }, delay);
    }
  }

  on(event: string, callback: Function): void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set());
    }
    this.listeners.get(event)!.add(callback);
  }

  off(event: string, callback: Function): void {
    const listeners = this.listeners.get(event);
    if (listeners) {
      listeners.delete(callback);
    }
  }

  send(message: CollaborationMessage): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    } else {
      console.error('WebSocket is not connected');
    }
  }

  disconnect(): void {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  // 发送光标位置
  sendCursorPosition(documentId: string, position: { line: number; column: number }): void {
    this.send({
      type: 'cursor',
      user_id: this.getCurrentUserId(),
      document_id: documentId,
      data: position,
      timestamp: new Date().toISOString()
    });
  }

  // 发送选区
  sendSelection(documentId: string, selection: { start: any; end: any }): void {
    this.send({
      type: 'selection',
      user_id: this.getCurrentUserId(),
      document_id: documentId,
      data: selection,
      timestamp: new Date().toISOString()
    });
  }

  // 发送编辑
  sendEdit(documentId: string, edit: { operation: string; range: any; text: string }): void {
    this.send({
      type: 'edit',
      user_id: this.getCurrentUserId(),
      document_id: documentId,
      data: edit,
      timestamp: new Date().toISOString()
    });
  }

  private getCurrentUserId(): string {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.id || '';
  }
}
```

## 5. 错误处理

```typescript
// lib/error-handler.ts
export enum ErrorCode {
  UNAUTHORIZED = 'UNAUTHORIZED',
  FORBIDDEN = 'FORBIDDEN',
  NOT_FOUND = 'NOT_FOUND',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  SERVER_ERROR = 'SERVER_ERROR',
  NETWORK_ERROR = 'NETWORK_ERROR',
  TIMEOUT = 'TIMEOUT'
}

export class ApiError extends Error {
  code: ErrorCode;
  details?: any;

  constructor(code: ErrorCode, message: string, details?: any) {
    super(message);
    this.code = code;
    this.details = details;
    this.name = 'ApiError';
  }
}

export class ErrorHandler {
  static handle(error: any): ApiError {
    if (error instanceof ApiError) {
      return error;
    }

    if (error.response) {
      // 服务器响应错误
      const status = error.response.status;
      const data = error.response.data;

      switch (status) {
        case 401:
          return new ApiError(ErrorCode.UNAUTHORIZED, '未授权访问', data);
        case 403:
          return new ApiError(ErrorCode.FORBIDDEN, '没有权限', data);
        case 404:
          return new ApiError(ErrorCode.NOT_FOUND, '资源不存在', data);
        case 400:
          return new ApiError(ErrorCode.VALIDATION_ERROR, '请求参数错误', data);
        default:
          if (status >= 500) {
            return new ApiError(ErrorCode.SERVER_ERROR, '服务器错误', data);
          }
          return new ApiError(ErrorCode.SERVER_ERROR, '未知错误', data);
      }
    } else if (error.request) {
      // 网络错误
      if (error.code === 'ECONNABORTED') {
        return new ApiError(ErrorCode.TIMEOUT, '请求超时');
      }
      return new ApiError(ErrorCode.NETWORK_ERROR, '网络连接失败');
    }

    return new ApiError(ErrorCode.SERVER_ERROR, error.message || '未知错误');
  }
}
```

## 6. SDK封装

```typescript
// sdk/aibid-sdk.ts
import { HttpClient } from '../lib/http-client';
import { AuthService } from '../services/auth.service';
import { ProjectService } from '../services/project.service';
import { DocumentService } from '../services/document.service';
import { AIService } from '../services/ai.service';
import { CollaborationService } from '../services/collaboration.service';

export interface SDKConfig {
  apiKey?: string;
  baseURL?: string;
  timeout?: number;
}

export class AIBidSDK {
  private http: HttpClient;
  public auth: AuthService;
  public projects: ProjectService;
  public documents: DocumentService;
  public ai: AIService;
  public collaboration: CollaborationService;

  constructor(config: SDKConfig = {}) {
    this.http = new HttpClient({
      baseURL: config.baseURL,
      timeout: config.timeout
    });

    if (config.apiKey) {
      this.http.setToken(config.apiKey);
    }

    this.auth = new AuthService(this.http);
    this.projects = new ProjectService(this.http);
    this.documents = new DocumentService(this.http);
    this.ai = new AIService(this.http);
    this.collaboration = new CollaborationService();
  }

  async initialize(): Promise<void> {
    // 从本地存储恢复认证状态
    const token = localStorage.getItem('access_token');
    if (token) {
      this.http.setToken(token);

      // 验证Token有效性
      try {
        await this.auth.getCurrentUser();
      } catch (error) {
        // Token无效，尝试刷新
        const refreshToken = localStorage.getItem('refresh_token');
        if (refreshToken) {
          try {
            await this.auth.refreshToken(refreshToken);
          } catch (refreshError) {
            // 刷新失败，清除认证信息
            this.http.clearAuth();
          }
        }
      }
    }
  }
}

// 导出单例
export const aibidSDK = new AIBidSDK();
```

## 7. 使用示例

### 7.1 完整的工作流示例

```typescript
// examples/complete-workflow.ts
import { aibidSDK } from '../sdk/aibid-sdk';

async function completeBiddingWorkflow() {
  try {
    // 1. 初始化SDK
    await aibidSDK.initialize();

    // 2. 用户登录
    await aibidSDK.auth.login({
      email: 'user@example.com',
      password: 'password123'
    });

    console.log('登录成功');

    // 3. 创建项目
    const project = await aibidSDK.projects.createProject({
      name: '政府采购项目投标',
      description: 'XX市政府信息化建设项目',
      deadline: '2024-12-31'
    });

    console.log('项目创建成功:', project.id);

    // 4. 上传招标文件
    const fileInput = document.getElementById('file-input') as HTMLInputElement;
    const file = fileInput.files![0];

    const uploadResult = await aibidSDK.documents.uploadDocument(
      file,
      (progress) => {
        console.log(`上传进度: ${progress}%`);
      }
    );

    console.log('文档上传成功:', uploadResult.document_id);

    // 5. 解析文档
    const parsedDoc = await aibidSDK.documents.parseDocument({
      document_id: uploadResult.document_id,
      parse_options: {
        extract_tables: true,
        extract_images: false,
        ocr_enabled: true
      }
    });

    console.log('文档解析完成，提取需求:', parsedDoc.requirements.length);

    // 6. 分析需求
    const analysisResult = await aibidSDK.ai.analyzeRequirements({
      document_id: uploadResult.document_id,
      analysis_depth: 'comprehensive'
    });

    console.log('需求分析完成');

    // 7. 匹配能力
    const matchResult = await aibidSDK.ai.matchCapabilities(
      project.id,
      parsedDoc.requirements.map(r => r.description)
    );

    console.log('能力匹配完成，匹配度:', matchResult.overall_score);

    // 8. 生成内容
    const generatedContent = await aibidSDK.ai.generateContent({
      project_id: project.id,
      requirements: parsedDoc.requirements
        .filter(r => r.priority === 'high')
        .map(r => r.description),
      options: {
        style: 'technical',
        length: 'detailed',
        language: 'zh-CN'
      }
    });

    console.log('内容生成完成');

    // 9. 质量检查
    const qualityResult = await aibidSDK.ai.checkQuality({
      document_id: generatedContent.document_id,
      check_type: 'full',
      check_items: ['plagiarism', 'compliance', 'completeness', 'risk']
    });

    console.log('质量检查完成，综合得分:', qualityResult.quality_score);

    // 10. 实时协作
    await aibidSDK.collaboration.connect(project.id, localStorage.getItem('access_token')!);

    // 监听协作事件
    aibidSDK.collaboration.on('edit', (message) => {
      console.log('收到编辑:', message);
      // 更新本地文档
    });

    // 发送编辑
    aibidSDK.collaboration.sendEdit(generatedContent.document_id, {
      operation: 'insert',
      range: { start: 0, end: 0 },
      text: '新增内容'
    });

  } catch (error) {
    console.error('工作流执行失败:', error);
  }
}
```

### 7.2 React组件示例

```tsx
// components/DocumentUploader.tsx
import React, { useState } from 'react';
import { aibidSDK } from '../sdk/aibid-sdk';

export const DocumentUploader: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [result, setResult] = useState<any>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setUploading(true);
    setProgress(0);

    try {
      // 上传文档
      const uploadResult = await aibidSDK.documents.uploadDocument(
        file,
        (prog) => setProgress(prog)
      );

      // 解析文档
      const parsedDoc = await aibidSDK.documents.parseDocument({
        document_id: uploadResult.document_id,
        parse_options: {
          extract_tables: true,
          ocr_enabled: true
        }
      });

      setResult(parsedDoc);
    } catch (error) {
      console.error('上传失败:', error);
      alert('文档处理失败');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="document-uploader">
      <input
        type="file"
        accept=".pdf,.doc,.docx"
        onChange={handleFileChange}
        disabled={uploading}
      />

      <button onClick={handleUpload} disabled={!file || uploading}>
        {uploading ? `上传中... ${progress}%` : '上传并解析'}
      </button>

      {result && (
        <div className="parse-result">
          <h3>解析结果</h3>
          <p>标题: {result.title}</p>
          <p>章节数: {result.sections.length}</p>
          <p>需求数: {result.requirements.length}</p>

          <h4>关键需求:</h4>
          <ul>
            {result.requirements
              .filter((r: any) => r.priority === 'high')
              .map((req: any) => (
                <li key={req.id}>
                  {req.description}
                </li>
              ))}
          </ul>
        </div>
      )}
    </div>
  );
};
```

### 7.3 Vue组件示例

```vue
<!-- components/QualityChecker.vue -->
<template>
  <div class="quality-checker">
    <h2>质量检查</h2>

    <div class="check-options">
      <label>
        <input type="checkbox" v-model="checkItems.plagiarism"> 查重检查
      </label>
      <label>
        <input type="checkbox" v-model="checkItems.compliance"> 合规性检查
      </label>
      <label>
        <input type="checkbox" v-model="checkItems.completeness"> 完整性检查
      </label>
      <label>
        <input type="checkbox" v-model="checkItems.risk"> 风险评估
      </label>
    </div>

    <button @click="runCheck" :disabled="checking">
      {{ checking ? '检查中...' : '开始检查' }}
    </button>

    <div v-if="result" class="check-result">
      <h3>检查结果</h3>
      <div class="score">综合得分: {{ result.quality_score }}</div>

      <div class="details">
        <div v-if="result.plagiarism">
          <h4>查重结果</h4>
          <p>相似度: {{ result.plagiarism.similarity_rate * 100 }}%</p>
        </div>

        <div v-if="result.compliance">
          <h4>合规性</h4>
          <p>违规项: {{ result.compliance.violations.length }}</p>
          <p>警告项: {{ result.compliance.warnings.length }}</p>
        </div>

        <div v-if="result.suggestions.length">
          <h4>改进建议</h4>
          <ul>
            <li v-for="suggestion in result.suggestions" :key="suggestion">
              {{ suggestion }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { aibidSDK } from '../sdk/aibid-sdk';

export default {
  props: {
    documentId: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      checking: false,
      checkItems: {
        plagiarism: true,
        compliance: true,
        completeness: true,
        risk: true
      },
      result: null
    };
  },

  methods: {
    async runCheck() {
      this.checking = true;

      try {
        const selectedItems = Object.keys(this.checkItems)
          .filter(key => this.checkItems[key]);

        this.result = await aibidSDK.ai.checkQuality({
          document_id: this.documentId,
          check_type: 'custom',
          check_items: selectedItems
        });

      } catch (error) {
        console.error('质量检查失败:', error);
        alert('质量检查失败');
      } finally {
        this.checking = false;
      }
    }
  }
};
</script>
```

## 8. 测试代码

```typescript
// __tests__/api-client.test.ts
import { HttpClient } from '../lib/http-client';
import { AuthService } from '../services/auth.service';

describe('API Client Tests', () => {
  let httpClient: HttpClient;
  let authService: AuthService;

  beforeEach(() => {
    httpClient = new HttpClient({
      baseURL: 'http://localhost:3000/api'
    });
    authService = new AuthService(httpClient);
  });

  test('should login successfully', async () => {
    const mockResponse = {
      success: true,
      data: {
        access_token: 'test_token',
        refresh_token: 'refresh_token',
        expires_in: 3600,
        user: {
          id: 'user_123',
          name: 'Test User',
          email: 'test@example.com',
          role: 'user'
        }
      }
    };

    // Mock HTTP请求
    jest.spyOn(httpClient, 'post').mockResolvedValue(mockResponse);

    const result = await authService.login({
      email: 'test@example.com',
      password: 'password123'
    });

    expect(result.user.email).toBe('test@example.com');
    expect(localStorage.getItem('access_token')).toBe('test_token');
  });

  test('should handle login failure', async () => {
    const mockResponse = {
      success: false,
      error: {
        code: 'INVALID_CREDENTIALS',
        message: 'Invalid email or password'
      }
    };

    jest.spyOn(httpClient, 'post').mockResolvedValue(mockResponse);

    await expect(authService.login({
      email: 'wrong@example.com',
      password: 'wrongpassword'
    })).rejects.toThrow('Invalid email or password');
  });
});
```

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-30 13:30 | 1.0 | claude-opus-4-1-20250805 | 创建API使用示例实现代码 |