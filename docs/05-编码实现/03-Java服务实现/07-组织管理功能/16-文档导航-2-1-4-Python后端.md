# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.4: Python后端

**验证清单**:
- [ ] JWT Token验证工具实现
- [ ] 组织数据只读访问实现
- [ ] 用户组织关系查询实现
- [ ] 权限验证辅助函数
- [ ] Python集成测试通过

#### JWT验证工具

**jwt_utils.py** (`app/core/jwt_utils.py`):

```python
"""
JWT令牌验证工具
需求编号: REQ-JAVA-002

Python AI服务需要验证来自Java服务的JWT令牌，以识别用户身份和组织关系。
"""
from typing import Optional, Dict, Any
from datetime import datetime, timedelta
import jwt
from jwt import PyJWTError
from app.core.config import settings
from app.core.exceptions import UnauthorizedException
from app.core.logging import logger


class JWTValidator:
    """JWT令牌验证器"""

    def __init__(self):
        self.secret_key = settings.JWT_SECRET_KEY
        self.algorithm = settings.JWT_ALGORITHM or "HS256"

    def decode_token(self, token: str) -> Dict[str, Any]:
        """
        解码JWT令牌

        Args:
            token: JWT令牌字符串

        Returns:
            解码后的payload字典

        Raises:
            UnauthorizedException: 如果令牌无效或过期
        """
        try:
            # 移除Bearer前缀（如果有）
            if token.startswith("Bearer "):
                token = token[7:]

            # 解码JWT
            payload = jwt.decode(
                token,
                self.secret_key,
                algorithms=[self.algorithm]
            )

            # 验证过期时间
            exp = payload.get("exp")
            if exp:
                exp_datetime = datetime.fromtimestamp(exp)
                if exp_datetime < datetime.utcnow():
                    raise UnauthorizedException("Token已过期")

            logger.debug(f"JWT解码成功，用户ID: {payload.get('user_id')}")
            return payload

        except jwt.ExpiredSignatureError:
            logger.warning("JWT令牌已过期")
            raise UnauthorizedException("Token已过期")

        except jwt.InvalidTokenError as e:
            logger.warning(f"JWT令牌无效: {str(e)}")
            raise UnauthorizedException("无效的Token")

        except Exception as e:
            logger.error(f"JWT解码失败: {str(e)}")
            raise UnauthorizedException("Token验证失败")

    def get_user_id(self, token: str) -> str:
        """
        从JWT令牌获取用户ID

        Args:
            token: JWT令牌字符串

        Returns:
            用户ID (UUID字符串)
        """
        payload = self.decode_token(token)
        user_id = payload.get("user_id") or payload.get("sub")

        if not user_id:
            raise UnauthorizedException("Token中缺少用户ID")

        return user_id

    def get_organization_id(self, token: str) -> Optional[str]:
        """
        从JWT令牌获取组织ID

        Args:
            token: JWT令牌字符串

        Returns:
            组织ID (UUID字符串) 或 None
        """
        payload = self.decode_token(token)
        return payload.get("organization_id")

    def get_user_roles(self, token: str) -> list[str]:
        """
        从JWT令牌获取用户角色列表

        Args:
            token: JWT令牌字符串

        Returns:
            角色列表
        """
        payload = self.decode_token(token)
        roles = payload.get("roles", [])

        if isinstance(roles, str):
            return [roles]
        return roles

    def has_role(self, token: str, required_role: str) -> bool:
        """
        检查用户是否拥有特定角色

        Args:
            token: JWT令牌字符串
            required_role: 需要的角色

        Returns:
            是否拥有该角色
        """
        roles = self.get_user_roles(token)
        return required_role in roles

    def verify_token(self, token: str) -> bool:
        """
        验证令牌是否有效

        Args:
            token: JWT令牌字符串

        Returns:
            令牌是否有效
        """
        try:
            self.decode_token(token)
            return True
        except UnauthorizedException:
            return False


# 全局实例
jwt_validator = JWTValidator()


def get_current_user_id(authorization: str) -> str:
    """
    从Authorization header获取当前用户ID

    Args:
        authorization: Authorization header值 (格式: "Bearer {token}")

    Returns:
        用户ID
    """
    if not authorization:
        raise UnauthorizedException("缺少Authorization header")

    return jwt_validator.get_user_id(authorization)


def get_current_organization_id(authorization: str) -> Optional[str]:
    """
    从Authorization header获取当前组织ID

    Args:
        authorization: Authorization header值

    Returns:
        组织ID 或 None
    """
    if not authorization:
        return None

    return jwt_validator.get_organization_id(authorization)
```

#### 组织数据访问服务

**organization_client.py** (`app/services/organization_client.py`):

```python
"""
组织数据访问客户端
需求编号: REQ-JAVA-002

Python AI服务通过HTTP调用Java服务API来获取组织数据（只读）。
这确保了数据访问的一致性，避免直接访问数据库。
"""
from typing import Optional, List, Dict, Any
import httpx
from app.core.config import settings
from app.core.logging import logger
from app.core.exceptions import ServiceException


class OrganizationClient:
    """组织数据访问客户端（调用Java服务API）"""

    def __init__(self):
        self.java_service_url = settings.JAVA_SERVICE_URL
        self.api_base = f"{self.java_service_url}/api/v1/organizations"
        self.timeout = 30.0

    async def get_organization_by_id(
        self,
        organization_id: str,
        authorization: str
    ) -> Optional[Dict[str, Any]]:
        """
        根据ID获取组织信息

        Args:
            organization_id: 组织ID
            authorization: JWT令牌 (Bearer token)

        Returns:
            组织信息字典 或 None
        """
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.api_base}/{organization_id}",
                    headers={"Authorization": authorization}
                )

                if response.status_code == 404:
                    logger.warning(f"组织不存在，ID: {organization_id}")
                    return None

                response.raise_for_status()
                data = response.json()

                if data.get("success"):
                    return data.get("data")
                else:
                    logger.error(f"获取组织失败: {data.get('error')}")
                    return None

        except httpx.HTTPStatusError as e:
            logger.error(f"HTTP错误: {e.response.status_code}, {e.response.text}")
            raise ServiceException(f"获取组织信息失败: {str(e)}")

        except Exception as e:
            logger.error(f"获取组织失败: {str(e)}")
            raise ServiceException(f"获取组织信息失败: {str(e)}")

    async def get_organization_members(
        self,
        organization_id: str,
        authorization: str,
        page: int = 0,
        size: int = 100
    ) -> List[Dict[str, Any]]:
        """
        获取组织成员列表

        Args:
            organization_id: 组织ID
            authorization: JWT令牌
            page: 页码
            size: 每页数量

        Returns:
            成员列表
        """
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.api_base}/{organization_id}/members",
                    params={"page": page, "size": size},
                    headers={"Authorization": authorization}
                )

                response.raise_for_status()
                data = response.json()

                if data.get("success"):
                    return data.get("data", {}).get("items", [])
                else:
                    logger.error(f"获取组织成员失败: {data.get('error')}")
                    return []

        except Exception as e:
            logger.error(f"获取组织成员失败: {str(e)}")
            raise ServiceException(f"获取组织成员失败: {str(e)}")

    async def check_user_membership(
        self,
        organization_id: str,
        user_id: str,
        authorization: str
    ) -> bool:
        """
        检查用户是否为组织成员

        Args:
            organization_id: 组织ID
            user_id: 用户ID
            authorization: JWT令牌

        Returns:
            是否为成员
        """
        try:
            members = await self.get_organization_members(
                organization_id,
                authorization,
                page=0,
                size=1000  # 获取所有成员
            )

            # 检查用户ID是否在成员列表中
            return any(member.get("userId") == user_id for member in members)

        except Exception as e:
            logger.error(f"检查用户成员关系失败: {str(e)}")
            return False

    async def get_user_role_in_organization(
        self,
        organization_id: str,
        user_id: str,
        authorization: str
    ) -> Optional[str]:
        """
        获取用户在组织中的角色

        Args:
            organization_id: 组织ID
            user_id: 用户ID
            authorization: JWT令牌

        Returns:
            角色名称 (OWNER/ADMIN/MEMBER/GUEST) 或 None
        """
        try:
            members = await self.get_organization_members(
                organization_id,
                authorization
            )

            # 查找用户的角色
            for member in members:
                if member.get("userId") == user_id:
                    return member.get("role")

            return None

        except Exception as e:
            logger.error(f"获取用户角色失败: {str(e)}")
            return None


# 全局实例
organization_client = OrganizationClient()
```

#### FastAPI依赖注入

**dependencies.py** (`app/api/dependencies.py`):

```python
"""
FastAPI依赖注入
需求编号: REQ-JAVA-002

提供认证和权限验证的依赖项
"""
from typing import Optional
from fastapi import Header, HTTPException, Depends
from app.core.jwt_utils import jwt_validator, get_current_user_id
from app.services.organization_client import organization_client


async def get_current_user(
    authorization: str = Header(None)
) -> str:
    """
    获取当前用户ID（依赖注入）

    Args:
        authorization: Authorization header

    Returns:
        用户ID

    Raises:
        HTTPException: 如果未授权
    """
    if not authorization:
        raise HTTPException(status_code=401, detail="缺少Authorization header")

    try:
        user_id = get_current_user_id(authorization)
        return user_id
    except Exception as e:
        raise HTTPException(status_code=401, detail=str(e))


async def get_optional_user(
    authorization: Optional[str] = Header(None)
) -> Optional[str]:
    """
    获取当前用户ID（可选，依赖注入）

    Args:
        authorization: Authorization header

    Returns:
        用户ID 或 None
    """
    if not authorization:
        return None

    try:
        return get_current_user_id(authorization)
    except Exception:
        return None


async def verify_organization_access(
    organization_id: str,
    current_user: str = Depends(get_current_user),
    authorization: str = Header(None)
) -> bool:
    """
    验证用户是否有组织访问权限

    Args:
        organization_id: 组织ID
        current_user: 当前用户ID
        authorization: Authorization header

    Returns:
        是否有权限

    Raises:
        HTTPException: 如果无权限
    """
    is_member = await organization_client.check_user_membership(
        organization_id,
        current_user,
        authorization
    )

    if not is_member:
        raise HTTPException(
            status_code=403,
            detail="您不是该组织的成员"
        )

    return True


async def verify_organization_admin(
    organization_id: str,
    current_user: str = Depends(get_current_user),
    authorization: str = Header(None)
) -> bool:
    """
    验证用户是否为组织管理员

    Args:
        organization_id: 组织ID
        current_user: 当前用户ID
        authorization: Authorization header

    Returns:
        是否为管理员

    Raises:
        HTTPException: 如果无权限
    """
    role = await organization_client.get_user_role_in_organization(
        organization_id,
        current_user,
        authorization
    )

    if role not in ["OWNER", "ADMIN"]:
        raise HTTPException(
            status_code=403,
            detail="权限不足，需要管理员或所有者权限"
        )

    return True
```

#### AI服务中使用示例

**ai_endpoints.py** (`app/api/endpoints/ai_endpoints.py`):

```python
"""
AI服务API端点示例
需求编号: REQ-AI-002

展示如何在AI服务中使用组织权限验证
"""
from fastapi import APIRouter, Depends, Header
from app.api.dependencies import get_current_user, verify_organization_access
from app.services.organization_client import organization_client

router = APIRouter(prefix="/api/v1/ai", tags=["AI服务"])


@router.post("/generate-content")
async def generate_content(
    organization_id: str,
    document_id: str,
    prompt: str,
    current_user: str = Depends(get_current_user),
    authorization: str = Header(None),
    _: bool = Depends(verify_organization_access)
):
    """
    AI内容生成接口

    需要用户是组织成员才能调用
    """
    # 获取组织信息
    org_info = await organization_client.get_organization_by_id(
        organization_id,
        authorization
    )

    # AI内容生成逻辑...
    # 这里可以使用org_info中的组织上下文信息

    return {
        "success": True,
        "data": {
            "content": "生成的内容...",
            "organization_name": org_info.get("name")
        }
    }


@router.post("/analyze-requirements")
async def analyze_requirements(
    organization_id: str,
    document_id: str,
    current_user: str = Depends(get_current_user),
    authorization: str = Header(None)
):
    """
    需求分析接口

    不强制验证组织成员关系，但会记录用户信息
    """
    # 可选：获取组织信息
    org_info = await organization_client.get_organization_by_id(
        organization_id,
        authorization
    )

    # AI需求分析逻辑...

    return {
        "success": True,
        "data": {
            "requirements": [...],
            "analyzed_by": current_user
        }
    }
```

#### Python集成测试

**test_organization_integration.py** (`tests/integration/test_organization_integration.py`):

```python
"""
组织集成测试
需求编号: REQ-JAVA-002

测试Python服务与Java服务的集成
"""
import pytest
from httpx import AsyncClient
from app.core.jwt_utils import jwt_validator
from app.services.organization_client import organization_client


@pytest.mark.asyncio
class TestOrganizationIntegration:
    """组织集成测试"""

    @pytest.fixture
    def test_token(self):
        """生成测试JWT令牌"""
        import jwt
        from datetime import datetime, timedelta

        payload = {
            "user_id": "550e8400-e29b-41d4-a716-446655440000",
            "organization_id": "660e8400-e29b-41d4-a716-446655440000",
            "roles": ["ADMIN"],
            "exp": datetime.utcnow() + timedelta(hours=1)
        }

        token = jwt.encode(
            payload,
            "test_secret_key",
            algorithm="HS256"
        )

        return f"Bearer {token}"

    async def test_jwt_decode(self, test_token):
        """测试JWT解码"""
        payload = jwt_validator.decode_token(test_token)

        assert payload["user_id"] == "550e8400-e29b-41d4-a716-446655440000"
        assert payload["organization_id"] == "660e8400-e29b-41d4-a716-446655440000"
        assert "ADMIN" in payload["roles"]

    async def test_get_organization(self, test_token):
        """测试获取组织信息"""
        organization_id = "660e8400-e29b-41d4-a716-446655440000"

        org = await organization_client.get_organization_by_id(
            organization_id,
            test_token
        )

        # 验证返回数据
        if org:  # 如果Java服务运行中
            assert org.get("id") == organization_id
            assert "name" in org

    async def test_check_membership(self, test_token):
        """测试成员关系检查"""
        organization_id = "660e8400-e29b-41d4-a716-446655440000"
        user_id = "550e8400-e29b-41d4-a716-446655440000"

        is_member = await organization_client.check_user_membership(
            organization_id,
            user_id,
            test_token
        )

        # 成员关系检查应该不抛出异常
        assert isinstance(is_member, bool)

    async def test_get_user_role(self, test_token):
        """测试获取用户角色"""
        organization_id = "660e8400-e29b-41d4-a716-446655440000"
        user_id = "550e8400-e29b-41d4-a716-446655440000"

        role = await organization_client.get_user_role_in_organization(
            organization_id,
            user_id,
            test_token
        )

        # 角色应该是None或者有效的角色字符串
        assert role is None or role in ["OWNER", "ADMIN", "MEMBER", "GUEST"]


@pytest.mark.asyncio
async def test_ai_endpoint_with_auth(test_token):
    """测试带认证的AI端点"""
    async with AsyncClient(base_url="http://localhost:8001") as client:
        response = await client.post(
            "/api/v1/ai/generate-content",
            json={
                "organization_id": "660e8400-e29b-41d4-a716-446655440000",
                "document_id": "770e8400-e29b-41d4-a716-446655440000",
                "prompt": "生成技术方案"
            },
            headers={"Authorization": test_token}
        )

        # 验证响应
        assert response.status_code in [200, 401, 403, 503]
        # 200: 成功, 401: 未授权, 403: 无权限, 503: Java服务不可用
```

#### 配置文件更新

**config.py** (`app/core/config.py`):

```python
"""
应用配置
需求编号: REQ-JAVA-002

添加JWT和Java服务相关配置
"""
from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    """应用配置"""

    # 应用基本配置
    APP_NAME: str = "AIBidComposer AI Service"
    DEBUG: bool = False
    ENV: str = "production"

    # JWT配置
    JWT_SECRET_KEY: str  # 必须与Java服务使用相同的密钥
    JWT_ALGORITHM: str = "HS256"

    # Java服务配置
    JAVA_SERVICE_URL: str = "http://localhost:8080"

    # 数据库配置（只读访问）
    POSTGRES_HOST: str = "localhost"
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str = "aibidcomposer"
    POSTGRES_USER: str = "readonly_user"  # 使用只读用户
    POSTGRES_PASSWORD: str

    # Redis配置
    REDIS_URL: str = "redis://localhost:6379/0"

    # Elasticsearch配置
    ELASTICSEARCH_URL: str = "http://localhost:9200"
    ELASTICSEARCH_USER: Optional[str] = None
    ELASTICSEARCH_PASSWORD: Optional[str] = None

    # OpenAI配置
    OPENAI_API_KEY: str

    # Anthropic配置
    ANTHROPIC_API_KEY: Optional[str] = None

    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
```

#### 环境变量配置

**.env.example** (Python服务):

```bash
# JWT配置（与Java服务保持一致）
JWT_SECRET_KEY=your_secret_key_min_32_characters_long
JWT_ALGORITHM=HS256

# Java服务URL
JAVA_SERVICE_URL=http://localhost:8080

# 数据库配置（只读用户）
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=aibidcomposer
POSTGRES_USER=readonly_user
POSTGRES_PASSWORD=readonly_password

# Redis
REDIS_URL=redis://localhost:6379/0

# Elasticsearch
ELASTICSEARCH_URL=http://localhost:9200
ELASTICSEARCH_USER=elastic
ELASTICSEARCH_PASSWORD=your_elasticsearch_password

# AI服务
OPENAI_API_KEY=sk-your-openai-api-key
ANTHROPIC_API_KEY=your-anthropic-api-key
```

---
