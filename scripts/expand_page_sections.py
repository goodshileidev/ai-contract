#!/usr/bin/env python3
"""
页面组件扩展生成器
为所有主要页面补充完整的组件定义
"""

import json
import os
from pathlib import Path
from typing import List, Dict, Any


class PageSectionExpander:
    """页面组件扩展器"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons"
        self.page_dir = self.output_dir / "12-页面定义"
        self.section_dir = self.output_dir / "13-页面组件"

        # 加载已有的页面定义
        self.pages = self._load_existing_pages()

        # 从现有组件中获取下一个ID
        existing_sections = list(self.section_dir.glob("*.json"))
        if existing_sections:
            max_id = 0
            for section_file in existing_sections:
                with open(section_file, 'r', encoding='utf-8') as f:
                    section = json.load(f)
                    if section['page_section_id'] > max_id:
                        max_id = section['page_section_id']
            self.section_id_counter = max_id + 1
        else:
            self.section_id_counter = 1

        self.new_sections = []

    def _load_existing_pages(self) -> Dict[str, Dict]:
        """加载所有已有的页面定义"""
        pages = {}
        for page_file in self.page_dir.glob("*.json"):
            with open(page_file, 'r', encoding='utf-8') as f:
                page = json.load(f)
                pages[page['page_key']] = page
        return pages

    def expand_sections(self):
        """扩展所有页面的组件定义"""

        # 定义每个页面需要添加的组件
        expansions = [
            # 登录注册页面
            {
                "page_key": "LOGIN",
                "sections": [
                    {"name": "登录表单", "key": "LOGIN_FORM", "type": "form_panel"},
                    {"name": "第三方登录区", "key": "SOCIAL_LOGIN_AREA", "type": "action_panel"}
                ]
            },
            {
                "page_key": "REGISTER",
                "sections": [
                    {"name": "注册表单", "key": "REGISTER_FORM", "type": "form_panel"},
                    {"name": "验证码输入", "key": "CAPTCHA_INPUT", "type": "input_field"}
                ]
            },
            {
                "page_key": "FORGOT_PASSWORD",
                "sections": [
                    {"name": "密码重置表单", "key": "PASSWORD_RESET_FORM", "type": "form_panel"},
                    {"name": "验证步骤指示器", "key": "RESET_STEPS", "type": "progress_indicator"}
                ]
            },

            # 用户详情和个人中心
            {
                "page_key": "USER_DETAIL",
                "sections": [
                    {"name": "用户信息卡片", "key": "USER_INFO_CARD", "type": "info_card"},
                    {"name": "用户角色标签", "key": "USER_ROLE_TAGS", "type": "tag_list"},
                    {"name": "用户操作历史", "key": "USER_ACTIVITY_LOG", "type": "timeline"}
                ]
            },
            {
                "page_key": "PROFILE",
                "sections": [
                    {"name": "个人信息展示", "key": "PROFILE_INFO", "type": "info_card"},
                    {"name": "头像上传器", "key": "AVATAR_UPLOADER", "type": "upload_component"},
                    {"name": "资料编辑表单", "key": "PROFILE_EDIT_FORM", "type": "form_panel"},
                    {"name": "密码修改区", "key": "PASSWORD_CHANGE_SECTION", "type": "form_panel"}
                ]
            },

            # 组织管理
            {
                "page_key": "ORGANIZATION_LIST",
                "sections": [
                    {"name": "组织搜索栏", "key": "ORG_SEARCH_BAR", "type": "search_form"},
                    {"name": "组织卡片网格", "key": "ORG_CARD_GRID", "type": "card_grid"},
                    {"name": "创建组织按钮", "key": "CREATE_ORG_BUTTON", "type": "action_button"}
                ]
            },
            {
                "page_key": "ORGANIZATION_DETAIL",
                "sections": [
                    {"name": "组织基本信息", "key": "ORG_BASIC_INFO", "type": "info_card"},
                    {"name": "组织成员表格", "key": "ORG_MEMBERS_TABLE", "type": "data_table"},
                    {"name": "成员角色管理", "key": "MEMBER_ROLE_MANAGER", "type": "action_panel"},
                    {"name": "组织设置面板", "key": "ORG_SETTINGS_PANEL", "type": "form_panel"}
                ]
            },

            # 项目管理
            {
                "page_key": "PROJECT_BOARD",
                "sections": [
                    {"name": "看板泳道", "key": "KANBAN_LANES", "type": "kanban_board"},
                    {"name": "项目卡片", "key": "PROJECT_CARDS", "type": "draggable_card"},
                    {"name": "看板筛选器", "key": "BOARD_FILTER", "type": "filter_panel"}
                ]
            },
            {
                "page_key": "PROJECT_DETAIL",
                "sections": [
                    {"name": "项目概览卡片", "key": "PROJECT_OVERVIEW_CARD", "type": "info_card"},
                    {"name": "项目进度条", "key": "PROJECT_PROGRESS_BAR", "type": "progress_indicator"},
                    {"name": "项目成员列表", "key": "PROJECT_MEMBERS_LIST", "type": "member_list"},
                    {"name": "项目文档列表", "key": "PROJECT_DOCS_LIST", "type": "data_table"},
                    {"name": "项目时间线", "key": "PROJECT_TIMELINE", "type": "timeline"}
                ]
            },
            {
                "page_key": "PROJECT_CREATE",
                "sections": [
                    {"name": "项目基本信息表单", "key": "PROJECT_BASIC_FORM", "type": "form_panel"},
                    {"name": "招标文件上传", "key": "BIDDING_DOC_UPLOAD", "type": "upload_component"},
                    {"name": "项目成员选择器", "key": "MEMBER_SELECTOR", "type": "select_component"}
                ]
            },

            # 文档管理
            {
                "page_key": "DOCUMENT_LIST",
                "sections": [
                    {"name": "文档搜索栏", "key": "DOC_SEARCH_BAR", "type": "search_form"},
                    {"name": "文档表格", "key": "DOC_TABLE", "type": "data_table"},
                    {"name": "文档状态筛选", "key": "DOC_STATUS_FILTER", "type": "filter_tabs"}
                ]
            },
            {
                "page_key": "DOCUMENT_PREVIEW",
                "sections": [
                    {"name": "PDF预览器", "key": "PDF_PREVIEW", "type": "preview_panel"},
                    {"name": "文档操作栏", "key": "DOC_ACTION_BAR", "type": "action_bar"},
                    {"name": "导出选项面板", "key": "EXPORT_OPTIONS_PANEL", "type": "form_panel"}
                ]
            },
            {
                "page_key": "DOCUMENT_VERSIONS",
                "sections": [
                    {"name": "版本列表", "key": "VERSION_LIST", "type": "timeline"},
                    {"name": "版本对比视图", "key": "VERSION_DIFF_VIEW", "type": "diff_panel"}
                ]
            },

            # 模板管理
            {
                "page_key": "TEMPLATE_LIST",
                "sections": [
                    {"name": "模板分类菜单", "key": "TEMPLATE_CATEGORY_MENU", "type": "navigation_menu"},
                    {"name": "模板卡片网格", "key": "TEMPLATE_CARD_GRID", "type": "card_grid"},
                    {"name": "模板搜索栏", "key": "TEMPLATE_SEARCH_BAR", "type": "search_form"}
                ]
            },
            {
                "page_key": "TEMPLATE_DETAIL",
                "sections": [
                    {"name": "模板信息卡片", "key": "TEMPLATE_INFO_CARD", "type": "info_card"},
                    {"name": "模板预览区", "key": "TEMPLATE_PREVIEW_AREA", "type": "preview_panel"},
                    {"name": "应用模板按钮", "key": "APPLY_TEMPLATE_BUTTON", "type": "action_button"}
                ]
            },
            {
                "page_key": "TEMPLATE_EDIT",
                "sections": [
                    {"name": "模板章节树", "key": "TEMPLATE_SECTION_TREE", "type": "navigation_tree"},
                    {"name": "章节编辑器", "key": "SECTION_EDITOR", "type": "editor"},
                    {"name": "变量管理面板", "key": "VARIABLE_MANAGER", "type": "form_panel"},
                    {"name": "模板属性面板", "key": "TEMPLATE_PROPERTIES", "type": "property_panel"}
                ]
            },

            # 企业能力
            {
                "page_key": "COMPANY_PROFILE",
                "sections": [
                    {"name": "企业基本信息", "key": "COMPANY_BASIC_INFO", "type": "info_card"},
                    {"name": "企业资质展示", "key": "COMPANY_CERTIFICATIONS", "type": "card_list"},
                    {"name": "信息编辑表单", "key": "COMPANY_EDIT_FORM", "type": "form_panel"}
                ]
            },
            {
                "page_key": "PRODUCTS_SERVICES",
                "sections": [
                    {"name": "产品服务表格", "key": "PRODUCTS_TABLE", "type": "data_table"},
                    {"name": "产品详情抽屉", "key": "PRODUCT_DETAIL_DRAWER", "type": "drawer_panel"},
                    {"name": "添加产品按钮", "key": "ADD_PRODUCT_BUTTON", "type": "action_button"}
                ]
            },
            {
                "page_key": "PROJECT_CASES",
                "sections": [
                    {"name": "案例搜索栏", "key": "CASE_SEARCH_BAR", "type": "search_form"},
                    {"name": "案例卡片列表", "key": "CASE_CARD_LIST", "type": "card_list"},
                    {"name": "案例详情模态框", "key": "CASE_DETAIL_MODAL", "type": "modal_panel"}
                ]
            },
            {
                "page_key": "PERSONNEL",
                "sections": [
                    {"name": "人员表格", "key": "PERSONNEL_TABLE", "type": "data_table"},
                    {"name": "资质标签", "key": "QUALIFICATION_TAGS", "type": "tag_list"},
                    {"name": "添加人员表单", "key": "ADD_PERSONNEL_FORM", "type": "form_panel"}
                ]
            },
            {
                "page_key": "CERTIFICATIONS",
                "sections": [
                    {"name": "证书列表", "key": "CERT_LIST", "type": "card_list"},
                    {"name": "证书上传器", "key": "CERT_UPLOADER", "type": "upload_component"},
                    {"name": "证书过期提醒", "key": "CERT_EXPIRY_ALERT", "type": "alert_component"}
                ]
            },

            # AI服务
            {
                "page_key": "AI_PARSE",
                "sections": [
                    {"name": "文件上传区", "key": "FILE_UPLOAD_AREA", "type": "upload_component"},
                    {"name": "解析进度显示", "key": "PARSE_PROGRESS", "type": "progress_indicator"},
                    {"name": "解析结果展示", "key": "PARSE_RESULT_DISPLAY", "type": "result_panel"},
                    {"name": "关键信息卡片", "key": "KEY_INFO_CARDS", "type": "card_grid"}
                ]
            },
            {
                "page_key": "AI_CHAT",
                "sections": [
                    {"name": "聊天消息列表", "key": "CHAT_MESSAGE_LIST", "type": "message_list"},
                    {"name": "消息输入框", "key": "CHAT_INPUT_BOX", "type": "input_field"}
                ]
            },
            {
                "page_key": "AI_RECOMMEND",
                "sections": [
                    {"name": "推荐案例卡片", "key": "RECOMMEND_CASE_CARDS", "type": "card_list"},
                    {"name": "推荐模板列表", "key": "RECOMMEND_TEMPLATE_LIST", "type": "card_list"},
                    {"name": "应用推荐按钮", "key": "APPLY_RECOMMEND_BUTTON", "type": "action_button"}
                ]
            },

            # 协作
            {
                "page_key": "COLLABORATION_SPACE",
                "sections": [
                    {"name": "协作会话列表", "key": "COLLAB_SESSION_LIST", "type": "data_table"},
                    {"name": "在线成员列表", "key": "ONLINE_MEMBERS", "type": "member_list"},
                    {"name": "创建会话按钮", "key": "CREATE_SESSION_BUTTON", "type": "action_button"}
                ]
            },
            {
                "page_key": "COLLABORATION_EDIT",
                "sections": [
                    {"name": "协作编辑器", "key": "COLLAB_EDITOR", "type": "editor"},
                    {"name": "在线光标显示", "key": "ONLINE_CURSORS", "type": "cursor_overlay"},
                    {"name": "评论侧边栏", "key": "COMMENT_SIDEBAR", "type": "sidebar_panel"},
                    {"name": "协作者头像栏", "key": "COLLABORATOR_AVATARS", "type": "avatar_group"}
                ]
            },

            # 审批
            {
                "page_key": "APPROVAL_LIST",
                "sections": [
                    {"name": "审批筛选器", "key": "APPROVAL_FILTER", "type": "filter_tabs"},
                    {"name": "审批任务表格", "key": "APPROVAL_TASK_TABLE", "type": "data_table"},
                    {"name": "批量审批按钮", "key": "BATCH_APPROVAL_BUTTON", "type": "action_button"}
                ]
            },

            # 系统管理
            {
                "page_key": "ROLE_PERMISSION",
                "sections": [
                    {"name": "角色列表", "key": "ROLE_LIST", "type": "data_table"},
                    {"name": "权限树", "key": "PERMISSION_TREE", "type": "tree_component"},
                    {"name": "角色编辑表单", "key": "ROLE_EDIT_FORM", "type": "form_panel"},
                    {"name": "权限分配面板", "key": "PERMISSION_ASSIGN_PANEL", "type": "transfer_panel"}
                ]
            },
            {
                "page_key": "SYSTEM_CONFIG",
                "sections": [
                    {"name": "配置分类菜单", "key": "CONFIG_CATEGORY_MENU", "type": "navigation_menu"},
                    {"name": "配置表单", "key": "CONFIG_FORM", "type": "form_panel"},
                    {"name": "保存配置按钮", "key": "SAVE_CONFIG_BUTTON", "type": "action_button"}
                ]
            },
            {
                "page_key": "AUDIT_LOGS",
                "sections": [
                    {"name": "日志搜索栏", "key": "LOG_SEARCH_BAR", "type": "search_form"},
                    {"name": "日志表格", "key": "LOG_TABLE", "type": "data_table"},
                    {"name": "日志详情抽屉", "key": "LOG_DETAIL_DRAWER", "type": "drawer_panel"}
                ]
            }
        ]

        for expansion in expansions:
            page_key = expansion["page_key"]

            # 找到对应的page
            page = self.pages.get(page_key)
            if not page:
                print(f"⚠️ 未找到页面: {page_key}")
                continue

            for section_def in expansion["sections"]:
                section = {
                    # 必填字段
                    "page_section_id": self.section_id_counter,
                    "page_id": page["page_id"],

                    # 扩展字段
                    "page_section_name": section_def["name"],
                    "page_section_key": section_def["key"],
                    "page_section_title_cn": section_def["name"],
                    "page_section_title_en": section_def["key"].replace("_", " ").title(),
                    "page_section_title_jp": section_def["name"],
                    "page_section_type": section_def["type"],
                    "scope_type": "function",
                    "project_id": 1,
                    "module_id": page["module_id"],
                    "entity_id": 0,
                    "entity_name": "",
                    "field_list": [],
                    "page_action_list": [],
                    "is_locked": "N"
                }

                self.new_sections.append(section)

                # 保存JSON文件
                filename = f"{section['page_section_key']}-{section['page_section_name']}.json"
                filepath = self.section_dir / filename

                with open(filepath, 'w', encoding='utf-8') as f:
                    json.dump(section, f, ensure_ascii=False, indent=2)

                print(f"✅ 创建页面组件: {section['page_section_name']} ({section['page_section_key']})")

                self.section_id_counter += 1

    def generate_statistics(self) -> Dict[str, Any]:
        """生成统计信息"""
        # 统计每个页面的组件数
        page_component_counts = {}
        all_sections = list(self.section_dir.glob("*.json"))

        for section_file in all_sections:
            with open(section_file, 'r', encoding='utf-8') as f:
                section = json.load(f)
                page_id = section["page_id"]

                # 找到对应的页面
                page = next((p for p in self.pages.values() if p["page_id"] == page_id), None)
                if page:
                    page_name = page["page_name"]
                    page_component_counts[page_name] = page_component_counts.get(page_name, 0) + 1

        return {
            "new_count": len(self.new_sections),
            "total_count": len(all_sections),
            "page_component_counts": page_component_counts
        }


def main():
    """主函数"""
    print("=" * 80)
    print("页面组件扩展生成器")
    print("=" * 80)
    print()

    expander = PageSectionExpander()

    # 扩展页面组件
    print("📦 扩展页面组件定义...")
    print()
    expander.expand_sections()
    print()

    # 输出统计信息
    stats = expander.generate_statistics()

    print("=" * 80)
    print("✅ 扩展完成！")
    print("=" * 80)
    print()
    print(f"📊 统计信息:")
    print(f"  - 新增组件数量: {stats['new_count']}")
    print(f"  - 组件总数量: {stats['total_count']}")
    print()
    print("📋 各页面组件数量:")
    for page_name, count in sorted(stats['page_component_counts'].items(), key=lambda x: x[1], reverse=True):
        print(f"  - {page_name}: {count}个组件")
    print()


if __name__ == "__main__":
    main()
