---
文档类型: 架构文档
需求编号: DOC-2025-11-001
created_at: 2025-11-29
author: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
updated_at: 2025-11-30 00:25
updater: gemini-pro
status: 已批准
---

# AI标书智能创作平台 - LLM服务架构

> **实现**: Python FastAPI独立服务
> **端口**: 8001
> **部署**: 独立于Java服务的Python容器

## 📋 文档导航

本文档是AI能力层设计的一部分，其他相关文档：

1. [00-AI能力层总览.md](./00-AI能力层总览.md) - AI能力矩阵和技术栈概览
2. **01-LLM服务架构.md**（本文档）- LLM客户端、Prompt管理、任务队列
3. [02-向量检索服务.md](./02-向量检索服务.md) - 向量嵌入服务、Elasticsearch、Pinecone存储
4. [03-知识图谱服务.md](./03-知识图谱服务.md) - 知识图谱管理、智能匹配引擎
5. [04-工作流与优化.md](./04-工作流与优化.md) - 工作流编排、成本优化、性能优化

## 🤖 LLM服务架构

### 1. LLM客户端设计

```python
# app/services/ai/llm_client.py
# Python AI服务 - LLM客户端
from typing import Optional, List, Dict, Any
from enum import Enum
import openai
import anthropic
from langchain.chat_models import ChatOpenAI, ChatAnthropic
from langchain.schema import SystemMessage, HumanMessage, AIMessage

class ModelProvider(str, Enum):
    OPENAI = "openai"
    ANTHROPIC = "anthropic"
    ZHIPU = "zhipu"
    BAIDU = "baidu"

class LLMClient:
    """统一的LLM客户端"""

    def __init__(self):
        self.providers = {
            ModelProvider.OPENAI: self._init_openai(),
            ModelProvider.ANTHROPIC: self._init_anthropic(),
            ModelProvider.ZHIPU: self._init_zhipu(),
        }

    def _init_openai(self) -> ChatOpenAI:
        """初始化OpenAI客户端"""
        return ChatOpenAI(
            model_name="gpt-4-turbo-preview",
            temperature=0.7,
            openai_api_key=settings.OPENAI_API_KEY,
        )

    def _init_anthropic(self) -> ChatAnthropic:
        """初始化Anthropic客户端"""
        return ChatAnthropic(
            model="claude-3-opus-20240229",
            anthropic_api_key=settings.ANTHROPIC_API_KEY,
        )

    async def chat(
        self,
        messages: List[Dict[str, str]],
        provider: ModelProvider = ModelProvider.OPENAI,
        model: Optional[str] = None,
        temperature: float = 0.7,
        max_tokens: int = 2000,
        **kwargs
    ) -> Dict[str, Any]:
        """
        统一的聊天接口

        Args:
            messages: 消息列表
            provider: 模型提供商
            model: 具体模型名称
            temperature: 温度参数
            max_tokens: 最大token数

        Returns:
            生成结果
        """
        llm = self.providers[provider]

        if model:
            llm.model_name = model
        llm.temperature = temperature
        llm.max_tokens = max_tokens

        # 转换消息格式
        formatted_messages = self._format_messages(messages)

        # 调用LLM
        response = await llm.agenerate([formatted_messages])

        return {
            "content": response.generations[0][0].text,
            "model": llm.model_name,
            "tokens_used": {
                "prompt": response.llm_output.get("token_usage", {}).get("prompt_tokens", 0),
                "completion": response.llm_output.get("token_usage", {}).get("completion_tokens", 0),
                "total": response.llm_output.get("token_usage", {}).get("total_tokens", 0),
            },
            "finish_reason": response.generations[0][0].generation_info.get("finish_reason"),
        }

    def _format_messages(self, messages: List[Dict[str, str]]) -> List:
        """格式化消息"""
        formatted = []
        for msg in messages:
            role = msg["role"]
            content = msg["content"]

            if role == "system":
                formatted.append(SystemMessage(content=content))
            elif role == "user":
                formatted.append(HumanMessage(content=content))
            elif role == "assistant":
                formatted.append(AIMessage(content=content))

        return formatted
```

### 2. Prompt管理

```python
# app/services/ai/prompt_manager.py
from typing import Dict, Any, Optional
from jinja2 import Template
from app.models.ai_prompt import AIPrompt
from app.core.cache import cache

class PromptManager:
    """Prompt模板管理器"""

    def __init__(self):
        self.cache_ttl = 3600  # 1小时缓存

    async def get_prompt(
        self,
        code: str,
        variables: Optional[Dict[str, Any]] = None
    ) -> str:
        """
        获取并渲染Prompt

        Args:
            code: Prompt代码
            variables: 变量字典

        Returns:
            渲染后的Prompt
        """
        # 从缓存获取
        cache_key = f"prompt:{code}"
        prompt_template = await cache.get(cache_key)

        if not prompt_template:
            # 从数据库获取
            prompt = await AIPrompt.get_by_code(code)
            if not prompt:
                raise ValueError(f"Prompt not found: {code}")

            prompt_template = prompt.prompt_template
            # 缓存
            await cache.set(cache_key, prompt_template, self.cache_ttl)

        # 渲染模板
        if variables:
            template = Template(prompt_template)
            return template.render(**variables)

        return prompt_template

    async def create_prompt(
        self,
        name: str,
        code: str,
        category: str,
        prompt_template: str,
        system_prompt: Optional[str] = None,
        variables: Optional[List[str]] = None,
        model_params: Optional[Dict[str, Any]] = None,
    ) -> AIPrompt:
        """创建Prompt模板"""
        prompt = await AIPrompt.create(
            name=name,
            code=code,
            category=category,
            prompt_template=prompt_template,
            system_prompt=system_prompt,
            variables=variables or [],
            model_params=model_params or {},
        )

        # 清除缓存
        await cache.delete(f"prompt:{code}")

        return prompt


# Prompt示例
REQUIREMENT_ANALYSIS_PROMPT = """
你是一位资深的招投标专家，擅长分析招标文件并提取关键需求。

请分析以下招标文件内容，提取关键需求信息：

招标文件内容：
{bidding_document_content}

请按照以下格式输出分析结果：

1. 项目基本信息
- 项目名称
- 招标单位
- 项目预算
- 提交截止时间

2. 技术需求
- 核心功能需求
- 技术指标要求
- 性能要求
- 安全要求

3. 商务需求
- 资质要求
- 业绩要求
- 人员要求
- 其他商务条件

4. 评分标准
- 技术评分（权重和标准）
- 商务评分（权重和标准）

5. 风险点
- 识别潜在的风险点
- 给出风险等级（高/中/低）

6. 建议
- 投标建议
- 需要重点关注的方面
"""

CONTENT_GENERATION_PROMPT = """
你是一位专业的标书撰写专家，擅长撰写技术方案和商务方案。

项目背景：
{project_background}

需求内容：
{requirements}

企业能力：
{capabilities}

历史案例：
{cases}

请基于以上信息，撰写{section_type}部分的内容。

要求：
1. 内容专业、准确、有说服力
2. 突出企业优势和竞争力
3. 紧密贴合招标需求
4. 结构清晰、逻辑严密
5. 字数约{word_count}字

请直接输出内容，不要包含额外的解释。
"""
```

### 3. AI任务队列

```python
# app/services/ai/task_queue.py
from celery import Task
from typing import Dict, Any
from app.tasks.celery_app import celery_app
from app.models.ai_task import AITask, TaskStatus
from app.services.ai.llm_client import LLMClient
from app.core.logging import logger

class AITaskQueue:
    """AI任务队列管理"""

    @staticmethod
    @celery_app.task(bind=True, max_retries=3)
    async def process_ai_task(self: Task, task_id: str) -> Dict[str, Any]:
        """
        处理AI任务

        Args:
            task_id: 任务ID

        Returns:
            处理结果
        """
        # 获取任务
        task = await AITask.get(task_id)
        if not task:
            raise ValueError(f"Task not found: {task_id}")

        try:
            # 更新状态为运行中
            await task.update(
                status=TaskStatus.RUNNING,
                started_at=datetime.utcnow()
            )

            # 获取LLM客户端
            llm_client = LLMClient()

            # 根据任务类型执行
            if task.task_type == "parse":
                result = await self._process_parse_task(task, llm_client)
            elif task.task_type == "analyze":
                result = await self._process_analyze_task(task, llm_client)
            elif task.task_type == "match":
                result = await self._process_match_task(task, llm_client)
            elif task.task_type == "generate":
                result = await self._process_generate_task(task, llm_client)
            elif task.task_type == "review":
                result = await self._process_review_task(task, llm_client)
            else:
                raise ValueError(f"Unknown task type: {task.task_type}")

            # 更新任务状态为成功
            await task.update(
                status=TaskStatus.SUCCESS,
                output_data=result,
                completed_at=datetime.utcnow(),
                duration_seconds=(datetime.utcnow() - task.started_at).total_seconds()
            )

            return result

        except Exception as e:
            logger.error(f"AI task failed: {task_id}, error: {str(e)}")

            # 更新任务状态为失败
            await task.update(
                status=TaskStatus.FAILED,
                error_message=str(e),
                completed_at=datetime.utcnow(),
                retry_count=task.retry_count + 1
            )

            # 重试
            if task.retry_count < task.max_retries:
                raise self.retry(exc=e, countdown=60 * (task.retry_count + 1))

            raise

    @staticmethod
    async def _process_generate_task(
        task: AITask,
        llm_client: LLMClient
    ) -> Dict[str, Any]:
        """处理内容生成任务"""
        input_data = task.input_data

        # 获取Prompt
        prompt_manager = PromptManager()
        prompt = await prompt_manager.get_prompt(
            code="content_generation",
            variables=input_data.get("context", {})
        )

        # 调用LLM
        response = await llm_client.chat(
            messages=[
                {"role": "system", "content": "你是一位专业的标书撰写专家。"},
                {"role": "user", "content": prompt}
            ],
            model=input_data.get("model", "gpt-4-turbo-preview"),
            temperature=input_data.get("temperature", 0.7),
            max_tokens=input_data.get("max_tokens", 2000)
        )

        return {
            "content": response["content"],
            "tokens_used": response["tokens_used"],
            "model": response["model"]
        }
```

## 🔗 相关文档

- **AI能力层总览**: [00-AI能力层总览.md](./00-AI能力层总览.md)
- **向量检索服务**: [02-向量检索服务.md](./02-向量检索服务.md)
- **知识图谱服务**: [03-知识图谱服务.md](./03-知识图谱服务.md)
- **工作流与优化**: [04-工作流与优化.md](./04-工作流与优化.md)

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-30 00:25 | 1.1 | gemini-pro | YAML头部时间戳更新。 |
| 2025-11-29 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从05-AI能力层设计.md拆分创建LLM服务架构文档 |

---

**文档版本**: v1.0
**创建时间**: 2025年11月29日
**文档状态**: ✅ 已批准
