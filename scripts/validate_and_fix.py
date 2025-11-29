#!/usr/bin/env python3
"""
JSONSchema 验证和修复脚本
用于根据 DDL schema 定义验证和修复生成的业务定义 JSON
"""

import json
import os
from typing import Dict, List, Tuple, Any
from pathlib import Path

# Schema 目录
SCHEMA_DIR = "/mnt/data/PythonProjects/generator-springcrud/src/04-projects/ngs-ex/ddl/schema"
# 业务定义 JSON 目录
JSON_DIR = "/mnt/data/ai-contract/structured-requirements"

# Schema 文件映射到业务 JSON 文件
SCHEMA_MAPPING = {
    # 0-需求层
    "user_requirement-schema.json": "0-需求层/user-requirements.json",
    "user_requirement_piece-schema.json": "0-需求层/requirement-pieces.json",
    "function_point-schema.json": "0-需求层/function-points.json",

    # 1-管理层
    "project-schema.json": "1-管理层/project.json",
    "module-schema.json": "1-管理层/modules.json",
    "function-schema.json": "1-管理层/functions.json",

    # 2-功能层
    "page-schema.json": "2-功能层/pages.json",
    # "process-schema.json": "2-功能层/processes.json",  # 可能需要其他schema
    "report-schema.json": "2-功能层/reports.json",

    # 3-UI详细层
    "page_section-schema.json": "3-UI详细层/page-sections.json",
    "page_field-schema.json": "3-UI详细层/page-fields.json",
    "page_action-schema.json": "3-UI详细层/page-actions.json",
    "page_field_action-schema.json": "3-UI详细层/page-field-actions.json",

    # 4-处理详细层
    "program_sql-schema.json": "4-处理详细层/business-sql.json",
    "data_transform_task-schema.json": "4-处理详细层/data-transformations.json",
    "internal_api-schema.json": "4-处理详细层/internal-apis.json",
    "external_api-schema.json": "4-处理详细层/external-apis.json",

    # 5-报表详细层
    "report_indicator-schema.json": "5-报表详细层/report-metrics.json",
}

class JSONSchemaValidator:
    def __init__(self):
        self.schemas = {}
        self.validation_results = []
        self.fixes_applied = []

    def load_schema(self, schema_file: str) -> Dict:
        """加载 JSONSchema 文件"""
        schema_path = os.path.join(SCHEMA_DIR, schema_file)
        if os.path.exists(schema_path):
            with open(schema_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        return None

    def load_json(self, json_file: str) -> Dict:
        """加载业务 JSON 文件"""
        json_path = os.path.join(JSON_DIR, json_file)
        if os.path.exists(json_path):
            with open(json_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        return None

    def analyze_schema_fields(self, schema: Dict) -> Dict[str, Dict]:
        """分析 schema 字段定义"""
        fields = {}
        if 'properties' in schema:
            for field_name, field_def in schema['properties'].items():
                fields[field_name] = {
                    'type': field_def.get('type', 'string'),
                    'title': field_def.get('title', ''),
                    'required': field_name in schema.get('required', [])
                }
        return fields

    def map_business_to_schema_fields(self, json_file: str) -> Dict[str, str]:
        """映射业务字段到 schema 字段"""
        # 这里定义业务JSON字段到schema字段的映射关系
        mappings = {
            "0-需求层/user-requirements.json": {
                "requirement_no": None,  # schema中没有对应字段
                "requirement_id": "user_requirement_id",
                "requirement_name": "user_requirement_name",
                "requirement_type": "requirement_type",
                "origin_content": "origin_content",
                "project_no": None,
                "project_id": "project_id",
                "module_code": None,
                "module_id": "module_id",
                "function_code": None,
                "function_id": "function_id"
            },
            "0-需求层/function-points.json": {
                "point_no": "function_point_no",
                "point_id": "function_point_id",
                "point_name": "function_point_name",
                "piece_no": None,
                "piece_id": "user_requirement_piece_id",
                "project_id": "project_id",
                "module_id": "module_id",
                "function_id": "function_id"
            },
            # ... 其他映射
        }

        return mappings.get(json_file, {})

    def validate_and_fix(self, schema_file: str, json_file: str) -> Tuple[List[str], List[str]]:
        """验证并修复 JSON 文件"""
        schema = self.load_schema(schema_file)
        business_json = self.load_json(json_file)

        if not schema or not business_json:
            return ([], [])

        errors = []
        fixes = []

        # 分析 schema 字段
        schema_fields = self.analyze_schema_fields(schema)

        # 获取业务JSON的根键
        root_key = list(business_json.keys())[0] if business_json else None

        if root_key and isinstance(business_json[root_key], list):
            # 处理数组类型的业务数据
            items = business_json[root_key]
            for i, item in enumerate(items):
                # 检查必需字段
                for field_name, field_def in schema_fields.items():
                    if field_def['required'] and field_name not in item:
                        errors.append(f"Item {i}: 缺少必需字段 '{field_name}' ({field_def['title']})")

                # 检查字段类型
                for field_name, value in item.items():
                    if field_name in schema_fields:
                        expected_type = schema_fields[field_name]['type']
                        actual_type = self.get_json_type(value)

                        if not self.type_matches(actual_type, expected_type):
                            errors.append(f"Item {i}: 字段 '{field_name}' 类型错误，期望 {expected_type}，实际 {actual_type}")

                            # 尝试修复类型
                            fixed_value = self.fix_type(value, expected_type)
                            if fixed_value is not None:
                                items[i][field_name] = fixed_value
                                fixes.append(f"Item {i}: 修复字段 '{field_name}' 类型从 {actual_type} 到 {expected_type}")

        return (errors, fixes)

    def get_json_type(self, value) -> str:
        """获取 JSON 值的类型"""
        if value is None:
            return "null"
        elif isinstance(value, bool):
            return "boolean"
        elif isinstance(value, int):
            return "integer"
        elif isinstance(value, float):
            return "number"
        elif isinstance(value, str):
            return "string"
        elif isinstance(value, list):
            return "array"
        elif isinstance(value, dict):
            return "object"
        else:
            return "unknown"

    def type_matches(self, actual: str, expected: str) -> bool:
        """检查类型是否匹配"""
        if expected == actual:
            return True
        if expected == "integer" and actual == "number":
            return True
        if expected == "number" and actual == "integer":
            return True
        return False

    def fix_type(self, value, expected_type: str):
        """尝试修复类型"""
        try:
            if expected_type == "integer":
                if isinstance(value, str) and value.isdigit():
                    return int(value)
                elif isinstance(value, float):
                    return int(value)
            elif expected_type == "string":
                return str(value)
            elif expected_type == "number":
                if isinstance(value, str) and value.replace('.', '', 1).isdigit():
                    return float(value)
        except:
            pass
        return None

    def run_validation(self):
        """运行验证"""
        print("=" * 80)
        print("JSONSchema 验证和修复报告")
        print("=" * 80)

        total_errors = 0
        total_fixes = 0

        for schema_file, json_file in SCHEMA_MAPPING.items():
            print(f"\n验证 {json_file}")
            print("-" * 60)

            errors, fixes = self.validate_and_fix(schema_file, json_file)

            if errors:
                print(f"❌ 发现 {len(errors)} 个错误:")
                for error in errors[:5]:  # 只显示前5个错误
                    print(f"  - {error}")
                if len(errors) > 5:
                    print(f"  ... 还有 {len(errors) - 5} 个错误")
            else:
                print("✅ 没有发现错误")

            if fixes:
                print(f"🔧 应用了 {len(fixes)} 个修复:")
                for fix in fixes[:5]:
                    print(f"  - {fix}")
                if len(fixes) > 5:
                    print(f"  ... 还有 {len(fixes) - 5} 个修复")

            total_errors += len(errors)
            total_fixes += len(fixes)

        print("\n" + "=" * 80)
        print(f"验证完成: 发现 {total_errors} 个错误，应用了 {total_fixes} 个修复")
        print("=" * 80)

if __name__ == "__main__":
    validator = JSONSchemaValidator()
    validator.run_validation()