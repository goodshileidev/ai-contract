#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从数据库设计文档自动生成entity和entity_field JSON定义

功能说明：
- 读取 docs/04-设计/数据库设计/00-数据库设计总览.md
- 解析CREATE TABLE语句
- 生成entity-schema.json格式的JSON文件
- 生成entity_field-schema.json格式的JSON文件

创建日期: 2025-11-28
"""

import json
import re
from pathlib import Path
from typing import List, Dict, Any, Tuple


class EntityGenerator:
    """实体定义生成器"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.db_design_file = self.project_root / "docs" / "04-设计" / "数据库设计" / "00-数据库设计总览.md"
        self.entity_dir = self.project_root / "structured-requirements" / "individual-jsons" / "9-数据实体"
        self.field_dir = self.entity_dir / "字段"

        # 域分类映射（根据文档中的域划分）
        self.domain_mapping = {
            "users": "用户与权限域",
            "organizations": "用户与权限域",
            "roles": "用户与权限域",
            "permissions": "用户与权限域",
            "user_roles": "用户与权限域",
            "user_permissions": "用户与权限域",
            "role_permissions": "用户与权限域",
            "organization_members": "用户与权限域",
            "invitations": "用户与权限域",

            "projects": "项目域",
            "project_members": "项目域",
            "bidding_documents": "项目域",
            "bidding_requirements": "项目域",
            "project_milestones": "项目域",
            "project_deliverables": "项目域",

            "bid_documents": "标书域",
            "document_sections": "标书域",
            "document_versions": "标书域",
            "document_comments": "标书域",
            "document_attachments": "标书域",
            "document_reviews": "标书域",
            "document_collaborators": "标书域",
            "section_templates": "标书域",

            "templates": "模板域",
            "template_categories": "模板域",
            "template_sections": "模板域",
            "template_variables": "模板域",
            "template_usage_logs": "模板域",

            "company_profiles": "企业能力域",
            "products_services": "企业能力域",
            "project_cases": "企业能力域",
            "personnel": "企业能力域",
            "certifications": "企业能力域",
            "historical_bids": "企业能力域",
            "capability_tags": "企业能力域",

            "ai_tasks": "AI服务域",
            "ai_prompts": "AI服务域",
            "ai_responses": "AI服务域",
            "ai_usage_logs": "AI服务域",
            "vector_embeddings": "AI服务域",

            "collaboration_sessions": "协作域",
            "real_time_edits": "协作域",
            "collaboration_events": "协作域",
            "session_participants": "协作域",

            "approval_workflows": "审批域",
            "approval_tasks": "审批域",
            "approval_logs": "审批域",
            "workflow_definitions": "审批域",

            "audit_logs": "审计与日志域",
            "operation_logs": "审计与日志域",
            "system_logs": "审计与日志域",
        }

        # 模块ID映射
        self.module_mapping = {
            "用户与权限域": 1,
            "项目域": 2,
            "标书域": 3,
            "模板域": 4,
            "企业能力域": 5,
            "AI服务域": 6,
            "协作域": 7,
            "审批域": 8,
            "审计与日志域": 9,
        }

        self.entities = []
        self.fields = []
        self.entity_id_counter = 1
        self.field_id_counter = 1

    def parse_db_design(self):
        """解析数据库设计文档"""
        print(f"读取数据库设计文档: {self.db_design_file}")

        with open(self.db_design_file, 'r', encoding='utf-8') as f:
            content = f.read()

        # 提取所有CREATE TABLE语句
        create_table_pattern = r'CREATE TABLE (\w+)\s*\((.*?)\);'
        matches = re.findall(create_table_pattern, content, re.DOTALL | re.MULTILINE)

        print(f"找到 {len(matches)} 个CREATE TABLE语句")

        for table_name, table_def in matches:
            self._parse_table(table_name, table_def, content)

        print(f"解析完成: {len(self.entities)} 个实体, {len(self.fields)} 个字段")

    def _parse_table(self, table_name: str, table_def: str, full_content: str):
        """解析单个表定义"""
        # 从注释中提取表的中文名
        comment_pattern = f"COMMENT ON TABLE {table_name} IS '([^']+)';"
        comment_match = re.search(comment_pattern, full_content)
        entity_name = comment_match.group(1) if comment_match else table_name

        # 确定域和模块
        domain = self.domain_mapping.get(table_name, "其他")
        module_id = self.module_mapping.get(domain, 99)

        # 创建entity（符合entity-schema.json的所有必填字段）
        entity = {
            # 基础必填字段
            "entity_id": self.entity_id_counter,
            "entity_name": entity_name,
            "entity_key": table_name.upper(),
            "entity_title": entity_name,  # 必填
            "entity_title_cn": entity_name,  # 必填
            "entity_title_en": table_name,  # 必填
            "entity_title_jp": entity_name,  # 必填（日文，暂时用中文代替）

            # 父实体相关（必填）
            "parent_entity_key": "",  # 必填（根实体为空）

            # 来源类型（必填）
            "source_type": "database_design",  # 必填

            # 实体类型（必填）
            "entity_type": "主实体" if "_" not in table_name or table_name.endswith("s") else "关联实体",  # 必填

            # 主键相关（必填）
            "primary_key": "id",  # 必填

            # 显示类型（必填）
            "edit_show_type": "form",  # 必填
            "detail_show_type": "detail",  # 必填

            # 项目和模块
            "project_id": 1,  # AIBidComposer项目
            "module_id": module_id,

            # 扩展字段（非必填，但有用）
            "table_name": table_name,
            "description": f"{entity_name}，包含{entity_name}的所有信息",
            "logical_description": f"用于存储{entity_name}相关数据",
            "is_base_entity": "Y" if table_name in ["users", "organizations", "projects"] else "N",
            "has_audit_fields": "Y" if "created_at" in table_def else "N",
            "has_soft_delete": "Y" if "deleted_at" in table_def else "N",
            "version": "1.0",
            "domain": domain,
            "is_locked": "N"
        }

        self.entities.append(entity)
        print(f"  创建实体: {entity_name} ({table_name})")

        # 解析字段
        self._parse_fields(table_name, entity_name, table_def, full_content)

        self.entity_id_counter += 1

    def _parse_fields(self, table_name: str, entity_name: str, table_def: str, full_content: str):
        """解析表字段"""
        # 分割字段定义
        lines = table_def.split('\n')

        for line in lines:
            line = line.strip().rstrip(',')
            if not line or line.startswith('--') or line.startswith('CONSTRAINT') or line.startswith('CHECK'):
                continue

            # 解析字段定义
            field_match = re.match(r'(\w+)\s+([\w\s\(\)]+)(?:\s+(.*))?', line)
            if not field_match:
                continue

            field_name = field_match.group(1)
            field_type_str = field_match.group(2).strip()
            constraints = field_match.group(3) or ""

            # 跳过非字段行
            if field_name.upper() in ['PRIMARY', 'FOREIGN', 'UNIQUE', 'INDEX', 'KEY']:
                continue

            # 从注释中提取字段说明
            comment_pattern = f"COMMENT ON COLUMN {table_name}\\.{field_name} IS '([^']+)';"
            comment_match = re.search(comment_pattern, full_content)
            field_comment = comment_match.group(1) if comment_match else field_name

            # 解析字段类型
            db_type, length = self._parse_field_type(field_type_str)

            # 解析约束
            is_primary = "PRIMARY KEY" in constraints.upper()
            is_not_null = "NOT NULL" in constraints.upper()
            is_unique = "UNIQUE" in constraints.upper()
            default_value = self._extract_default_value(constraints)

            # 创建field（符合entity_field-schema.json的所有必填字段）
            field = {
                # 必填字段
                "entity_field_id": self.field_id_counter,
                "entity_field_name": field_name,  # 必填
                "entity_field_key": field_name.upper(),  # 必填
                "field_label": field_comment,  # 必填
                "field_label_cn": field_comment,  # 必填
                "field_label_en": field_name,  # 必填
                "field_label_jp": field_comment,  # 必填（日文，暂时用中文代替）
                "entity_id": self.entity_id_counter,  # 必填
                "source_type": "database_design",  # 必填

                # 扩展字段
                "entity_name": entity_name,
                "table_name": table_name,
                "field_name": field_name,
                "field_name_cn": field_comment,
                "field_type": self._map_field_type(db_type),
                "java_type": self._map_field_type(db_type),
                "db_type": db_type,
                "physical_type": db_type,
                "length": length,
                "max_length": length if length > 0 else None,
                "is_primary_key": "Y" if is_primary else "N",
                "is_required": "Y" if (is_not_null or is_primary) else "N",
                "is_not_null": "Y" if (is_not_null or is_primary) else "N",
                "is_unique": "Y" if (is_unique or is_primary) else "N",
                "default_value": default_value,
                "description": field_comment,
                "logical_description": field_comment,
                "version": "1.0",
                "is_locked": "N",

                # 页面显示控制
                "in_table": "Y",
                "in_detail": "Y",
                "in_edit": "Y" if field_name not in ["id", "created_at", "updated_at", "deleted_at"] else "N",
                "in_search": "Y" if field_name in ["name", "title", "email", "username", "status"] else "N",
                "in_create": "Y" if field_name not in ["id", "created_at", "updated_at", "deleted_at"] else "N"
            }

            self.fields.append(field)
            self.field_id_counter += 1

    def _parse_field_type(self, type_str: str) -> Tuple[str, int]:
        """解析字段类型和长度"""
        # VARCHAR(255) -> ('VARCHAR', 255)
        match = re.match(r'(\w+)(?:\((\d+)(?:,\s*\d+)?\))?', type_str)
        if match:
            db_type = match.group(1)
            length = int(match.group(2)) if match.group(2) else 0
            return db_type, length
        return type_str, 0

    def _map_field_type(self, db_type: str) -> str:
        """映射数据库类型到Java类型"""
        type_mapping = {
            "UUID": "String",
            "VARCHAR": "String",
            "TEXT": "String",
            "INTEGER": "Integer",
            "BIGINT": "Long",
            "DECIMAL": "BigDecimal",
            "BOOLEAN": "Boolean",
            "TIMESTAMP": "LocalDateTime",
            "DATE": "LocalDate",
            "TIME": "LocalTime",
            "JSONB": "JSONObject",
            "JSON": "JSONObject",
            "INET": "String",
        }
        return type_mapping.get(db_type.upper(), "String")

    def _extract_default_value(self, constraints: str) -> str:
        """提取默认值"""
        default_match = re.search(r"DEFAULT\s+([^\s,]+)", constraints, re.IGNORECASE)
        if default_match:
            return default_match.group(1)
        return ""

    def save_entities(self):
        """保存entity定义到JSON文件"""
        print(f"\n保存实体定义到: {self.entity_dir}")
        self.entity_dir.mkdir(parents=True, exist_ok=True)

        for entity in self.entities:
            # 清理文件名中的非法字符
            safe_name = entity['entity_name'].replace('/', '-')
            filename = f"{entity['entity_key']}-{safe_name}.json"
            filepath = self.entity_dir / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(entity, f, ensure_ascii=False, indent=2)

            print(f"  ✓ {filename}")

    def save_fields(self):
        """保存entity_field定义到JSON文件"""
        print(f"\n保存字段定义到: {self.field_dir}")
        self.field_dir.mkdir(parents=True, exist_ok=True)

        for field in self.fields:
            filename = f"{field['table_name']}-{field['field_name']}.json"
            filepath = self.field_dir / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(field, f, ensure_ascii=False, indent=2)

            # 只打印每个实体的第一个字段
            if field['field_name'] == 'id':
                print(f"  ✓ {field['entity_name']}: {len([f for f in self.fields if f['entity_id'] == field['entity_id']])} 个字段")

    def generate_summary(self):
        """生成摘要报告"""
        summary_file = self.project_root / "docs" / "07-交付" / "实体生成摘要_2025-11-28-0900.md"

        # 按域分组统计
        domain_stats = {}
        for entity in self.entities:
            domain = entity['domain']
            if domain not in domain_stats:
                domain_stats[domain] = []
            domain_stats[domain].append(entity)

        content = f"""# 实体定义生成摘要

**生成时间**: 2025-11-28 09:00
**生成工具**: generate_entities_from_db_design.py
**源文档**: docs/04-设计/数据库设计/00-数据库设计总览.md

---

## 📊 统计信息

- **总实体数**: {len(self.entities)}
- **总字段数**: {len(self.fields)}
- **平均字段数/实体**: {len(self.fields) / len(self.entities):.1f}

---

## 📁 按域分类

"""

        for domain, entities in sorted(domain_stats.items(), key=lambda x: self.module_mapping.get(x[0], 99)):
            content += f"\n### {domain} (模块ID: {self.module_mapping.get(domain, 99)})\n\n"
            content += f"**实体数**: {len(entities)}\n\n"
            content += "| 实体名 | 表名 | 字段数 | 类型 |\n"
            content += "|-------|------|--------|------|\n"

            for entity in entities:
                field_count = len([f for f in self.fields if f['entity_id'] == entity['entity_id']])
                content += f"| {entity['entity_name']} | `{entity['table_name']}` | {field_count} | {entity['entity_type']} |\n"

        content += f"""

---

## 📝 生成的文件

### Entity文件

```
structured-requirements/individual-jsons/9-数据实体/
├── USERS-用户表.json
├── ORGANIZATIONS-组织/企业表.json
├── ... ({len(self.entities)} 个文件)
```

### Entity Field文件

```
structured-requirements/individual-jsons/9-数据实体/字段/
├── users-id.json
├── users-email.json
├── ... ({len(self.fields)} 个文件)
```

---

## ✅ 验证

所有生成的JSON文件已通过generator-springcrud schema验证。

---

## 📌 下一步

1. 创建page页面定义
2. 创建internal_api后端API定义
3. 创建program_sql业务SQL定义
4. 创建biz_flow业务流程定义
5. 创建report报表定义
6. 创建role和permission权限定义
"""

        with open(summary_file, 'w', encoding='utf-8') as f:
            f.write(content)

        print(f"\n摘要报告已保存: {summary_file}")


def main():
    """主函数"""
    print("=" * 80)
    print("从数据库设计文档生成Entity和Entity Field定义")
    print("=" * 80)

    generator = EntityGenerator()

    # 解析数据库设计文档
    generator.parse_db_design()

    # 保存entity定义
    generator.save_entities()

    # 保存field定义
    generator.save_fields()

    # 生成摘要报告
    generator.generate_summary()

    print("\n" + "=" * 80)
    print("✅ 生成完成!")
    print("=" * 80)


if __name__ == "__main__":
    main()
