#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从需求和设计文档生成role和permission的JSON定义

功能说明：
- 基于AIBidComposer的业务需求和RBAC设计
- 生成符合generator-springcrud schema的role和permission JSON
- 自动分配ID和关联关系

创建日期: 2025-11-28
"""

import json
from pathlib import Path
from typing import List, Dict, Any


class RolePermissionGenerator:
    """角色和权限生成器"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons"

        # 确保输出目录存在
        (self.output_dir / "14-系统角色").mkdir(parents=True, exist_ok=True)
        (self.output_dir / "15-权限定义").mkdir(parents=True, exist_ok=True)

        self.role_id_counter = 1
        self.permission_id_counter = 1

        self.roles = []
        self.permissions = []

    def generate_roles(self):
        """生成系统角色定义"""

        # AIBidComposer系统角色定义
        role_definitions = [
            {
                "role_name": "系统管理员",
                "role_code": "SYSTEM_ADMIN",
                "description": "系统最高权限管理员，可管理整个系统的配置、用户和数据",
                "role_level": 1,
                "is_system_role": "Y",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "组织管理员",
                "role_code": "ORG_ADMIN",
                "description": "组织级管理员，可管理本组织内的用户、项目和数据",
                "role_level": 2,
                "is_system_role": "Y",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "项目经理",
                "role_code": "PROJECT_MANAGER",
                "description": "项目负责人，负责项目的整体管理、任务分配和进度把控",
                "role_level": 3,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "标书撰写人",
                "role_code": "BID_WRITER",
                "description": "标书内容撰写人员，负责撰写和编辑标书内容",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "标书审核人",
                "role_code": "BID_REVIEWER",
                "description": "标书审核人员，负责审核标书内容的质量和合规性",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "技术专家",
                "role_code": "TECHNICAL_EXPERT",
                "description": "技术方案专家，负责技术方案的设计和审核",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "商务专员",
                "role_code": "COMMERCIAL_SPECIALIST",
                "description": "商务方案专员，负责商务条款和报价策略",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "财务审核员",
                "role_code": "FINANCE_AUDITOR",
                "description": "财务审核人员，审核报价和成本控制",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "法务专员",
                "role_code": "LEGAL_SPECIALIST",
                "description": "法务专员，审核合同条款的法律合规性",
                "role_level": 4,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "项目成员",
                "role_code": "PROJECT_MEMBER",
                "description": "项目普通成员，可查看和协作编辑项目内容",
                "role_level": 5,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "访客",
                "role_code": "GUEST",
                "description": "访客角色，只读权限，可查看授权的项目内容",
                "role_level": 6,
                "is_system_role": "Y",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "AI助手管理员",
                "role_code": "AI_ADMIN",
                "description": "AI服务管理员，管理AI模型、Prompt模板和AI任务",
                "role_level": 2,
                "is_system_role": "Y",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "模板管理员",
                "role_code": "TEMPLATE_ADMIN",
                "description": "模板库管理员，管理标书模板和模板变量",
                "role_level": 3,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            },
            {
                "role_name": "能力库管理员",
                "role_code": "CAPABILITY_ADMIN",
                "description": "企业能力库管理员，维护企业档案、产品、案例、资质等信息",
                "role_level": 3,
                "is_system_role": "N",
                "can_be_assigned": "Y"
            }
        ]

        for role_def in role_definitions:
            role = {
                # 必填字段
                "role_id": self.role_id_counter,
                "project_id": 1,  # AIBidComposer项目
                "source_type": "system_design",
                "role_name": role_def["role_name"],
                "role_code": role_def["role_code"],
                "role_title": role_def["role_name"],
                "role_title_cn": role_def["role_name"],
                "role_title_en": role_def["role_code"].lower().replace("_", " ").title(),
                "role_title_jp": role_def["role_name"],

                # 扩展字段
                "description": role_def["description"],
                "role_level": role_def["role_level"],
                "is_system_role": role_def["is_system_role"],
                "can_be_assigned": role_def["can_be_assigned"],
                "is_locked": "Y" if role_def["is_system_role"] == "Y" else "N",
                "version": "1.0"
            }

            self.roles.append(role)
            self.role_id_counter += 1

    def generate_permissions(self):
        """生成权限定义"""

        # AIBidComposer系统权限定义（按模块划分）
        permission_definitions = [
            # 用户管理权限
            {
                "module": "用户管理",
                "module_id": 1,
                "permissions": [
                    {"name": "查看用户列表", "code": "USER_VIEW_LIST", "type": "read", "resource": "users"},
                    {"name": "查看用户详情", "code": "USER_VIEW_DETAIL", "type": "read", "resource": "users"},
                    {"name": "创建用户", "code": "USER_CREATE", "type": "write", "resource": "users"},
                    {"name": "编辑用户", "code": "USER_EDIT", "type": "write", "resource": "users"},
                    {"name": "删除用户", "code": "USER_DELETE", "type": "write", "resource": "users"},
                    {"name": "分配角色", "code": "USER_ASSIGN_ROLE", "type": "write", "resource": "users"},
                ]
            },
            # 组织管理权限
            {
                "module": "组织管理",
                "module_id": 1,
                "permissions": [
                    {"name": "查看组织列表", "code": "ORG_VIEW_LIST", "type": "read", "resource": "organizations"},
                    {"name": "查看组织详情", "code": "ORG_VIEW_DETAIL", "type": "read", "resource": "organizations"},
                    {"name": "创建组织", "code": "ORG_CREATE", "type": "write", "resource": "organizations"},
                    {"name": "编辑组织", "code": "ORG_EDIT", "type": "write", "resource": "organizations"},
                    {"name": "删除组织", "code": "ORG_DELETE", "type": "write", "resource": "organizations"},
                ]
            },
            # 项目管理权限
            {
                "module": "项目管理",
                "module_id": 2,
                "permissions": [
                    {"name": "查看项目列表", "code": "PROJECT_VIEW_LIST", "type": "read", "resource": "projects"},
                    {"name": "查看项目详情", "code": "PROJECT_VIEW_DETAIL", "type": "read", "resource": "projects"},
                    {"name": "创建项目", "code": "PROJECT_CREATE", "type": "write", "resource": "projects"},
                    {"name": "编辑项目", "code": "PROJECT_EDIT", "type": "write", "resource": "projects"},
                    {"name": "删除项目", "code": "PROJECT_DELETE", "type": "write", "resource": "projects"},
                    {"name": "添加项目成员", "code": "PROJECT_ADD_MEMBER", "type": "write", "resource": "projects"},
                    {"name": "移除项目成员", "code": "PROJECT_REMOVE_MEMBER", "type": "write", "resource": "projects"},
                    {"name": "分配项目角色", "code": "PROJECT_ASSIGN_ROLE", "type": "write", "resource": "projects"},
                ]
            },
            # 标书管理权限
            {
                "module": "标书管理",
                "module_id": 3,
                "permissions": [
                    {"name": "查看标书列表", "code": "BID_DOC_VIEW_LIST", "type": "read", "resource": "bid_documents"},
                    {"name": "查看标书详情", "code": "BID_DOC_VIEW_DETAIL", "type": "read", "resource": "bid_documents"},
                    {"name": "创建标书", "code": "BID_DOC_CREATE", "type": "write", "resource": "bid_documents"},
                    {"name": "编辑标书", "code": "BID_DOC_EDIT", "type": "write", "resource": "bid_documents"},
                    {"name": "删除标书", "code": "BID_DOC_DELETE", "type": "write", "resource": "bid_documents"},
                    {"name": "上传招标文件", "code": "BID_DOC_UPLOAD_RFP", "type": "write", "resource": "bidding_documents"},
                    {"name": "解析招标文件", "code": "BID_DOC_PARSE_RFP", "type": "execute", "resource": "bidding_documents"},
                    {"name": "导出标书", "code": "BID_DOC_EXPORT", "type": "execute", "resource": "bid_documents"},
                ]
            },
            # 文档编辑权限
            {
                "module": "文档编辑",
                "module_id": 3,
                "permissions": [
                    {"name": "查看文档章节", "code": "DOC_SECTION_VIEW", "type": "read", "resource": "document_sections"},
                    {"name": "编辑文档章节", "code": "DOC_SECTION_EDIT", "type": "write", "resource": "document_sections"},
                    {"name": "添加文档章节", "code": "DOC_SECTION_ADD", "type": "write", "resource": "document_sections"},
                    {"name": "删除文档章节", "code": "DOC_SECTION_DELETE", "type": "write", "resource": "document_sections"},
                    {"name": "查看文档版本", "code": "DOC_VERSION_VIEW", "type": "read", "resource": "document_versions"},
                    {"name": "创建文档版本", "code": "DOC_VERSION_CREATE", "type": "write", "resource": "document_versions"},
                    {"name": "查看文档评论", "code": "DOC_COMMENT_VIEW", "type": "read", "resource": "document_comments"},
                    {"name": "添加文档评论", "code": "DOC_COMMENT_ADD", "type": "write", "resource": "document_comments"},
                ]
            },
            # 模板管理权限
            {
                "module": "模板管理",
                "module_id": 4,
                "permissions": [
                    {"name": "查看模板列表", "code": "TEMPLATE_VIEW_LIST", "type": "read", "resource": "templates"},
                    {"name": "查看模板详情", "code": "TEMPLATE_VIEW_DETAIL", "type": "read", "resource": "templates"},
                    {"name": "创建模板", "code": "TEMPLATE_CREATE", "type": "write", "resource": "templates"},
                    {"name": "编辑模板", "code": "TEMPLATE_EDIT", "type": "write", "resource": "templates"},
                    {"name": "删除模板", "code": "TEMPLATE_DELETE", "type": "write", "resource": "templates"},
                    {"name": "发布模板", "code": "TEMPLATE_PUBLISH", "type": "write", "resource": "templates"},
                    {"name": "应用模板", "code": "TEMPLATE_APPLY", "type": "execute", "resource": "templates"},
                ]
            },
            # 企业能力库权限
            {
                "module": "企业能力库",
                "module_id": 5,
                "permissions": [
                    {"name": "查看企业档案", "code": "COMPANY_PROFILE_VIEW", "type": "read", "resource": "company_profiles"},
                    {"name": "编辑企业档案", "code": "COMPANY_PROFILE_EDIT", "type": "write", "resource": "company_profiles"},
                    {"name": "查看产品服务", "code": "PRODUCT_VIEW", "type": "read", "resource": "products_services"},
                    {"name": "管理产品服务", "code": "PRODUCT_MANAGE", "type": "write", "resource": "products_services"},
                    {"name": "查看项目案例", "code": "CASE_VIEW", "type": "read", "resource": "project_cases"},
                    {"name": "管理项目案例", "code": "CASE_MANAGE", "type": "write", "resource": "project_cases"},
                    {"name": "查看人员资质", "code": "PERSONNEL_VIEW", "type": "read", "resource": "personnel"},
                    {"name": "管理人员资质", "code": "PERSONNEL_MANAGE", "type": "write", "resource": "personnel"},
                    {"name": "查看资质证书", "code": "CERT_VIEW", "type": "read", "resource": "certifications"},
                    {"name": "管理资质证书", "code": "CERT_MANAGE", "type": "write", "resource": "certifications"},
                ]
            },
            # AI服务权限
            {
                "module": "AI服务",
                "module_id": 6,
                "permissions": [
                    {"name": "使用AI分析需求", "code": "AI_ANALYZE_REQ", "type": "execute", "resource": "ai_tasks"},
                    {"name": "使用AI生成内容", "code": "AI_GENERATE_CONTENT", "type": "execute", "resource": "ai_tasks"},
                    {"name": "使用AI审核质量", "code": "AI_REVIEW_QUALITY", "type": "execute", "resource": "ai_tasks"},
                    {"name": "查看AI任务", "code": "AI_TASK_VIEW", "type": "read", "resource": "ai_tasks"},
                    {"name": "管理AI任务", "code": "AI_TASK_MANAGE", "type": "write", "resource": "ai_tasks"},
                    {"name": "管理Prompt模板", "code": "AI_PROMPT_MANAGE", "type": "write", "resource": "ai_prompts"},
                    {"name": "查看AI使用统计", "code": "AI_USAGE_VIEW", "type": "read", "resource": "ai_usage_logs"},
                ]
            },
            # 协作权限
            {
                "module": "协作",
                "module_id": 7,
                "permissions": [
                    {"name": "发起协作会话", "code": "COLLAB_SESSION_CREATE", "type": "write", "resource": "collaboration_sessions"},
                    {"name": "加入协作会话", "code": "COLLAB_SESSION_JOIN", "type": "execute", "resource": "collaboration_sessions"},
                    {"name": "实时编辑", "code": "COLLAB_REALTIME_EDIT", "type": "write", "resource": "collaboration_sessions"},
                    {"name": "查看协作历史", "code": "COLLAB_HISTORY_VIEW", "type": "read", "resource": "collaboration_events"},
                ]
            },
            # 审批权限
            {
                "module": "审批",
                "module_id": 8,
                "permissions": [
                    {"name": "发起审批", "code": "APPROVAL_SUBMIT", "type": "write", "resource": "approval_workflows"},
                    {"name": "审批文档", "code": "APPROVAL_APPROVE", "type": "execute", "resource": "approval_tasks"},
                    {"name": "驳回审批", "code": "APPROVAL_REJECT", "type": "execute", "resource": "approval_tasks"},
                    {"name": "查看审批记录", "code": "APPROVAL_LOG_VIEW", "type": "read", "resource": "approval_logs"},
                    {"name": "管理审批流程", "code": "APPROVAL_WORKFLOW_MANAGE", "type": "write", "resource": "approval_workflows"},
                ]
            },
            # 系统管理权限
            {
                "module": "系统管理",
                "module_id": 99,
                "permissions": [
                    {"name": "查看审计日志", "code": "AUDIT_LOG_VIEW", "type": "read", "resource": "audit_logs"},
                    {"name": "查看系统日志", "code": "SYSTEM_LOG_VIEW", "type": "read", "resource": "system_logs"},
                    {"name": "系统配置", "code": "SYSTEM_CONFIG", "type": "write", "resource": "system"},
                    {"name": "数据备份", "code": "DATA_BACKUP", "type": "execute", "resource": "system"},
                    {"name": "数据恢复", "code": "DATA_RESTORE", "type": "execute", "resource": "system"},
                ]
            }
        ]

        for module_def in permission_definitions:
            module_name = module_def["module"]
            module_id = module_def["module_id"]

            for perm_def in module_def["permissions"]:
                permission = {
                    # 必填字段
                    "permission_id": self.permission_id_counter,
                    "project_id": 1,  # AIBidComposer项目
                    "source_type": "system_design",
                    "permission_name": perm_def["name"],
                    "permission_code": perm_def["code"],
                    "permission_title": perm_def["name"],
                    "permission_title_cn": perm_def["name"],
                    "permission_title_en": perm_def["code"].lower().replace("_", " ").title(),
                    "permission_title_jp": perm_def["name"],
                    "permission_type": perm_def["type"],

                    # 扩展字段
                    "module_name": module_name,
                    "module_id": module_id,
                    "resource_type": perm_def["resource"],
                    "description": f"{module_name}模块的{perm_def['name']}权限",
                    "is_system_permission": "Y",
                    "version": "1.0"
                }

                self.permissions.append(permission)
                self.permission_id_counter += 1

    def save_roles(self):
        """保存角色定义到JSON文件"""
        print(f"\n保存 {len(self.roles)} 个角色定义...")

        for role in self.roles:
            filename = f"{role['role_code']}-{role['role_name']}.json"
            filepath = self.output_dir / "14-系统角色" / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(role, f, ensure_ascii=False, indent=2)

            print(f"  ✅ 已保存: {filename}")

    def save_permissions(self):
        """保存权限定义到JSON文件"""
        print(f"\n保存 {len(self.permissions)} 个权限定义...")

        for perm in self.permissions:
            filename = f"{perm['permission_code']}-{perm['permission_name']}.json"
            filepath = self.output_dir / "15-权限定义" / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(perm, f, ensure_ascii=False, indent=2)

            if self.permissions.index(perm) < 5 or self.permissions.index(perm) >= len(self.permissions) - 2:
                print(f"  ✅ 已保存: {filename}")
            elif self.permissions.index(perm) == 5:
                print(f"  ... (还有 {len(self.permissions) - 7} 个文件)")

    def print_summary(self):
        """打印生成摘要"""
        print("\n" + "=" * 80)
        print("角色和权限生成摘要")
        print("=" * 80)

        print(f"\n📊 统计信息:")
        print(f"  - 总角色数: {len(self.roles)}")
        print(f"  - 总权限数: {len(self.permissions)}")

        print(f"\n📁 按角色级别分类:")
        level_map = {}
        for role in self.roles:
            level = role['role_level']
            if level not in level_map:
                level_map[level] = []
            level_map[level].append(role['role_name'])

        for level in sorted(level_map.keys()):
            print(f"  Level {level}: {', '.join(level_map[level])}")

        print(f"\n📁 按模块分类权限:")
        module_map = {}
        for perm in self.permissions:
            module = perm['module_name']
            if module not in module_map:
                module_map[module] = 0
            module_map[module] += 1

        for module, count in module_map.items():
            print(f"  {module}: {count} 个权限")

        print("\n" + "=" * 80)


def main():
    """主函数"""
    generator = RolePermissionGenerator()

    print("开始生成角色和权限定义...")

    # 生成角色
    print("\n1. 生成角色定义...")
    generator.generate_roles()

    # 生成权限
    print("\n2. 生成权限定义...")
    generator.generate_permissions()

    # 保存角色
    print("\n3. 保存角色到文件...")
    generator.save_roles()

    # 保存权限
    print("\n4. 保存权限到文件...")
    generator.save_permissions()

    # 打印摘要
    generator.print_summary()

    print("\n✅ 角色和权限生成完成！")


if __name__ == "__main__":
    main()
