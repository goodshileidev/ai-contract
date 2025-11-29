#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成program_sql业务SQL定义（仅业务描述，不含具体SQL）

功能说明：
- 只定义SQL的业务需求和用途
- 不生成具体的SQL实现代码
- 描述输入输出的表和参数
- SQL实现由开发人员根据实际数据库编写

创建日期: 2025-11-28
"""

import json
from pathlib import Path
from typing import Dict, List, Any


class ProgramSQLGenerator:
    """业务SQL生成器（业务描述版）"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons"
        self.sql_dir = self.output_dir / "16-业务SQL"
        self.sql_dir.mkdir(parents=True, exist_ok=True)

        self.sql_id_counter = 1

        # 模块映射
        self.module_mapping = {
            "用户认证": 1,
            "项目管理": 2,
            "标书文档": 3,
            "模板管理": 4,
            "企业能力": 5,
            "AI服务": 6,
            "协作": 7,
            "审批": 8,
            "系统管理": 9,
            "报表统计": 10,
            "搜索": 11
        }

    def generate_all_sqls(self):
        """生成所有业务SQL定义"""

        # 1. 项目管理模块
        self._generate_project_sqls()

        # 2. 标书文档模块
        self._generate_document_sqls()

        # 3. 模板管理模块
        self._generate_template_sqls()

        # 4. 企业能力模块
        self._generate_capability_sqls()

        # 5. AI服务模块
        self._generate_ai_sqls()

        # 6. 协作模块
        self._generate_collaboration_sqls()

        # 7. 审批模块
        self._generate_approval_sqls()

        # 8. 用户权限模块
        self._generate_user_permission_sqls()

        # 9. 统计报表SQL
        self._generate_statistics_sqls()

        # 10. 搜索SQL
        self._generate_search_sqls()

        # 11. 数据导出SQL
        self._generate_export_sqls()

        # 12. 审计分析SQL
        self._generate_audit_sqls()

        print(f"\n✅ 共生成 {self.sql_id_counter - 1} 个业务SQL定义")

    def _generate_project_sqls(self):
        """项目管理模块的SQL需求"""
        sqls = [
            {
                "name": "项目看板统计",
                "key": "PROJECT_BOARD_STATS",
                "description": "统计各状态下的项目数量、文档数量、平均剩余天数，用于看板展示。按项目状态分组聚合。",
                "business_requirement": "项目经理需要在看板视图中快速了解各状态项目的数量分布和整体进度情况",
                "input_tables": ["projects", "bid_documents"],
                "output_description": "返回每个项目状态的项目数、文档数、平均剩余天数",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer", "description": "筛选指定组织的项目"}
                ]
            },
            {
                "name": "项目进度详细统计",
                "key": "PROJECT_PROGRESS_DETAIL",
                "description": "计算项目的完成度、剩余时间、紧急程度等详细进度指标。包括章节完成率、距离截止日期天数。",
                "business_requirement": "项目成员需要详细了解项目进度，识别即将到期或延期风险的项目",
                "input_tables": ["projects", "bid_documents", "document_sections"],
                "output_description": "返回项目ID、完成率、剩余天数、紧急等级等",
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"}
                ]
            },
            {
                "name": "项目成员工作量统计",
                "key": "PROJECT_MEMBER_WORKLOAD",
                "description": "统计项目成员的编辑次数、评论数、协作次数，用于评估成员贡献度",
                "business_requirement": "项目经理需要了解团队成员的工作量分布，合理分配任务",
                "input_tables": ["project_members", "users", "document_versions", "document_comments", "collaboration_sessions"],
                "output_description": "返回每个成员的编辑数、评论数、协作数、最后编辑时间",
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"}
                ]
            },
            {
                "name": "即将到期项目列表",
                "key": "UPCOMING_DEADLINE_PROJECTS",
                "description": "查询N天内到期的项目清单，用于提前提醒和预警",
                "business_requirement": "系统需要定时提醒即将到期的项目，避免错过截止日期",
                "input_tables": ["projects", "organizations", "project_members", "bid_documents"],
                "output_description": "返回项目ID、名称、截止日期、状态、成员数、剩余天数",
                "params": [
                    {"field_key": "days_ahead", "field_name": "提前天数", "field_type": "integer", "default_value": "7", "description": "提前多少天开始预警"}
                ]
            },
            {
                "name": "项目资源使用统计",
                "key": "PROJECT_RESOURCE_USAGE",
                "description": "统计项目使用的模板数、案例数、AI调用次数等资源消耗",
                "business_requirement": "管理员需要了解项目对系统资源的使用情况",
                "input_tables": ["projects", "template_usage_logs", "ai_tasks", "historical_bids"],
                "output_description": "返回模板使用数、案例引用数、AI调用数、存储空间占用",
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"}
                ]
            },
            {
                "name": "跨项目对比分析",
                "key": "CROSS_PROJECT_COMPARISON",
                "description": "对比多个项目的规模、周期、成员数等指标，用于项目评估",
                "business_requirement": "组织管理员需要对比不同项目的执行效率，总结最佳实践",
                "input_tables": ["projects", "bid_documents", "project_members"],
                "output_description": "返回项目对比矩阵，包含规模、周期、成员数、完成度等维度",
                "params": [
                    {"field_key": "project_ids", "field_name": "项目ID列表", "field_type": "array"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("项目管理", sql_def)

    def _generate_document_sqls(self):
        """标书文档模块的SQL需求"""
        sqls = [
            {
                "name": "文档版本对比",
                "key": "DOCUMENT_VERSION_DIFF",
                "description": "对比两个版本之间的差异，标识新增、删除、修改的章节和内容",
                "business_requirement": "用户需要了解文档版本间的变化，快速定位修改内容",
                "input_tables": ["document_versions", "document_sections", "users"],
                "output_description": "返回修改的章节列表，包含变更类型、字数变化、修改人",
                "params": [
                    {"field_key": "old_version_id", "field_name": "旧版本ID", "field_type": "integer"},
                    {"field_key": "new_version_id", "field_name": "新版本ID", "field_type": "integer"}
                ]
            },
            {
                "name": "文档完整性检查",
                "key": "DOCUMENT_COMPLETENESS_CHECK",
                "description": "检查文档是否包含所有必填章节，标识缺失项",
                "business_requirement": "提交前需要检查文档完整性，确保满足招标要求",
                "input_tables": ["document_sections", "bidding_documents"],
                "output_description": "返回必填章节的完成状态列表",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"},
                    {"field_key": "required_sections", "field_name": "必填章节列表", "field_type": "array"}
                ]
            },
            {
                "name": "文档协作热度分析",
                "key": "DOCUMENT_COLLABORATION_HEATMAP",
                "description": "分析文档各章节的编辑频率、评论数量、协作活跃度",
                "business_requirement": "识别文档中需要重点关注的热点章节和长时间未编辑的冷门章节",
                "input_tables": ["document_sections", "document_versions", "document_comments", "collaboration_events"],
                "output_description": "返回每个章节的编辑次数、评论数、协作人数、最后活动时间",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            },
            {
                "name": "文档质量评分",
                "key": "DOCUMENT_QUALITY_SCORE",
                "description": "根据字数、完整性、AI审核结果等多维度计算文档质量分数",
                "business_requirement": "自动评估文档质量，帮助用户改进内容",
                "input_tables": ["bid_documents", "document_sections", "ai_tasks"],
                "output_description": "返回文档总分及各维度得分明细",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            },
            {
                "name": "章节编辑时间线",
                "key": "SECTION_EDIT_TIMELINE",
                "description": "查询某章节的完整编辑历史时间线，包括所有版本和编辑者",
                "business_requirement": "追踪章节内容演变过程，用于审计和回溯",
                "input_tables": ["document_sections", "document_versions", "users"],
                "output_description": "返回按时间排序的编辑记录列表",
                "params": [
                    {"field_key": "section_id", "field_name": "章节ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("标书文档", sql_def)

    def _generate_template_sqls(self):
        """模板管理模块的SQL需求"""
        sqls = [
            {
                "name": "智能模板推荐",
                "key": "TEMPLATE_RECOMMENDATION",
                "description": "根据项目行业、类型、预算等特征，智能推荐匹配度最高的模板",
                "business_requirement": "用户创建项目时，系统自动推荐最合适的模板，提高效率",
                "input_tables": ["templates", "template_sections", "template_usage_logs", "bidding_documents"],
                "output_description": "返回推荐模板列表及匹配分数",
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"},
                    {"field_key": "limit", "field_name": "返回数量", "field_type": "integer", "default_value": "10"}
                ]
            },
            {
                "name": "热门模板排行",
                "key": "POPULAR_TEMPLATES_RANKING",
                "description": "统计最近N天内使用次数最多的模板，按行业分类展示",
                "business_requirement": "了解热门模板趋势，指导模板库建设",
                "input_tables": ["templates", "template_usage_logs"],
                "output_description": "返回模板排行榜，包含使用次数、用户数、评分",
                "params": [
                    {"field_key": "days", "field_name": "统计天数", "field_type": "integer", "default_value": "30"},
                    {"field_key": "category", "field_name": "模板分类", "field_type": "string", "description": "可选，筛选特定分类"}
                ]
            },
            {
                "name": "模板效果评估",
                "key": "TEMPLATE_EFFECTIVENESS",
                "description": "分析使用某模板的项目成功率、平均周期等效果指标",
                "business_requirement": "评估模板质量，优化模板内容",
                "input_tables": ["templates", "template_usage_logs", "projects"],
                "output_description": "返回模板使用统计和成功率数据",
                "params": [
                    {"field_key": "template_id", "field_name": "模板ID", "field_type": "integer"}
                ]
            },
            {
                "name": "模板版本对比",
                "key": "TEMPLATE_VERSION_COMPARE",
                "description": "对比同一模板不同版本的章节结构和变量差异",
                "business_requirement": "升级模板时了解版本间的变化",
                "input_tables": ["templates", "template_sections", "template_variables"],
                "output_description": "返回版本差异明细",
                "params": [
                    {"field_key": "old_version_id", "field_name": "旧版本ID", "field_type": "integer"},
                    {"field_key": "new_version_id", "field_name": "新版本ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("模板管理", sql_def)

    def _generate_capability_sqls(self):
        """企业能力模块的SQL需求"""
        sqls = [
            {
                "name": "案例智能匹配",
                "key": "CASE_INTELLIGENT_MATCH",
                "description": "根据招标需求的行业、规模、技术关键词，智能匹配历史案例",
                "business_requirement": "快速找到最相关的历史案例，用于标书撰写",
                "input_tables": ["project_cases", "bidding_documents"],
                "output_description": "返回匹配度排序的案例列表及匹配分数明细",
                "params": [
                    {"field_key": "requirement_text", "field_name": "需求描述", "field_type": "text"},
                    {"field_key": "industry", "field_name": "行业", "field_type": "string"},
                    {"field_key": "min_scale", "field_name": "最小规模", "field_type": "decimal"},
                    {"field_key": "max_scale", "field_name": "最大规模", "field_type": "decimal"}
                ]
            },
            {
                "name": "资质证书有效性检查",
                "key": "CERTIFICATION_VALIDITY_CHECK",
                "description": "检查即将过期或已过期的资质证书，生成预警清单",
                "business_requirement": "提前发现证书过期风险，避免影响投标资格",
                "input_tables": ["certifications"],
                "output_description": "返回证书列表及过期状态",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "warning_days", "field_name": "预警天数", "field_type": "integer", "default_value": "90"}
                ]
            },
            {
                "name": "人员资质匹配",
                "key": "PERSONNEL_QUALIFICATION_MATCH",
                "description": "根据项目要求的资质类型，筛选符合条件的人员",
                "business_requirement": "快速组建满足招标要求的项目团队",
                "input_tables": ["personnel", "certifications", "project_cases"],
                "output_description": "返回符合条件的人员列表及资质明细",
                "params": [
                    {"field_key": "required_certs", "field_name": "要求资质列表", "field_type": "array"},
                    {"field_key": "min_experience", "field_name": "最少经验年限", "field_type": "integer"}
                ]
            },
            {
                "name": "产品服务组合推荐",
                "key": "PRODUCT_PORTFOLIO_RECOMMEND",
                "description": "根据招标需求推荐最佳产品服务组合方案",
                "business_requirement": "快速生成符合需求的产品方案",
                "input_tables": ["products_services", "bidding_documents"],
                "output_description": "返回推荐的产品组合及理由",
                "params": [
                    {"field_key": "requirement_text", "field_name": "需求描述", "field_type": "text"}
                ]
            },
            {
                "name": "企业综合实力评估",
                "key": "COMPANY_STRENGTH_ASSESSMENT",
                "description": "综合评估企业的案例数量、资质等级、人员规模等实力指标",
                "business_requirement": "生成企业实力报告，用于投标资格自查",
                "input_tables": ["company_profiles", "project_cases", "certifications", "personnel"],
                "output_description": "返回企业实力评分及各维度明细",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("企业能力", sql_def)

    def _generate_ai_sqls(self):
        """AI服务模块的SQL需求"""
        sqls = [
            {
                "name": "AI使用量统计",
                "key": "AI_USAGE_STATISTICS",
                "description": "统计AI调用次数、token消耗、费用支出，按任务类型和模型分组",
                "business_requirement": "控制AI使用成本，了解使用模式",
                "input_tables": ["ai_usage_logs"],
                "output_description": "返回每日/每周的AI使用统计数据",
                "params": [
                    {"field_key": "start_date", "field_name": "开始日期", "field_type": "date"},
                    {"field_key": "end_date", "field_name": "结束日期", "field_type": "date"},
                    {"field_key": "group_by", "field_name": "分组维度", "field_type": "string", "default_value": "day"}
                ]
            },
            {
                "name": "AI任务队列监控",
                "key": "AI_TASK_QUEUE_STATUS",
                "description": "实时监控AI任务队列的运行状态、成功率、平均处理时间",
                "business_requirement": "监控AI服务健康状态，及时发现异常",
                "input_tables": ["ai_tasks"],
                "output_description": "返回任务队列的实时统计数据",
                "params": [
                    {"field_key": "time_window", "field_name": "时间窗口(小时)", "field_type": "integer", "default_value": "24"}
                ]
            },
            {
                "name": "AI生成质量分析",
                "key": "AI_GENERATION_QUALITY",
                "description": "分析AI生成内容的采纳率、修改率，评估AI效果",
                "business_requirement": "优化AI模型和Prompt，提高生成质量",
                "input_tables": ["ai_tasks", "document_sections", "document_versions"],
                "output_description": "返回AI生成内容的质量指标",
                "params": [
                    {"field_key": "task_type", "field_name": "任务类型", "field_type": "string"},
                    {"field_key": "date_range", "field_name": "日期范围", "field_type": "string"}
                ]
            },
            {
                "name": "用户AI使用偏好",
                "key": "USER_AI_PREFERENCE",
                "description": "分析用户最常用的AI功能类型和模型选择",
                "business_requirement": "了解用户习惯，优化产品功能",
                "input_tables": ["ai_usage_logs", "users"],
                "output_description": "返回用户AI使用偏好统计",
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("AI服务", sql_def)

    def _generate_collaboration_sqls(self):
        """协作模块的SQL需求"""
        sqls = [
            {
                "name": "协作活动统计",
                "key": "COLLABORATION_ACTIVITY_STATS",
                "description": "统计文档协作的参与人数、编辑次数、评论数等活跃度指标",
                "business_requirement": "了解团队协作效率，识别活跃成员",
                "input_tables": ["collaboration_sessions", "collaboration_events", "bid_documents"],
                "output_description": "返回协作活动统计数据",
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"},
                    {"field_key": "time_window", "field_name": "时间窗口(小时)", "field_type": "integer", "default_value": "24"}
                ]
            },
            {
                "name": "实时在线用户列表",
                "key": "REALTIME_ONLINE_USERS",
                "description": "查询当前正在协作编辑文档的在线用户",
                "business_requirement": "显示协作者实时状态，避免编辑冲突",
                "input_tables": ["collaboration_sessions", "users"],
                "output_description": "返回在线用户列表及正在编辑的章节",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            },
            {
                "name": "协作冲突检测",
                "key": "COLLABORATION_CONFLICT_DETECT",
                "description": "检测同一章节的并发编辑冲突",
                "business_requirement": "预防协作冲突，提前提醒用户",
                "input_tables": ["collaboration_events"],
                "output_description": "返回可能存在冲突的编辑事件",
                "params": [
                    {"field_key": "section_id", "field_name": "章节ID", "field_type": "integer"},
                    {"field_key": "time_window", "field_name": "时间窗口(分钟)", "field_type": "integer", "default_value": "5"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("协作", sql_def)

    def _generate_approval_sqls(self):
        """审批模块的SQL需求"""
        sqls = [
            {
                "name": "审批流程进度查询",
                "key": "APPROVAL_WORKFLOW_PROGRESS",
                "description": "查询审批流程各节点的完成状态和当前进度",
                "business_requirement": "可视化展示审批进度，了解卡在哪个节点",
                "input_tables": ["approval_workflows", "approval_tasks", "users"],
                "output_description": "返回流程节点列表及完成状态",
                "params": [
                    {"field_key": "workflow_id", "field_name": "流程ID", "field_type": "integer"}
                ]
            },
            {
                "name": "待审批任务列表",
                "key": "PENDING_APPROVAL_LIST",
                "description": "查询用户的所有待审批任务，按紧急程度排序",
                "business_requirement": "提醒用户及时处理审批任务",
                "input_tables": ["approval_tasks", "approval_workflows", "bid_documents", "projects"],
                "output_description": "返回待审批任务清单及紧急等级",
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"}
                ]
            },
            {
                "name": "审批效率统计",
                "key": "APPROVAL_EFFICIENCY_STATS",
                "description": "统计审批节点的平均处理时间、超时率等效率指标",
                "business_requirement": "优化审批流程，减少等待时间",
                "input_tables": ["approval_tasks", "approval_logs"],
                "output_description": "返回各审批节点的效率数据",
                "params": [
                    {"field_key": "workflow_type", "field_name": "流程类型", "field_type": "string"},
                    {"field_key": "date_range", "field_name": "日期范围", "field_type": "string"}
                ]
            },
            {
                "name": "审批历史追溯",
                "key": "APPROVAL_HISTORY_TRACE",
                "description": "查询文档的完整审批历史，包括所有审批人和审批意见",
                "business_requirement": "审计和追溯审批过程",
                "input_tables": ["approval_logs", "approval_tasks", "users"],
                "output_description": "返回按时间排序的审批记录",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("审批", sql_def)

    def _generate_user_permission_sqls(self):
        """用户权限模块的SQL需求"""
        sqls = [
            {
                "name": "用户有效权限计算",
                "key": "USER_EFFECTIVE_PERMISSIONS",
                "description": "计算用户的实际权限，包括角色继承的权限",
                "business_requirement": "权限判断，确定用户能否执行某操作",
                "input_tables": ["users", "user_role_mapping", "roles", "role_permission_mapping", "permissions"],
                "output_description": "返回用户的所有有效权限列表",
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"}
                ]
            },
            {
                "name": "组织成员活跃度排行",
                "key": "MEMBER_ACTIVITY_RANKING",
                "description": "统计组织成员的活跃度，按操作次数排序",
                "business_requirement": "了解成员活跃情况，激励团队",
                "input_tables": ["users", "audit_logs", "project_members", "document_versions"],
                "output_description": "返回成员活跃度排行榜",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "time_window", "field_name": "统计天数", "field_type": "integer", "default_value": "30"}
                ]
            },
            {
                "name": "角色权限矩阵",
                "key": "ROLE_PERMISSION_MATRIX",
                "description": "生成角色-权限对照矩阵，用于权限管理",
                "business_requirement": "可视化展示各角色的权限配置",
                "input_tables": ["roles", "role_permission_mapping", "permissions"],
                "output_description": "返回角色权限矩阵数据",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("系统管理", sql_def)

    def _generate_statistics_sqls(self):
        """统计报表SQL"""
        sqls = [
            {
                "name": "组织数据看板",
                "key": "ORGANIZATION_DASHBOARD",
                "description": "汇总组织的项目、文档、用户、AI使用等各类数据的统计指标",
                "business_requirement": "管理层查看组织整体运营数据",
                "input_tables": ["organizations", "projects", "bid_documents", "users", "ai_tasks", "templates"],
                "output_description": "返回综合数据看板指标",
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            },
            {
                "name": "项目趋势分析",
                "key": "PROJECT_TREND_ANALYSIS",
                "description": "按时间维度分析项目创建和完成的趋势",
                "business_requirement": "了解业务增长趋势",
                "input_tables": ["projects"],
                "output_description": "返回按时间分组的项目统计数据",
                "params": [
                    {"field_key": "start_date", "field_name": "开始日期", "field_type": "date"},
                    {"field_key": "end_date", "field_name": "结束日期", "field_type": "date"},
                    {"field_key": "time_unit", "field_name": "时间粒度", "field_type": "string", "default_value": "week"}
                ]
            },
            {
                "name": "用户留存分析",
                "key": "USER_RETENTION_ANALYSIS",
                "description": "分析用户的活跃留存情况",
                "business_requirement": "评估产品用户粘性",
                "input_tables": ["users", "audit_logs"],
                "output_description": "返回用户留存率数据",
                "params": [
                    {"field_key": "cohort_date", "field_name": "起始日期", "field_type": "date"}
                ]
            },
            {
                "name": "功能使用热力图",
                "key": "FEATURE_USAGE_HEATMAP",
                "description": "统计各功能模块的使用频率",
                "business_requirement": "指导产品功能优化方向",
                "input_tables": ["audit_logs"],
                "output_description": "返回功能使用频率数据",
                "params": [
                    {"field_key": "date_range", "field_name": "日期范围", "field_type": "string"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("报表统计", sql_def)

    def _generate_search_sqls(self):
        """搜索SQL"""
        sqls = [
            {
                "name": "全局搜索",
                "key": "GLOBAL_SEARCH",
                "description": "跨项目、文档、模板、案例的全文搜索",
                "business_requirement": "用户快速找到所需内容",
                "input_tables": ["projects", "bid_documents", "templates", "project_cases"],
                "output_description": "返回搜索结果列表，按相关度排序",
                "params": [
                    {"field_key": "keyword", "field_name": "搜索关键词", "field_type": "string"},
                    {"field_key": "scope", "field_name": "搜索范围", "field_type": "string", "default_value": "all"}
                ]
            },
            {
                "name": "高级筛选查询",
                "key": "ADVANCED_FILTER_QUERY",
                "description": "支持多条件组合的高级筛选",
                "business_requirement": "精确定位符合条件的记录",
                "input_tables": ["projects", "bid_documents"],
                "output_description": "返回符合筛选条件的结果",
                "params": [
                    {"field_key": "filters", "field_name": "筛选条件", "field_type": "json"}
                ]
            },
            {
                "name": "相关内容推荐",
                "key": "RELATED_CONTENT_RECOMMEND",
                "description": "基于当前内容推荐相关的文档、模板、案例",
                "business_requirement": "帮助用户发现相关资源",
                "input_tables": ["bid_documents", "templates", "project_cases"],
                "output_description": "返回相关内容列表",
                "params": [
                    {"field_key": "content_id", "field_name": "内容ID", "field_type": "integer"},
                    {"field_key": "content_type", "field_name": "内容类型", "field_type": "string"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("搜索", sql_def)

    def _generate_export_sqls(self):
        """数据导出SQL"""
        sqls = [
            {
                "name": "文档导出数据准备",
                "key": "DOCUMENT_EXPORT_DATA",
                "description": "准备文档导出所需的完整数据，包括所有章节、变量、格式",
                "business_requirement": "导出Word/PDF格式文档",
                "input_tables": ["bid_documents", "document_sections", "template_variables"],
                "output_description": "返回文档导出所需的结构化数据",
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            },
            {
                "name": "批量数据导出",
                "key": "BATCH_DATA_EXPORT",
                "description": "批量导出项目、案例等数据为Excel格式",
                "business_requirement": "数据备份和外部分析",
                "input_tables": ["projects", "bid_documents", "project_cases"],
                "output_description": "返回导出数据集",
                "params": [
                    {"field_key": "export_type", "field_name": "导出类型", "field_type": "string"},
                    {"field_key": "ids", "field_name": "ID列表", "field_type": "array"}
                ]
            },
            {
                "name": "报表数据汇总",
                "key": "REPORT_DATA_SUMMARY",
                "description": "汇总生成各类统计报表的数据",
                "business_requirement": "生成管理报表",
                "input_tables": ["projects", "users", "ai_usage_logs"],
                "output_description": "返回报表数据",
                "params": [
                    {"field_key": "report_type", "field_name": "报表类型", "field_type": "string"},
                    {"field_key": "date_range", "field_name": "日期范围", "field_type": "string"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("系统管理", sql_def)

    def _generate_audit_sqls(self):
        """审计分析SQL"""
        sqls = [
            {
                "name": "操作日志查询",
                "key": "AUDIT_LOG_QUERY",
                "description": "查询用户操作日志，支持多维度筛选",
                "business_requirement": "安全审计和问题排查",
                "input_tables": ["audit_logs", "users"],
                "output_description": "返回操作日志记录",
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"},
                    {"field_key": "action_type", "field_name": "操作类型", "field_type": "string"},
                    {"field_key": "start_time", "field_name": "开始时间", "field_type": "timestamp"},
                    {"field_key": "end_time", "field_name": "结束时间", "field_type": "timestamp"}
                ]
            },
            {
                "name": "异常操作检测",
                "key": "ABNORMAL_OPERATION_DETECT",
                "description": "检测异常频繁的操作，如大量删除、频繁失败登录",
                "business_requirement": "安全监控，识别潜在风险",
                "input_tables": ["audit_logs"],
                "output_description": "返回异常操作记录",
                "params": [
                    {"field_key": "time_window", "field_name": "时间窗口(小时)", "field_type": "integer", "default_value": "24"}
                ]
            },
            {
                "name": "数据变更追踪",
                "key": "DATA_CHANGE_TRACKING",
                "description": "追踪关键数据的变更历史",
                "business_requirement": "数据变更审计",
                "input_tables": ["audit_logs"],
                "output_description": "返回数据变更记录",
                "params": [
                    {"field_key": "table_name", "field_name": "表名", "field_type": "string"},
                    {"field_key": "record_id", "field_name": "记录ID", "field_type": "integer"}
                ]
            }
        ]
        for sql_def in sqls:
            self._create_sql_file("系统管理", sql_def)

    def _create_sql_file(self, module_name: str, sql_def: Dict[str, Any]):
        """创建单个SQL定义文件（仅业务描述）"""

        module_id = self.module_mapping.get(module_name, 1)

        # 构建input_table_list
        input_table_list = []
        for table_name in sql_def.get("input_tables", []):
            input_table_list.append({
                "entity_id": 0,
                "entity_name": table_name,
                "entity_key": table_name,
                "table_key": table_name,
                "table_name": table_name,
                "input_output_type": "input"
            })

        # 构建sql_param_list
        sql_param_list = []
        for param in sql_def.get("params", []):
            sql_param_list.append({
                "field_key": param["field_key"],
                "field_name": param["field_name"],
                "field_type": param["field_type"],
                "default_value": param.get("default_value", ""),
                "description": param.get("description", "")
            })

        # 主表
        main_table = sql_def["input_tables"][0] if sql_def.get("input_tables") else ""

        # 业务需求说明（作为SQL注释）
        business_note = f"""
        -- 业务需求: {sql_def.get('business_requirement', '')}
        -- 功能说明: {sql_def['description']}
        -- 输出说明: {sql_def.get('output_description', '')}
        -- 注意: 具体SQL实现由开发人员根据实际数据库结构编写
        """

        program_sql = {
            "program_sql_id": self.sql_id_counter,
            "program_sql_name": sql_def["name"],
            "program_sql_no": f"SQL-{self.sql_id_counter:03d}",
            "project_id": 1,
            "module_id": module_id,
            "function_id": 0,
            "source_type": "business_requirement",
            "sql_structure": {
                "table_type": "main",
                "table_key": main_table,
                "column_list": "",
                "join_condition_list": "",
                "where_condition_list": "",
                "group_field_list": "",
                "group_key_list": "",
                "sort_key_list": ""
            },
            "sql_text": business_note.strip(),
            "sql_param_list": sql_param_list,
            "input_table_list": input_table_list,
            "input_table_field_list": [],
            "output_table_list": [],
            "output_table_field_list": [],
            "mock_var_list": {},
            "demand_function_point_list": [],
            "is_locked": "N"
        }

        # 保存文件
        filename = f"{sql_def['key']}-{sql_def['name']}.json"
        filepath = self.sql_dir / filename

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(program_sql, f, ensure_ascii=False, indent=2)

        print(f"  ✅ 创建: {filename}")
        self.sql_id_counter += 1


def main():
    """主函数"""
    print("=" * 80)
    print("开始生成业务SQL定义（仅业务描述）")
    print("=" * 80)

    generator = ProgramSQLGenerator()
    generator.generate_all_sqls()

    print("\n" + "=" * 80)
    print("✅ 所有SQL定义生成完成！")
    print("=" * 80)


if __name__ == "__main__":
    main()
