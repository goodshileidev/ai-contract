#!/usr/bin/env python3
"""
修复独立JSON文件的验证错误
根据JSONSchema要求补充缺失的必需字段
"""

import json
import os
from pathlib import Path

# 基础目录
BASE_DIR = "/mnt/data/ai-contract/structured-requirements/individual-jsons"

def fix_project_definition():
    """修复项目定义文件"""
    print("修复项目定义文件...")

    filepath = os.path.join(BASE_DIR, "3-项目定义", "AIBC-2025-AIBidComposer智能标书创作平台.json")

    # 读取现有数据
    with open(filepath, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # 添加缺失的必需字段
    # 修改错误的字段名
    if 'project_code' in data:
        data['project_no'] = data.pop('project_code')  # project_code 应该是 project_no

    # 添加缺失的必需字段
    data['project_key'] = 'AIBC'  # 项目键值
    data['app_name'] = 'AIBidComposer'  # 应用名称
    data['company_name'] = 'AIBidComposer科技有限公司'  # 公司名称
    data['default_page_layout_template_id'] = 1  # 默认页面布局模板ID

    # 保存修复后的文件
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print(f"✅ 修复项目定义: {filepath}")

def fix_module_definitions():
    """修复模块定义文件"""
    print("\n修复模块定义文件...")

    module_dir = os.path.join(BASE_DIR, "4-模块定义")

    # 模块编号映射
    module_numbers = {
        'AUTH-用户认证模块.json': 'M001',
        'PARSER-文档解析模块.json': 'M002',
        'CAPABILITY-能力库模块.json': 'M003',
        'GENERATOR-内容生成模块.json': 'M004',
        'TEMPLATE-模板管理模块.json': 'M005',
        'COLLAB-协作编辑模块.json': 'M006',
        'EXPORT-文档导出模块.json': 'M007'
    }

    for filename in os.listdir(module_dir):
        if filename.endswith('.json'):
            filepath = os.path.join(module_dir, filename)

            # 读取现有数据
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)

            # 修改错误的字段名
            if 'module_code' in data:
                # module_code 的值应该保存到 module_key
                data['module_key'] = data['module_code']
                # module_no 是模块编号
                data['module_no'] = module_numbers.get(filename, 'M999')
                # 删除错误的 module_code
                del data['module_code']

            # 确保有 module_key
            if 'module_key' not in data:
                # 从文件名提取
                module_key = filename.split('-')[0]
                data['module_key'] = module_key

            # 确保有 module_no
            if 'module_no' not in data:
                data['module_no'] = module_numbers.get(filename, 'M999')

            # 保存修复后的文件
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)

            print(f"✅ 修复模块定义: {filename}")

def fix_function_definitions():
    """修复功能定义文件"""
    print("\n修复功能定义文件...")

    function_dir = os.path.join(BASE_DIR, "5-功能定义")

    # 功能键值映射
    function_keys = {
        'USER_AUTH-用户认证功能.json': 'USER_AUTH',
        'DOC_PARSE-文档解析功能.json': 'DOC_PARSE',
        'CAP_MGMT-能力管理功能.json': 'CAP_MGMT',
        'AI_GEN-智能生成功能.json': 'AI_GEN',
        'TPL_MGMT-模板管理功能.json': 'TPL_MGMT',
        'COLLAB_EDIT-协作编辑功能.json': 'COLLAB_EDIT',
        'DOC_EXPORT-文档导出功能.json': 'DOC_EXPORT'
    }

    for filename in os.listdir(function_dir):
        if filename.endswith('.json'):
            filepath = os.path.join(function_dir, filename)

            # 读取现有数据
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)

            # 修改错误的字段名
            if 'function_code' in data:
                # function_code 的值应该保存到 function_key
                data['function_key'] = data.pop('function_code')

            # 确保有 function_key
            if 'function_key' not in data:
                # 从文件名提取
                function_key = filename.split('-')[0]
                data['function_key'] = function_key

            # 添加 project_id（所有功能都属于项目1）
            if 'project_id' not in data:
                data['project_id'] = 1

            # 保存修复后的文件
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)

            print(f"✅ 修复功能定义: {filename}")

def main():
    """主函数"""
    print("=" * 60)
    print("开始修复验证错误")
    print("=" * 60)

    # 修复各类文件
    fix_project_definition()
    fix_module_definitions()
    fix_function_definitions()

    print("\n" + "=" * 60)
    print("✅ 所有文件修复完成！")
    print("请运行 validate_individual_files.py 验证修复结果")
    print("=" * 60)

if __name__ == "__main__":
    main()