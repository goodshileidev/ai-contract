#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成program_sql业务SQL定义

功能说明：
- 只生成复杂查询SQL，不包含基本CRUD
- 聚焦于多表关联、统计聚合、报表查询
- 涵盖11个业务模块的特殊SQL需求

创建日期: 2025-11-28
"""

import json
from pathlib import Path
from typing import Dict, List, Any


class ProgramSQLGenerator:
    """业务SQL生成器（复杂查询）"""

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
        """生成所有复杂SQL定义"""

        # 1. 项目管理模块的复杂查询
        self._generate_project_sqls()

        # 2. 标书文档模块的复杂查询
        self._generate_document_sqls()

        # 3. 模板管理模块的复杂查询
        self._generate_template_sqls()

        # 4. 企业能力模块的复杂查询
        self._generate_capability_sqls()

        # 5. AI服务模块的复杂查询
        self._generate_ai_sqls()

        # 6. 协作模块的复杂查询
        self._generate_collaboration_sqls()

        # 7. 审批模块的复杂查询
        self._generate_approval_sqls()

        # 8. 用户权限模块的复杂查询
        self._generate_user_permission_sqls()

        # 9. 统计报表SQL
        self._generate_statistics_sqls()

        # 10. 搜索SQL
        self._generate_search_sqls()

        # 11. 扩展：更多项目管理SQL
        self._generate_project_advanced_sqls()

        # 12. 扩展：更多文档分析SQL
        self._generate_document_analysis_sqls()

        # 13. 扩展：数据导出SQL
        self._generate_export_sqls()

        # 14. 扩展：审计日志SQL
        self._generate_audit_sqls()

        # 15. 扩展：性能监控SQL
        self._generate_monitoring_sqls()

        # 16. 扩展：批量操作SQL
        self._generate_batch_operation_sqls()

        print(f"\n✅ 共生成 {self.sql_id_counter - 1} 个复杂SQL定义")

    def _generate_project_sqls(self):
        """项目管理模块的复杂SQL"""

        sqls = [
            {
                "name": "项目看板统计",
                "key": "PROJECT_BOARD_STATS",
                "description": "统计各状态下的项目数量，用于看板展示",
                "sql_text": """
                SELECT
                    p.status,
                    COUNT(*) as project_count,
                    COUNT(DISTINCT bd.id) as doc_count,
                    AVG(EXTRACT(EPOCH FROM (p.deadline - CURRENT_DATE))/86400) as avg_days_remaining
                FROM projects p
                LEFT JOIN bid_documents bd ON p.id = bd.project_id
                WHERE p.organization_id = :org_id
                  AND p.deleted_at IS NULL
                GROUP BY p.status
                ORDER BY
                    CASE p.status
                        WHEN 'draft' THEN 1
                        WHEN 'in_progress' THEN 2
                        WHEN 'under_review' THEN 3
                        WHEN 'completed' THEN 4
                        WHEN 'archived' THEN 5
                    END
                """,
                "input_tables": ["projects", "bid_documents"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            },
            {
                "name": "项目进度统计",
                "key": "PROJECT_PROGRESS_STATS",
                "description": "计算项目完成度、剩余时间等进度指标",
                "sql_text": """
                SELECT
                    p.id,
                    p.name,
                    p.deadline,
                    COUNT(DISTINCT ds.id) as total_sections,
                    COUNT(DISTINCT CASE WHEN ds.status = 'completed' THEN ds.id END) as completed_sections,
                    ROUND(COUNT(DISTINCT CASE WHEN ds.status = 'completed' THEN ds.id END) * 100.0 /
                          NULLIF(COUNT(DISTINCT ds.id), 0), 2) as completion_rate,
                    EXTRACT(EPOCH FROM (p.deadline - CURRENT_TIMESTAMP))/86400 as days_remaining,
                    CASE
                        WHEN p.deadline < CURRENT_DATE THEN 'overdue'
                        WHEN EXTRACT(EPOCH FROM (p.deadline - CURRENT_TIMESTAMP))/86400 <= 3 THEN 'urgent'
                        WHEN EXTRACT(EPOCH FROM (p.deadline - CURRENT_TIMESTAMP))/86400 <= 7 THEN 'warning'
                        ELSE 'normal'
                    END as urgency_level
                FROM projects p
                LEFT JOIN bid_documents bd ON p.id = bd.project_id
                LEFT JOIN document_sections ds ON bd.id = ds.document_id
                WHERE p.id = :project_id
                  AND p.deleted_at IS NULL
                GROUP BY p.id, p.name, p.deadline
                """,
                "input_tables": ["projects", "bid_documents", "document_sections"],
                "output_tables": [],
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"}
                ]
            },
            {
                "name": "项目成员工作量统计",
                "key": "PROJECT_MEMBER_WORKLOAD",
                "description": "统计项目成员的文档编辑次数、评论数等",
                "sql_text": """
                SELECT
                    u.id as user_id,
                    u.username,
                    u.display_name,
                    pm.role as project_role,
                    COUNT(DISTINCT dv.id) as edit_count,
                    COUNT(DISTINCT dc.id) as comment_count,
                    COUNT(DISTINCT cs.id) as collaboration_count,
                    MAX(dv.created_at) as last_edit_time
                FROM project_members pm
                JOIN users u ON pm.user_id = u.id
                LEFT JOIN document_versions dv ON dv.created_by = u.id
                    AND dv.document_id IN (SELECT id FROM bid_documents WHERE project_id = :project_id)
                LEFT JOIN document_comments dc ON dc.user_id = u.id
                    AND dc.document_id IN (SELECT id FROM bid_documents WHERE project_id = :project_id)
                LEFT JOIN collaboration_sessions cs ON cs.user_id = u.id
                    AND cs.document_id IN (SELECT id FROM bid_documents WHERE project_id = :project_id)
                WHERE pm.project_id = :project_id
                GROUP BY u.id, u.username, u.display_name, pm.role
                ORDER BY edit_count DESC, comment_count DESC
                """,
                "input_tables": ["project_members", "users", "document_versions", "document_comments", "collaboration_sessions"],
                "output_tables": [],
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"}
                ]
            },
            {
                "name": "即将到期项目列表",
                "key": "UPCOMING_DEADLINE_PROJECTS",
                "description": "查询N天内到期的项目，用于提醒",
                "sql_text": """
                SELECT
                    p.id,
                    p.name,
                    p.deadline,
                    p.status,
                    o.name as org_name,
                    COUNT(DISTINCT pm.user_id) as member_count,
                    COUNT(DISTINCT bd.id) as doc_count,
                    EXTRACT(EPOCH FROM (p.deadline - CURRENT_TIMESTAMP))/86400 as days_remaining
                FROM projects p
                JOIN organizations o ON p.organization_id = o.id
                LEFT JOIN project_members pm ON p.id = pm.project_id
                LEFT JOIN bid_documents bd ON p.id = bd.project_id
                WHERE p.deadline BETWEEN CURRENT_DATE AND (CURRENT_DATE + :days_ahead * INTERVAL '1 day')
                  AND p.status NOT IN ('completed', 'archived')
                  AND p.deleted_at IS NULL
                GROUP BY p.id, p.name, p.deadline, p.status, o.name
                ORDER BY p.deadline ASC
                """,
                "input_tables": ["projects", "organizations", "project_members", "bid_documents"],
                "output_tables": [],
                "params": [
                    {"field_key": "days_ahead", "field_name": "提前天数", "field_type": "integer", "default_value": "7"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("项目管理", sql_def)

    def _generate_document_sqls(self):
        """标书文档模块的复杂SQL"""

        sqls = [
            {
                "name": "文档版本对比查询",
                "key": "DOCUMENT_VERSION_DIFF",
                "description": "对比两个版本的差异，返回修改的章节",
                "sql_text": """
                WITH version_changes AS (
                    SELECT
                        dv1.id as old_version_id,
                        dv2.id as new_version_id,
                        ds.id as section_id,
                        ds.title as section_title,
                        ds.section_order,
                        dv1.content as old_content,
                        dv2.content as new_content,
                        LENGTH(dv2.content) - LENGTH(dv1.content) as content_diff_length,
                        dv2.created_at as modified_at,
                        u.display_name as modified_by
                    FROM document_versions dv1
                    JOIN document_versions dv2 ON dv1.document_id = dv2.document_id
                    JOIN document_sections ds ON dv2.document_id = ds.document_id
                    JOIN users u ON dv2.created_by = u.id
                    WHERE dv1.id = :old_version_id
                      AND dv2.id = :new_version_id
                      AND (dv1.content IS DISTINCT FROM dv2.content)
                )
                SELECT
                    section_id,
                    section_title,
                    section_order,
                    content_diff_length,
                    CASE
                        WHEN content_diff_length > 0 THEN 'added'
                        WHEN content_diff_length < 0 THEN 'removed'
                        ELSE 'modified'
                    END as change_type,
                    modified_at,
                    modified_by
                FROM version_changes
                ORDER BY section_order
                """,
                "input_tables": ["document_versions", "document_sections", "users"],
                "output_tables": [],
                "params": [
                    {"field_key": "old_version_id", "field_name": "旧版本ID", "field_type": "integer"},
                    {"field_key": "new_version_id", "field_name": "新版本ID", "field_type": "integer"}
                ]
            },
            {
                "name": "文档完整性检查",
                "key": "DOCUMENT_COMPLETENESS_CHECK",
                "description": "检查文档是否满足招标要求的必填章节",
                "sql_text": """
                WITH required_sections AS (
                    SELECT unnest(:required_section_keys) as section_key
                ),
                existing_sections AS (
                    SELECT ds.section_key
                    FROM document_sections ds
                    WHERE ds.document_id = :document_id
                      AND ds.status = 'completed'
                      AND LENGTH(TRIM(ds.content)) > 0
                )
                SELECT
                    rs.section_key,
                    CASE WHEN es.section_key IS NOT NULL THEN true ELSE false END as is_completed,
                    CASE WHEN es.section_key IS NULL THEN 'missing' ELSE 'completed' END as status
                FROM required_sections rs
                LEFT JOIN existing_sections es ON rs.section_key = es.section_key
                ORDER BY rs.section_key
                """,
                "input_tables": ["document_sections"],
                "output_tables": [],
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"},
                    {"field_key": "required_section_keys", "field_name": "必填章节键列表", "field_type": "array"}
                ]
            },
            {
                "name": "文档协作热度统计",
                "key": "DOCUMENT_COLLABORATION_HEATMAP",
                "description": "统计文档各章节的编辑、评论热度",
                "sql_text": """
                SELECT
                    ds.id as section_id,
                    ds.title as section_title,
                    ds.section_order,
                    COUNT(DISTINCT dv.id) as version_count,
                    COUNT(DISTINCT dc.id) as comment_count,
                    COUNT(DISTINCT ce.id) as edit_event_count,
                    COUNT(DISTINCT ce.user_id) as contributor_count,
                    MAX(ce.created_at) as last_activity_time,
                    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - MAX(ce.created_at)))/3600 as hours_since_last_edit
                FROM document_sections ds
                LEFT JOIN document_versions dv ON ds.document_id = dv.document_id
                LEFT JOIN document_comments dc ON ds.id = dc.section_id
                LEFT JOIN collaboration_events ce ON ds.document_id = ce.document_id
                    AND ce.metadata->>'section_id' = ds.id::text
                WHERE ds.document_id = :document_id
                GROUP BY ds.id, ds.title, ds.section_order
                ORDER BY ds.section_order
                """,
                "input_tables": ["document_sections", "document_versions", "document_comments", "collaboration_events"],
                "output_tables": [],
                "params": [
                    {"field_key": "document_id", "field_name": "文档ID", "field_type": "integer"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("标书文档", sql_def)

    def _generate_template_sqls(self):
        """模板管理模块的复杂SQL"""

        sqls = [
            {
                "name": "模板推荐匹配",
                "key": "TEMPLATE_RECOMMENDATION",
                "description": "根据项目需求推荐相似模板",
                "sql_text": """
                WITH project_requirements AS (
                    SELECT
                        bd.industry,
                        bd.project_type,
                        bd.budget_range,
                        string_agg(DISTINCT ds.section_key, ',') as required_sections
                    FROM bidding_documents bd
                    LEFT JOIN bid_documents doc ON bd.project_id = doc.project_id
                    LEFT JOIN document_sections ds ON doc.id = ds.document_id
                    WHERE bd.project_id = :project_id
                    GROUP BY bd.industry, bd.project_type, bd.budget_range
                )
                SELECT
                    t.id,
                    t.name,
                    t.category,
                    t.industry,
                    t.usage_count,
                    t.average_rating,
                    COUNT(DISTINCT ts.id) as section_count,
                    COUNT(DISTINCT tul.id) as usage_history_count,
                    -- 匹配度计算
                    (CASE WHEN t.industry = pr.industry THEN 30 ELSE 0 END +
                     CASE WHEN t.category = pr.project_type THEN 20 ELSE 0 END +
                     t.average_rating * 10 +
                     LOG(t.usage_count + 1) * 5) as match_score
                FROM templates t
                CROSS JOIN project_requirements pr
                LEFT JOIN template_sections ts ON t.id = ts.template_id
                LEFT JOIN template_usage_logs tul ON t.id = tul.template_id
                WHERE t.status = 'published'
                  AND t.deleted_at IS NULL
                GROUP BY t.id, t.name, t.category, t.industry, t.usage_count, t.average_rating, pr.industry, pr.project_type
                ORDER BY match_score DESC, t.usage_count DESC
                LIMIT :limit
                """,
                "input_tables": ["templates", "template_sections", "template_usage_logs", "bidding_documents", "bid_documents", "document_sections"],
                "output_tables": [],
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"},
                    {"field_key": "limit", "field_name": "返回数量", "field_type": "integer", "default_value": "10"}
                ]
            },
            {
                "name": "热门模板统计",
                "key": "POPULAR_TEMPLATES_STATS",
                "description": "统计最近N天的热门模板",
                "sql_text": """
                SELECT
                    t.id,
                    t.name,
                    t.category,
                    t.industry,
                    COUNT(DISTINCT tul.id) as usage_count,
                    COUNT(DISTINCT tul.user_id) as user_count,
                    AVG(t.average_rating) as avg_rating,
                    COUNT(DISTINCT tul.id) FILTER (WHERE tul.created_at >= CURRENT_DATE - :days * INTERVAL '1 day') as recent_usage_count
                FROM templates t
                LEFT JOIN template_usage_logs tul ON t.id = tul.template_id
                WHERE t.status = 'published'
                  AND t.deleted_at IS NULL
                GROUP BY t.id, t.name, t.category, t.industry
                HAVING COUNT(DISTINCT tul.id) FILTER (WHERE tul.created_at >= CURRENT_DATE - :days * INTERVAL '1 day') > 0
                ORDER BY recent_usage_count DESC, avg_rating DESC
                LIMIT :limit
                """,
                "input_tables": ["templates", "template_usage_logs"],
                "output_tables": [],
                "params": [
                    {"field_key": "days", "field_name": "统计天数", "field_type": "integer", "default_value": "30"},
                    {"field_key": "limit", "field_name": "返回数量", "field_type": "integer", "default_value": "20"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("模板管理", sql_def)

    def _generate_capability_sqls(self):
        """企业能力模块的复杂SQL"""

        sqls = [
            {
                "name": "案例智能匹配",
                "key": "CASE_INTELLIGENT_MATCH",
                "description": "根据招标需求智能匹配历史案例",
                "sql_text": """
                WITH requirement_keywords AS (
                    SELECT unnest(string_to_array(:requirement_text, ' ')) as keyword
                ),
                case_scores AS (
                    SELECT
                        pc.id,
                        pc.project_name,
                        pc.client,
                        pc.industry,
                        pc.project_scale,
                        pc.completion_date,
                        -- 行业匹配
                        CASE WHEN pc.industry = :target_industry THEN 30 ELSE 0 END as industry_score,
                        -- 规模匹配
                        CASE
                            WHEN pc.project_scale BETWEEN :min_scale * 0.8 AND :max_scale * 1.2 THEN 20
                            WHEN pc.project_scale BETWEEN :min_scale * 0.5 AND :max_scale * 2 THEN 10
                            ELSE 0
                        END as scale_score,
                        -- 关键词匹配
                        (SELECT COUNT(*) FROM requirement_keywords rk
                         WHERE pc.description ILIKE '%' || rk.keyword || '%'
                            OR pc.key_technologies ILIKE '%' || rk.keyword || '%') * 5 as keyword_score,
                        -- 时效性
                        CASE
                            WHEN pc.completion_date >= CURRENT_DATE - INTERVAL '2 years' THEN 15
                            WHEN pc.completion_date >= CURRENT_DATE - INTERVAL '5 years' THEN 10
                            ELSE 5
                        END as recency_score
                    FROM project_cases pc
                    WHERE pc.organization_id = :org_id
                      AND pc.status = 'completed'
                )
                SELECT
                    id,
                    project_name,
                    client,
                    industry,
                    project_scale,
                    completion_date,
                    (industry_score + scale_score + keyword_score + recency_score) as total_score,
                    industry_score,
                    scale_score,
                    keyword_score,
                    recency_score
                FROM case_scores
                WHERE (industry_score + scale_score + keyword_score + recency_score) >= :min_score
                ORDER BY total_score DESC, completion_date DESC
                LIMIT :limit
                """,
                "input_tables": ["project_cases"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "requirement_text", "field_name": "需求描述", "field_type": "string"},
                    {"field_key": "target_industry", "field_name": "目标行业", "field_type": "string"},
                    {"field_key": "min_scale", "field_name": "最小规模", "field_type": "decimal"},
                    {"field_key": "max_scale", "field_name": "最大规模", "field_type": "decimal"},
                    {"field_key": "min_score", "field_name": "最低匹配分", "field_type": "integer", "default_value": "20"},
                    {"field_key": "limit", "field_name": "返回数量", "field_type": "integer", "default_value": "10"}
                ]
            },
            {
                "name": "资质证书有效性检查",
                "key": "CERTIFICATION_VALIDITY_CHECK",
                "description": "检查即将过期或已过期的资质证书",
                "sql_text": """
                SELECT
                    c.id,
                    c.certification_name,
                    c.certification_number,
                    c.issuing_authority,
                    c.issue_date,
                    c.expiry_date,
                    c.status,
                    EXTRACT(EPOCH FROM (c.expiry_date - CURRENT_DATE))/86400 as days_until_expiry,
                    CASE
                        WHEN c.expiry_date < CURRENT_DATE THEN 'expired'
                        WHEN c.expiry_date <= CURRENT_DATE + :warning_days * INTERVAL '1 day' THEN 'expiring_soon'
                        ELSE 'valid'
                    END as validity_status
                FROM certifications c
                WHERE c.organization_id = :org_id
                  AND c.deleted_at IS NULL
                  AND (c.expiry_date IS NULL
                       OR c.expiry_date <= CURRENT_DATE + :warning_days * INTERVAL '1 day')
                ORDER BY c.expiry_date ASC NULLS LAST
                """,
                "input_tables": ["certifications"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "warning_days", "field_name": "预警天数", "field_type": "integer", "default_value": "90"}
                ]
            },
            {
                "name": "人员资质匹配",
                "key": "PERSONNEL_QUALIFICATION_MATCH",
                "description": "根据项目需求匹配符合资质的人员",
                "sql_text": """
                WITH required_qualifications AS (
                    SELECT unnest(:required_cert_types) as cert_type
                )
                SELECT
                    p.id,
                    p.name,
                    p.position,
                    p.education,
                    p.years_of_experience,
                    array_agg(DISTINCT c.certification_name) as certifications,
                    array_agg(DISTINCT pc.project_role) as past_roles,
                    COUNT(DISTINCT pc.id) as project_count,
                    COUNT(DISTINCT rq.cert_type) as matched_cert_count,
                    (SELECT COUNT(*) FROM required_qualifications) as required_cert_count
                FROM personnel p
                LEFT JOIN certifications c ON p.id = c.personnel_id
                    AND c.status = 'valid'
                    AND c.expiry_date >= CURRENT_DATE
                LEFT JOIN project_cases pc ON p.id = ANY(pc.team_members)
                LEFT JOIN required_qualifications rq ON c.certification_type = rq.cert_type
                WHERE p.organization_id = :org_id
                  AND p.status = 'active'
                GROUP BY p.id, p.name, p.position, p.education, p.years_of_experience
                HAVING COUNT(DISTINCT rq.cert_type) >= :min_match_count
                ORDER BY matched_cert_count DESC, p.years_of_experience DESC
                """,
                "input_tables": ["personnel", "certifications", "project_cases"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "required_cert_types", "field_name": "要求资质类型", "field_type": "array"},
                    {"field_key": "min_match_count", "field_name": "最少匹配数", "field_type": "integer", "default_value": "1"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("企业能力", sql_def)

    def _generate_ai_sqls(self):
        """AI服务模块的复杂SQL"""

        sqls = [
            {
                "name": "AI使用量统计",
                "key": "AI_USAGE_STATISTICS",
                "description": "统计AI调用次数、token消耗、成本等",
                "sql_text": """
                SELECT
                    DATE(created_at) as usage_date,
                    task_type,
                    model_name,
                    COUNT(*) as call_count,
                    SUM(tokens_used) as total_tokens,
                    SUM(cost_amount) as total_cost,
                    AVG(duration_seconds) as avg_duration,
                    COUNT(*) FILTER (WHERE status = 'success') as success_count,
                    COUNT(*) FILTER (WHERE status = 'failed') as failed_count,
                    ROUND(COUNT(*) FILTER (WHERE status = 'success') * 100.0 / COUNT(*), 2) as success_rate
                FROM ai_usage_logs
                WHERE organization_id = :org_id
                  AND created_at >= :start_date
                  AND created_at < :end_date
                GROUP BY DATE(created_at), task_type, model_name
                ORDER BY usage_date DESC, total_tokens DESC
                """,
                "input_tables": ["ai_usage_logs"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "start_date", "field_name": "开始日期", "field_type": "date"},
                    {"field_key": "end_date", "field_name": "结束日期", "field_type": "date"}
                ]
            },
            {
                "name": "AI任务队列状态",
                "key": "AI_TASK_QUEUE_STATUS",
                "description": "查询AI任务队列的运行状态",
                "sql_text": """
                SELECT
                    task_type,
                    status,
                    COUNT(*) as task_count,
                    AVG(EXTRACT(EPOCH FROM (completed_at - created_at))) as avg_processing_time,
                    MIN(created_at) as oldest_task_time,
                    MAX(created_at) as newest_task_time
                FROM ai_tasks
                WHERE organization_id = :org_id
                  AND created_at >= CURRENT_TIMESTAMP - INTERVAL '24 hours'
                GROUP BY task_type, status
                ORDER BY task_type,
                    CASE status
                        WHEN 'pending' THEN 1
                        WHEN 'running' THEN 2
                        WHEN 'success' THEN 3
                        WHEN 'failed' THEN 4
                    END
                """,
                "input_tables": ["ai_tasks"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("AI服务", sql_def)

    def _generate_collaboration_sqls(self):
        """协作模块的复杂SQL"""

        sqls = [
            {
                "name": "协作活动统计",
                "key": "COLLABORATION_ACTIVITY_STATS",
                "description": "统计文档协作的活跃度",
                "sql_text": """
                SELECT
                    cs.document_id,
                    bd.title as document_title,
                    COUNT(DISTINCT cs.user_id) as participant_count,
                    COUNT(DISTINCT ce.id) as event_count,
                    COUNT(DISTINCT ce.id) FILTER (WHERE ce.event_type = 'edit') as edit_count,
                    COUNT(DISTINCT ce.id) FILTER (WHERE ce.event_type = 'comment') as comment_count,
                    MAX(ce.created_at) as last_activity_time,
                    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - MAX(ce.created_at)))/3600 as hours_inactive
                FROM collaboration_sessions cs
                JOIN bid_documents bd ON cs.document_id = bd.id
                LEFT JOIN collaboration_events ce ON cs.id = ce.session_id
                WHERE cs.project_id = :project_id
                  AND cs.created_at >= CURRENT_TIMESTAMP - :time_window * INTERVAL '1 hour'
                GROUP BY cs.document_id, bd.title
                ORDER BY event_count DESC, participant_count DESC
                """,
                "input_tables": ["collaboration_sessions", "bid_documents", "collaboration_events"],
                "output_tables": [],
                "params": [
                    {"field_key": "project_id", "field_name": "项目ID", "field_type": "integer"},
                    {"field_key": "time_window", "field_name": "时间窗口(小时)", "field_type": "integer", "default_value": "24"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("协作", sql_def)

    def _generate_approval_sqls(self):
        """审批模块的复杂SQL"""

        sqls = [
            {
                "name": "审批流程进度查询",
                "key": "APPROVAL_WORKFLOW_PROGRESS",
                "description": "查询审批流程的各节点完成情况",
                "sql_text": """
                WITH workflow_nodes AS (
                    SELECT
                        aw.id as workflow_id,
                        jsonb_array_elements(aw.workflow_definition->'nodes') as node
                ),
                node_status AS (
                    SELECT
                        wn.workflow_id,
                        wn.node->>'id' as node_id,
                        wn.node->>'name' as node_name,
                        wn.node->>'type' as node_type,
                        at.id as task_id,
                        at.status as task_status,
                        at.approved_at,
                        at.approved_by,
                        u.display_name as approver_name
                    FROM workflow_nodes wn
                    LEFT JOIN approval_tasks at ON at.workflow_id = wn.workflow_id
                        AND at.node_id = wn.node->>'id'
                    LEFT JOIN users u ON at.approved_by = u.id
                    WHERE wn.workflow_id = :workflow_id
                )
                SELECT
                    node_id,
                    node_name,
                    node_type,
                    COALESCE(task_status, 'pending') as status,
                    approved_at,
                    approver_name,
                    CASE
                        WHEN task_status = 'approved' THEN 100
                        WHEN task_status = 'rejected' THEN -1
                        WHEN task_status = 'pending' THEN 0
                        ELSE 0
                    END as progress
                FROM node_status
                ORDER BY node_id
                """,
                "input_tables": ["approval_workflows", "approval_tasks", "users"],
                "output_tables": [],
                "params": [
                    {"field_key": "workflow_id", "field_name": "流程ID", "field_type": "integer"}
                ]
            },
            {
                "name": "待审批任务统计",
                "key": "PENDING_APPROVAL_STATS",
                "description": "统计用户的待审批任务",
                "sql_text": """
                SELECT
                    at.id,
                    at.workflow_id,
                    aw.workflow_name,
                    at.task_type,
                    bd.title as document_title,
                    p.name as project_name,
                    at.created_at,
                    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - at.created_at))/86400 as days_pending,
                    CASE
                        WHEN at.due_date < CURRENT_DATE THEN 'overdue'
                        WHEN at.due_date <= CURRENT_DATE + INTERVAL '1 day' THEN 'urgent'
                        WHEN at.due_date <= CURRENT_DATE + INTERVAL '3 days' THEN 'soon'
                        ELSE 'normal'
                    END as urgency
                FROM approval_tasks at
                JOIN approval_workflows aw ON at.workflow_id = aw.id
                JOIN bid_documents bd ON aw.document_id = bd.id
                JOIN projects p ON bd.project_id = p.id
                WHERE at.assigned_to = :user_id
                  AND at.status = 'pending'
                ORDER BY
                    CASE urgency
                        WHEN 'overdue' THEN 1
                        WHEN 'urgent' THEN 2
                        WHEN 'soon' THEN 3
                        ELSE 4
                    END,
                    at.created_at ASC
                """,
                "input_tables": ["approval_tasks", "approval_workflows", "bid_documents", "projects"],
                "output_tables": [],
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("审批", sql_def)

    def _generate_user_permission_sqls(self):
        """用户权限模块的复杂SQL"""

        sqls = [
            {
                "name": "用户有效权限计算",
                "key": "USER_EFFECTIVE_PERMISSIONS",
                "description": "计算用户的实际有效权限（包括角色继承）",
                "sql_text": """
                WITH RECURSIVE role_hierarchy AS (
                    -- 用户直接角色
                    SELECT
                        urm.user_id,
                        r.id as role_id,
                        r.name as role_name,
                        r.parent_role_id,
                        1 as level
                    FROM user_role_mapping urm
                    JOIN roles r ON urm.role_id = r.id
                    WHERE urm.user_id = :user_id

                    UNION ALL

                    -- 父角色（递归）
                    SELECT
                        rh.user_id,
                        r.id,
                        r.name,
                        r.parent_role_id,
                        rh.level + 1
                    FROM role_hierarchy rh
                    JOIN roles r ON rh.parent_role_id = r.id
                    WHERE r.parent_role_id IS NOT NULL
                )
                SELECT DISTINCT
                    p.id as permission_id,
                    p.permission_key,
                    p.permission_name,
                    p.resource_type,
                    p.action,
                    array_agg(DISTINCT rh.role_name) as granted_by_roles
                FROM role_hierarchy rh
                JOIN role_permission_mapping rpm ON rh.role_id = rpm.role_id
                JOIN permissions p ON rpm.permission_id = p.id
                WHERE p.status = 'active'
                GROUP BY p.id, p.permission_key, p.permission_name, p.resource_type, p.action
                ORDER BY p.resource_type, p.action
                """,
                "input_tables": ["user_role_mapping", "roles", "role_permission_mapping", "permissions"],
                "output_tables": [],
                "params": [
                    {"field_key": "user_id", "field_name": "用户ID", "field_type": "integer"}
                ]
            },
            {
                "name": "组织成员活跃度",
                "key": "ORGANIZATION_MEMBER_ACTIVITY",
                "description": "统计组织成员的活跃度",
                "sql_text": """
                SELECT
                    u.id,
                    u.username,
                    u.display_name,
                    u.last_login_at,
                    COUNT(DISTINCT al.id) as action_count,
                    COUNT(DISTINCT p.id) as project_count,
                    COUNT(DISTINCT dv.id) as document_version_count,
                    COUNT(DISTINCT dc.id) as comment_count,
                    MAX(al.created_at) as last_action_time,
                    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - MAX(al.created_at)))/86400 as days_inactive
                FROM users u
                LEFT JOIN audit_logs al ON u.id = al.user_id
                    AND al.created_at >= CURRENT_TIMESTAMP - :time_window * INTERVAL '1 day'
                LEFT JOIN project_members pm ON u.id = pm.user_id
                LEFT JOIN projects p ON pm.project_id = p.id AND p.organization_id = :org_id
                LEFT JOIN document_versions dv ON u.id = dv.created_by
                    AND dv.created_at >= CURRENT_TIMESTAMP - :time_window * INTERVAL '1 day'
                LEFT JOIN document_comments dc ON u.id = dc.user_id
                    AND dc.created_at >= CURRENT_TIMESTAMP - :time_window * INTERVAL '1 day'
                WHERE u.organization_id = :org_id
                  AND u.status = 'active'
                GROUP BY u.id, u.username, u.display_name, u.last_login_at
                ORDER BY action_count DESC, last_action_time DESC
                """,
                "input_tables": ["users", "audit_logs", "project_members", "projects", "document_versions", "document_comments"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "time_window", "field_name": "统计天数", "field_type": "integer", "default_value": "30"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("系统管理", sql_def)

    def _generate_statistics_sqls(self):
        """统计报表SQL"""

        sqls = [
            {
                "name": "组织整体数据看板",
                "key": "ORGANIZATION_DASHBOARD",
                "description": "组织的整体数据统计看板",
                "sql_text": """
                SELECT
                    -- 项目统计
                    COUNT(DISTINCT p.id) as total_projects,
                    COUNT(DISTINCT p.id) FILTER (WHERE p.status = 'in_progress') as active_projects,
                    COUNT(DISTINCT p.id) FILTER (WHERE p.status = 'completed') as completed_projects,

                    -- 文档统计
                    COUNT(DISTINCT bd.id) as total_documents,
                    COUNT(DISTINCT dv.id) as total_versions,

                    -- 用户统计
                    COUNT(DISTINCT u.id) as total_users,
                    COUNT(DISTINCT u.id) FILTER (WHERE u.last_login_at >= CURRENT_DATE - 7) as active_users_week,

                    -- AI使用统计
                    COUNT(DISTINCT ait.id) as total_ai_tasks,
                    SUM(aul.tokens_used) as total_tokens_used,
                    SUM(aul.cost_amount) as total_ai_cost,

                    -- 模板统计
                    COUNT(DISTINCT t.id) as total_templates,
                    COUNT(DISTINCT tul.id) as template_usage_count
                FROM organizations o
                LEFT JOIN projects p ON o.id = p.organization_id AND p.deleted_at IS NULL
                LEFT JOIN bid_documents bd ON p.id = bd.project_id AND bd.deleted_at IS NULL
                LEFT JOIN document_versions dv ON bd.id = dv.document_id
                LEFT JOIN users u ON o.id = u.organization_id AND u.status = 'active'
                LEFT JOIN ai_tasks ait ON o.id = ait.organization_id
                LEFT JOIN ai_usage_logs aul ON o.id = aul.organization_id
                LEFT JOIN templates t ON o.id = t.organization_id AND t.status = 'published'
                LEFT JOIN template_usage_logs tul ON t.id = tul.template_id
                WHERE o.id = :org_id
                GROUP BY o.id
                """,
                "input_tables": ["organizations", "projects", "bid_documents", "document_versions", "users", "ai_tasks", "ai_usage_logs", "templates", "template_usage_logs"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"}
                ]
            },
            {
                "name": "项目趋势分析",
                "key": "PROJECT_TREND_ANALYSIS",
                "description": "按时间维度分析项目创建和完成趋势",
                "sql_text": """
                SELECT
                    DATE_TRUNC(:time_unit, p.created_at) as time_period,
                    COUNT(*) as created_count,
                    COUNT(*) FILTER (WHERE p.status = 'completed') as completed_count,
                    COUNT(*) FILTER (WHERE p.status = 'in_progress') as in_progress_count,
                    AVG(EXTRACT(EPOCH FROM (p.updated_at - p.created_at))/86400) as avg_duration_days
                FROM projects p
                WHERE p.organization_id = :org_id
                  AND p.created_at >= :start_date
                  AND p.created_at < :end_date
                  AND p.deleted_at IS NULL
                GROUP BY time_period
                ORDER BY time_period
                """,
                "input_tables": ["projects"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "time_unit", "field_name": "时间粒度", "field_type": "string", "default_value": "week"},
                    {"field_key": "start_date", "field_name": "开始日期", "field_type": "date"},
                    {"field_key": "end_date", "field_name": "结束日期", "field_type": "date"}
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
                "description": "跨项目、文档、模板的全局搜索",
                "sql_text": """
                -- 项目搜索
                SELECT
                    'project' as result_type,
                    p.id,
                    p.name as title,
                    p.description,
                    p.created_at,
                    p.updated_at,
                    ts_rank(to_tsvector('simple', p.name || ' ' || COALESCE(p.description, '')),
                            plainto_tsquery('simple', :search_query)) as relevance_score
                FROM projects p
                WHERE p.organization_id = :org_id
                  AND (to_tsvector('simple', p.name || ' ' || COALESCE(p.description, '')) @@ plainto_tsquery('simple', :search_query))
                  AND p.deleted_at IS NULL

                UNION ALL

                -- 文档搜索
                SELECT
                    'document' as result_type,
                    bd.id,
                    bd.title,
                    ds.content as description,
                    bd.created_at,
                    bd.updated_at,
                    ts_rank(to_tsvector('simple', bd.title || ' ' || COALESCE(ds.content, '')),
                            plainto_tsquery('simple', :search_query)) as relevance_score
                FROM bid_documents bd
                LEFT JOIN document_sections ds ON bd.id = ds.document_id
                JOIN projects p ON bd.project_id = p.id
                WHERE p.organization_id = :org_id
                  AND (to_tsvector('simple', bd.title || ' ' || COALESCE(ds.content, '')) @@ plainto_tsquery('simple', :search_query))
                  AND bd.deleted_at IS NULL

                UNION ALL

                -- 模板搜索
                SELECT
                    'template' as result_type,
                    t.id,
                    t.name as title,
                    t.description,
                    t.created_at,
                    t.updated_at,
                    ts_rank(to_tsvector('simple', t.name || ' ' || COALESCE(t.description, '')),
                            plainto_tsquery('simple', :search_query)) as relevance_score
                FROM templates t
                WHERE t.organization_id = :org_id
                  AND (to_tsvector('simple', t.name || ' ' || COALESCE(t.description, '')) @@ plainto_tsquery('simple', :search_query))
                  AND t.status = 'published'
                  AND t.deleted_at IS NULL

                ORDER BY relevance_score DESC, updated_at DESC
                LIMIT :limit
                """,
                "input_tables": ["projects", "bid_documents", "document_sections", "templates"],
                "output_tables": [],
                "params": [
                    {"field_key": "org_id", "field_name": "组织ID", "field_type": "integer"},
                    {"field_key": "search_query", "field_name": "搜索关键词", "field_type": "string"},
                    {"field_key": "limit", "field_name": "返回数量", "field_type": "integer", "default_value": "50"}
                ]
            }
        ]

        for sql_def in sqls:
            self._create_sql_file("搜索", sql_def)

    def _create_sql_file(self, module_name: str, sql_def: Dict[str, Any]):
        """创建单个SQL定义文件"""

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

        # 构建output_table_list
        output_table_list = []
        for table_name in sql_def.get("output_tables", []):
            output_table_list.append({
                "entity_id": 0,
                "entity_name": table_name,
                "entity_key": table_name,
                "table_key": table_name,
                "table_name": table_name,
                "input_output_type": "output"
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

        # 构建主表结构
        main_table = sql_def["input_tables"][0] if sql_def.get("input_tables") else ""

        program_sql = {
            "program_sql_id": self.sql_id_counter,
            "program_sql_name": sql_def["name"],
            "program_sql_no": f"SQL-{self.sql_id_counter:03d}",
            "project_id": 1,
            "module_id": module_id,
            "function_id": 0,
            "source_type": "business_logic",
            "sql_structure": {
                "table_type": "main",
                "table_key": main_table,
                "column_list": "*",
                "join_condition_list": "",
                "where_condition_list": "",
                "group_field_list": "",
                "group_key_list": "",
                "sort_key_list": ""
            },
            "sql_text": sql_def["sql_text"].strip(),
            "sql_param_list": sql_param_list,
            "input_table_list": input_table_list,
            "input_table_field_list": [],
            "output_table_list": output_table_list,
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
    print("开始生成复杂业务SQL定义")
    print("=" * 80)

    generator = ProgramSQLGenerator()
    generator.generate_all_sqls()

    print("\n" + "=" * 80)
    print("✅ 所有SQL定义生成完成！")
    print("=" * 80)


if __name__ == "__main__":
    main()
