# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2.3: Java后端 - 2.2.4 Python后端

> **服务**: Python FastAPI (端口 8001)
> **职责**: AI服务需要访问项目信息时，通过HTTP调用Java服务的项目管理API
> **技术栈**: FastAPI + httpx + Pydantic

##### 验证清单

**验证清单**:
- [ ] Java服务HTTP客户端实现完成
- [ ] JWT token传递逻辑实现完成
- [ ] 项目信息查询集成完成
- [ ] 错误处理和重试机制完成
- [ ] 环境配置和服务发现完成
- [ ] 集成测试编写完成

##### 核心代码实现

**1. Java服务HTTP客户端**

```python
# app/clients/java_client.py
"""
Java Spring Boot服务HTTP客户端

负责与Java服务通信，获取项目、组织等业务数据
"""
import httpx
from typing import Optional, Dict, Any, List
from app.core.config import settings
from app.core.logging import logger
from app.schemas.project import ProjectDetailSchema, ProjectListSchema


class JavaServiceClient:
    """Java服务客户端"""

    def __init__(self):
        self.base_url = settings.JAVA_SERVICE_URL  # http://backend-java-service:8080
        self.timeout = httpx.Timeout(30.0, connect=5.0)

        # 创建持久化HTTP客户端（连接池）
        self.client = httpx.AsyncClient(
            base_url=self.base_url,
            timeout=self.timeout,
            limits=httpx.Limits(max_keepalive_connections=20, max_connections=100)
        )

    async def close(self):
        """关闭HTTP客户端"""
        await self.client.aclose()

    def _get_headers(self, token: str) -> Dict[str, str]:
        """
        构建请求头

        Args:
            token: JWT访问令牌

        Returns:
            请求头字典
        """
        return {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
            "Accept": "application/json"
        }

    async def get_project_by_id(
        self,
        project_id: str,
        token: str
    ) -> Optional[ProjectDetailSchema]:
        """
        根据ID获取项目详情

        Args:
            project_id: 项目ID
            token: JWT访问令牌

        Returns:
            项目详情，如果不存在返回None

        Raises:
            httpx.HTTPError: HTTP请求错误
        """
        try:
            response = await self.client.get(
                f"/api/v1/projects/{project_id}",
                headers=self._get_headers(token)
            )

            if response.status_code == 404:
                logger.warning(f"项目不存在: {project_id}")
                return None

            response.raise_for_status()

            data = response.json()
            if data.get("success"):
                return ProjectDetailSchema(**data["data"])
            else:
                logger.error(f"获取项目失败: {data.get('error')}")
                return None

        except httpx.HTTPStatusError as e:
            logger.error(f"获取项目HTTP错误: {e.response.status_code}, {e.response.text}")
            raise
        except httpx.RequestError as e:
            logger.error(f"获取项目请求错误: {str(e)}")
            raise
        except Exception as e:
            logger.error(f"获取项目未知错误: {str(e)}")
            raise

    async def get_projects(
        self,
        organization_id: str,
        token: str,
        status: Optional[str] = None,
        priority: Optional[str] = None,
        search: Optional[str] = None,
        page: int = 1,
        page_size: int = 20
    ) -> ProjectListSchema:
        """
        获取项目列表

        Args:
            organization_id: 组织ID
            token: JWT访问令牌
            status: 项目状态过滤
            priority: 优先级过滤
            search: 搜索关键词
            page: 页码
            page_size: 每页数量

        Returns:
            项目列表（包含分页信息）
        """
        params = {
            "organizationId": organization_id,
            "page": page,
            "pageSize": page_size
        }

        if status:
            params["status"] = status
        if priority:
            params["priority"] = priority
        if search:
            params["search"] = search

        try:
            response = await self.client.get(
                "/api/v1/projects",
                params=params,
                headers=self._get_headers(token)
            )

            response.raise_for_status()

            data = response.json()
            if data.get("success"):
                return ProjectListSchema(**data["data"])
            else:
                logger.error(f"获取项目列表失败: {data.get('error')}")
                raise ValueError(f"获取项目列表失败: {data.get('error')}")

        except httpx.HTTPError as e:
            logger.error(f"获取项目列表错误: {str(e)}")
            raise

    async def get_organization_by_id(
        self,
        organization_id: str,
        token: str
    ) -> Optional[Dict[str, Any]]:
        """
        根据ID获取组织信息

        Args:
            organization_id: 组织ID
            token: JWT访问令牌

        Returns:
            组织信息，如果不存在返回None
        """
        try:
            response = await self.client.get(
                f"/api/v1/organizations/{organization_id}",
                headers=self._get_headers(token)
            )

            if response.status_code == 404:
                return None

            response.raise_for_status()

            data = response.json()
            return data["data"] if data.get("success") else None

        except httpx.HTTPError as e:
            logger.error(f"获取组织错误: {str(e)}")
            raise


# 全局客户端实例
java_client = JavaServiceClient()


async def get_java_client() -> JavaServiceClient:
    """依赖注入：获取Java服务客户端"""
    return java_client
```

**2. Pydantic模型定义**

```python
# app/schemas/project.py
"""项目相关的Pydantic模型"""
from typing import Optional, List
from pydantic import BaseModel, UUID4
from datetime import datetime
from enum import Enum


class ProjectStatus(str, Enum):
    """项目状态枚举"""
    DRAFT = "DRAFT"
    IN_PROGRESS = "IN_PROGRESS"
    REVIEW = "REVIEW"
    SUBMITTED = "SUBMITTED"
    WON = "WON"
    LOST = "LOST"
    ARCHIVED = "ARCHIVED"


class ProjectPriority(str, Enum):
    """项目优先级枚举"""
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    URGENT = "URGENT"


class ProjectSimpleSchema(BaseModel):
    """项目简单信息模型（列表视图）"""
    id: UUID4
    name: str
    code: str
    status: ProjectStatus
    priority: ProjectPriority
    budget_amount: Optional[float]
    submission_deadline: Optional[datetime]
    win_probability: Optional[int]
    created_at: datetime

    class Config:
        from_attributes = True


class ProjectDetailSchema(BaseModel):
    """项目详细信息模型"""
    id: UUID4
    name: str
    code: str
    description: Optional[str]
    organization_id: UUID4
    bidding_type: Optional[str]
    industry: Optional[str]
    budget_amount: Optional[float]
    currency: str
    start_date: Optional[datetime]
    end_date: Optional[datetime]
    submission_deadline: Optional[datetime]
    status: ProjectStatus
    priority: ProjectPriority
    win_probability: Optional[int]
    tags: List[str]
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class ProjectListSchema(BaseModel):
    """项目列表响应模型（包含分页）"""
    items: List[ProjectSimpleSchema]
    total: int
    page: int
    page_size: int
    total_pages: int
```

**3. FastAPI端点集成示例**

```python
# app/api/v1/ai_endpoints.py
"""AI服务端点 - 集成Java服务的项目数据"""
from fastapi import APIRouter, Depends, HTTPException, status
from typing import Optional
from app.clients.java_client import JavaServiceClient, get_java_client
from app.core.security import get_current_user_token
from app.schemas.ai_task import AITaskCreate, AITaskResponse

router = APIRouter(prefix="/ai", tags=["AI服务"])


@router.post("/analyze-requirements", response_model=AITaskResponse)
async def analyze_requirements(
    task_data: AITaskCreate,
    token: str = Depends(get_current_user_token),
    java_client: JavaServiceClient = Depends(get_java_client)
):
    """
    分析招标需求（需要获取项目信息）

    Args:
        task_data: AI任务创建数据（包含project_id）
        token: JWT令牌
        java_client: Java服务客户端

    Returns:
        AI任务响应
    """
    # 1. 从Java服务获取项目信息
    project = await java_client.get_project_by_id(
        project_id=str(task_data.project_id),
        token=token
    )

    if not project:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="项目不存在"
        )

    # 2. 使用项目信息进行AI分析
    # TODO: 实现AI分析逻辑
    analysis_result = await perform_requirement_analysis(
        project=project,
        bidding_document=task_data.input_data.get("bidding_document")
    )

    # 3. 创建AI任务记录
    ai_task = await create_ai_task(
        task_type="analyze_requirements",
        project_id=task_data.project_id,
        input_data=task_data.input_data,
        output_data=analysis_result
    )

    return AITaskResponse.from_orm(ai_task)


@router.post("/generate-content", response_model=AITaskResponse)
async def generate_content(
    task_data: AITaskCreate,
    token: str = Depends(get_current_user_token),
    java_client: JavaServiceClient = Depends(get_java_client)
):
    """
    生成标书内容（需要项目背景信息）

    Args:
        task_data: AI任务创建数据
        token: JWT令牌
        java_client: Java服务客户端

    Returns:
        AI任务响应
    """
    # 从Java服务获取项目详情
    project = await java_client.get_project_by_id(
        project_id=str(task_data.project_id),
        token=token
    )

    if not project:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="项目不存在"
        )

    # 使用项目信息生成内容
    generated_content = await perform_content_generation(
        project=project,
        section_type=task_data.input_data.get("section_type"),
        requirements=task_data.input_data.get("requirements")
    )

    # 创建AI任务记录
    ai_task = await create_ai_task(
        task_type="generate_content",
        project_id=task_data.project_id,
        input_data=task_data.input_data,
        output_data={"content": generated_content}
    )

    return AITaskResponse.from_orm(ai_task)
```

**4. JWT令牌传递和验证**

```python
# app/core/security.py
"""安全相关工具 - JWT令牌处理"""
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from typing import Optional

security = HTTPBearer()


async def get_current_user_token(
    credentials: HTTPAuthorizationCredentials = Depends(security)
) -> str:
    """
    从请求头提取JWT令牌

    Args:
        credentials: HTTP Bearer凭证

    Returns:
        JWT令牌字符串

    Raises:
        HTTPException: 如果令牌无效
    """
    if not credentials:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="缺少认证令牌",
            headers={"WWW-Authenticate": "Bearer"},
        )

    token = credentials.credentials

    # Python服务不验证JWT（由Java服务验证）
    # 只负责传递token给Java服务
    return token


async def get_current_user_id(token: str = Depends(get_current_user_token)) -> str:
    """
    从JWT令牌中提取用户ID（可选：如果Python服务需要解析token）

    注意：大多数情况下，Python服务不需要解析JWT，
    只需要将token原样传递给Java服务即可。

    Args:
        token: JWT令牌

    Returns:
        用户ID
    """
    # 简单实现：从token payload提取user_id
    # 注意：这里不验证签名，只是读取payload
    try:
        import jwt
        payload = jwt.decode(token, options={"verify_signature": False})
        return payload.get("user_id")
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="无效的令牌格式"
        )
```

**5. 环境配置**

```python
# app/core/config.py
"""应用配置"""
from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    """应用设置"""

    # 应用基本配置
    APP_NAME: str = "AIBidComposer AI Service"
    VERSION: str = "1.0.0"
    ENVIRONMENT: str = "development"

    # Java服务配置
    JAVA_SERVICE_URL: str = "http://backend-java-service:8080"
    JAVA_SERVICE_TIMEOUT: int = 30  # 秒

    # Redis配置
    REDIS_URL: str = "redis://redis:6379/0"

    # Elasticsearch配置
    ELASTICSEARCH_URL: str = "http://elasticsearch:9200"
    ELASTICSEARCH_USER: str = "elastic"
    ELASTICSEARCH_PASSWORD: str = "elastic"

    # AI服务配置
    OPENAI_API_KEY: str
    ANTHROPIC_API_KEY: Optional[str] = None

    # RabbitMQ配置
    RABBITMQ_URL: str = "amqp://rabbitmq:rabbitmq@rabbitmq:5672/"

    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
```

**6. 错误处理和重试机制**

```python
# app/clients/retry_client.py
"""带重试机制的HTTP客户端包装器"""
import httpx
from tenacity import (
    retry,
    stop_after_attempt,
    wait_exponential,
    retry_if_exception_type
)
from app.core.logging import logger


class RetryableJavaClient:
    """带重试机制的Java服务客户端"""

    def __init__(self, base_client: httpx.AsyncClient):
        self.client = base_client

    @retry(
        stop=stop_after_attempt(3),
        wait=wait_exponential(multiplier=1, min=2, max=10),
        retry=retry_if_exception_type((httpx.ConnectError, httpx.TimeoutException)),
        before_sleep=lambda retry_state: logger.warning(
            f"重试Java服务调用，第 {retry_state.attempt_number} 次尝试"
        )
    )
    async def get_with_retry(self, url: str, **kwargs) -> httpx.Response:
        """
        带重试的GET请求

        Args:
            url: 请求URL
            **kwargs: httpx.get参数

        Returns:
            HTTP响应

        Raises:
            httpx.HTTPError: 请求失败且重试耗尽
        """
        response = await self.client.get(url, **kwargs)
        response.raise_for_status()
        return response

    @retry(
        stop=stop_after_attempt(3),
        wait=wait_exponential(multiplier=1, min=2, max=10),
        retry=retry_if_exception_type((httpx.ConnectError, httpx.TimeoutException))
    )
    async def post_with_retry(self, url: str, **kwargs) -> httpx.Response:
        """
        带重试的POST请求

        Args:
            url: 请求URL
            **kwargs: httpx.post参数

        Returns:
            HTTP响应
        """
        response = await self.client.post(url, **kwargs)
        response.raise_for_status()
        return response
```

**7. 集成测试示例**

```python
# tests/integration/test_java_client_integration.py
"""Java服务客户端集成测试"""
import pytest
from httpx import AsyncClient
from app.clients.java_client import JavaServiceClient
from app.core.config import settings


@pytest.fixture
async def java_client():
    """创建Java服务客户端fixture"""
    client = JavaServiceClient()
    yield client
    await client.close()


@pytest.fixture
def mock_jwt_token():
    """模拟JWT令牌"""
    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature"


@pytest.mark.asyncio
@pytest.mark.integration
async def test_get_project_by_id_success(java_client, mock_jwt_token):
    """测试：成功获取项目详情"""
    # Given
    project_id = "550e8400-e29b-41d4-a716-446655440000"

    # When
    project = await java_client.get_project_by_id(
        project_id=project_id,
        token=mock_jwt_token
    )

    # Then
    assert project is not None
    assert str(project.id) == project_id
    assert project.name is not None
    assert project.status is not None


@pytest.mark.asyncio
@pytest.mark.integration
async def test_get_project_not_found(java_client, mock_jwt_token):
    """测试：项目不存在"""
    # Given
    invalid_project_id = "00000000-0000-0000-0000-000000000000"

    # When
    project = await java_client.get_project_by_id(
        project_id=invalid_project_id,
        token=mock_jwt_token
    )

    # Then
    assert project is None


@pytest.mark.asyncio
@pytest.mark.integration
async def test_get_projects_with_filters(java_client, mock_jwt_token):
    """测试：带过滤条件查询项目列表"""
    # Given
    organization_id = "550e8400-e29b-41d4-a716-446655440000"

    # When
    projects = await java_client.get_projects(
        organization_id=organization_id,
        token=mock_jwt_token,
        status="IN_PROGRESS",
        priority="HIGH",
        page=1,
        page_size=10
    )

    # Then
    assert projects is not None
    assert projects.total >= 0
    assert len(projects.items) <= 10
    assert all(p.status == "IN_PROGRESS" for p in projects.items)


@pytest.mark.asyncio
@pytest.mark.integration
async def test_unauthorized_request(java_client):
    """测试：未授权请求"""
    # Given
    project_id = "550e8400-e29b-41d4-a716-446655440000"
    invalid_token = "invalid_token"

    # When & Then
    with pytest.raises(Exception) as exc_info:
        await java_client.get_project_by_id(
            project_id=project_id,
            token=invalid_token
        )

    assert "401" in str(exc_info.value) or "Unauthorized" in str(exc_info.value)
```

**8. Docker Compose配置更新**

```yaml
# docker-compose.yml（增量更新）
services:
  backend-python:
    environment:
      # 添加Java服务URL配置
      - JAVA_SERVICE_URL=http://backend-java:8080
      - JAVA_SERVICE_TIMEOUT=30
    depends_on:
      - backend-java  # 确保Java服务先启动
```

**9. Kubernetes配置更新**

```yaml
# k8s/deployments/backend-python-deployment.yaml（增量更新）
spec:
  template:
    spec:
      containers:
      - name: backend-python
        env:
        - name: JAVA_SERVICE_URL
          value: "http://backend-java-service:8080"
        - name: JAVA_SERVICE_TIMEOUT
          value: "30"
```

##### 关键技术点

**1. 服务间通信模式**
- Python服务不直接访问PostgreSQL数据库
- 通过HTTP REST API调用Java服务获取业务数据
- 使用JWT令牌进行身份传递（透传）

**2. JWT令牌处理**
- Python服务不验证JWT签名（由Java服务验证）
- 只负责从请求头提取token并传递给Java服务
- 简化了Python服务的安全逻辑

**3. 错误处理策略**
- 网络错误（连接超时、DNS失败）：使用重试机制
- HTTP错误（4xx、5xx）：记录日志并向上传递异常
- 业务错误（项目不存在）：返回None或抛出HTTPException

**4. 性能优化**
- 使用httpx持久化连接（连接池）
- 配置合理的超时时间（连接5秒，总共30秒）
- 限制最大连接数（防止资源耗尽）

**5. 依赖注入**
- FastAPI的Depends机制注入Java客户端
- 全局单例客户端实例（避免重复创建）
- 应用生命周期管理（startup/shutdown事件）

##### 部署注意事项

**1. 服务发现**
- 开发环境：使用Docker Compose服务名（backend-java）
- Kubernetes：使用Service DNS名（backend-java-service.aibidcomposer.svc.cluster.local）

**2. 健康检查**
- Python服务启动前需确保Java服务已就绪
- Kubernetes探针配置initialDelaySeconds留出足够启动时间

**3. 环境变量配置**
```bash
# .env文件示例
JAVA_SERVICE_URL=http://backend-java-service:8080
JAVA_SERVICE_TIMEOUT=30
```

---
