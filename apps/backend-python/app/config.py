"""
Configuration Management

Manages all application settings using Pydantic BaseSettings.
Environment variables are loaded from .env file.

需求编号: REQ-PYTHON-AI-001
"""

from functools import lru_cache
from typing import List, Optional

from pydantic import Field, validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application settings loaded from environment variables"""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="allow",
    )

    # Application Settings
    app_name: str = Field(default="AIBidComposer AI Service", alias="APP_NAME")
    app_env: str = Field(default="development", alias="APP_ENV")
    debug: bool = Field(default=True, alias="DEBUG")
    log_level: str = Field(default="INFO", alias="LOG_LEVEL")
    api_version: str = Field(default="v1", alias="API_VERSION")

    # Server Settings
    host: str = Field(default="0.0.0.0", alias="HOST")
    port: int = Field(default=8001, alias="PORT")
    workers: int = Field(default=4, alias="WORKERS")
    reload: bool = Field(default=True, alias="RELOAD")

    # CORS Settings
    cors_origins: List[str] = Field(
        default=["http://localhost:5173", "http://localhost:8080"],
        alias="CORS_ORIGINS",
    )
    cors_allow_credentials: bool = Field(default=True, alias="CORS_ALLOW_CREDENTIALS")
    cors_allow_methods: List[str] = Field(default=["*"], alias="CORS_ALLOW_METHODS")
    cors_allow_headers: List[str] = Field(default=["*"], alias="CORS_ALLOW_HEADERS")

    @validator("cors_origins", pre=True)
    def parse_cors_origins(cls, v):
        if isinstance(v, str):
            return [origin.strip() for origin in v.split(",")]
        return v

    # Security
    secret_key: str = Field(..., alias="SECRET_KEY")
    algorithm: str = Field(default="HS256", alias="ALGORITHM")
    access_token_expire_minutes: int = Field(
        default=30, alias="ACCESS_TOKEN_EXPIRE_MINUTES"
    )

    # Java Backend Service Integration
    java_service_url: str = Field(
        default="http://localhost:8080", alias="JAVA_SERVICE_URL"
    )
    java_service_api_key: Optional[str] = Field(default=None, alias="JAVA_SERVICE_API_KEY")

    # Redis Configuration
    redis_url: str = Field(default="redis://localhost:6379/0", alias="REDIS_URL")
    redis_password: Optional[str] = Field(default=None, alias="REDIS_PASSWORD")
    redis_max_connections: int = Field(default=50, alias="REDIS_MAX_CONNECTIONS")

    # Elasticsearch Configuration
    elasticsearch_url: str = Field(
        default="http://localhost:9200", alias="ELASTICSEARCH_URL"
    )
    elasticsearch_user: str = Field(default="elastic", alias="ELASTICSEARCH_USER")
    elasticsearch_password: str = Field(..., alias="ELASTICSEARCH_PASSWORD")
    elasticsearch_index_prefix: str = Field(
        default="aibidcomposer", alias="ELASTICSEARCH_INDEX_PREFIX"
    )
    elasticsearch_timeout: int = Field(default=30, alias="ELASTICSEARCH_TIMEOUT")
    elasticsearch_max_retries: int = Field(default=3, alias="ELASTICSEARCH_MAX_RETRIES")

    # RabbitMQ Configuration
    rabbitmq_url: str = Field(
        default="amqp://guest:guest@localhost:5672/", alias="RABBITMQ_URL"
    )
    rabbitmq_queue_name: str = Field(default="ai_tasks", alias="RABBITMQ_QUEUE_NAME")
    celery_broker_url: str = Field(
        default="amqp://guest:guest@localhost:5672/", alias="CELERY_BROKER_URL"
    )
    celery_result_backend: str = Field(
        default="redis://localhost:6379/1", alias="CELERY_RESULT_BACKEND"
    )

    # OpenAI Configuration
    openai_api_key: str = Field(..., alias="OPENAI_API_KEY")
    openai_api_base: str = Field(
        default="https://api.openai.com/v1", alias="OPENAI_API_BASE"
    )
    openai_model: str = Field(default="gpt-4-turbo-preview", alias="OPENAI_MODEL")
    openai_embedding_model: str = Field(
        default="text-embedding-ada-002", alias="OPENAI_EMBEDDING_MODEL"
    )
    openai_max_tokens: int = Field(default=4000, alias="OPENAI_MAX_TOKENS")
    openai_temperature: float = Field(default=0.7, alias="OPENAI_TEMPERATURE")
    openai_timeout: int = Field(default=60, alias="OPENAI_TIMEOUT")

    # Anthropic Configuration
    anthropic_api_key: Optional[str] = Field(default=None, alias="ANTHROPIC_API_KEY")
    anthropic_model: str = Field(
        default="claude-3-opus-20240229", alias="ANTHROPIC_MODEL"
    )
    anthropic_max_tokens: int = Field(default=4000, alias="ANTHROPIC_MAX_TOKENS")
    anthropic_timeout: int = Field(default=60, alias="ANTHROPIC_TIMEOUT")

    # Pinecone Configuration (Optional)
    pinecone_api_key: Optional[str] = Field(default=None, alias="PINECONE_API_KEY")
    pinecone_environment: Optional[str] = Field(default=None, alias="PINECONE_ENVIRONMENT")
    pinecone_index_name: str = Field(default="aibidcomposer", alias="PINECONE_INDEX_NAME")

    # Neo4j Configuration (Optional)
    neo4j_uri: Optional[str] = Field(default=None, alias="NEO4J_URI")
    neo4j_user: Optional[str] = Field(default=None, alias="NEO4J_USER")
    neo4j_password: Optional[str] = Field(default=None, alias="NEO4J_PASSWORD")
    neo4j_database: str = Field(default="neo4j", alias="NEO4J_DATABASE")

    # LlamaIndex Settings
    llamaindex_chunk_size: int = Field(default=1024, alias="LLAMAINDEX_CHUNK_SIZE")
    llamaindex_chunk_overlap: int = Field(default=200, alias="LLAMAINDEX_CHUNK_OVERLAP")
    llamaindex_similarity_top_k: int = Field(
        default=10, alias="LLAMAINDEX_SIMILARITY_TOP_K"
    )
    llamaindex_cache_dir: str = Field(
        default=".cache/llama_index", alias="LLAMAINDEX_CACHE_DIR"
    )

    # Document Processing Settings
    max_file_size_mb: int = Field(default=50, alias="MAX_FILE_SIZE_MB")
    allowed_extensions: List[str] = Field(
        default=["pdf", "docx", "doc", "xlsx", "xls", "txt"],
        alias="ALLOWED_EXTENSIONS",
    )
    upload_dir: str = Field(default="/tmp/uploads", alias="UPLOAD_DIR")
    processing_timeout: int = Field(default=300, alias="PROCESSING_TIMEOUT")

    @validator("allowed_extensions", pre=True)
    def parse_extensions(cls, v):
        if isinstance(v, str):
            return [ext.strip() for ext in v.split(",")]
        return v

    # Rate Limiting
    rate_limit_enabled: bool = Field(default=True, alias="RATE_LIMIT_ENABLED")
    rate_limit_per_minute: int = Field(default=60, alias="RATE_LIMIT_PER_MINUTE")
    rate_limit_burst: int = Field(default=10, alias="RATE_LIMIT_BURST")

    # Monitoring & Logging
    enable_metrics: bool = Field(default=True, alias="ENABLE_METRICS")
    metrics_port: int = Field(default=9090, alias="METRICS_PORT")
    sentry_dsn: Optional[str] = Field(default=None, alias="SENTRY_DSN")
    sentry_environment: str = Field(default="development", alias="SENTRY_ENVIRONMENT")
    sentry_traces_sample_rate: float = Field(
        default=0.1, alias="SENTRY_TRACES_SAMPLE_RATE"
    )

    # Feature Flags
    enable_cache: bool = Field(default=True, alias="ENABLE_CACHE")
    enable_async_tasks: bool = Field(default=True, alias="ENABLE_ASYNC_TASKS")
    enable_distributed_tracing: bool = Field(
        default=False, alias="ENABLE_DISTRIBUTED_TRACING"
    )

    @property
    def is_production(self) -> bool:
        """Check if running in production environment"""
        return self.app_env == "production"

    @property
    def is_development(self) -> bool:
        """Check if running in development environment"""
        return self.app_env == "development"

    @property
    def api_base_url(self) -> str:
        """Get API base URL"""
        return f"/api/{self.api_version}"


@lru_cache()
def get_settings() -> Settings:
    """
    Get cached settings instance.
    This function is cached to avoid reading .env file multiple times.
    """
    return Settings()


# Global settings instance
settings = get_settings()
