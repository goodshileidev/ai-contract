---
文档类型: 知识库文档
需求编号: DOC-2025-11-004
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# Python 代码规范

**基于**: [Google Python Style Guide](https://google.github.io/styleguide/pyguide.html) + [PEP 8](https://peps.python.org/pep-0008/)
**适用范围**: AIBidComposer 项目的 Python FastAPI AI 服务
**技术栈**: Python 3.11+ + FastAPI 0.104+ + LlamaIndex 0.9+

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.0 | claude-sonnet-4-5 | 基于 Google Python Style Guide 创建规范 |

---

## 1. 源文件基础

### 1.1 文件编码

- **UTF-8** 编码（Python 3 默认）
- 文件头部不需要编码声明（Python 2 已废弃）

```python
# ✅ Python 3 默认 UTF-8，无需声明

# ❌ 不需要（Python 2 遗留）
# -*- coding: utf-8 -*-
```

### 1.2 导入语句

**导入顺序**（组间用空行分隔）：
1. 标准库导入
2. 第三方库导入
3. 本地应用/库导入

```python
# ✅ 正确的导入顺序
import os
import sys
from typing import List, Optional

from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from llama_index import VectorStoreIndex, ServiceContext

from app.services.ai_service import AIService
from app.models.document import Document

# ❌ 错误：不要使用通配符导入
from app.services import *  # ❌

# ❌ 错误：不要使用相对导入的星号
from . import *  # ❌
```

**导入规范**：
```python
# ✅ 正确：每个导入单独一行
import os
import sys

# ✅ 正确：from imports 可以多个
from typing import List, Dict, Optional

# ❌ 错误：多个导入在一行
import os, sys  # ❌
```

### 1.3 模块文件名

- **小写字母**
- **下划线分隔**（snake_case）
- **避免使用连字符**

```python
# ✅ 正确
ai_service.py
document_parser.py
vector_store_manager.py

# ❌ 错误
AIService.py       # ❌ 不要用驼峰
ai-service.py      # ❌ 不要用连字符
aiservice.py       # ❌ 多单词应该用下划线
```

---

## 2. 格式化规范

### 2.1 缩进

- **4个空格** 为一个缩进层级
- **禁止使用 Tab**
- **禁止混用 Tab 和空格**

```python
# ✅ 正确：4空格缩进
def process_document(document: Document) -> dict:
    if document.is_valid():
        result = analyze_content(document.content)
        return result
    else:
        raise ValueError("无效的文档")
```

### 2.2 行长度

- **每行最多 79 字符**（代码）
- **每行最多 72 字符**（文档字符串和注释）
- 长 URL 等可以例外

```python
# ✅ 正确：超长行使用括号换行
user = User(
    username="john_doe",
    email="john@example.com",
    first_name="John",
    last_name="Doe"
)

# ✅ 正确：长字符串换行
message = (
    "这是一个很长的消息，"
    "需要分成多行来表示，"
    "以保持代码的可读性。"
)

# ✅ 正确：使用反斜杠续行（最后手段）
with open('/path/to/some/file/you/want/to/read') as file_1, \
     open('/path/to/some/file/being/written', 'w') as file_2:
    file_2.write(file_1.read())
```

### 2.3 空行

```python
# ✅ 顶级定义之间空两行
class DocumentParser:
    """文档解析器"""
    pass


class VectorStoreManager:
    """向量存储管理器"""
    pass


# ✅ 方法定义之间空一行
class AIService:

    def __init__(self):
        self.index = None

    def analyze_document(self, doc_id: str) -> dict:
        """分析文档"""
        pass

    def generate_content(self, prompt: str) -> str:
        """生成内容"""
        pass
```

### 2.4 空格

```python
# ✅ 正确：运算符两侧有空格
x = y + z
if x == y:
    pass

# ✅ 正确：逗号后有空格
my_list = [1, 2, 3]
my_dict = {"key": "value"}

# ✅ 正确：冒号在字典中的使用
{"key": "value"}  # 冒号后有空格，前面没有

# ❌ 错误：多余的空格
my_list = [ 1, 2, 3 ]  # ❌ 括号内不要空格
my_dict = { "key":"value" }  # ❌ 格式不对

# ✅ 正确：函数调用
func(arg1, arg2, kwarg1=value1, kwarg2=value2)

# ❌ 错误：多余的空格
func( arg1, arg2 )  # ❌ 括号内不要空格
func(arg1 , arg2)   # ❌ 逗号前不要空格
```

### 2.5 尾随逗号

```python
# ✅ 推荐：多行时使用尾随逗号（方便版本控制）
FILES = [
    "setup.py",
    "main.py",
    "config.py",  # ✅ 尾随逗号
]

# ✅ 单行时不需要尾随逗号
FILES = ["setup.py", "main.py", "config.py"]
```

---

## 3. 命名规范

### 3.1 命名风格

| 类型 | 命名风格 | 示例 |
|------|---------|------|
| 模块/包 | `lowercase` or `lower_with_under` | `parser` or `document_parser` |
| 类 | `CapWords` (PascalCase) | `DocumentParser`, `AIService` |
| 函数/方法 | `lower_with_under` | `parse_document`, `analyze_content` |
| 变量 | `lower_with_under` | `user_name`, `doc_id` |
| 常量 | `UPPER_WITH_UNDER` | `MAX_RETRIES`, `DEFAULT_TIMEOUT` |
| 类型变量 | `CapWords` | `T`, `RequestType`, `ResponseType` |

### 3.2 模块名

```python
# ✅ 正确
import document_parser
import ai_service
from utils import text_processor

# ❌ 错误
import DocumentParser  # ❌ 不要用驼峰
import ai-service      # ❌ 不要用连字符
```

### 3.3 类名

```python
# ✅ 正确：大驼峰（PascalCase）
class DocumentParser:
    pass

class AIService:
    pass

class VectorStoreManager:
    pass

# ❌ 错误
class document_parser:  # ❌ 应该用大驼峰
class Document_Parser:  # ❌ 不要用下划线
```

### 3.4 函数和方法名

```python
# ✅ 正确：小写+下划线
def parse_document(content: str) -> dict:
    pass

def analyze_requirements(doc_id: str) -> list:
    pass

def is_valid_format(file_path: str) -> bool:
    pass

# ❌ 错误
def parseDocument(content):  # ❌ 不要用驼峰
def ParseDocument(content):  # ❌ 不要用大驼峰
```

**常见命名模式**：
```python
# 查询方法
def get_document(doc_id: str) -> Document:
    pass

def find_by_id(doc_id: str) -> Optional[Document]:
    pass

def list_all() -> List[Document]:
    pass

# 布尔查询
def is_valid() -> bool:
    pass

def has_permission() -> bool:
    pass

def can_edit() -> bool:
    pass

# 处理方法
def process_document(doc: Document) -> dict:
    pass

def analyze_content(content: str) -> dict:
    pass
```

### 3.5 常量

```python
# ✅ 正确：全大写+下划线
MAX_RETRY_COUNT = 3
DEFAULT_TIMEOUT = 30
OPENAI_API_BASE_URL = "https://api.openai.com/v1"

# ✅ 正确：不可变集合也算常量
ALLOWED_FORMATS = frozenset(["pdf", "docx", "txt"])
DEFAULT_CONFIG = {
    "model": "gpt-4",
    "temperature": 0.7,
}

# ❌ 错误
maxRetryCount = 3  # ❌ 应该全大写
Max_Retry_Count = 3  # ❌ 不要混用大小写
```

### 3.6 私有属性和方法

```python
class DocumentParser:

    def __init__(self):
        self.public_attr = "公开属性"
        self._protected_attr = "保护属性（约定）"
        self.__private_attr = "私有属性（名称改编）"

    def public_method(self):
        """公开方法"""
        pass

    def _protected_method(self):
        """保护方法（约定，仅内部使用）"""
        pass

    def __private_method(self):
        """私有方法（名称改编，避免子类覆盖）"""
        pass
```

**命名约定**：
- **单下划线前缀** `_name`：内部使用（约定，非强制）
- **双下划线前缀** `__name`：名称改编（name mangling）
- **双下划线前后** `__name__`：魔术方法（如 `__init__`）

---

## 4. 注释和文档字符串

### 4.1 文档字符串（Docstrings）

**使用 Google 风格的文档字符串**：

```python
def analyze_document(
    doc_id: str,
    include_metadata: bool = True,
    max_chunks: int = 100
) -> dict:
    """分析文档并提取关键信息

    使用 LlamaIndex 对文档进行语义分析，提取关键信息和元数据。

    需求编号: REQ-AI-001

    Args:
        doc_id: 文档ID，必须是有效的UUID格式
        include_metadata: 是否包含元数据，默认为True
        max_chunks: 最大处理的文档块数，默认100

    Returns:
        包含分析结果的字典，格式如下：
        {
            "summary": str,      # 文档摘要
            "keywords": List[str],  # 关键词列表
            "entities": List[dict],  # 实体列表
        }

    Raises:
        ValueError: 当 doc_id 格式无效时
        DocumentNotFoundError: 当文档不存在时
        AIServiceError: 当AI服务调用失败时

    Example:
        >>> result = analyze_document("123e4567-e89b-12d3-a456-426614174000")
        >>> print(result["summary"])
        "这是一份招标文件..."
    """
    pass
```

**类的文档字符串**：
```python
class DocumentParser:
    """文档解析器，支持多种格式的文档解析

    该类负责解析 PDF、Word、Excel 等格式的文档，
    并提取结构化的文本内容。

    需求编号: REQ-AI-001
    实现日期: 2025-11-26

    Attributes:
        supported_formats: 支持的文件格式列表
        max_file_size: 最大文件大小（字节）

    Example:
        >>> parser = DocumentParser()
        >>> content = parser.parse_file("document.pdf")
        >>> print(content["text"])
    """

    def __init__(self, max_file_size: int = 10 * 1024 * 1024):
        """初始化文档解析器

        Args:
            max_file_size: 最大文件大小，默认10MB
        """
        self.max_file_size = max_file_size
        self.supported_formats = ["pdf", "docx", "xlsx"]
```

### 4.2 注释

```python
def process_bid_document(doc_id: str) -> dict:
    """处理招标文档"""

    # 1. 从数据库获取文档
    document = get_document_from_db(doc_id)

    # 2. 验证文档格式
    if not is_valid_format(document.format):
        raise ValueError(f"不支持的文档格式: {document.format}")

    # 3. 解析文档内容
    # 使用 PDF 解析器提取文本和表格
    parsed_content = parse_pdf_content(document.file_path)

    # 4. 向量化文档
    # TODO: 优化向量化性能，考虑批处理
    embeddings = vectorize_content(parsed_content)

    # 5. 存储到 Elasticsearch
    store_in_elasticsearch(doc_id, embeddings)

    return {
        "status": "success",
        "chunks_count": len(embeddings),
    }
```

**注释最佳实践**：
```python
# ✅ 好的注释：解释"为什么"
# 使用指数退避策略避免API限流
await asyncio.sleep(2 ** retry_count)

# ✅ 好的注释：解释复杂逻辑
# 计算加权平均分：技术分(60%) + 商务分(30%) + 经验分(10%)
total_score = tech_score * 0.6 + business_score * 0.3 + exp_score * 0.1

# ❌ 坏的注释：重复代码内容
# 设置 name 为 "John"
name = "John"

# ❌ 坏的注释：过时的注释
# 使用 MySQL 数据库
engine = create_engine("postgresql://...")  # ❌ 注释过时
```

---

## 5. 类型注解

### 5.1 使用类型注解

```python
from typing import List, Dict, Optional, Union, Any

# ✅ 正确：函数参数和返回值都有类型注解
def analyze_document(
    doc_id: str,
    options: Optional[Dict[str, Any]] = None
) -> Dict[str, Union[str, List[str]]]:
    """分析文档"""
    if options is None:
        options = {}

    return {
        "summary": "文档摘要",
        "keywords": ["关键词1", "关键词2"],
    }

# ✅ 正确：变量类型注解
user_name: str = "John"
age: int = 30
scores: List[float] = [85.5, 90.0, 88.5]
metadata: Dict[str, Any] = {"key": "value"}

# ✅ 正确：类属性类型注解
class DocumentParser:
    max_size: int
    supported_formats: List[str]

    def __init__(self):
        self.max_size = 1024 * 1024
        self.supported_formats = ["pdf", "docx"]
```

### 5.2 复杂类型

```python
from typing import (
    List, Dict, Set, Tuple, Optional, Union, Any,
    Callable, TypeVar, Generic
)

# 函数类型
Callback = Callable[[str, int], bool]

def register_callback(callback: Callback) -> None:
    pass

# 泛型
T = TypeVar('T')

def first_element(items: List[T]) -> Optional[T]:
    return items[0] if items else None

# 多种可能的类型
def process_input(data: Union[str, bytes, dict]) -> str:
    if isinstance(data, str):
        return data
    elif isinstance(data, bytes):
        return data.decode()
    else:
        return str(data)

# Optional 等价于 Union[X, None]
def find_user(user_id: str) -> Optional[dict]:
    pass
```

---

## 6. 编程实践

### 6.1 使用生成器

```python
# ✅ 推荐：使用生成器处理大数据
def read_large_file(file_path: str):
    """逐行读取大文件"""
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            yield line.strip()

# ✅ 使用生成器表达式
total = sum(len(line) for line in read_large_file("large.txt"))

# ❌ 不推荐：一次性加载到内存
with open(file_path) as f:
    lines = f.readlines()  # ❌ 大文件会占用大量内存
```

### 6.2 上下文管理器

```python
# ✅ 推荐：使用 with 语句
with open("file.txt", "r") as f:
    content = f.read()

# ✅ 推荐：多个上下文管理器
with open("input.txt") as input_file, \
     open("output.txt", "w") as output_file:
    output_file.write(input_file.read())

# ✅ 自定义上下文管理器
from contextlib import contextmanager

@contextmanager
def timer(name: str):
    """计时上下文管理器"""
    import time
    start = time.time()
    try:
        yield
    finally:
        end = time.time()
        print(f"{name} 耗时: {end - start:.2f}秒")

# 使用
with timer("文档解析"):
    parse_document("document.pdf")
```

### 6.3 异常处理

```python
# ✅ 正确：捕获特定异常
try:
    result = parse_document(doc_id)
except FileNotFoundError:
    logger.error(f"文档不存在: {doc_id}")
    raise DocumentNotFoundError(f"文档 {doc_id} 不存在")
except ParseError as e:
    logger.error(f"解析失败: {e}")
    raise
except Exception as e:
    logger.error(f"未预期的错误: {e}")
    raise AIServiceError("文档处理失败") from e

# ✅ 正确：使用 finally
try:
    file = open("file.txt")
    process_file(file)
finally:
    file.close()

# ❌ 错误：捕获所有异常但不处理
try:
    do_something()
except:  # ❌ 不要使用裸 except
    pass

# ❌ 错误：过于宽泛的异常捕获
try:
    do_something()
except Exception:  # ❌ 太宽泛，应该捕获具体异常
    pass
```

### 6.4 列表推导式和字典推导式

```python
# ✅ 推荐：列表推导式（简洁清晰）
squares = [x ** 2 for x in range(10)]
even_squares = [x ** 2 for x in range(10) if x % 2 == 0]

# ✅ 推荐：字典推导式
word_lengths = {word: len(word) for word in words}

# ✅ 推荐：集合推导式
unique_lengths = {len(word) for word in words}

# ❌ 不推荐：复杂的推导式（影响可读性）
result = [
    process(item)
    for sublist in nested_list
    for item in sublist
    if item.is_valid() and item.score > 0.5
]  # ❌ 太复杂，应该用普通循环

# ✅ 复杂逻辑用普通循环
result = []
for sublist in nested_list:
    for item in sublist:
        if item.is_valid() and item.score > 0.5:
            result.append(process(item))
```

### 6.5 字符串格式化

```python
# ✅ 推荐：f-string（Python 3.6+）
name = "John"
age = 30
message = f"用户 {name} 的年龄是 {age} 岁"

# ✅ 推荐：f-string 支持表达式
message = f"明年 {name} 将 {age + 1} 岁"

# ✅ 推荐：f-string 支持格式化
pi = 3.14159
message = f"圆周率约等于 {pi:.2f}"

# ⚠️ 可以但不推荐：str.format()
message = "用户 {} 的年龄是 {} 岁".format(name, age)

# ❌ 不推荐：% 格式化（旧式）
message = "用户 %s 的年龄是 %d 岁" % (name, age)

# ✅ 多行字符串
query = f"""
SELECT *
FROM users
WHERE name = '{name}'
AND age > {age}
"""
```

---

## 7. FastAPI 项目特定规范

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

## 8. LlamaIndex 使用规范

### 8.1 索引构建

```python
from llama_index import (
    VectorStoreIndex,
    ServiceContext,
    StorageContext,
    SimpleDirectoryReader
)
from llama_index.vector_stores import ElasticsearchStore
from llama_index.embeddings import OpenAIEmbedding

class RAGService:
    """RAG 服务"""

    def __init__(self):
        # ✅ 配置向量存储
        self.vector_store = ElasticsearchStore(
            index_name="bid_documents",
            es_url="http://localhost:9200"
        )

        # ✅ 配置服务上下文
        self.service_context = ServiceContext.from_defaults(
            embed_model=OpenAIEmbedding(),
            chunk_size=512,
            chunk_overlap=50
        )

    async def build_index(
        self,
        document_path: str
    ) -> VectorStoreIndex:
        """构建文档索引

        需求编号: REQ-AI-002

        Args:
            document_path: 文档路径

        Returns:
            构建好的索引
        """
        # 加载文档
        documents = SimpleDirectoryReader(document_path).load_data()

        # 创建存储上下文
        storage_context = StorageContext.from_defaults(
            vector_store=self.vector_store
        )

        # 构建索引
        index = VectorStoreIndex.from_documents(
            documents,
            service_context=self.service_context,
            storage_context=storage_context
        )

        return index
```

### 8.2 查询处理

```python
class RAGService:

    async def query_document(
        self,
        index: VectorStoreIndex,
        query: str,
        top_k: int = 5
    ) -> dict:
        """查询文档

        Args:
            index: 文档索引
            query: 查询字符串
            top_k: 返回的top结果数量

        Returns:
            查询结果字典
        """
        # ✅ 创建查询引擎
        query_engine = index.as_query_engine(
            similarity_top_k=top_k,
            response_mode="compact"
        )

        # ✅ 执行查询
        response = query_engine.query(query)

        return {
            "answer": str(response),
            "source_nodes": [
                {
                    "text": node.node.text,
                    "score": node.score,
                    "metadata": node.node.metadata
                }
                for node in response.source_nodes
            ]
        }
```

---

## 9. 项目特定补充规范

### 9.1 需求编号标注

```python
"""
文档解析模块

需求编号: REQ-AI-001
实现日期: 2025-11-26
"""

class DocumentParser:
    """
    文档解析器

    需求编号: REQ-AI-001
    """

    def parse_pdf(self, file_path: str) -> dict:
        """
        解析 PDF 文档

        需求编号: REQ-AI-001

        Args:
            file_path: PDF 文件路径

        Returns:
            解析结果
        """
        # 需求编号: REQ-AI-001 - PDF 解析功能
        logger.debug(f"开始解析 PDF: {file_path}")
        # ... 实现代码
```

### 9.2 日志规范

```python
import logging

# ✅ 使用模块级 logger
logger = logging.getLogger(__name__)

class AIService:

    async def analyze_document(self, doc_id: str) -> dict:
        """分析文档"""

        # DEBUG：调试信息
        logger.debug(f"开始分析文档: {doc_id}")

        # INFO：重要业务流程
        logger.info(f"文档分析成功: {doc_id}")

        # WARNING：警告但不影响流程
        logger.warning(f"文档部分内容无法解析: {doc_id}")

        # ERROR：错误异常
        try:
            result = await self._call_llm(doc_id)
        except Exception as e:
            logger.error(f"调用 LLM 失败: {doc_id}", exc_info=True)
            raise

        return result
```

### 9.3 异步编程规范

```python
import asyncio
from typing import List

# ✅ 使用 async/await
async def fetch_document(doc_id: str) -> dict:
    """异步获取文档"""
    async with httpx.AsyncClient() as client:
        response = await client.get(f"/api/documents/{doc_id}")
        return response.json()

# ✅ 并发处理多个任务
async def fetch_multiple_documents(doc_ids: List[str]) -> List[dict]:
    """并发获取多个文档"""
    tasks = [fetch_document(doc_id) for doc_id in doc_ids]
    results = await asyncio.gather(*tasks, return_exceptions=True)
    return [r for r in results if not isinstance(r, Exception)]

# ✅ 使用 async 上下文管理器
async def process_with_lock():
    """使用异步锁"""
    async with asyncio.Lock():
        # 临界区代码
        pass
```

---

## 10. 代码审查检查清单

提交代码前，请确保：

### 格式化
- [ ] 使用 4 空格缩进（不使用 Tab）
- [ ] 每行不超过 79 字符（代码）/ 72 字符（注释）
- [ ] 顶级定义之间空两行
- [ ] 正确使用空格

### 命名
- [ ] 模块名小写+下划线 (`module_name`)
- [ ] 类名大驼峰 (`ClassName`)
- [ ] 函数/方法名小写+下划线 (`function_name`)
- [ ] 常量全大写+下划线 (`CONSTANT_NAME`)

### 文档
- [ ] 所有公开函数都有文档字符串
- [ ] 所有类都有文档字符串
- [ ] 文档字符串使用 Google 风格
- [ ] 包含需求编号

### 类型注解
- [ ] 函数参数有类型注解
- [ ] 函数返回值有类型注解
- [ ] 复杂变量有类型注解

### 编程实践
- [ ] 使用 with 语句管理资源
- [ ] 不使用裸 except
- [ ] 使用 f-string 格式化字符串
- [ ] 大数据使用生成器

### FastAPI
- [ ] 使用 Pydantic 模型定义请求/响应
- [ ] 路由有完整的文档说明
- [ ] 使用依赖注入
- [ ] 统一的异常处理

### 异步
- [ ] 正确使用 async/await
- [ ] I/O 操作使用异步方法
- [ ] 并发任务使用 asyncio.gather

### 项目特定
- [ ] 代码注释包含需求编号
- [ ] 使用中文注释解释业务逻辑
- [ ] 日志级别使用正确
- [ ] 敏感信息不记录到日志

---

## 11. 工具配置

### 11.1 代码格式化 - Black

安装：
```bash
pip install black
```

配置 `pyproject.toml`：
```toml
[tool.black]
line-length = 79
target-version = ['py311']
include = '\.pyi?$'
extend-exclude = '''
/(
  # 默认排除目录
  \.git
  | \.venv
  | build
  | dist
)/
'''
```

运行：
```bash
black .
```

### 11.2 代码检查 - Ruff

安装：
```bash
pip install ruff
```

配置 `pyproject.toml`：
```toml
[tool.ruff]
line-length = 79
target-version = "py311"

[tool.ruff.lint]
select = [
    "E",  # pycodestyle errors
    "W",  # pycodestyle warnings
    "F",  # pyflakes
    "I",  # isort
    "C",  # flake8-comprehensions
    "B",  # flake8-bugbear
]
ignore = [
    "E501",  # line too long (由 black 处理)
]
```

运行：
```bash
ruff check .
```

### 11.3 类型检查 - Mypy

安装：
```bash
pip install mypy
```

配置 `mypy.ini`：
```ini
[mypy]
python_version = 3.11
warn_return_any = True
warn_unused_configs = True
disallow_untyped_defs = True

[mypy-tests.*]
disallow_untyped_defs = False
```

运行：
```bash
mypy .
```

---

## 参考资料

- [Google Python Style Guide](https://google.github.io/styleguide/pyguide.html)
- [PEP 8 – Style Guide for Python Code](https://peps.python.org/pep-0008/)
- [FastAPI Best Practices](https://fastapi.tiangolo.com/tutorial/)
- [LlamaIndex Documentation](https://docs.llamaindex.ai/)

---

**最后更新**: 2025-11-26
**版本**: 1.0
