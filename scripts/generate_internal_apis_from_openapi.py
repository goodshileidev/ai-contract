#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从OpenAPI规范生成internal_api的JSON定义

功能说明：
- 读取 docs/04-设计/API接口设计/openapi/ 下的所有OpenAPI YAML文件
- 解析API路径和操作定义
- 生成符合generator-springcrud schema的internal_api JSON
- 自动分配ID和关联关系

创建日期: 2025-11-28
"""

import json
import yaml
from pathlib import Path
from typing import List, Dict, Any


class InternalApiGenerator:
    """Internal API生成器（基于OpenAPI）"""

    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.openapi_dir = self.project_root / "docs" / "04-设计" / "API接口设计" / "openapi"
        self.output_dir = self.project_root / "structured-requirements" / "individual-jsons" / "11-内部API"

        # 确保输出目录存在
        self.output_dir.mkdir(parents=True, exist_ok=True)

        self.api_id_counter = 1
        self.apis = []

        # 模块映射
        self.module_mapping = {
            "认证授权": 1,
            "用户管理": 1,
            "组织管理": 1,
            "项目管理": 2,
            "标书管理": 3,
            "文档管理": 3,
            "模板管理": 4,
            "企业能力": 5,
            "AI服务": 6,
            "协作": 7,
            "审批": 8,
            "导出": 3,
            "系统管理": 99
        }

    def parse_openapi_files(self):
        """解析所有OpenAPI文件"""
        yaml_files = list(self.openapi_dir.glob("*.yaml")) + list(self.openapi_dir.glob("*.yml"))

        print(f"\n找到 {len(yaml_files)} 个OpenAPI文件:")
        for yaml_file in yaml_files:
            print(f"  - {yaml_file.name}")

        for yaml_file in yaml_files:
            self._parse_openapi_file(yaml_file)

    def _parse_openapi_file(self, yaml_file: Path):
        """解析单个OpenAPI文件"""
        print(f"\n解析: {yaml_file.name}")

        with open(yaml_file, 'r', encoding='utf-8') as f:
            openapi_spec = yaml.safe_load(f)

        # 获取基本信息
        info = openapi_spec.get('info', {})
        api_title = info.get('title', '')
        api_description = info.get('description', '')

        # 解析paths
        paths = openapi_spec.get('paths', {})

        for path, path_item in paths.items():
            # 每个HTTP方法对应一个API
            for method, operation in path_item.items():
                if method.lower() not in ['get', 'post', 'put', 'delete', 'patch']:
                    continue

                self._create_api_from_operation(
                    path=path,
                    method=method.upper(),
                    operation=operation,
                    api_title=api_title
                )

    def _create_api_from_operation(
        self,
        path: str,
        method: str,
        operation: Dict[str, Any],
        api_title: str
    ):
        """从OpenAPI operation创建API定义"""

        # 提取基本信息
        summary = operation.get('summary', '')
        description = operation.get('description', '')
        operation_id = operation.get('operationId', '')
        tags = operation.get('tags', [])

        # 生成API key和name
        api_key = f"{method}_{path.replace('/', '_').replace('{', '').replace('}', '').replace('-', '_').upper()}"
        if api_key.startswith("_"):
            api_key = api_key[1:]

        # 限制API key长度（最多50字符）
        if len(api_key) > 50:
            # 使用operation_id生成更短的key
            if operation_id:
                api_key = f"{method}_{operation_id.upper()}"
            else:
                # 截断
                api_key = api_key[:50]

        api_name = summary or f"{method} {path}"

        # 推断模块ID
        module_id = 99  # 默认
        for tag in tags:
            if tag in self.module_mapping:
                module_id = self.module_mapping[tag]
                break

        # 推断API类型
        api_type = self._infer_api_type(method, path)

        # 提取请求参数
        request_params = self._extract_request_params(operation)

        # 提取响应字段
        response_fields = self._extract_response_fields(operation)

        # 创建API定义
        api = {
            # 必填字段
            "internal_api_id": self.api_id_counter,
            "internal_api_name": api_name,
            "internal_api_key": api_key,
            "source_type": "openapi_design",
            "create_type": "auto_generated",

            # 基本信息
            "project_id": 1,
            "module_id": module_id,
            "internal_api_type": api_type,

            # API详情
            "basic_description": description or summary,
            "api_version": "v1",
            "api_template_version": "1.0",

            # 请求信息
            "request_param_list": request_params,

            # 响应信息
            "response_field_list": response_fields,

            # 扩展字段
            "http_method": method,
            "http_path": path,
            "operation_id": operation_id,
            "tags": tags,
            "is_locked": "N"
        }

        self.apis.append(api)
        self.api_id_counter += 1

    def _infer_api_type(self, method: str, path: str) -> str:
        """推断API类型"""
        if method == "GET":
            if "list" in path or path.endswith("s"):
                return "query_list"
            else:
                return "query_detail"
        elif method == "POST":
            return "create"
        elif method == "PUT":
            return "update"
        elif method == "DELETE":
            return "delete"
        elif method == "PATCH":
            return "partial_update"
        else:
            return "custom"

    def _extract_request_params(self, operation: Dict[str, Any]) -> List[Dict[str, Any]]:
        """提取请求参数"""
        params = []

        # 提取路径参数
        if 'parameters' in operation:
            for param in operation['parameters']:
                field_name = param.get('name', '')
                params.append({
                    "field_name": field_name,
                    "field_key": field_name.upper(),
                    "param_name": field_name,
                    "param_type": param.get('in', ''),  # path, query, header
                    "data_type": param.get('schema', {}).get('type', 'string'),
                    "is_required": param.get('required', False),
                    "description": param.get('description', '')
                })

        # 提取请求体参数
        if 'requestBody' in operation:
            request_body = operation['requestBody']
            content = request_body.get('content', {})

            for content_type, media_type in content.items():
                schema = media_type.get('schema', {})

                if schema.get('type') == 'object':
                    properties = schema.get('properties', {})
                    required_fields = schema.get('required', [])

                    for field_name, field_schema in properties.items():
                        params.append({
                            "field_name": field_name,
                            "field_key": field_name.upper(),
                            "param_name": field_name,
                            "param_type": "body",
                            "data_type": field_schema.get('type', 'string'),
                            "is_required": field_name in required_fields,
                            "description": field_schema.get('description', ''),
                            "example": field_schema.get('example', '')
                        })

        return params

    def _extract_response_fields(self, operation: Dict[str, Any]) -> List[Dict[str, Any]]:
        """提取响应字段"""
        fields = []

        responses = operation.get('responses', {})

        # 提取成功响应（200, 201等）
        for status_code, response in responses.items():
            if not status_code.startswith('2'):
                continue

            content = response.get('content', {})

            for content_type, media_type in content.items():
                schema = media_type.get('schema', {})

                # 处理data字段
                if schema.get('type') == 'object':
                    properties = schema.get('properties', {})

                    # 提取data字段的properties
                    if 'data' in properties:
                        data_schema = properties['data']
                        if data_schema.get('type') == 'object':
                            data_properties = data_schema.get('properties', {})

                            for field_name, field_schema in data_properties.items():
                                fields.append({
                                    "field_name": field_name,
                                    "field_key": field_name.upper(),
                                    "data_type": field_schema.get('type', 'string'),
                                    "description": field_schema.get('description', ''),
                                    "example": field_schema.get('example', '')
                                })

        return fields

    def save_apis(self):
        """保存API定义到JSON文件"""
        print(f"\n保存 {len(self.apis)} 个API定义...")

        for api in self.apis:
            # 清理文件名中的非法字符
            safe_name = api['internal_api_name'].replace('/', '-').replace('\\', '-').replace(':', '-')
            filename = f"{api['internal_api_key']}-{safe_name}.json"

            filepath = self.output_dir / filename

            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(api, f, ensure_ascii=False, indent=2)

            if self.apis.index(api) < 5 or self.apis.index(api) >= len(self.apis) - 2:
                print(f"  ✅ 已保存: {filename}")
            elif self.apis.index(api) == 5:
                print(f"  ... (还有 {len(self.apis) - 7} 个文件)")

    def print_summary(self):
        """打印生成摘要"""
        print("\n" + "=" * 80)
        print("Internal API生成摘要")
        print("=" * 80)

        print(f"\n📊 统计信息:")
        print(f"  - 总API数: {len(self.apis)}")

        # 按模块统计
        module_map = {}
        for api in self.apis:
            module_id = api['module_id']
            if module_id not in module_map:
                module_map[module_id] = 0
            module_map[module_id] += 1

        print(f"\n📁 按模块分类:")
        module_names = {
            1: "用户与权限",
            2: "项目管理",
            3: "标书与文档",
            4: "模板管理",
            5: "企业能力",
            6: "AI服务",
            7: "协作",
            8: "审批",
            99: "系统管理"
        }
        for module_id in sorted(module_map.keys()):
            module_name = module_names.get(module_id, f"模块{module_id}")
            count = module_map[module_id]
            print(f"  {module_name}: {count} 个API")

        # 按HTTP方法统计
        method_map = {}
        for api in self.apis:
            method = api.get('http_method', 'UNKNOWN')
            if method not in method_map:
                method_map[method] = 0
            method_map[method] += 1

        print(f"\n📁 按HTTP方法分类:")
        for method in ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']:
            if method in method_map:
                print(f"  {method}: {method_map[method]} 个API")

        # 按API类型统计
        type_map = {}
        for api in self.apis:
            api_type = api.get('internal_api_type', 'unknown')
            if api_type not in type_map:
                type_map[api_type] = 0
            type_map[api_type] += 1

        print(f"\n📁 按API类型分类:")
        for api_type, count in sorted(type_map.items()):
            print(f"  {api_type}: {count} 个API")

        print("\n" + "=" * 80)


def main():
    """主函数"""
    generator = InternalApiGenerator()

    print("开始生成Internal API定义...")

    # 解析OpenAPI文件
    print("\n1. 解析OpenAPI文件...")
    generator.parse_openapi_files()

    # 保存API定义
    print("\n2. 保存API定义到文件...")
    generator.save_apis()

    # 打印摘要
    generator.print_summary()

    print("\n✅ Internal API生成完成！")


if __name__ == "__main__":
    main()
