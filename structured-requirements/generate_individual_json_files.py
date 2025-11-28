#!/usr/bin/env python3
"""
根据JSONSchema生成独立的业务定义JSON文件
每个业务定义保存为一个独立文件，按照业务意义命名
"""

import json
import os
from pathlib import Path

# Schema 目录
SCHEMA_DIR = "/mnt/data/PythonProjects/generator-springcrud/src/04-projects/ngs-ex/ddl/schema"
# 输出目录
OUTPUT_DIR = "/mnt/data/ai-contract/structured-requirements/individual-jsons"

def ensure_output_dirs():
    """创建输出目录结构"""
    dirs = [
        "0-用户需求",
        "1-功能点",
        "2-需求分片",
        "3-项目定义",
        "4-模块定义",
        "5-功能定义",
        "6-页面定义",
        "7-流程定义",
        "8-报表定义"
    ]

    for dir_name in dirs:
        dir_path = os.path.join(OUTPUT_DIR, dir_name)
        os.makedirs(dir_path, exist_ok=True)

    print(f"✅ 创建输出目录结构完成")

def generate_user_requirements():
    """生成用户需求独立文件"""
    requirements = [
        {
            "user_requirement_id": 1,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "用户注册和登录",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 1,
            "function_id": 1,
            "origin_content": "系统需要支持用户注册、登录、找回密码等基础认证功能",
            "extension_requires": "需要支持邮箱注册、手机号注册、第三方OAuth登录（微信、企业微信）",
            "final_content": "实现完整的用户认证体系，包括多种注册方式、安全的密码管理、会话管理、多因素认证等",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 2,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "招标文件智能解析",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 2,
            "function_id": 2,
            "origin_content": "系统需要能够自动解析Word、PDF格式的招标文件，提取关键信息",
            "extension_requires": "使用AI技术识别需求点、评分标准、资质要求、商务条款等关键信息",
            "final_content": "基于大语言模型的智能文档解析引擎，支持多格式文档解析，自动提取结构化需求信息",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 3,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "企业能力库管理",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 3,
            "function_id": 3,
            "origin_content": "建立企业的产品、服务、案例、人员、资质等能力信息库",
            "extension_requires": "支持多维度能力标签、智能分类、向量化存储便于语义检索",
            "final_content": "构建全面的企业能力知识库，支持结构化存储和智能检索，为标书生成提供素材支撑",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 4,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "智能内容生成",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 4,
            "function_id": 4,
            "origin_content": "基于需求和企业能力，自动生成标书内容",
            "extension_requires": "使用GPT-4等大模型，结合RAG技术，生成专业的技术方案和商务方案",
            "final_content": "AI驱动的标书内容生成引擎，基于需求匹配和企业能力，自动生成高质量的标书文本",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 5,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "标书模板管理",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 5,
            "function_id": 5,
            "origin_content": "提供标书模板的创建、编辑、管理功能",
            "extension_requires": "支持行业模板、企业模板、动态变量、智能推荐",
            "final_content": "灵活的标书模板系统，支持多层级模板管理、变量替换、条件渲染等高级功能",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 6,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "多人协作编辑",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 6,
            "function_id": 6,
            "origin_content": "支持多人同时编辑标书文档",
            "extension_requires": "实时协作、版本控制、评论批注、任务分配",
            "final_content": "基于CRDT技术的实时协作系统，支持多人并发编辑、冲突解决、历史追溯",
            "version": "1.0",
            "is_locked": "N"
        },
        {
            "user_requirement_id": 7,
            "user_document_id": 1,
            "user_document_name": "AIBidComposer产品需求文档",
            "user_requirement_name": "文档导出功能",
            "requirement_type": "功能需求",
            "project_id": 1,
            "module_id": 7,
            "function_id": 7,
            "origin_content": "将编辑完成的标书导出为Word、PDF等格式",
            "extension_requires": "保持格式完整性、支持水印、电子签章",
            "final_content": "专业的文档导出引擎，确保导出文档的格式规范性和完整性",
            "version": "1.0",
            "is_locked": "N"
        }
    ]

    # 保存每个需求为独立文件
    for req in requirements:
        filename = f"{req['user_requirement_id']:03d}-{req['user_requirement_name']}.json"
        filepath = os.path.join(OUTPUT_DIR, "0-用户需求", filename)

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(req, f, ensure_ascii=False, indent=2)

        print(f"✅ 生成: {filepath}")

    return requirements

def generate_function_points():
    """生成功能点独立文件"""
    function_points = [
        {
            "function_point_id": 1,
            "function_point_name": "用户注册功能",
            "function_point_no": "FP001",
            "project_id": 1,
            "module_id": 1,
            "function_id": 1,
            "user_requirement_id": 1,
            "demand_description": "实现用户通过邮箱或手机号注册账号",
            "basic_description": "用户填写基本信息完成注册，发送验证邮件/短信",
            "detail_description": "包括表单验证、重复检查、验证码发送、激活流程等",
            "is_locked": "N"
        },
        {
            "function_point_id": 2,
            "function_point_name": "用户登录功能",
            "function_point_no": "FP002",
            "project_id": 1,
            "module_id": 1,
            "function_id": 1,
            "user_requirement_id": 1,
            "demand_description": "用户通过账号密码或第三方OAuth登录",
            "basic_description": "支持账号密码登录、记住登录状态、自动登录",
            "detail_description": "JWT令牌管理、会话控制、登录日志记录、异常登录检测",
            "is_locked": "N"
        },
        {
            "function_point_id": 3,
            "function_point_name": "Word文档解析",
            "function_point_no": "FP003",
            "project_id": 1,
            "module_id": 2,
            "function_id": 2,
            "user_requirement_id": 2,
            "demand_description": "解析Word格式的招标文件",
            "basic_description": "提取文档结构、段落、表格、图片等内容",
            "detail_description": "使用python-docx库解析，保持格式信息，识别章节结构",
            "is_locked": "N"
        },
        {
            "function_point_id": 4,
            "function_point_name": "PDF文档解析",
            "function_point_no": "FP004",
            "project_id": 1,
            "module_id": 2,
            "function_id": 2,
            "user_requirement_id": 2,
            "demand_description": "解析PDF格式的招标文件",
            "basic_description": "提取PDF中的文本、表格、图片内容",
            "detail_description": "使用PyPDF2/pdfplumber库，处理扫描件OCR识别",
            "is_locked": "N"
        },
        {
            "function_point_id": 5,
            "function_point_name": "需求信息提取",
            "function_point_no": "FP005",
            "project_id": 1,
            "module_id": 2,
            "function_id": 2,
            "user_requirement_id": 2,
            "demand_description": "从文档中智能提取需求信息",
            "basic_description": "识别技术需求、商务需求、评分标准等",
            "detail_description": "使用LLM进行语义理解和信息抽取，结构化存储需求",
            "is_locked": "N"
        },
        {
            "function_point_id": 6,
            "function_point_name": "产品信息管理",
            "function_point_no": "FP006",
            "project_id": 1,
            "module_id": 3,
            "function_id": 3,
            "user_requirement_id": 3,
            "demand_description": "管理企业的产品和服务信息",
            "basic_description": "产品的增删改查、分类管理、特性描述",
            "detail_description": "支持富文本描述、图片上传、技术参数、应用场景等",
            "is_locked": "N"
        },
        {
            "function_point_id": 7,
            "function_point_name": "项目案例管理",
            "function_point_no": "FP007",
            "project_id": 1,
            "module_id": 3,
            "function_id": 3,
            "user_requirement_id": 3,
            "demand_description": "管理历史项目案例",
            "basic_description": "案例的录入、编辑、分类、搜索",
            "detail_description": "包括客户信息、项目规模、实施效果、亮点提炼等",
            "is_locked": "N"
        },
        {
            "function_point_id": 8,
            "function_point_name": "人员资质管理",
            "function_point_no": "FP008",
            "project_id": 1,
            "module_id": 3,
            "function_id": 3,
            "user_requirement_id": 3,
            "demand_description": "管理团队成员和资质证书",
            "basic_description": "人员简历、技能特长、资质证书的管理",
            "detail_description": "支持批量导入、资质有效期提醒、人员画像生成",
            "is_locked": "N"
        },
        {
            "function_point_id": 9,
            "function_point_name": "需求能力匹配",
            "function_point_no": "FP009",
            "project_id": 1,
            "module_id": 4,
            "function_id": 4,
            "user_requirement_id": 4,
            "demand_description": "将需求与企业能力进行智能匹配",
            "basic_description": "基于语义相似度匹配相关能力",
            "detail_description": "向量化检索、知识图谱推理、匹配度评分",
            "is_locked": "N"
        },
        {
            "function_point_id": 10,
            "function_point_name": "技术方案生成",
            "function_point_no": "FP010",
            "project_id": 1,
            "module_id": 4,
            "function_id": 4,
            "user_requirement_id": 4,
            "demand_description": "自动生成技术解决方案",
            "basic_description": "基于需求和能力生成技术方案文本",
            "detail_description": "调用GPT-4 API，结合RAG技术，生成专业技术内容",
            "is_locked": "N"
        },
        {
            "function_point_id": 11,
            "function_point_name": "商务方案生成",
            "function_point_no": "FP011",
            "project_id": 1,
            "module_id": 4,
            "function_id": 4,
            "user_requirement_id": 4,
            "demand_description": "自动生成商务方案",
            "basic_description": "生成报价、服务承诺、实施计划等",
            "detail_description": "基于模板和规则引擎，结合AI优化商务条款",
            "is_locked": "N"
        }
    ]

    # 保存每个功能点为独立文件
    for fp in function_points:
        filename = f"{fp['function_point_no']}-{fp['function_point_name']}.json"
        filepath = os.path.join(OUTPUT_DIR, "1-功能点", filename)

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(fp, f, ensure_ascii=False, indent=2)

        print(f"✅ 生成: {filepath}")

    return function_points

def generate_projects():
    """生成项目定义独立文件"""
    projects = [
        {
            "project_id": 1,
            "project_name": "AIBidComposer智能标书创作平台",
            "project_code": "AIBC-2025",
            "project_description": "基于AI技术的企业级标书智能创作SaaS平台",
            "project_type": "产品研发",
            "start_date": "2025-11-01",
            "end_date": "2026-06-30",
            "project_status": "进行中",
            "project_manager": "张三",
            "budget": 5000000,
            "team_size": 20
        }
    ]

    # 保存项目定义文件
    for proj in projects:
        filename = f"{proj['project_code']}-{proj['project_name']}.json"
        filepath = os.path.join(OUTPUT_DIR, "3-项目定义", filename)

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(proj, f, ensure_ascii=False, indent=2)

        print(f"✅ 生成: {filepath}")

    return projects

def generate_modules():
    """生成模块定义独立文件"""
    modules = [
        {
            "module_id": 1,
            "module_name": "用户认证模块",
            "module_code": "AUTH",
            "module_description": "用户注册、登录、权限管理",
            "project_id": 1,
            "module_type": "基础模块",
            "priority": "高"
        },
        {
            "module_id": 2,
            "module_name": "文档解析模块",
            "module_code": "PARSER",
            "module_description": "招标文件的智能解析和信息提取",
            "project_id": 1,
            "module_type": "AI模块",
            "priority": "高"
        },
        {
            "module_id": 3,
            "module_name": "能力库模块",
            "module_code": "CAPABILITY",
            "module_description": "企业能力信息的管理和检索",
            "project_id": 1,
            "module_type": "业务模块",
            "priority": "高"
        },
        {
            "module_id": 4,
            "module_name": "内容生成模块",
            "module_code": "GENERATOR",
            "module_description": "AI驱动的标书内容生成",
            "project_id": 1,
            "module_type": "AI模块",
            "priority": "高"
        },
        {
            "module_id": 5,
            "module_name": "模板管理模块",
            "module_code": "TEMPLATE",
            "module_description": "标书模板的创建和管理",
            "project_id": 1,
            "module_type": "业务模块",
            "priority": "中"
        },
        {
            "module_id": 6,
            "module_name": "协作编辑模块",
            "module_code": "COLLAB",
            "module_description": "多人实时协作编辑",
            "project_id": 1,
            "module_type": "功能模块",
            "priority": "中"
        },
        {
            "module_id": 7,
            "module_name": "文档导出模块",
            "module_code": "EXPORT",
            "module_description": "标书文档的格式转换和导出",
            "project_id": 1,
            "module_type": "功能模块",
            "priority": "高"
        }
    ]

    # 保存每个模块为独立文件
    for mod in modules:
        filename = f"{mod['module_code']}-{mod['module_name']}.json"
        filepath = os.path.join(OUTPUT_DIR, "4-模块定义", filename)

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(mod, f, ensure_ascii=False, indent=2)

        print(f"✅ 生成: {filepath}")

    return modules

def generate_functions():
    """生成功能定义独立文件"""
    functions = [
        {
            "function_id": 1,
            "function_name": "用户认证功能",
            "function_code": "USER_AUTH",
            "function_description": "用户注册、登录、密码管理等",
            "module_id": 1,
            "function_type": "基础功能",
            "is_core": "Y"
        },
        {
            "function_id": 2,
            "function_name": "文档智能解析",
            "function_code": "DOC_PARSE",
            "function_description": "解析Word、PDF等格式的招标文件",
            "module_id": 2,
            "function_type": "AI功能",
            "is_core": "Y"
        },
        {
            "function_id": 3,
            "function_name": "能力信息管理",
            "function_code": "CAP_MANAGE",
            "function_description": "产品、案例、人员等能力信息管理",
            "module_id": 3,
            "function_type": "业务功能",
            "is_core": "Y"
        },
        {
            "function_id": 4,
            "function_name": "智能内容生成",
            "function_code": "CONTENT_GEN",
            "function_description": "基于AI的标书内容生成",
            "module_id": 4,
            "function_type": "AI功能",
            "is_core": "Y"
        },
        {
            "function_id": 5,
            "function_name": "模板管理",
            "function_code": "TEMPLATE_MGT",
            "function_description": "标书模板的创建、编辑、管理",
            "module_id": 5,
            "function_type": "业务功能",
            "is_core": "N"
        },
        {
            "function_id": 6,
            "function_name": "实时协作",
            "function_code": "REALTIME_COLLAB",
            "function_description": "多用户实时协作编辑",
            "module_id": 6,
            "function_type": "协作功能",
            "is_core": "N"
        },
        {
            "function_id": 7,
            "function_name": "文档导出",
            "function_code": "DOC_EXPORT",
            "function_description": "导出为Word、PDF等格式",
            "module_id": 7,
            "function_type": "输出功能",
            "is_core": "Y"
        }
    ]

    # 保存每个功能为独立文件
    for func in functions:
        filename = f"{func['function_code']}-{func['function_name']}.json"
        filepath = os.path.join(OUTPUT_DIR, "5-功能定义", filename)

        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(func, f, ensure_ascii=False, indent=2)

        print(f"✅ 生成: {filepath}")

    return functions

def generate_summary_report():
    """生成汇总报告"""
    report = {
        "生成时间": "2025-11-27",
        "生成说明": "根据JSONSchema定义生成独立的业务定义JSON文件",
        "文件统计": {
            "用户需求": 7,
            "功能点": 11,
            "项目定义": 1,
            "模块定义": 7,
            "功能定义": 7,
            "总计": 33
        },
        "目录结构": [
            "individual-jsons/",
            "├── 0-用户需求/       (7个文件)",
            "├── 1-功能点/         (11个文件)",
            "├── 3-项目定义/       (1个文件)",
            "├── 4-模块定义/       (7个文件)",
            "└── 5-功能定义/       (7个文件)"
        ],
        "命名规则": {
            "用户需求": "{需求ID:03d}-{需求名称}.json",
            "功能点": "{功能点编号}-{功能点名称}.json",
            "项目定义": "{项目代码}-{项目名称}.json",
            "模块定义": "{模块代码}-{模块名称}.json",
            "功能定义": "{功能代码}-{功能名称}.json"
        }
    }

    report_path = os.path.join(OUTPUT_DIR, "生成报告.json")
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=2)

    print(f"\n✅ 生成汇总报告: {report_path}")
    return report

def main():
    """主函数"""
    print("=" * 60)
    print("开始生成独立的业务定义JSON文件")
    print("=" * 60)

    # 创建输出目录
    ensure_output_dirs()

    # 生成各类文件
    print("\n📄 生成用户需求文件...")
    generate_user_requirements()

    print("\n📄 生成功能点文件...")
    generate_function_points()

    print("\n📄 生成项目定义文件...")
    generate_projects()

    print("\n📄 生成模块定义文件...")
    generate_modules()

    print("\n📄 生成功能定义文件...")
    generate_functions()

    # 生成汇总报告
    print("\n📊 生成汇总报告...")
    report = generate_summary_report()

    print("\n" + "=" * 60)
    print("✅ 所有独立JSON文件生成完成！")
    print(f"📁 输出目录: {OUTPUT_DIR}")
    print("=" * 60)

if __name__ == "__main__":
    main()