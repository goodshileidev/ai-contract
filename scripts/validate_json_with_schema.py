#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用generator-springcrud的schema验证ai-contract的业务定义json

功能说明：
- 读取 structured-requirements/individual-jsons/ 下的所有json文件
- 使用 generator-springcrud 项目的 schema 进行验证
- 生成验证报告和修复建议

创建日期: 2025-11-28
"""

import json
from pathlib import Path
from typing import Dict, List, Any, Tuple
import jsonschema
from jsonschema import Draft7Validator


class JsonSchemaValidator:
    """业务定义JSON验证器（使用外部schema）"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.json_dir = self.project_root / "structured-requirements" / "individual-jsons"

        # generator-springcrud项目的schema目录
        self.schema_dir = Path("/mnt/data/PythonProjects/generator-springcrud/src/04-projects/ngs-ex/ddl/schema")

        self.schemas = {}  # schema缓存
        self.errors = []
        self.warnings = []

    def load_schema(self, schema_name: str) -> Dict:
        """加载指定的schema文件"""
        if schema_name in self.schemas:
            return self.schemas[schema_name]

        schema_file = self.schema_dir / f"{schema_name}-schema.json"
        if not schema_file.exists():
            raise FileNotFoundError(f"Schema文件不存在: {schema_file}")

        with open(schema_file, 'r', encoding='utf-8') as f:
            schema = json.load(f)
            self.schemas[schema_name] = schema
            return schema

    def infer_schema_from_filename(self, json_file: Path) -> str:
        """根据文件名和目录推断应该使用的schema"""
        filename = json_file.stem
        parent_dir = json_file.parent.name

        # 0-用户需求目录下的文件使用 user_requirement schema
        if parent_dir == '0-用户需求':
            return 'user_requirement'

        # 9-数据实体目录下的文件使用 entity schema
        if parent_dir == '9-数据实体':
            return 'entity'

        # 9-数据实体/字段目录下的文件使用 entity_field schema
        if parent_dir == '字段' and '9-数据实体' in str(json_file.parent.parent):
            return 'entity_field'

        # 11-内部API目录下的文件使用 internal_api schema
        if parent_dir == '11-内部API':
            return 'internal_api'

        # 14-系统角色目录下的文件使用 role schema
        if parent_dir == '14-系统角色':
            return 'role'

        # 15-权限定义目录下的文件使用 permission schema
        if parent_dir == '15-权限定义':
            return 'permission'

        # 12-页面定义目录下的文件使用 page schema
        if parent_dir == '12-页面定义':
            return 'page'

        # 13-页面组件目录下的文件使用 page_section schema
        if parent_dir == '13-页面组件':
            return 'page_section'

        # 16-业务SQL目录下的文件使用 program_sql schema
        if parent_dir == '16-业务SQL':
            return 'program_sql'

        # 17-业务流程目录下的文件使用 biz_flow schema
        if parent_dir == '17-业务流程':
            return 'biz_flow'

        # 映射规则（针对文件名）
        schema_mapping = {
            # 4-模块定义（只匹配纯模块关键字，避免与功能混淆）
            '-用户认证模块': 'module',
            '-文档解析模块': 'module',
            '-能力库模块': 'module',
            '-内容生成模块': 'module',
            '-模板管理模块': 'module',
            '-协作编辑模块': 'module',
            '-文档导出模块': 'module',

            # 5-功能定义（更精确的匹配）
            'USER_AUTH-用户认证功能': 'function',
            'DOC_PARSE-文档智能解析': 'function',
            'CAP_MANAGE-能力信息管理': 'function',
            'CONTENT_GEN-智能内容生成': 'function',
            'TEMPLATE_MGT-模板管理': 'function',
            'REALTIME_COLLAB-实时协作': 'function',
            'DOC_EXPORT-文档导出': 'function',

            # 3-项目定义
            'AIBC': 'project',
        }

        # 从文件名中提取关键字
        for key, schema_name in schema_mapping.items():
            if key in filename:
                return schema_name

        return None

    def validate_json_file(self, json_file: Path) -> Dict:
        """验证单个JSON文件"""
        result = {
            'file': json_file.name,
            'path': str(json_file.relative_to(self.project_root)),
            'schema_used': None,
            'is_valid': False,
            'errors': [],
            'warnings': [],
            'extra_fields': [],
            'missing_fields': []
        }

        try:
            # 读取JSON文件
            with open(json_file, 'r', encoding='utf-8') as f:
                data = json.load(f)

            # 推断schema
            schema_name = self.infer_schema_from_filename(json_file)
            if not schema_name:
                result['warnings'].append("无法推断schema类型，跳过验证")
                return result

            result['schema_used'] = schema_name

            # 加载schema
            try:
                schema = self.load_schema(schema_name)
            except FileNotFoundError as e:
                result['errors'].append(str(e))
                return result

            # 验证
            validator = Draft7Validator(schema)
            validation_errors = list(validator.iter_errors(data))

            if not validation_errors:
                result['is_valid'] = True
            else:
                for error in validation_errors:
                    path = ".".join(str(p) for p in error.path) if error.path else "root"
                    result['errors'].append({
                        'path': path,
                        'message': error.message,
                        'validator': error.validator
                    })

            # 检查额外字段
            schema_properties = set(schema.get('properties', {}).keys())
            data_properties = set(data.keys())
            extra_fields = data_properties - schema_properties
            if extra_fields:
                result['extra_fields'] = list(extra_fields)
                result['warnings'].append(f"发现额外字段: {', '.join(extra_fields)}")

            # 检查缺少字段
            required_fields = set(schema.get('required', []))
            missing_fields = required_fields - data_properties
            if missing_fields:
                result['missing_fields'] = list(missing_fields)

        except json.JSONDecodeError as e:
            result['errors'].append({'message': f"JSON解析错误: {e}"})
        except Exception as e:
            result['errors'].append({'message': f"验证异常: {e}"})

        return result

    def validate_all(self) -> Dict[str, List[Dict]]:
        """验证所有JSON文件"""
        results = {
            '0-用户需求': [],
            '3-项目定义': [],
            '4-模块定义': [],
            '5-功能定义': [],
            '9-数据实体': [],
            '9-数据实体/字段': [],
            '11-内部API': [],
            '12-页面定义': [],
            '13-页面组件': [],
            '14-系统角色': [],
            '15-权限定义': [],
            '16-业务SQL': [],
            '17-业务流程': []
        }

        for category in results.keys():
            if category == '9-数据实体/字段':
                category_dir = self.json_dir / "9-数据实体" / "字段"
            else:
                category_dir = self.json_dir / category

            if not category_dir.exists():
                continue

            json_files = list(category_dir.glob("*.json"))
            print(f"\n验证 {category} ({len(json_files)} 个文件)...")

            for json_file in sorted(json_files):
                if json_files.index(json_file) < 5 or json_files.index(json_file) >= len(json_files) - 2:
                    # 只打印前5个和后2个，避免输出太多
                    print(f"  验证: {json_file.name}")
                elif json_files.index(json_file) == 5:
                    print(f"  ... ({len(json_files) - 7} 个文件)")

                result = self.validate_json_file(json_file)
                results[category].append(result)

        return results

    def print_report(self, results: Dict[str, List[Dict]]):
        """打印验证报告"""
        print("\n" + "=" * 80)
        print("业务定义JSON验证报告")
        print("=" * 80)

        total_files = sum(len(v) for v in results.values())
        total_valid = sum(1 for v in results.values() for r in v if r['is_valid'])
        total_invalid = total_files - total_valid

        print(f"\n📊 总体统计:")
        print(f"  总文件数: {total_files}")
        print(f"  有效文件: {total_valid} ✅")
        print(f"  无效文件: {total_invalid} ❌")

        # 按类别统计
        print(f"\n📁 分类统计:")
        for category, category_results in results.items():
            if not category_results:
                continue

            valid_count = sum(1 for r in category_results if r['is_valid'])
            total_count = len(category_results)
            status = "✅" if valid_count == total_count else "⚠️"

            print(f"  {status} {category:20s}: {valid_count}/{total_count} 通过")

        # 详细错误报告
        if total_invalid > 0:
            print(f"\n❌ 验证失败的文件:")
            for category, category_results in results.items():
                for result in category_results:
                    if not result['is_valid'] and result['errors']:
                        print(f"\n  文件: {result['path']}")
                        print(f"  Schema: {result['schema_used']}")

                        for error in result['errors'][:5]:  # 只显示前5个错误
                            if isinstance(error, dict):
                                print(f"    ❌ {error.get('path', 'root')}: {error.get('message', str(error))}")
                            else:
                                print(f"    ❌ {error}")

                        if len(result['errors']) > 5:
                            print(f"    ... 还有 {len(result['errors']) - 5} 个错误")

        # 警告报告
        total_warnings = sum(len(r.get('warnings', [])) for v in results.values() for r in v)
        if total_warnings > 0:
            print(f"\n⚠️  警告信息:")
            for category, category_results in results.items():
                for result in category_results:
                    if result.get('warnings'):
                        print(f"\n  文件: {result['file']}")
                        for warning in result['warnings']:
                            print(f"    ⚠️  {warning}")

                        if result.get('extra_fields'):
                            print(f"    建议: 考虑删除额外字段或更新schema")

        # 总结
        print("\n" + "=" * 80)
        if total_invalid == 0:
            print("✅ 验证通过! 所有JSON文件都符合schema规范")
        else:
            print(f"⚠️  发现 {total_invalid} 个文件存在问题，请检查并修复")
        print("=" * 80)

    def generate_fix_suggestions(self, results: Dict[str, List[Dict]]) -> str:
        """生成修复建议"""
        suggestions = []

        suggestions.append("# 修复建议\n")
        suggestions.append("## 需要修复的文件\n")

        for category, category_results in results.items():
            for result in category_results:
                if not result['is_valid']:
                    suggestions.append(f"\n### {result['path']}\n")
                    suggestions.append(f"**Schema**: {result['schema_used']}\n")

                    if result.get('missing_fields'):
                        suggestions.append("\n**缺少必填字段**:")
                        for field in result['missing_fields']:
                            suggestions.append(f"- `{field}`: 需要添加")

                    if result.get('extra_fields'):
                        suggestions.append("\n**额外字段**:")
                        for field in result['extra_fields']:
                            suggestions.append(f"- `{field}`: 不在schema定义中，建议删除或更新schema")

                    if result.get('errors'):
                        suggestions.append("\n**验证错误**:")
                        for error in result['errors'][:10]:
                            if isinstance(error, dict):
                                suggestions.append(f"- {error.get('path', 'root')}: {error.get('message', '')}")

        return "\n".join(suggestions)


def main():
    """主函数"""
    validator = JsonSchemaValidator()
    results = validator.validate_all()
    validator.print_report(results)

    # 生成修复建议
    suggestions = validator.generate_fix_suggestions(results)
    suggestion_file = validator.project_root / "docs" / "07-交付" / "JSON验证修复建议_2025-11-28-0830.md"
    with open(suggestion_file, 'w', encoding='utf-8') as f:
        f.write(suggestions)

    print(f"\n💡 修复建议已保存到: {suggestion_file.relative_to(validator.project_root)}")

    # 返回状态码
    total_invalid = sum(1 for v in results.values() for r in v if not r['is_valid'])
    return 0 if total_invalid == 0 else 1


if __name__ == "__main__":
    exit(main())
