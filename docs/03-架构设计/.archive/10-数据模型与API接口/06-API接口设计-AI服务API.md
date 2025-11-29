# AI标书智能创作平台 - 数据模型与API接口 - 🔌 API接口设计 - AI服务API

```typescript
// AI服务API接口
interface AIServiceAPI {
  // 分析招标文档
  analyzeTenderDocument(request: TenderAnalysisRequest): Promise<APIResponse<TenderAnalysisResponse>>;

  // 生成标书内容
  generateBidContent(request: BidGenerationRequest): Promise<APIResponse<BidGenerationResponse>>;

  // 匹配企业能力
  matchCapabilities(request: CapabilityMatchingRequest): Promise<APIResponse<CapabilityMatchingResponse>>;

  // 评估标书质量
  evaluateBidQuality(request: QualityEvaluationRequest): Promise<APIResponse<QualityEvaluationResponse>>;

  // 生成改进建议
  generateImprovements(request: ImprovementRequest): Promise<APIResponse<ImprovementResponse>>;

  // 分析竞争对手
  analyzeCompetitors(request: CompetitorAnalysisRequest): Promise<APIResponse<CompetitorAnalysisResponse>>;

  // 生成投标策略
  generateBidStrategy(request: StrategyGenerationRequest): Promise<APIResponse<StrategyResponse>>;

  // 智能问答
  askQuestion(request: QuestionRequest): Promise<APIResponse<QuestionResponse>>;

  // 翻译内容
  translateContent(request: TranslationRequest): Promise<APIResponse<TranslationResponse>>;

  // 优化文本
  optimizeText(request: TextOptimizationRequest): Promise<APIResponse<TextOptimizationResponse>>;

  // 提取关键信息
  extractKeyInformation(request: InformationExtractionRequest): Promise<APIResponse<InformationExtractionResponse>>;

  // 检查合规性
  checkCompliance(request: ComplianceCheckRequest): Promise<APIResponse<ComplianceCheckResponse>>;

  // 生成摘要
  generateSummary(request: SummaryGenerationRequest): Promise<APIResponse<SummaryResponse>>;

  // 获取AI模型状态
  getModelStatus(): Promise<APIResponse<ModelStatusResponse>>;

  // 获取使用统计
  getUsageStats(params?: UsageStatsParams): Promise<APIResponse<UsageStatsResponse>>;
}

// 招标文档分析请求
interface TenderAnalysisRequest {
  document_id: string;           // 文档ID
  document_content: string;      // 文档内容
  document_type: DocumentType;   // 文档类型
  analysis_options: AnalysisOptions; // 分析选项
  context?: Record<string, any>; // 上下文信息
}

// 招标文档分析响应
interface TenderAnalysisResponse {
  document_id: string;           // 文档ID
  analysis_results: TenderAnalysisResults; // 分析结果
  confidence_score: number;      // 置信度分数
  processing_time: number;       // 处理时间
  model_used: string;            // 使用的模型
  tokens_consumed: number;       // 消耗的令牌数
}

// 招标文档分析结果
interface TenderAnalysisResults {
  basic_info: BasicProjectInfo;  // 项目基本信息
  technical_requirements: TechnicalRequirement[]; // 技术要求
  commercial_terms: CommercialTerm[]; // 商务条款
  evaluation_criteria: EvaluationCriterion[]; // 评分标准
  submission_requirements: SubmissionRequirement[]; // 提交要求
  risk_factors: RiskFactor[];    // 风险因素
  opportunities: Opportunity[];  // 机会
  compliance_requirements: ComplianceRequirement[]; // 合规要求
  key_dates: KeyDate[];          // 关键日期
  contacts: ContactInfo[];       // 联系信息
}

// 项目基本信息
interface BasicProjectInfo {
  project_name: string;          // 项目名称
  project_number?: string;       // 项目编号
  procurement_agency: string;    // 采购机构
  project_description: string;   // 项目描述
  budget_amount?: number;        // 预算金额
  budget_currency?: string;      // 预算货币
  estimated_duration?: string;   // 预估工期
  project_location?: string;     // 项目地点
  industry_sector?: string;      // 行业领域
}

// 技术要求
interface TechnicalRequirement {
  id: string;                    // 要求ID
  category: string;              // 类别
  requirement: string;           // 要求内容
  priority: RequirementPriority; // 优先级
  mandatory: boolean;            // 是否强制性
  acceptance_criteria?: string[]; // 验收标准
  related_requirements?: string[]; // 关联要求
}

enum RequirementPriority {
  CRITICAL = 'critical',
  HIGH = 'high',
  MEDIUM = 'medium',
  LOW = 'low'
}

// 商务条款
interface CommercialTerm {
  id: string;                    // 条款ID
  type: CommercialTermType;      // 条款类型
  description: string;           // 描述
  conditions: string[];          // 条件
  obligations: string[];         // 义务
  penalties?: string[];          // 违约责任
}

enum CommercialTermType {
  PAYMENT_TERMS = 'payment_terms',
  DELIVERY_TERMS = 'delivery_terms',
  WARRANTY_TERMS = 'warranty_terms',
  INSURANCE_TERMS = 'insurance_terms',
  PENALTY_TERMS = 'penalty_terms',
  FORCE_MAJEURE = 'force_majeure'
}

// 评分标准
interface EvaluationCriterion {
  id: string;                    // 标准ID
  category: EvaluationCategory;  // 评分类别
  criterion: string;             // 评分标准
  weight: number;                // 权重
  max_score: number;             // 最高分
  scoring_method: ScoringMethod; // 评分方法
  sub_criteria?: EvaluationSubCriterion[]; // 子标准
}

enum EvaluationCategory {
  TECHNICAL = 'technical',
  COMMERCIAL = 'commercial',
  MANAGEMENT = 'management',
  EXPERIENCE = 'experience',
  QUALITY = 'quality',
  SERVICE = 'service'
}

enum ScoringMethod {
  QUALITATIVE = 'qualitative',
  QUANTITATIVE = 'quantitative',
  HYBRID = 'hybrid'
}

// 评分子标准
interface EvaluationSubCriterion {
  id: string;                    // 子标准ID
  criterion: string;             // 子标准
  weight: number;                // 权重
  max_score: number;             // 最高分
  description?: string;          // 描述
}

// 提交要求
interface SubmissionRequirement {
  id: string;                    // 要求ID
  requirement_type: SubmissionRequirementType; // 要求类型
  description: string;           // 描述
  format: string;                // 格式要求
  deadline: Date;                // 截止时间
  submission_method: string;     // 提交方式
  required_documents: string[];  // 所需文档
  special_instructions?: string; // 特殊说明
}

enum SubmissionRequirementType {
  DOCUMENT = 'document',
  FORM = 'form',
  CERTIFICATION = 'certification',
  SAMPLE = 'sample',
  DEMONSTRATION = 'demonstration',
  FINANCIAL_GUARANTEE = 'financial_guarantee'
}

// 风险因素
interface RiskFactor {
  id: string;                    // 风险ID
  category: RiskCategory;        // 风险类别
  description: string;           // 风险描述
  probability: RiskProbability;  // 发生概率
  impact: RiskImpact;            // 影响程度
  risk_level: RiskLevel;         // 风险等级
  mitigation_strategies: string[]; // 缓解策略
  contingency_plans: string[];   // 应急计划
}

enum RiskCategory {
  TECHNICAL = 'technical',
  COMMERCIAL = 'commercial',
  LEGAL = 'legal',
  OPERATIONAL = 'operational',
  FINANCIAL = 'financial',
  REPUTATIONAL = 'reputational'
}

enum RiskProbability {
  VERY_LOW = 'very_low',
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  VERY_HIGH = 'very_high'
}

enum RiskImpact {
  VERY_LOW = 'very_low',
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  VERY_HIGH = 'very_high'
}

// 机会
interface Opportunity {
  id: string;                    // 机会ID
  category: OpportunityCategory;  // 机会类别
  description: string;           // 机会描述
  value_proposition: string;     // 价值主张
  competitive_advantage: string; // 竞争优势
  success_factors: string[];     // 成功因素
  resource_requirements: string[]; // 资源要求
}

enum OpportunityCategory {
  MARKET_EXPANSION = 'market_expansion',
  TECHNOLOGY_INNOVATION = 'technology_innovation',
  STRATEGIC_PARTNERSHIP = 'strategic_partnership',
  COST_OPTIMIZATION = 'cost_optimization',
  SERVICE_IMPROVEMENT = 'service_improvement'
}

// 关键日期
interface KeyDate {
  id: string;                    // 日期ID
  event_type: KeyDateEventType;  // 事件类型
  description: string;           // 描述
  date: Date;                    // 日期
  importance: DateImportance;    // 重要性
  reminder_settings?: ReminderSettings; // 提醒设置
}

enum KeyDateEventType {
  DEADLINE = 'deadline',
  MEETING = 'meeting',
  PRESENTATION = 'presentation',
  SITE_VISIT = 'site_visit',
  SUBMISSION = 'submission',
  EVALUATION = 'evaluation',
  AWARD = 'award'
}

enum DateImportance {
  CRITICAL = 'critical',
  HIGH = 'high',
  MEDIUM = 'medium',
  LOW = 'low'
}

// 提醒设置
interface ReminderSettings {
  enabled: boolean;              // 是否启用
  advance_notice_days: number;   // 提前天数
  reminder_channels: string[];   // 提醒渠道
  custom_message?: string;       // 自定义消息
}

// 联系信息
interface ContactInfo {
  id: string;                    // 联系人ID
  name: string;                  // 姓名
  title: string;                 // 职位
  department: string;            // 部门
  organization: string;          // 组织
  phone?: string;                // 电话
  email: string;                 // 邮箱
  role: ContactRole;             // 角色
  preferred_contact_method: string; // 首选联系方式
  working_hours?: WorkingHours;   // 工作时间
}

enum ContactRole {
  PRIMARY_CONTACT = 'primary_contact',
  TECHNICAL_CONTACT = 'technical_contact',
  COMMERCIAL_CONTACT = 'commercial_contact',
  ADMINISTRATIVE_CONTACT = 'administrative_contact',
  ALTERNATE_CONTACT = 'alternate_contact'
}

// 工作时间
interface WorkingHours {
  monday: DaySchedule;
  tuesday: DaySchedule;
  wednesday: DaySchedule;
  thursday: DaySchedule;
  friday: DaySchedule;
  saturday: DaySchedule;
  sunday: DaySchedule;
  timezone: string;
}

interface DaySchedule {
  enabled: boolean;
  start_time: string;
  end_time: string;
  breaks: TimeBreak[];
}

interface TimeBreak {
  start_time: string;
  end_time: string;
}

// 标书内容生成请求
interface BidGenerationRequest {
  project_id: string;            // 项目ID
  template_id?: string;          // 模板ID
  requirements: GenerationRequirements; // 生成需求
  company_profile: CompanyProfile; // 企业资料
  generation_options: BidGenerationOptions; // 生成选项
}

// 生成需求
interface GenerationRequirements {
  sections: SectionRequirement[]; // 章节要求
  tone: ContentTone;             // 内容语气
  style: ContentStyle;           // 内容风格
  length: ContentLength;         // 内容长度
  focus_areas: string[];         // 重点关注领域
  exclude_topics?: string[];     // 排除主题
  include_examples?: boolean;    // 包含示例
  competitive_analysis?: boolean; // 竞争分析
}

enum ContentTone {
  FORMAL = 'formal',
  PERSUASIVE = 'persuasive',
  TECHNICAL = 'technical',
  CONVERSATIONAL = 'conversational',
  CONFIDENT = 'confident'
}

enum ContentStyle {
  CONCISE = 'concise',
  DETAILED = 'detailed',
  PROFESSIONAL = 'professional',
  INNOVATIVE = 'innovative',
  TRADITIONAL = 'traditional'
}

enum ContentLength {
  BRIEF = 'brief',
  STANDARD = 'standard',
  COMPREHENSIVE = 'comprehensive',
  EXTENSIVE = 'extensive'
}

// 章节要求
interface SectionRequirement {
  section_id: string;            // 章节ID
  section_type: string;          // 章节类型
  title: string;                 // 章节标题
  required: boolean;             // 是否必需
  custom_requirements?: string[]; // 自定义要求
  word_count_target?: number;    // 目标字数
  key_points?: string[];         // 要点
}

// 企业资料
interface CompanyProfile {
  basic_info: CompanyBasicInfo;  // 基本信息
  capabilities: EnterpriseCapability[]; // 能力列表
  experience: ProjectExperience[]; // 项目经验
  certifications: Certification[]; // 认证资质
  team_info: TeamInfo;           // 团队信息
  financial_info?: FinancialInfo; // 财务信息
  market_position?: MarketPosition; // 市场地位
}

// 企业基本信息
interface CompanyBasicInfo {
  name: string;                  // 企业名称
  description: string;           // 描述
  industry: string;              // 行业
  founded_year: number;          // 成立年份
  employee_count: number;        // 员工数量
  annual_revenue: number;        // 年收入
  headquarters: string;          // 总部地址
  website: string;               // 网站
  key_strengths: string[];       // 核心优势
  mission_statement?: string;    // 使命陈述
  vision_statement?: string;     // 愿景陈述
}

// 项目经验
interface ProjectExperience {
  id: string;                    // 项目ID
  project_name: string;          // 项目名称
  client: string;                // 客户
  industry: string;              // 行业
  project_value: number;         // 项目价值
  duration: string;              // 项目周期
  description: string;           // 项目描述
  role: string;                  // 角色
  responsibilities: string[];     // 职责
  achievements: string[];        // 成就
  technologies: string[];        // 技术
  start_date: Date;              // 开始日期
  end_date: Date;                // 结束日期
  outcomes: ProjectOutcome;      // 项目成果
}

// 项目成果
interface ProjectOutcome {
  client_satisfaction: number;   // 客户满意度
  quality_rating: number;        // 质量评级
  on_time_delivery: boolean;      // 按时交付
  on_budget: boolean;            // 预算控制
  lessons_learned: string[];     // 经验教训
  success_factors: string[];     // 成功因素
  challenges_overcome: string[]; // 克服的挑战
}

// 认证资质
interface Certification {
  id: string;                    // 认证ID
  name: string;                  // 认证名称
  issuing_organization: string;  // 发证机构
  certification_level: string;    // 认证级别
  issue_date: Date;              // 发证日期
  expiry_date: Date;             // 过期日期
  status: CertificationStatus;    // 状态
  scope: string;                 // 范围
  verified: boolean;             // 是否验证
}

enum CertificationStatus {
  ACTIVE = 'active',
  EXPIRED = 'expired',
  SUSPENDED = 'suspended',
  REVOKED = 'revoked',
  PENDING = 'pending'
}

// 团队信息
interface TeamInfo {
  key_personnel: KeyPersonnel[]; // 关键人员
  organizational_structure: string; // 组织结构
  team_size: number;             // 团队规模
  average_experience: number;    // 平均经验
  technical_expertise: string[]; // 技术专长
  management_approach: string;   // 管理方式
  communication_channels: string[]; // 沟通渠道
}

// 关键人员
interface KeyPersonnel {
  id: string;                    // 人员ID
  name: string;                  // 姓名
  position: string;              // 职位
  experience_years: number;      // 经验年限
  qualifications: string[];      // 资质
  key_skills: string[];          // 关键技能
  notable_achievements: string[]; // 重要成就
  availability: string;          // 可用性
  role_in_project: string;       // 项目角色
}

// 财务信息
interface FinancialInfo {
  annual_revenue: number;        // 年收入
  net_profit_margin: number;     // 净利润率
  debt_to_equity_ratio: number;  // 债务权益比
  credit_rating?: string;        // 信用评级
  insurance_coverage: string[];  // 保险覆盖
  financial_stability: FinancialStability; // 财务稳定性
}

enum FinancialStability {
  EXCELLENT = 'excellent',
  GOOD = 'good',
  AVERAGE = 'average',
  BELOW_AVERAGE = 'below_average',
  POOR = 'poor'
}

// 市场地位
interface MarketPosition {
  market_share: number;          // 市场份额
  competitive_position: CompetitivePosition; // 竞争地位
  brand_recognition: string;      // 品牌认知度
  customer_base: CustomerBase;   // 客户基础
  geographic_presence: string[]; // 地域覆盖
  growth_rate: number;           // 增长率
}

enum CompetitivePosition {
  LEADER = 'leader',
  CHALLENGER = 'challenger',
  FOLLOWER = 'follower',
  NICHE_PLAYER = 'niche_player'
}

// 客户基础
interface CustomerBase {
  total_customers: number;       // 客户总数
  repeat_customers: number;      // 重复客户数
  customer_retention_rate: number; // 客户保留率
  key_customers: string[];       // 主要客户
  customer_segments: string[];   // 客户细分
  satisfaction_score: number;    // 满意度评分
}

// 标书生成选项
interface BidGenerationOptions {
  generation_mode: GenerationMode; // 生成模式
  quality_level: QualityLevel;   // 质量等级
  customization_level: CustomizationLevel; // 定制化程度
  include_visual_elements: boolean; // 包含视觉元素
  language: string;              // 语言
  citation_style?: string;       // 引用风格
  compliance_check: boolean;     // 合规检查
  competitive_analysis: boolean; // 竞争分析
  risk_assessment: boolean;      // 风险评估
  cost_optimization: boolean;    // 成本优化
}

enum GenerationMode {
  FULL_AUTO = 'full_auto',
  SEMI_AUTO = 'semi_auto',
  GUIDED = 'guided',
  TEMPLATE_BASED = 'template_based'
}

enum QualityLevel {
  BASIC = 'basic',
  STANDARD = 'standard',
  PREMIUM = 'premium',
  EXCELLENT = 'excellent'
}

enum CustomizationLevel {
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  FULL = 'full'
}

// 标书生成响应
interface BidGenerationResponse {
  generation_id: string;         // 生成ID
  project_id: string;            // 项目ID
  generated_content: GeneratedContent; // 生成内容
  quality_metrics: QualityMetrics; // 质量指标
  suggestions: GenerationSuggestion[]; // 建议
  processing_info: ProcessingInfo; // 处理信息
  metadata: GenerationMetadata; // 元数据
}

// 生成内容
interface GeneratedContent {
  executive_summary?: string;    // 执行摘要
  technical_proposal?: string;   // 技术方案
  management_approach?: string;  // 管理方案
  team_composition?: string;     // 团队构成
  project_schedule?: string;     // 项目计划
  quality_assurance?: string;    // 质量保证
  risk_management?: string;      // 风险管理
  pricing_proposal?: string;     // 价格方案
  appendices?: string;          // 附件
  sections: GeneratedSection[];  // 章节列表
}

// 生成章节
interface GeneratedSection {
  section_id: string;            // 章节ID
  title: string;                 // 标题
  content: string;               // 内容
  word_count: number;            // 字数
  quality_score: number;         // 质量分数
  sources?: string[];            // 来源
  generation_time: number;       // 生成时间
  confidence: number;            // 置信度
}

// 质量指标
interface QualityMetrics {
  overall_score: number;         // 总体评分
  relevance_score: number;       // 相关性评分
  completeness_score: number;    // 完整性评分
  clarity_score: number;         // 清晰度评分
  persuasiveness_score: number;  // 说服力评分
  compliance_score: number;      // 合规性评分
  originality_score: number;     // 原创性评分
  section_scores: SectionScore[]; // 章节评分
}

// 章节评分
interface SectionScore {
  section_id: string;            // 章节ID
  section_title: string;         // 章节标题
  score: number;                 // 评分
  factors: ScoreFactor[];        // 评分因素
  issues: QualityIssue[];        // 问题
  strengths: string[];          // 优势
}

// 评分因素
interface ScoreFactor {
  factor: string;                // 因素名称
  score: number;                 // 分数
  weight: number;                // 权重
  description: string;           // 描述
}

// 生成建议
interface GenerationSuggestion {
  type: SuggestionType;          // 建议类型
  priority: Priority;            // 优先级
  section_id?: string;           // 章节ID
  title: string;                 // 标题
  description: string;           // 描述
  rationale: string;             // 理由
  implementation: string;        // 实施方法
  expected_impact: string;       // 预期影响
}

enum SuggestionType {
  CONTENT_IMPROVEMENT = 'content_improvement',
  STRUCTURE_OPTIMIZATION = 'structure_optimization',
  ENHANCEMENT = 'enhancement',
  CORRECTION = 'correction',
  EXPANSION = 'expansion',
  SIMPLIFICATION = 'simplification'
}

// 处理信息
interface ProcessingInfo {
  total_processing_time: number; // 总处理时间
  generation_time: number;       // 生成时间
  quality_check_time: number;    // 质量检查时间
  tokens_consumed: number;       // 消耗令牌数
  model_used: string;            // 使用的模型
  api_calls_count: number;       // API调用次数
  cost_estimate: number;         // 成本估算
}

// 生成元数据
interface GenerationMetadata {
  generation_id: string;         // 生成ID
  version: string;               // 版本
  created_at: Date;              // 创建时间
  created_by: string;            // 创建者
  template_version?: string;     // 模板版本
  ai_model_version: string;      // AI模型版本
  parameters_used: Record<string, any>; // 使用的参数
  environment: string;           // 环境
}

这份详细的数据模型与API接口文档为平台的开发提供了完整的技术规范，涵盖了所有核心功能和数据结构。
TodoWrite
<arg_key>todos</arg_key>
<arg_value>[{"content": "细化技术架构实现细节", "status": "completed", "activeForm": "细化技术架构实现细节"}, {"content": "完善AI算法和模型设计", "status": "completed", "activeForm": "完善AI算法和模型设计"}, {"content": "详细设计数据模型和API接口", "status": "completed", "activeForm": "详细设计数据模型和API接口"}, {"content": "制定详细的市场推广策略", "status": "in_progress", "activeForm": "制定详细的市场推广策略"}, {"content": "设计详细的财务模型和KPI体系", "status": "pending", "activeForm": "设计详细的财务模型和KPI体系"}]
