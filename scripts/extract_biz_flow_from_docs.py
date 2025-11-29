#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从04-设计文档中抽取业务流程定义

功能说明：
- 读取 docs/04-设计/ 目录下的Markdown文档
- 提取文档中的Mermaid流程图
- 提取业务需求描述
- 生成符合biz_flow schema的JSON文件

数据来源：docs/04-设计/API接口设计/*.md
创建日期: 2025-11-28
"""

import json
import re
from pathlib import Path
from typing import Dict, List, Any, Optional


class DocumentFlowExtractor:
    """从设计文档中抽取业务流程"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.docs_dir = self.project_root / "docs" / "04-设计"
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons" / "17-业务流程"
        self.flow_id_counter = 1

        # 确保输出目录存在
        self.output_dir.mkdir(parents=True, exist_ok=True)

        # 模块ID映射
        self.module_map = {
            "认证授权": 1,
            "项目管理": 2,
            "文档管理": 3,
            "模板管理": 4,
            "企业能力": 5,
            "AI服务": 6,
            "协作": 7,
            "审批": 8,
            "系统管理": 9,
            "导出": 10
        }

    def extract_all_flows(self):
        """从所有设计文档中抽取业务流程"""

        print("=" * 80)
        print("从设计文档中抽取业务流程")
        print("=" * 80)
        print(f"数据来源: {self.docs_dir}")
        print()

        # 读取API接口设计文档
        api_docs_dir = self.docs_dir / "API接口设计"
        if not api_docs_dir.exists():
            print(f"❌ 目录不存在: {api_docs_dir}")
            return

        md_files = list(api_docs_dir.glob("*.md"))
        print(f"找到 {len(md_files)} 个Markdown文档")
        print()

        flows_extracted = 0

        for md_file in sorted(md_files):
            if md_file.name.startswith("00-") or md_file.name == "INDEX.md":
                # 跳过总览和索引文档
                continue

            print(f"正在处理: {md_file.name}")

            # 读取文档内容
            with open(md_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 提取Mermaid流程图
            flows = self._extract_mermaid_flows(content, md_file.stem)

            if flows:
                for flow in flows:
                    self._create_flow_file(flow, md_file.stem)
                    flows_extracted += 1
                    print(f"  ✅ 提取流程: {flow['name']}")
            else:
                print(f"  ⚠️  未找到Mermaid流程图")

            print()

        print("=" * 80)
        print(f"✅ 完成! 共从文档中提取 {flows_extracted} 个业务流程")
        print(f"📁 输出目录: {self.output_dir.relative_to(self.project_root)}")
        print("=" * 80)

    def _extract_mermaid_flows(self, content: str, file_stem: str) -> List[Dict[str, Any]]:
        """从文档内容中提取Mermaid流程图"""

        flows = []

        # 正则表达式匹配Mermaid代码块
        # 支持: ```mermaid 或 ```mermaid graph TD
        mermaid_pattern = r'```mermaid\s*(.*?)```'
        matches = re.findall(mermaid_pattern, content, re.DOTALL)

        for idx, mermaid_code in enumerate(matches):
            # 只提取sequenceDiagram和flowchart/graph类型
            if 'sequenceDiagram' in mermaid_code or 'graph' in mermaid_code or 'flowchart' in mermaid_code:

                # 尝试从上下文提取流程描述
                # 查找Mermaid代码块前的标题
                title = self._extract_flow_title(content, mermaid_code)
                description = self._extract_flow_description(content, mermaid_code)

                # 推断模块名称
                module_name = self._infer_module_from_filename(file_stem)

                flow = {
                    "name": title or f"{file_stem}-流程{idx+1}",
                    "key": f"{file_stem.upper()}_FLOW_{idx+1}",
                    "module": module_name,
                    "demand_description": description or "从设计文档中提取的业务流程",
                    "basic_description": "参见文档详细说明",
                    "mermaid": mermaid_code.strip(),
                    "source_file": file_stem
                }

                flows.append(flow)

        return flows

    def _extract_flow_title(self, content: str, mermaid_code: str) -> Optional[str]:
        """从文档内容中提取流程标题"""

        # 找到Mermaid代码块的位置
        code_pos = content.find(mermaid_code)
        if code_pos == -1:
            return None

        # 向前查找最近的标题
        before_content = content[:code_pos]

        # 查找 ### 或 ## 标题
        title_pattern = r'###?\s+(.+?)(?:\n|$)'
        matches = list(re.finditer(title_pattern, before_content))

        if matches:
            # 返回最后一个（最近的）标题
            last_match = matches[-1]
            title = last_match.group(1).strip()

            # 清理标题中的特殊字符
            title = re.sub(r'\[.*?\]', '', title)  # 移除链接
            title = title.replace('*', '').replace('_', '').strip()

            return title

        return None

    def _extract_flow_description(self, content: str, mermaid_code: str) -> Optional[str]:
        """从文档内容中提取流程描述"""

        # 找到Mermaid代码块的位置
        code_pos = content.find(mermaid_code)
        if code_pos == -1:
            return None

        # 向前查找描述文本（在标题和代码块之间）
        before_content = content[:code_pos]

        # 提取最后一个标题后的文本
        title_pattern = r'###?\s+.+?\n(.*?)$'
        match = re.search(title_pattern, before_content, re.DOTALL)

        if match:
            description = match.group(1).strip()

            # 取描述的前200个字符
            if len(description) > 200:
                description = description[:197] + "..."

            return description

        return None

    def _infer_module_from_filename(self, filename: str) -> str:
        """从文件名推断模块名称"""

        module_mapping = {
            "01-认证授权API": "认证授权",
            "02-项目管理API": "项目管理",
            "03-文档管理API": "文档管理",
            "04-AI服务API": "AI服务",
            "05-模板管理API": "模板管理",
            "06-企业能力API": "企业能力",
            "07-协作API": "协作",
            "08-导出API": "导出"
        }

        return module_mapping.get(filename, "系统管理")

    def _create_flow_file(self, flow_def: Dict[str, Any], source_file: str):
        """创建单个业务流程定义文件"""

        module_name = flow_def["module"]
        module_id = self.module_map.get(module_name, 0)

        mermaid_text = flow_def["mermaid"]

        biz_flow = {
            "biz_flow_id": self.flow_id_counter,
            "biz_flow_name": flow_def["name"],
            "project_id": 1,  # AIBidComposer项目
            "module_id": module_id,
            "demand_description": flow_def["demand_description"],
            "basic_description": flow_def["basic_description"],
            "detail_description": f"数据来源: docs/04-设计/API接口设计/{source_file}.md",
            "demand_flow_json": {},
            "basic_flow_json": {},
            "detail_flow_json": {},
            "content": {
                "description": flow_def["demand_description"],
                "source_document": f"{source_file}.md",
                "participants": [],
                "steps": []
            },
            "flow_json": {
                "type": "mermaid",
                "mermaid": mermaid_text
            },
            "user_requirement_id": 0,
            "user_requirement_piece_id": 0,
            "flow_generate_step_list": [
                {
                    "demand_content": flow_def["demand_description"],
                    "mermaid_flow_text": mermaid_text,
                    "mermaid_flow_json": "",
                    "flow_json_autolayout": "",
                    "flow_json_final": ""
                }
            ],
            "demand_function_point_list": [],
            "is_locked": "N"
        }

        # 保存文件
        filename = f"{flow_def['key']}-{flow_def['name']}.json"
        # 清理文件名中的非法字符
        filename = re.sub(r'[<>:"/\\|?*]', '-', filename)

        file_path = self.output_dir / filename

        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(biz_flow, f, ensure_ascii=False, indent=2)

        self.flow_id_counter += 1


def main():
    """主函数"""
    extractor = DocumentFlowExtractor()
    extractor.extract_all_flows()


if __name__ == "__main__":
    main()
