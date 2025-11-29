# AI标书智能创作平台 - 数据模型与API接口 - 🔌 API接口设计 - 文档管理API

```typescript
// 文档API接口
interface DocumentAPI {
  // 获取文档列表
  getDocuments(projectId: string, params?: QueryParams): Promise<APIResponse<PaginatedResponse<DocumentResponse>>>;

  // 获取文档详情
  getDocument(documentId: string): Promise<APIResponse<DocumentDetailResponse>>;

  // 创建文档
  createDocument(projectId: string, documentData: CreateDocumentRequest): Promise<APIResponse<DocumentResponse>>;

  // 更新文档
  updateDocument(documentId: string, documentData: UpdateDocumentRequest): Promise<APIResponse<DocumentResponse>>;

  // 删除文档
  deleteDocument(documentId: string): Promise<APIResponse<null>>;

  // 上传文档
  uploadDocument(projectId: string, file: File, metadata?: UploadMetadata): Promise<APIResponse<DocumentResponse>>;

  // 下载文档
  downloadDocument(documentId: string, format?: DownloadFormat): Promise<APIResponse<DownloadResponse>>;

  // 复制文档
  duplicateDocument(documentId: string, data: DuplicateDocumentRequest): Promise<APIResponse<DocumentResponse>>;

  // 获取文档版本
  getDocumentVersions(documentId: string): Promise<APIResponse<DocumentVersionResponse[]>>;

  // 创建文档版本
  createDocumentVersion(documentId: string, versionData: CreateVersionRequest): Promise<APIResponse<DocumentVersionResponse>>;

  // 恢复文档版本
  restoreDocumentVersion(documentId: string, versionId: string): Promise<APIResponse<DocumentResponse>>;

  // 获取文档评论
  getDocumentComments(documentId: string, params?: QueryParams): Promise<APIResponse<CommentResponse[]>>;

  // 添加文档评论
  addDocumentComment(documentId: string, commentData: CreateCommentRequest): Promise<APIResponse<CommentResponse>>;

  // 更新文档评论
  updateDocumentComment(documentId: string, commentId: string, commentData: UpdateCommentRequest): Promise<APIResponse<CommentResponse>>;

  // 删除文档评论
  deleteDocumentComment(documentId: string, commentId: string): Promise<APIResponse<null>>;

  // 分析文档
  analyzeDocument(documentId: string, analysisOptions?: AnalysisOptions): Promise<APIResponse<AnalysisResponse>>;

  // 生成内容
  generateContent(documentId: string, generationRequest: ContentGenerationRequest): Promise<APIResponse<ContentGenerationResponse>>;

  // 检查文档质量
  checkDocumentQuality(documentId: string): Promise<APIResponse<QualityCheckResponse>>;

  // 获取文档协作者
  getDocumentCollaborators(documentId: string): Promise<APIResponse<CollaboratorResponse[]>>;

  // 添加文档协作者
  addDocumentCollaborator(documentId: string, collaboratorData: AddCollaboratorRequest): Promise<APIResponse<CollaboratorResponse>>;

  // 更新文档协作者
  updateDocumentCollaborator(documentId: string, collaboratorId: string, collaboratorData: UpdateCollaboratorRequest): Promise<APIResponse<CollaboratorResponse>>;

  // 移除文档协作者
  removeDocumentCollaborator(documentId: string, collaboratorId: string): Promise<APIResponse<null>>;

  // 锁定文档章节
  lockDocumentSection(documentId: string, sectionId: string): Promise<APIResponse<null>>;

  // 解锁文档章节
  unlockDocumentSection(documentId: string, sectionId: string): Promise<APIResponse<null>>;

  // 获取文档活动日志
  getDocumentActivity(documentId: string, params?: QueryParams): Promise<APIResponse<ActivityLog[]>>;
}

// 文档响应
interface DocumentResponse {
  id: string;                    // 文档ID
  project_id: string;            // 项目ID
  title: string;                 // 文档标题
  document_type: DocumentType;   // 文档类型
  file_name?: string;            // 文件名
  file_type: FileType;           // 文件类型
  file_size?: number;            // 文件大小
  status: DocumentStatus;        // 文档状态
  version: number;               // 版本号
  quality_score?: number;        // 质量评分
  completeness_score?: number;   // 完整性评分
  ai_generated: boolean;         // 是否AI生成
  created_at: Date;              // 创建时间
  updated_at: Date;              // 更新时间
  created_by: UserResponse;      // 创建者
  updated_by: UserResponse;      // 更新者
}

// 文档详情响应
interface DocumentDetailResponse extends DocumentResponse {
  template_id?: string;          // 模板ID
  content?: DocumentContent;     // 文档内容
  raw_content?: string;          // 原始内容
  analysis_result?: AnalysisResult; // 分析结果
  collaborators: CollaboratorResponse[]; // 协作者
  comment_count: number;         // 评论数量
  approval_status?: ApprovalStatus; // 审批状态
  word_count?: number;           // 字数
  page_count?: number;           // 页数
  last_accessed_at?: Date;       // 最后访问时间
}

// 创建文档请求
interface CreateDocumentRequest {
  title: string;                 // 文档标题
  document_type: DocumentType;   // 文档类型
  template_id?: string;          // 模板ID
  content?: DocumentContent;     // 文档内容
  variables?: Record<string, any>; // 变量值
  ai_generate?: boolean;         // 是否AI生成
  generation_options?: GenerationOptions; // 生成选项
}

// 更新文档请求
interface UpdateDocumentRequest {
  title?: string;                // 文档标题
  content?: DocumentContent;     // 文档内容
  variables?: Record<string, any>; // 变量值
  status?: DocumentStatus;       // 文档状态
  quality_score?: number;        // 质量评分
  completeness_score?: number;   // 完整性评分
}

// 上传元数据
interface UploadMetadata {
  title?: string;                // 文档标题
  document_type?: DocumentType;  // 文档类型
  description?: string;          // 描述
  tags?: string[];               // 标签
}

// 下载格式
enum DownloadFormat {
  ORIGINAL = 'original',
  PDF = 'pdf',
  DOCX = 'docx',
  HTML = 'html',
  TXT = 'txt'
}

// 下载响应
interface DownloadResponse {
  download_url: string;         // 下载链接
  file_name: string;             // 文件名
  file_size: number;             // 文件大小
  mime_type: string;             // MIME类型
  expires_at: Date;             // 过期时间
}

// 复制文档请求
interface DuplicateDocumentRequest {
  title: string;                 // 新文档标题
  project_id?: string;           // 目标项目ID
  copy_content?: boolean;        // 是否复制内容
  copy_comments?: boolean;       // 是否复制评论
  copy_collaborators?: boolean;  // 是否复制协作者
}

// 文档版本响应
interface DocumentVersionResponse {
  id: string;                    // 版本ID
  document_id: string;           // 文档ID
  version: number;               // 版本号
  title: string;                 // 标题
  description?: string;          // 描述
  content?: DocumentContent;     // 内容
  changes: VersionChange[];      // 变更记录
  created_by: UserResponse;      // 创建者
  created_at: Date;              // 创建时间
  file_size?: number;            // 文件大小
  download_url?: string;         // 下载链接
}

// 版本变更
interface VersionChange {
  type: ChangeType;              // 变更类型
  section_id?: string;           // 章节ID
  description: string;           // 描述
  old_value?: any;               // 旧值
  new_value?: any;               // 新值
}

enum ChangeType {
  CONTENT_ADDED = 'content_added',
  CONTENT_MODIFIED = 'content_modified',
  CONTENT_DELETED = 'content_deleted',
  SECTION_ADDED = 'section_added',
  SECTION_MODIFIED = 'section_modified',
  SECTION_DELETED = 'section_deleted',
  STYLE_CHANGED = 'style_changed'
}

// 创建版本请求
interface CreateVersionRequest {
  description: string;           // 版本描述
  content?: DocumentContent;     // 内容
  changes?: VersionChange[];     // 变更记录
  is_major?: boolean;            // 是否主版本
}

// 评论响应
interface CommentResponse {
  id: string;                    // 评论ID
  user_id: string;               // 用户ID
  user: UserResponse;            // 用户信息
  section_id?: string;           // 章节ID
  content: string;               // 评论内容
  type: CommentType;             // 评论类型
  parent_id?: string;            // 父评论ID
  resolved: boolean;             // 是否已解决
  resolved_by?: UserResponse;    // 解决者
  resolved_at?: Date;            // 解决时间
  replies?: CommentResponse[];   // 回复
  created_at: Date;              // 创建时间
  updated_at: Date;              // 更新时间
}

// 创建评论请求
interface CreateCommentRequest {
  section_id?: string;           // 章节ID
  content: string;               // 评论内容
  type?: CommentType;            // 评论类型
  parent_id?: string;            // 父评论ID
  mentions?: string[];           // 提及用户
}

// 更新评论请求
interface UpdateCommentRequest {
  content: string;               // 评论内容
  resolved?: boolean;            // 是否已解决
}

// 分析选项
interface AnalysisOptions {
  analyze_requirements?: boolean; // 分析需求
  analyze_risks?: boolean;       // 分析风险
  analyze_compliance?: boolean;  // 分析合规性
  check_quality?: boolean;       // 检查质量
  extract_keywords?: boolean;    // 提取关键词
  generate_summary?: boolean;    // 生成摘要
}

// 内容生成请求
interface ContentGenerationRequest {
  section_id?: string;           // 章节ID
  generation_type: GenerationType; // 生成类型
  prompt?: string;               // 提示词
  requirements?: string[];        // 需求
  context?: Record<string, any>; // 上下文
  options?: GenerationOptions;   // 生成选项
}

enum GenerationType {
  SECTION_CONTENT = 'section_content',
  EXECUTIVE_SUMMARY = 'executive_summary',
  TECHNICAL_PROPOSAL = 'technical_proposal',
  MANAGEMENT_APPROACH = 'management_approach',
  PRICING_PROPOSAL = 'pricing_proposal',
  IMPROVEMENT_SUGGESTIONS = 'improvement_suggestions'
}

// 生成选项
interface GenerationOptions {
  tone?: 'formal' | 'persuasive' | 'technical' | 'conversational'; // 语气
  length?: 'short' | 'medium' | 'long'; // 长度
  creativity?: number;            // 创造性 (0-1)
  include_examples?: boolean;    // 包含示例
  focus_areas?: string[];        // 重点关注领域
}

// 内容生成响应
interface ContentGenerationResponse {
  generated_content: any;        // 生成的内容
  quality_score: number;         // 质量分数
  suggestions: string[];         // 改进建议
  processing_time: number;       // 处理时间
  tokens_used: number;           // 使用的令牌数
}

// 质量检查响应
interface QualityCheckResponse {
  overall_score: number;         // 总体评分
  section_scores: SectionQualityScore[]; // 章节评分
  issues: QualityIssue[];        // 质量问题
  recommendations: QualityRecommendation[]; // 改进建议
  compliance_status: ComplianceStatus; // 合规状态
}

// 章节质量评分
interface SectionQualityScore {
  section_id: string;            // 章节ID
  section_title: string;         // 章节标题
  relevance_score: number;       // 相关性评分
  completeness_score: number;    // 完整性评分
  clarity_score: number;         // 清晰度评分
  persuasiveness_score: number;  // 说服力评分
  overall_score: number;         // 总体评分
}

// 质量问题
interface QualityIssue {
  type: QualityIssueType;        // 问题类型
  severity: IssueSeverity;       // 严重程度
  section_id?: string;           // 章节ID
  description: string;           // 描述
  suggestion: string;            // 建议
  auto_fix_available?: boolean;  // 是否可自动修复
}

enum QualityIssueType {
  GRAMMAR_ERROR = 'grammar_error',
  STYLE_INCONSISTENCY = 'style_inconsistency',
  CONTENT_GAP = 'content_gap',
  CLARITY_ISSUE = 'clarity_issue',
  COMPLIANCE_VIOLATION = 'compliance_violation',
  FORMATTING_ERROR = 'formatting_error'
}

// 质量建议
interface QualityRecommendation {
  type: RecommendationType;      // 建议类型
  priority: Priority;            // 优先级
  description: string;           // 描述
  action_items: string[];        // 行动项
  estimated_impact: string;      // 预估影响
}

enum RecommendationType {
  CONTENT_IMPROVEMENT = 'content_improvement',
  STRUCTURE_OPTIMIZATION = 'structure_optimization',
  STYLE_ENHANCEMENT = 'style_enhancement',
  COMPLIANCE_ENSURE = 'compliance_ensure'
}

// 合规状态
interface ComplianceStatus {
  overall_status: ComplianceStatusType; // 总体状态
  checked_requirements: ComplianceRequirement[]; // 检查的要求
  violations: ComplianceViolation[]; // 违规项
  passed_checks: number;         // 通过的检查数
  total_checks: number;          // 总检查数
}

enum ComplianceStatusType {
  COMPLIANT = 'compliant',
  NON_COMPLIANT = 'non_compliant',
  PARTIALLY_COMPLIANT = 'partially_compliant',
  NOT_CHECKED = 'not_checked'
}

// 合规要求
interface ComplianceRequirement {
  id: string;                    // 要求ID
  category: string;              // 类别
  description: string;           // 描述
  mandatory: boolean;            // 是否必需
  status: ComplianceStatusType;  // 状态
  last_checked: Date;            // 最后检查时间
}

// 合规违规
interface ComplianceViolation {
  requirement_id: string;        // 要求ID
  severity: IssueSeverity;       // 严重程度
  description: string;           // 描述
  location?: string;             // 位置
  remediation: string;           // 补救措施
}

// 协作者响应
interface CollaboratorResponse {
  id: string;                    // 协作者ID
  user_id: string;               // 用户ID
  user: UserResponse;            // 用户信息
  permission: CollaborationPermission; // 权限
  joined_at: Date;               // 加入时间
  last_activity_at?: Date;       // 最后活动时间
  is_online: boolean;            // 是否在线
}

// 添加协作者请求
interface AddCollaboratorRequest {
  user_id: string;               // 用户ID
  permission: CollaborationPermission; // 权限
  send_notification?: boolean;   // 发送通知
  message?: string;              // 消息
}

// 更新协作者请求
interface UpdateCollaboratorRequest {
  permission?: CollaborationPermission; // 权限
  is_active?: boolean;           // 是否激活
}

// 审批状态
interface ApprovalStatus {
  current_step?: string;         // 当前步骤
  overall_status: ApprovalOverallStatus; // 总体状态
  completed_steps: ApprovalStep[]; // 完成的步骤
  pending_steps: ApprovalStep[]; // 待处理步骤
  progress_percentage: number;   // 进度百分比
}

enum ApprovalOverallStatus {
  NOT_STARTED = 'not_started',
  IN_PROGRESS = 'in_progress',
  APPROVED = 'approved',
  REJECTED = 'rejected',
  CANCELLED = 'cancelled'
}

// 审批步骤
interface ApprovalStep {
  id: string;                    // 步骤ID
  name: string;                  // 步骤名称
  description: string;           // 描述
  assignee_id?: string;          // 指派人ID
  assignee?: UserResponse;       // 指派人
  status: ApprovalStepStatus;    // 步骤状态
  decision?: ApprovalDecision;   // 决定
  comments?: string;             // 评论
  completed_at?: Date;           // 完成时间
  due_date?: Date;               // 截止时间
}

enum ApprovalStepStatus {
  PENDING = 'pending',
  IN_REVIEW = 'in_review',
  COMPLETED = 'completed',
  SKIPPED = 'skipped'
}
```
