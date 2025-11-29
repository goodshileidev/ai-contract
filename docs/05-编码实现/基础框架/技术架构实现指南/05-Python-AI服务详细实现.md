---
文档类型: 架构文档
需求编号: DOC-2025-11-002
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术架构详细实现 - 🤖 Python AI服务详细实现

### FastAPI项目结构
```
python-ai-service/
├── app/
│   ├── __init__.py
│   ├── main.py                      # FastAPI应用入口
│   ├── config/                      # 配置
│   │   ├── __init__.py
│   │   ├── settings.py
│   │   └── database.py
│   ├── api/                         # API路由
│   │   ├── __init__.py
│   │   ├── v1/
│   │   │   ├── __init__.py
│   │   │   ├── analyze.py
│   │   │   ├── generate.py
│   │   │   └── match.py
│   ├── services/                    # 业务逻辑层
│   │   ├── __init__.py
│   │   ├── llm_service.py
│   │   ├── rag_service.py          # LlamaIndex RAG服务
│   │   ├── vector_service.py       # Elasticsearch向量服务
│   │   ├── analysis_service.py
│   │   └── generation_service.py
│   ├── models/                      # 数据模型
│   │   ├── __init__.py
│   │   ├── request.py
│   │   └── response.py
│   ├── core/                        # 核心组件
│   │   ├── __init__.py
│   │   ├── llama_index_config.py
│   │   ├── langchain_config.py
│   │   └── elasticsearch_config.py
│   └── utils/                       # 工具函数
│       ├── __init__.py
│       └── helpers.py
├── requirements.txt
├── Dockerfile
└── .env
```

### FastAPI主应用
```python
# main.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from contextlib import asynccontextmanager

from app.api.v1 import analyze, generate, match
from app.config.settings import settings
from app.core.llama_index_config import initialize_llama_index
from app.core.elasticsearch_config import initialize_elasticsearch

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 启动时初始化
    print("初始化AI服务...")
    await initialize_llama_index()
    await initialize_elasticsearch()
    yield
    # 关闭时清理
    print("关闭AI服务...")

app = FastAPI(
    title="AI Bid Composer - AI Service",
    description="AI能力服务，提供文档分析、内容生成等智能功能",
    version="2.0.0",
    lifespan=lifespan
)

# CORS中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Gzip压缩
app.add_middleware(GZipMiddleware, minimum_size=1000)

# 注册路由
app.include_router(analyze.router, prefix="/api/v1/analyze", tags=["analyze"])
app.include_router(generate.router, prefix="/api/v1/generate", tags=["generate"])
app.include_router(match.router, prefix="/api/v1/match", tags=["match"])

@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "service": "ai-service",
        "version": "2.0.0"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8001,
        reload=settings.DEBUG
    )

# 配置 - config/settings.py
from pydantic_settings import BaseSettings
from typing import List

class Settings(BaseSettings):
    # 应用配置
    APP_NAME: str = "AI Bid Composer"
    DEBUG: bool = False
    ALLOWED_ORIGINS: List[str] = ["http://localhost:3000"]

    # 数据库配置
    POSTGRES_URL: str = "postgresql://user:pass@postgres:5432/aibid"

    # Redis配置
    REDIS_URL: str = "redis://redis:6379/0"

    # Elasticsearch配置
    ELASTICSEARCH_URL: str = "http://elasticsearch:9200"
    ELASTICSEARCH_INDEX_PREFIX: str = "bid_"

    # LLM API配置
    OPENAI_API_KEY: str
    ANTHROPIC_API_KEY: str = ""
    ZHIPU_API_KEY: str = ""

    # 模型配置
    DEFAULT_LLM_MODEL: str = "gpt-4"
    DEFAULT_EMBEDDING_MODEL: str = "text-embedding-3-small"
    DEFAULT_TEMPERATURE: float = 0.1
    MAX_TOKENS: int = 4000

    # RAG配置
    CHUNK_SIZE: int = 512
    CHUNK_OVERLAP: int = 50
    SIMILARITY_TOP_K: int = 5

    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
```

### LlamaIndex RAG服务实现
```python
# services/rag_service.py
from typing import List, Dict, Any, Optional
from llama_index.core import (
    VectorStoreIndex,
    ServiceContext,
    StorageContext,
    Document,
    Settings
)
from llama_index.vector_stores.elasticsearch import ElasticsearchStore
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core.node_parser import SentenceSplitter

from app.config.settings import settings
from app.core.elasticsearch_config import get_elasticsearch_client

class RAGService:
    """基于LlamaIndex的RAG服务"""

    def __init__(self):
        self.es_client = get_elasticsearch_client()

        # 配置LLM
        Settings.llm = OpenAI(
            model=settings.DEFAULT_LLM_MODEL,
            temperature=settings.DEFAULT_TEMPERATURE,
            api_key=settings.OPENAI_API_KEY
        )

        # 配置Embedding模型
        Settings.embed_model = OpenAIEmbedding(
            model=settings.DEFAULT_EMBEDDING_MODEL,
            api_key=settings.OPENAI_API_KEY
        )

        # 配置文本分割器
        Settings.node_parser = SentenceSplitter(
            chunk_size=settings.CHUNK_SIZE,
            chunk_overlap=settings.CHUNK_OVERLAP
        )

        # 初始化向量存储
        self.vector_store = ElasticsearchStore(
            index_name=f"{settings.ELASTICSEARCH_INDEX_PREFIX}knowledge",
            es_url=settings.ELASTICSEARCH_URL
        )

        # 创建存储上下文
        self.storage_context = StorageContext.from_defaults(
            vector_store=self.vector_store
        )

        # 创建索引
        self.index = VectorStoreIndex.from_vector_store(
            vector_store=self.vector_store
        )

    async def analyze_tender(
        self,
        tender_doc: str,
        analysis_type: str = "comprehensive"
    ) -> Dict[str, Any]:
        """分析招标文档"""

        # 创建查询引擎
        query_engine = self.index.as_query_engine(
            similarity_top_k=settings.SIMILARITY_TOP_K,
            response_mode="tree_summarize"
        )

        # 构建分析提示
        prompt = f"""
        请详细分析以下招标文档，提取关键信息：

        招标文档内容：
        {tender_doc[:4000]}  # 限制长度

        请从以下维度进行分析：
        1. 项目基本信息（项目名称、预算、时间等）
        2. 技术要求（技术规格、性能指标等）
        3. 商务条款（付款方式、交付要求等）
        4. 评分标准（技术分、商务分等）
        5. 风险因素（技术风险、商务风险等）
        6. 投标建议（策略建议、注意事项等）

        请以结构化的JSON格式输出结果。
        """

        # 执行查询
        response = await query_engine.aquery(prompt)

        return {
            "analysis": response.response,
            "source_nodes": [
                {
                    "text": node.node.text,
                    "score": node.score,
                    "metadata": node.node.metadata
                }
                for node in response.source_nodes
            ],
            "confidence_score": self._calculate_confidence(response.source_nodes)
        }

    async def generate_content(
        self,
        requirements: Dict[str, Any],
        company_profile: Dict[str, Any],
        section: str,
        style: str = "professional"
    ) -> Dict[str, Any]:
        """生成标书内容"""

        # 创建查询引擎
        query_engine = self.index.as_query_engine(
            similarity_top_k=settings.SIMILARITY_TOP_K,
            response_mode="compact"
        )

        # 构建生成提示
        prompt = f"""
        请基于以下信息生成标书的{section}部分内容：

        项目要求：
        {requirements}

        公司信息：
        {company_profile}

        生成要求：
        1. 风格：{style}（专业/创新/保守）
        2. 突出公司优势和核心竞争力
        3. 针对性解决客户需求和痛点
        4. 体现专业性和可信度
        5. 结构清晰、逻辑严密

        请生成高质量、有说服力的内容。
        """

        # 执行查询生成
        response = await query_engine.aquery(prompt)

        # 评估生成质量
        quality_score = await self._assess_quality(response.response)

        return {
            "content": response.response,
            "quality_score": quality_score,
            "source_references": [
                node.node.text[:200] for node in response.source_nodes
            ]
        }

    async def index_documents(
        self,
        documents: List[Dict[str, Any]]
    ) -> Dict[str, Any]:
        """索引文档到向量数据库"""

        # 转换为LlamaIndex Document对象
        llama_docs = [
            Document(
                text=doc["content"],
                metadata=doc.get("metadata", {}),
                id_=doc.get("id")
            )
            for doc in documents
        ]

        # 创建索引
        index = VectorStoreIndex.from_documents(
            llama_docs,
            storage_context=self.storage_context
        )

        return {
            "indexed_count": len(documents),
            "index_name": self.vector_store.index_name,
            "status": "success"
        }

    async def semantic_search(
        self,
        query: str,
        filters: Optional[Dict[str, Any]] = None,
        top_k: int = 5
    ) -> List[Dict[str, Any]]:
        """语义搜索"""

        # 创建检索器
        retriever = self.index.as_retriever(
            similarity_top_k=top_k
        )

        # 执行检索
        nodes = await retriever.aretrieve(query)

        return [
            {
                "text": node.node.text,
                "score": node.score,
                "metadata": node.node.metadata,
                "id": node.node.id_
            }
            for node in nodes
        ]

    def _calculate_confidence(self, source_nodes) -> float:
        """计算置信度分数"""
        if not source_nodes:
            return 0.0

        # 基于检索得分计算置信度
        avg_score = sum(node.score for node in source_nodes) / len(source_nodes)
        return min(avg_score, 1.0)

    async def _assess_quality(self, content: str) -> float:
        """评估内容质量"""
        # 简单的质量评估逻辑
        # 实际应用中可以使用更复杂的评估模型

        factors = {
            "length": min(len(content) / 1000, 1.0) * 0.3,
            "structure": 0.4 if "\n\n" in content else 0.2,
            "professional": 0.3 if any(keyword in content for keyword in ["技术", "方案", "实施"]) else 0.1
        }

        return sum(factors.values())

# 获取RAG服务实例
_rag_service = None

def get_rag_service() -> RAGService:
    global _rag_service
    if _rag_service is None:
        _rag_service = RAGService()
    return _rag_service
```

### 文档分析API
```python
# api/v1/analyze.py
from fastapi import APIRouter, HTTPException, BackgroundTasks
from typing import List, Optional
import time

from app.models.request import DocumentAnalysisRequest
from app.models.response import DocumentAnalysisResponse, ApiResponse
from app.services.rag_service import get_rag_service
from app.services.analysis_service import get_analysis_service

router = APIRouter()

@router.post("/document", response_model=ApiResponse[DocumentAnalysisResponse])
async def analyze_document(
    request: DocumentAnalysisRequest,
    background_tasks: BackgroundTasks
):
    """
    分析招标文档

    - **document_id**: 文档ID
    - **document_content**: 文档内容
    - **document_type**: 文档类型（tender/proposal/contract）
    - **analysis_options**: 分析选项列表
    """
    start_time = time.time()

    try:
        rag_service = get_rag_service()
        analysis_service = get_analysis_service()

        # 执行文档分析
        analysis_results = await rag_service.analyze_tender(
            tender_doc=request.document_content,
            analysis_type="comprehensive"
        )

        # 提取需求和风险
        requirements = await analysis_service.extract_requirements(
            request.document_content
        )
        risks = await analysis_service.analyze_risks(
            request.document_content
        )

        # 计算处理时间
        processing_time = time.time() - start_time

        response_data = DocumentAnalysisResponse(
            document_id=request.document_id,
            analysis_results={
                "main_analysis": analysis_results["analysis"],
                "requirements": requirements,
                "risks": risks,
                "source_nodes": analysis_results["source_nodes"]
            },
            confidence_score=analysis_results["confidence_score"],
            processing_time=processing_time
        )

        # 后台任务：保存分析结果
        background_tasks.add_task(
            save_analysis_results,
            request.document_id,
            response_data.model_dump()
        )

        return ApiResponse(
            success=True,
            data=response_data,
            message="文档分析完成"
        )

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"文档分析失败: {str(e)}"
        )

async def save_analysis_results(document_id: str, results: dict):
    """后台任务：保存分析结果"""
    # 保存到数据库或缓存
    pass

# models/request.py
from pydantic import BaseModel, Field
from typing import List, Optional

class DocumentAnalysisRequest(BaseModel):
    document_id: str = Field(..., description="文档ID")
    document_content: str = Field(..., description="文档内容")
    document_type: str = Field(..., description="文档类型")
    analysis_options: List[str] = Field(
        default=["requirements", "risks", "opportunities"],
        description="分析选项"
    )

# models/response.py
from pydantic import BaseModel, Field
from typing import Dict, Any, Optional, Generic, TypeVar

T = TypeVar('T')

class ApiResponse(BaseModel, Generic[T]):
    success: bool = Field(..., description="是否成功")
    data: Optional[T] = Field(None, description="响应数据")
    message: str = Field("", description="响应消息")
    timestamp: str = Field(
        default_factory=lambda: datetime.utcnow().isoformat(),
        description="时间戳"
    )

class DocumentAnalysisResponse(BaseModel):
    document_id: str = Field(..., description="文档ID")
    analysis_results: Dict[str, Any] = Field(..., description="分析结果")
    confidence_score: float = Field(..., description="置信度分数")
    processing_time: float = Field(..., description="处理时间（秒）")
```

### 内容生成API
```python
# api/v1/generate.py
from fastapi import APIRouter, HTTPException, BackgroundTasks
from typing import Dict, Any
import time

from app.models.request import ContentGenerationRequest
from app.models.response import ContentGenerationResponse, ApiResponse
from app.services.rag_service import get_rag_service
from app.services.generation_service import get_generation_service

router = APIRouter()

@router.post("/content", response_model=ApiResponse[ContentGenerationResponse])
async def generate_content(
    request: ContentGenerationRequest,
    background_tasks: BackgroundTasks
):
    """
    生成标书内容

    - **project_id**: 项目ID
    - **template_id**: 模板ID
    - **requirements**: 项目要求
    - **company_profile**: 公司信息
    - **generation_options**: 生成选项
    """
    start_time = time.time()

    try:
        rag_service = get_rag_service()
        generation_service = get_generation_service()

        # 生成各个章节内容
        sections = {}

        # 生成执行摘要
        if "executive_summary" in request.generation_options.get("sections", []):
            sections["executive_summary"] = await rag_service.generate_content(
                requirements=request.requirements,
                company_profile=request.company_profile,
                section="执行摘要",
                style="professional"
            )

        # 生成技术方案
        if "technical_proposal" in request.generation_options.get("sections", []):
            sections["technical_proposal"] = await rag_service.generate_content(
                requirements=request.requirements,
                company_profile=request.company_profile,
                section="技术方案",
                style="professional"
            )

        # 生成管理方案
        if "management_approach" in request.generation_options.get("sections", []):
            sections["management_approach"] = await rag_service.generate_content(
                requirements=request.requirements,
                company_profile=request.company_profile,
                section="项目管理方案",
                style="professional"
            )

        # 评估整体质量
        quality_score = await generation_service.assess_overall_quality(sections)

        # 生成改进建议
        suggestions = await generation_service.generate_suggestions(
            sections,
            quality_score
        )

        processing_time = time.time() - start_time

        response_data = ContentGenerationResponse(
            generated_content={
                "sections": sections,
                "generation_metadata": {
                    "project_id": request.project_id,
                    "template_id": request.template_id,
                    "timestamp": datetime.utcnow().isoformat()
                }
            },
            quality_score=quality_score,
            suggestions=suggestions,
            processing_time=processing_time
        )

        return ApiResponse(
            success=True,
            data=response_data,
            message="内容生成完成"
        )

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"内容生成失败: {str(e)}"
        )
```
