# AI标书智能创作平台 - 数据模型与API接口 - 🔌 API接口设计 - 项目管理API

```typescript
// 项目API接口
interface ProjectAPI {
  // 获取项目列表
  getProjects(params?: QueryParams): Promise<APIResponse<PaginatedResponse<ProjectResponse>>>;

  // 获取项目详情
  getProject(projectId: string): Promise<APIResponse<ProjectDetailResponse>>;

  // 创建项目
  createProject(projectData: CreateProjectRequest): Promise<APIResponse<ProjectResponse>>;

  // 更新项目
  updateProject(projectId: string, projectData: UpdateProjectRequest): Promise<APIResponse<ProjectResponse>>;

  // 删除项目
  deleteProject(projectId: string): Promise<APIResponse<null>>;

  // 复制项目
  duplicateProject(projectId: string, data: DuplicateProjectRequest): Promise<APIResponse<ProjectResponse>>;

  // 获取项目成员
  getProjectMembers(projectId: string): Promise<APIResponse<ProjectMemberResponse[]>>;

  // 添加项目成员
  addProjectMember(projectId: string, memberData: AddProjectMemberRequest): Promise<APIResponse<ProjectMemberResponse>>;

  // 更新项目成员
  updateProjectMember(projectId: string, memberId: string, memberData: UpdateProjectMemberRequest): Promise<APIResponse<ProjectMemberResponse>>;

  // 移除项目成员
  removeProjectMember(projectId: string, memberId: string): Promise<APIResponse<null>>;

  // 获取项目统计
  getProjectStats(projectId: string): Promise<APIResponse<ProjectStatsResponse>>;

  // 导出项目数据
  exportProject(projectId: string, format: ExportFormat): Promise<APIResponse<ExportResponse>>;
}

// 项目响应
interface ProjectResponse {
  id: string;                    // 项目ID
  name: string;                  // 项目名称
  description?: string;          // 描述
  client_name: string;           // 客户名称
  project_type: ProjectType;     // 项目类型
  status: ProjectStatus;         // 项目状态
  priority: Priority;            // 优先级
  submission_deadline?: Date;    // 提交截止时间
  budget_amount?: number;        // 预算金额
  created_at: Date;              // 创建时间
  updated_at: Date;              // 更新时间
  member_count: number;          // 成员数量
  document_count: number;        // 文档数量
}

// 项目详情响应
interface ProjectDetailResponse extends ProjectResponse {
  project_number?: string;       // 项目编号
  client_industry?: string;      // 客户行业
  project_category?: string;     // 项目分类
  tender_document_url?: string;  // 招标文档URL
  bid_opening_date?: Date;       // 开标时间
  estimated_duration?: number;   // 预估天数
  actual_duration?: number;      // 实际天数
  team_size?: number;            // 团队规模
  competitors: CompetitorInfo[]; // 竞争对手
  market_analysis?: MarketAnalysis; // 市场分析
  created_by: UserResponse;      // 创建者
  members: ProjectMemberResponse[]; // 成员列表
  recent_activities: ActivityLog[]; // 最近活动
}

// 创建项目请求
interface CreateProjectRequest {
  name: string;                  // 项目名称
  description?: string;          // 描述
  client_name: string;           // 客户名称
  client_industry?: string;      // 客户行业
  project_type: ProjectType;     // 项目类型
  project_category?: string;     // 项目分类
  project_number?: string;       // 项目编号
  tender_document_url?: string;  // 招标文档URL
  submission_deadline?: Date;    // 提交截止时间
  bid_opening_date?: Date;       // 开标时间
  budget_amount?: number;        // 预算金额
  budget_currency?: string;      // 预算货币
  priority?: Priority;           // 优先级
  estimated_duration?: number;   // 预估天数
  team_size?: number;            // 团队规模
  tags?: string[];               // 标签
}

// 更新项目请求
interface UpdateProjectRequest {
  name?: string;                 // 项目名称
  description?: string;          // 描述
  client_name?: string;          // 客户名称
  client_industry?: string;      // 客户行业
  project_type?: ProjectType;    // 项目类型
  project_category?: string;     // 项目分类
  status?: ProjectStatus;        // 项目状态
  priority?: Priority;           // 优先级
  submission_deadline?: Date;    // 提交截止时间
  bid_opening_date?: Date;       // 开标时间
  budget_amount?: number;        // 预算金额
  actual_duration?: number;      // 实际天数
  team_size?: number;            // 团队规模
  tags?: string[];               // 标签
}

// 复制项目请求
interface DuplicateProjectRequest {
  name: string;                  // 新项目名称
  copy_members?: boolean;        // 是否复制成员
  copy_documents?: boolean;      // 是否复制文档
  copy_settings?: boolean;       // 是否复制设置
}

// 项目成员响应
interface ProjectMemberResponse {
  id: string;                    // 成员ID
  user_id: string;               // 用户ID
  user: UserResponse;            // 用户信息
  role: ProjectRole;             // 项目角色
  responsibilities: string[];     // 职责
  can_edit: boolean;             // 编辑权限
  can_delete: boolean;           // 删除权限
  can_invite: boolean;           // 邀请权限
  can_approve: boolean;          // 审批权限
  status: MemberStatus;          // 成员状态
  joined_at: Date;               // 加入时间
  last_activity_at?: Date;       // 最后活动时间
}

// 添加项目成员请求
interface AddProjectMemberRequest {
  user_id: string;               // 用户ID
  role: ProjectRole;             // 项目角色
  responsibilities?: string[];     // 职责
  can_edit?: boolean;            // 编辑权限
  can_delete?: boolean;          // 删除权限
  can_invite?: boolean;          // 邀请权限
  can_approve?: boolean;         // 审批权限
  send_notification?: boolean;   // 发送通知
}

// 更新项目成员请求
interface UpdateProjectMemberRequest {
  role?: ProjectRole;            // 项目角色
  responsibilities?: string[];    // 职责
  can_edit?: boolean;            // 编辑权限
  can_delete?: boolean;          // 删除权限
  can_invite?: boolean;          // 邀请权限
  can_approve?: boolean;         // 审批权限
  status?: MemberStatus;         // 成员状态
}

// 项目统计响应
interface ProjectStatsResponse {
  total_documents: number;       // 总文档数
  completed_sections: number;    // 完成章节数
  total_sections: number;        // 总章节数
  completion_percentage: number; // 完成百分比
  days_until_deadline: number;   // 距离截止日期天数
  active_members: number;        // 活跃成员数
  recent_activity_count: number; // 最近活动数
  quality_score: number;         // 质量分数
  risk_level: RiskLevel;         // 风险等级
}

// 分页响应
interface PaginatedResponse<T> {
  items: T[];                    // 数据项
  pagination: PaginationInfo;    // 分页信息
}

// 活动日志
interface ActivityLog {
  id: string;                    // 活动ID
  type: ActivityType;            // 活动类型
  description: string;           // 描述
  user_id: string;               // 用户ID
  user: UserResponse;            // 用户信息
  target_type: string;           // 目标类型
  target_id: string;             // 目标ID
  metadata?: Record<string, any>; // 元数据
  created_at: Date;              // 创建时间
}

// 活动类型
enum ActivityType {
  PROJECT_CREATED = 'project_created',
  PROJECT_UPDATED = 'project_updated',
  PROJECT_DELETED = 'project_deleted',
  MEMBER_ADDED = 'member_added',
  MEMBER_REMOVED = 'member_removed',
  DOCUMENT_CREATED = 'document_created',
  DOCUMENT_UPDATED = 'document_updated',
  DOCUMENT_DELETED = 'document_deleted',
  COMMENT_ADDED = 'comment_added',
  APPROVAL_REQUESTED = 'approval_requested',
  APPROVAL_COMPLETED = 'approval_completed'
}

// 风险等级
enum RiskLevel {
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  CRITICAL = 'critical'
}

// 导出格式
enum ExportFormat {
  PDF = 'pdf',
  DOCX = 'docx',
  XLSX = 'xlsx',
  JSON = 'json',
  CSV = 'csv'
}

// 导出响应
interface ExportResponse {
  download_url: string;         // 下载链接
  file_name: string;             // 文件名
  file_size: number;             // 文件大小
  expires_at: Date;             // 过期时间
}
```
