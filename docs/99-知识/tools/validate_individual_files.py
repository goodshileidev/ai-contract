#!/usr/bin/env python3
"""
验证独立的业务定义JSON文件是否符合JSONSchema定义
"""

import json
import os
import jsonschema
from jsonschema import validate, ValidationError
from pathlib import Path

# Schema 目录
SCHEMA_DIR = "/mnt/data/PythonProjects/generator-springcrud/src/04-projects/ngs-ex/ddl/schema"
# 独立文件目录
JSON_DIR = "/mnt/data/ai-contract/structured-requirements/individual-jsons"

# Schema文件映射
VALIDATION_MAP = {
    "0-用户需求": "user_requirement-schema.json",
    "1-功能点": "function_point-schema.json",
    "3-项目定义": "project-schema.json",
    "4-模块定义": "module-schema.json",
    "5-功能定义": "function-schema.json"
}

def load_schema(schema_file):
    """加载JSONSchema文件"""
    schema_path = os.path.join(SCHEMA_DIR, schema_file)
    if os.path.exists(schema_path):
        with open(schema_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    return None

def validate_files_in_directory(directory, schema):
    """验证目录中的所有JSON文件"""
    results = []
    dir_path = os.path.join(JSON_DIR, directory)

    if not os.path.exists(dir_path):
        return results

    files = [f for f in os.listdir(dir_path) if f.endswith('.json')]

    for filename in files:
        filepath = os.path.join(dir_path, filename)

        # 跳过报告文件
        if filename == "生成报告.json":
            continue

        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)

            # 验证
            validate(instance=data, schema=schema)

            results.append({
                'file': filename,
                'status': '✅ 通过',
                'errors': []
            })

        except ValidationError as e:
            results.append({
                'file': filename,
                'status': '❌ 失败',
                'errors': [str(e)]
            })
        except Exception as e:
            results.append({
                'file': filename,
                'status': '⚠️ 错误',
                'errors': [str(e)]
            })

    return results

def generate_validation_report():
    """生成验证报告"""
    print("=" * 60)
    print("JSONSchema 验证报告")
    print("=" * 60)

    all_results = {}
    total_files = 0
    passed_files = 0
    failed_files = 0

    for directory, schema_file in VALIDATION_MAP.items():
        schema = load_schema(schema_file)

        if not schema:
            print(f"\n⚠️ 无法加载Schema: {schema_file}")
            continue

        print(f"\n📁 验证目录: {directory}")
        print(f"   使用Schema: {schema_file}")
        print("-" * 40)

        results = validate_files_in_directory(directory, schema)
        all_results[directory] = results

        for result in results:
            total_files += 1
            status = result['status']

            if '✅' in status:
                passed_files += 1
                print(f"  {status} {result['file']}")
            elif '❌' in status:
                failed_files += 1
                print(f"  {status} {result['file']}")
                for error in result['errors']:
                    print(f"       错误: {error}")
            else:
                print(f"  {status} {result['file']}")
                for error in result['errors']:
                    print(f"       问题: {error}")

    # 生成汇总
    print("\n" + "=" * 60)
    print("📊 验证汇总")
    print("-" * 60)
    print(f"总文件数: {total_files}")
    print(f"✅ 通过: {passed_files}")
    print(f"❌ 失败: {failed_files}")
    print(f"⚠️ 其他: {total_files - passed_files - failed_files}")

    if passed_files == total_files:
        print("\n🎉 所有文件验证通过！")
    else:
        print(f"\n⚠️ 有 {failed_files} 个文件验证失败")

    print("=" * 60)

    # 保存报告
    report = {
        "验证时间": "2025-11-27",
        "总文件数": total_files,
        "通过文件数": passed_files,
        "失败文件数": failed_files,
        "详细结果": all_results
    }

    report_path = os.path.join(JSON_DIR, "验证报告.json")
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=2)

    print(f"\n💾 验证报告已保存: {report_path}")

    return report

if __name__ == "__main__":
    generate_validation_report()