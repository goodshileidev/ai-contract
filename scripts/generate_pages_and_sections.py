#!/usr/bin/env python3
"""
页面和页面组件定义生成器
基于前端实现计划和业务模块生成page和page_section JSON定义
"""

import json
import os
from pathlib import Path
from typing import List, Dict, Any, Optional
from datetime import datetime


class PageGenerator:
    """页面生成器"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons"
        self.page_dir = self.output_dir / "12-页面定义"
        self.section_dir = self.output_dir / "13-页面组件"

        # 创建输出目录
        self.page_dir.mkdir(parents=True, exist_ok=True)
        self.section_dir.mkdir(parents=True, exist_ok=True)

        self.page_id_counter = 1
        self.section_id_counter = 1
        self.pages = []
        self.sections = []

        # 模块映射
        self.module_mapping = {
            "认证授权": 1,
            "用户管理": 1,
            "组织管理": 1,
            "项目管理": 2,
            "标书文档": 3,
            "模板管理": 4,
            "企业能力": 5,
            "AI服务": 6,
            "协作": 7,
            "审批": 8,
            "系统管理": 11
        }

    def generate_pages(self):
        """生成所有页面定义"""

        page_definitions = [
            # 认证模块
            {
                "module": "认证授权",
                "page_name": "用户登录",
                "page_key": "LOGIN",
                "page_type": "主页面",
                "scope_type": "global",
                "description": "用户登录页面，支持邮箱密码登录",
                "route": "/login",
                "entities": []
            },
            {
                "module": "认证授权",
                "page_name": "用户注册",
                "page_key": "REGISTER",
                "page_type": "主页面",
                "scope_type": "global",
                "description": "新用户注册页面",
                "route": "/register",
                "entities": []
            },
            {
                "module": "认证授权",
                "page_name": "忘记密码",
                "page_key": "FORGOT_PASSWORD",
                "page_type": "主页面",
                "scope_type": "global",
                "description": "忘记密码找回页面",
                "route": "/forgot-password",
                "entities": []
            },

            # 用户管理
            {
                "module": "用户管理",
                "page_name": "用户列表",
                "page_key": "USER_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "用户管理列表页面",
                "route": "/users",
                "entities": [{"entity_name": "用户", "entity_key": "users"}]
            },
            {
                "module": "用户管理",
                "page_name": "用户详情",
                "page_key": "USER_DETAIL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "用户详细信息页面",
                "route": "/users/:id",
                "entities": [{"entity_name": "用户", "entity_key": "users"}]
            },
            {
                "module": "用户管理",
                "page_name": "个人中心",
                "page_key": "PROFILE",
                "page_type": "主页面",
                "scope_type": "global",
                "description": "当前用户个人中心",
                "route": "/profile",
                "entities": [{"entity_name": "用户", "entity_key": "users"}]
            },

            # 组织管理
            {
                "module": "组织管理",
                "page_name": "组织列表",
                "page_key": "ORGANIZATION_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "组织管理列表页面",
                "route": "/organizations",
                "entities": [{"entity_name": "组织", "entity_key": "organizations"}]
            },
            {
                "module": "组织管理",
                "page_name": "组织详情",
                "page_key": "ORGANIZATION_DETAIL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "组织详细信息和成员管理",
                "route": "/organizations/:id",
                "entities": [{"entity_name": "组织", "entity_key": "organizations"}]
            },

            # 项目管理
            {
                "module": "项目管理",
                "page_name": "项目列表",
                "page_key": "PROJECT_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "项目列表视图",
                "route": "/projects",
                "entities": [{"entity_name": "项目", "entity_key": "projects"}]
            },
            {
                "module": "项目管理",
                "page_name": "项目看板",
                "page_key": "PROJECT_BOARD",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "项目看板视图，展示项目进度",
                "route": "/projects/board",
                "entities": [{"entity_name": "项目", "entity_key": "projects"}]
            },
            {
                "module": "项目管理",
                "page_name": "项目详情",
                "page_key": "PROJECT_DETAIL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "项目详细信息和管理",
                "route": "/projects/:id",
                "entities": [{"entity_name": "项目", "entity_key": "projects"}]
            },
            {
                "module": "项目管理",
                "page_name": "创建项目",
                "page_key": "PROJECT_CREATE",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "创建新项目",
                "route": "/projects/new",
                "entities": [{"entity_name": "项目", "entity_key": "projects"}]
            },

            # 标书文档
            {
                "module": "标书文档",
                "page_name": "文档列表",
                "page_key": "DOCUMENT_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "标书文档列表",
                "route": "/documents",
                "entities": [{"entity_name": "标书文档", "entity_key": "bid_documents"}]
            },
            {
                "module": "标书文档",
                "page_name": "文档编辑",
                "page_key": "DOCUMENT_EDIT",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "标书文档编辑器",
                "route": "/documents/:id/edit",
                "entities": [
                    {"entity_name": "标书文档", "entity_key": "bid_documents"},
                    {"entity_name": "文档章节", "entity_key": "document_sections"}
                ]
            },
            {
                "module": "标书文档",
                "page_name": "文档预览",
                "page_key": "DOCUMENT_PREVIEW",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "文档预览和导出",
                "route": "/documents/:id/preview",
                "entities": [{"entity_name": "标书文档", "entity_key": "bid_documents"}]
            },
            {
                "module": "标书文档",
                "page_name": "版本历史",
                "page_key": "DOCUMENT_VERSIONS",
                "page_type": "子页面",
                "scope_type": "function",
                "description": "文档版本历史和比较",
                "route": "/documents/:id/versions",
                "entities": [{"entity_name": "文档版本", "entity_key": "document_versions"}]
            },

            # 模板管理
            {
                "module": "模板管理",
                "page_name": "模板库",
                "page_key": "TEMPLATE_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "标书模板库",
                "route": "/templates",
                "entities": [{"entity_name": "模板", "entity_key": "templates"}]
            },
            {
                "module": "模板管理",
                "page_name": "模板详情",
                "page_key": "TEMPLATE_DETAIL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "模板详情和预览",
                "route": "/templates/:id",
                "entities": [{"entity_name": "模板", "entity_key": "templates"}]
            },
            {
                "module": "模板管理",
                "page_name": "模板编辑",
                "page_key": "TEMPLATE_EDIT",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "模板编辑器",
                "route": "/templates/:id/edit",
                "entities": [
                    {"entity_name": "模板", "entity_key": "templates"},
                    {"entity_name": "模板章节", "entity_key": "template_sections"}
                ]
            },

            # 企业能力
            {
                "module": "企业能力",
                "page_name": "企业档案",
                "page_key": "COMPANY_PROFILE",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "企业基本信息管理",
                "route": "/capabilities/profile",
                "entities": [{"entity_name": "企业档案", "entity_key": "company_profiles"}]
            },
            {
                "module": "企业能力",
                "page_name": "产品服务",
                "page_key": "PRODUCTS_SERVICES",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "产品服务管理",
                "route": "/capabilities/products",
                "entities": [{"entity_name": "产品服务", "entity_key": "products_services"}]
            },
            {
                "module": "企业能力",
                "page_name": "项目案例库",
                "page_key": "PROJECT_CASES",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "历史项目案例管理",
                "route": "/capabilities/cases",
                "entities": [{"entity_name": "项目案例", "entity_key": "project_cases"}]
            },
            {
                "module": "企业能力",
                "page_name": "人员资质",
                "page_key": "PERSONNEL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "人员资质管理",
                "route": "/capabilities/personnel",
                "entities": [{"entity_name": "人员资质", "entity_key": "personnel"}]
            },
            {
                "module": "企业能力",
                "page_name": "资质证书",
                "page_key": "CERTIFICATIONS",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "企业资质证书管理",
                "route": "/capabilities/certifications",
                "entities": [{"entity_name": "资质证书", "entity_key": "certifications"}]
            },

            # AI服务
            {
                "module": "AI服务",
                "page_name": "招标文件解析",
                "page_key": "AI_PARSE",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "上传并解析招标文件",
                "route": "/ai/parse",
                "entities": [{"entity_name": "招标文件", "entity_key": "bidding_documents"}]
            },
            {
                "module": "AI服务",
                "page_name": "AI内容生成",
                "page_key": "AI_GENERATE",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "AI智能内容生成",
                "route": "/ai/generate",
                "entities": [{"entity_name": "AI任务", "entity_key": "ai_tasks"}]
            },
            {
                "module": "AI服务",
                "page_name": "AI对话助手",
                "page_key": "AI_CHAT",
                "page_type": "组件",
                "scope_type": "global",
                "description": "AI智能助手对话界面",
                "route": "/ai/chat",
                "entities": []
            },
            {
                "module": "AI服务",
                "page_name": "智能推荐",
                "page_key": "AI_RECOMMEND",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "智能案例和模板推荐",
                "route": "/ai/recommend",
                "entities": []
            },

            # 协作
            {
                "module": "协作",
                "page_name": "协作空间",
                "page_key": "COLLABORATION_SPACE",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "团队协作工作空间",
                "route": "/collaboration",
                "entities": [{"entity_name": "协作会话", "entity_key": "collaboration_sessions"}]
            },
            {
                "module": "协作",
                "page_name": "实时协作编辑",
                "page_key": "COLLABORATION_EDIT",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "多人实时协作编辑",
                "route": "/collaboration/:id/edit",
                "entities": [{"entity_name": "协作会话", "entity_key": "collaboration_sessions"}]
            },

            # 审批
            {
                "module": "审批",
                "page_name": "审批列表",
                "page_key": "APPROVAL_LIST",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "待审批和已审批任务列表",
                "route": "/approvals",
                "entities": [{"entity_name": "审批任务", "entity_key": "approval_tasks"}]
            },
            {
                "module": "审批",
                "page_name": "审批详情",
                "page_key": "APPROVAL_DETAIL",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "审批任务详情和处理",
                "route": "/approvals/:id",
                "entities": [{"entity_name": "审批任务", "entity_key": "approval_tasks"}]
            },

            # 系统管理
            {
                "module": "系统管理",
                "page_name": "角色权限管理",
                "page_key": "ROLE_PERMISSION",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "系统角色和权限配置",
                "route": "/admin/roles",
                "entities": [
                    {"entity_name": "角色", "entity_key": "roles"},
                    {"entity_name": "权限", "entity_key": "permissions"}
                ]
            },
            {
                "module": "系统管理",
                "page_name": "系统配置",
                "page_key": "SYSTEM_CONFIG",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "系统全局配置",
                "route": "/admin/config",
                "entities": []
            },
            {
                "module": "系统管理",
                "page_name": "审计日志",
                "page_key": "AUDIT_LOGS",
                "page_type": "主页面",
                "scope_type": "module",
                "description": "系统审计日志查询",
                "route": "/admin/audit-logs",
                "entities": [{"entity_name": "审计日志", "entity_key": "audit_logs"}]
            },
        ]

        for page_def in page_definitions:
            module = page_def["module"]
            module_id = self.module_mapping.get(module, 1)

            # 构建data_src_table_list
            data_src_tables = []
            for entity in page_def.get("entities", []):
                data_src_tables.append({
                    "entity_id": 0,  # 将在关联时填充
                    "entity_name": entity["entity_name"],
                    "entity_key": entity["entity_key"],
                    "table_key": entity["entity_key"],
                    "table_name": entity["entity_name"],
                    "input_output_type": "input_output"
                })

            page = {
                # 必填字段
                "page_id": self.page_id_counter,
                "page_name": page_def["page_name"],
                "page_key": page_def["page_key"],
                "page_title": page_def["page_name"],
                "page_title_cn": page_def["page_name"],
                "page_title_en": page_def["page_key"].replace("_", " ").title(),
                "page_title_jp": page_def["page_name"],
                "module_id": module_id,
                "source_type": "frontend_design",

                # 扩展字段
                "page_no": f"PAGE-{self.page_id_counter:03d}",
                "project_id": 1,
                "demand_description": page_def["description"],
                "basic_description": page_def["description"],
                "scope_type": page_def["scope_type"],
                "page_type": page_def["page_type"],
                "page_version": "1.0",
                "data_src_table_list": data_src_tables,
                "program_api_list": [],
                "is_locked": "N"
            }

            self.pages.append(page)

            # 保存JSON文件
            filename = f"{page['page_key']}-{page['page_name']}.json"
            filepath = self.page_dir / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(page, f, ensure_ascii=False, indent=2)

            print(f"✅ 创建页面: {page['page_name']} ({page['page_key']})")

            self.page_id_counter += 1

    def generate_page_sections(self):
        """生成页面组件定义"""

        # 为每个主要页面生成典型的页面组件
        section_definitions = [
            # 用户列表页面的组件
            {
                "page_key": "USER_LIST",
                "sections": [
                    {
                        "section_name": "用户搜索栏",
                        "section_key": "USER_SEARCH_BAR",
                        "section_type": "search_form",
                        "description": "用户搜索和筛选表单"
                    },
                    {
                        "section_name": "用户表格",
                        "section_key": "USER_TABLE",
                        "section_type": "data_table",
                        "description": "用户数据表格"
                    },
                    {
                        "section_name": "用户操作按钮组",
                        "section_key": "USER_ACTION_BUTTONS",
                        "section_type": "action_bar",
                        "description": "新增、批量操作等按钮"
                    }
                ]
            },

            # 项目列表页面的组件
            {
                "page_key": "PROJECT_LIST",
                "sections": [
                    {
                        "section_name": "项目筛选器",
                        "section_key": "PROJECT_FILTER",
                        "section_type": "filter_panel",
                        "description": "项目筛选条件面板"
                    },
                    {
                        "section_name": "项目卡片列表",
                        "section_key": "PROJECT_CARD_LIST",
                        "section_type": "card_list",
                        "description": "项目卡片展示列表"
                    }
                ]
            },

            # 文档编辑页面的组件
            {
                "page_key": "DOCUMENT_EDIT",
                "sections": [
                    {
                        "section_name": "文档大纲",
                        "section_key": "DOCUMENT_OUTLINE",
                        "section_type": "navigation_tree",
                        "description": "文档章节大纲导航树"
                    },
                    {
                        "section_name": "富文本编辑器",
                        "section_key": "RICH_TEXT_EDITOR",
                        "section_type": "editor",
                        "description": "富文本内容编辑器"
                    },
                    {
                        "section_name": "编辑工具栏",
                        "section_key": "EDITOR_TOOLBAR",
                        "section_type": "toolbar",
                        "description": "编辑器工具栏"
                    },
                    {
                        "section_name": "文档属性面板",
                        "section_key": "DOCUMENT_PROPERTIES",
                        "section_type": "property_panel",
                        "description": "文档属性和元数据"
                    }
                ]
            },

            # AI内容生成页面的组件
            {
                "page_key": "AI_GENERATE",
                "sections": [
                    {
                        "section_name": "生成配置面板",
                        "section_key": "GENERATE_CONFIG_PANEL",
                        "section_type": "form_panel",
                        "description": "AI生成参数配置"
                    },
                    {
                        "section_name": "生成进度条",
                        "section_key": "GENERATE_PROGRESS",
                        "section_type": "progress_indicator",
                        "description": "内容生成进度展示"
                    },
                    {
                        "section_name": "生成结果预览",
                        "section_key": "GENERATE_RESULT",
                        "section_type": "preview_panel",
                        "description": "生成内容预览和编辑"
                    }
                ]
            },

            # 审批详情页面的组件
            {
                "page_key": "APPROVAL_DETAIL",
                "sections": [
                    {
                        "section_name": "审批信息卡片",
                        "section_key": "APPROVAL_INFO_CARD",
                        "section_type": "info_card",
                        "description": "审批基本信息展示"
                    },
                    {
                        "section_name": "审批流程时间线",
                        "section_key": "APPROVAL_TIMELINE",
                        "section_type": "timeline",
                        "description": "审批流程步骤和历史"
                    },
                    {
                        "section_name": "审批操作区",
                        "section_key": "APPROVAL_ACTIONS",
                        "section_type": "action_panel",
                        "description": "通过、驳回等审批操作"
                    }
                ]
            }
        ]

        for section_group in section_definitions:
            page_key = section_group["page_key"]

            # 找到对应的page
            page = next((p for p in self.pages if p["page_key"] == page_key), None)
            if not page:
                print(f"⚠️ 未找到页面: {page_key}")
                continue

            for section_def in section_group["sections"]:
                section = {
                    # 必填字段
                    "page_section_id": self.section_id_counter,
                    "page_id": page["page_id"],

                    # 扩展字段
                    "page_section_name": section_def["section_name"],
                    "page_section_key": section_def["section_key"],
                    "page_section_title_cn": section_def["section_name"],
                    "page_section_title_en": section_def["section_key"].replace("_", " ").title(),
                    "page_section_title_jp": section_def["section_name"],
                    "page_section_type": section_def["section_type"],
                    "scope_type": "function",
                    "project_id": 1,
                    "module_id": page["module_id"],
                    "entity_id": 0,
                    "entity_name": "",
                    "field_list": [],
                    "page_action_list": [],
                    "is_locked": "N"
                }

                self.sections.append(section)

                # 保存JSON文件
                filename = f"{section['page_section_key']}-{section['page_section_name']}.json"
                filepath = self.section_dir / filename

                with open(filepath, 'w', encoding='utf-8') as f:
                    json.dump(section, f, ensure_ascii=False, indent=2)

                print(f"✅ 创建页面组件: {section['page_section_name']} ({section['page_section_key']})")

                self.section_id_counter += 1

    def generate_statistics(self) -> Dict[str, Any]:
        """生成统计信息"""
        return {
            "page_count": len(self.pages),
            "section_count": len(self.sections),
            "module_breakdown": self._get_module_breakdown(),
            "page_type_breakdown": self._get_page_type_breakdown()
        }

    def _get_module_breakdown(self) -> Dict[str, int]:
        """按模块统计页面数量"""
        breakdown = {}
        for page in self.pages:
            module_id = page["module_id"]
            # 找到模块名称
            module_name = next((name for name, mid in self.module_mapping.items() if mid == module_id), f"模块{module_id}")
            breakdown[module_name] = breakdown.get(module_name, 0) + 1
        return breakdown

    def _get_page_type_breakdown(self) -> Dict[str, int]:
        """按页面类型统计"""
        breakdown = {}
        for page in self.pages:
            page_type = page.get("page_type", "未知")
            breakdown[page_type] = breakdown.get(page_type, 0) + 1
        return breakdown


def main():
    """主函数"""
    print("=" * 80)
    print("页面和页面组件定义生成器")
    print("=" * 80)
    print()

    generator = PageGenerator()

    # 生成页面定义
    print("📄 生成页面定义...")
    print()
    generator.generate_pages()
    print()

    # 生成页面组件定义
    print("📦 生成页面组件定义...")
    print()
    generator.generate_page_sections()
    print()

    # 输出统计信息
    stats = generator.generate_statistics()

    print("=" * 80)
    print("✅ 生成完成！")
    print("=" * 80)
    print()
    print(f"📊 统计信息:")
    print(f"  - 页面数量: {stats['page_count']}")
    print(f"  - 页面组件数量: {stats['section_count']}")
    print()
    print("📋 模块分布:")
    for module, count in sorted(stats['module_breakdown'].items(), key=lambda x: x[1], reverse=True):
        print(f"  - {module}: {count}个页面")
    print()
    print("📋 页面类型分布:")
    for page_type, count in sorted(stats['page_type_breakdown'].items(), key=lambda x: x[1], reverse=True):
        print(f"  - {page_type}: {count}个页面")
    print()
    print(f"📁 输出目录:")
    print(f"  - 页面定义: {generator.page_dir}")
    print(f"  - 页面组件: {generator.section_dir}")
    print()


if __name__ == "__main__":
    main()
