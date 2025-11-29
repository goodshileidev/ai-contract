---
文档类型: 知识库文档
需求编号: DOC-2025-11-004
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# Python 代码规范 - 7. FastAPI 项目特定规范

### 7.1 路由定义

```python
from fastapi import FastAPI, HTTPException, Depends, status
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI(title="AI 标书服务")

# ✅ 使用 Pydantic 模型定义请求和响应
class DocumentAnalysisRequest(BaseModel):
    """文档分析请求"""
    document_id: str
    include_metadata: bool = True
    max_chunks: int = 100

    class Config:
        schema_extra = {
            "example": {
                "document_id": "123e4567-e89b-12d3-a456-426614174000",
                "include_metadata": True,
                "max_chunks": 100,
            }
        }

class DocumentAnalysisResponse(BaseModel):
    """文档分析响应"""
    document_id: str
    summary: str
    keywords: List[str]
    confidence: float

# ✅ 路由定义
@app.post(
    "/api/ai/analyze-document",
    response_model=DocumentAnalysisResponse,
    status_code=status.HTTP_200_OK,
    summary="分析文档",
    description="使用AI分析招标文档，提取关键信息",
    tags=["文档分析"]
)
async def analyze_document(
    request: DocumentAnalysisRequest,
    ai_service: AIService = Depends(get_ai_service)
) -> DocumentAnalysisResponse:
    """
    分析文档并提取关键信息

    需求编号: REQ-AI-001
    """
    try:
        result = await ai_service.analyze_document(
            doc_id=request.document_id,
            include_metadata=request.include_metadata,
            max_chunks=request.max_chunks
        )
        return DocumentAnalysisResponse(**result)
    except DocumentNotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"文档分析失败: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="文档分析失败"
        )
```

### 7.2 依赖注入

```python
from fastapi import Depends
from typing import Annotated

# ✅ 定义依赖
async def get_ai_service() -> AIService:
    """获取 AI 服务实例"""
    return AIService()

async def get_current_user(
    token: str = Depends(oauth2_scheme)
) -> User:
    """获取当前用户"""
    user = await verify_token(token)
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="无效的认证凭据"
        )
    return user

# ✅ 使用依赖（推荐使用 Annotated）
@app.get("/api/documents")
async def list_documents(
    current_user: Annotated[User, Depends(get_current_user)],
    ai_service: Annotated[AIService, Depends(get_ai_service)]
):
    """列出用户的所有文档"""
    return await ai_service.list_user_documents(current_user.id)
```

### 7.3 异常处理

```python
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError

app = FastAPI()

# ✅ 自定义异常
class DocumentNotFoundError(Exception):
    """文档未找到异常"""
    pass

class AIServiceError(Exception):
    """AI 服务错误"""
    pass

# ✅ 异常处理器
@app.exception_handler(DocumentNotFoundError)
async def document_not_found_handler(
    request: Request,
    exc: DocumentNotFoundError
):
    """文档未找到异常处理"""
    return JSONResponse(
        status_code=404,
        content={
            "code": "DOCUMENT_NOT_FOUND",
            "message": str(exc),
        }
    )

@app.exception_handler(AIServiceError)
async def ai_service_error_handler(
    request: Request,
    exc: AIServiceError
):
    """AI 服务错误处理"""
    logger.error(f"AI 服务错误: {exc}")
    return JSONResponse(
        status_code=500,
        content={
            "code": "AI_SERVICE_ERROR",
            "message": "AI 服务暂时不可用，请稍后重试",
        }
    )

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(
    request: Request,
    exc: RequestValidationError
):
    """请求验证错误处理"""
    return JSONResponse(
        status_code=422,
        content={
            "code": "VALIDATION_ERROR",
            "message": "请求参数验证失败",
            "details": exc.errors(),
        }
    )
```

---
