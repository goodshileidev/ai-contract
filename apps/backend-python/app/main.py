"""
FastAPI Application Entry Point

Main application file that initializes FastAPI, registers middleware,
exception handlers, and API routes.

需求编号: REQ-PYTHON-AI-001
创建时间: 2025-11-26
"""

from contextlib import asynccontextmanager
from typing import AsyncGenerator

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse

from app.config import settings
from app.api.v1 import health
# from app.core import exceptions, logging  # Will be created next


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator:
    """
    Lifespan context manager for FastAPI.
    Handles startup and shutdown events.
    """
    # Startup
    print(f"🚀 Starting {settings.app_name}")
    print(f"📍 Environment: {settings.app_env}")
    print(f"🔧 Debug mode: {settings.debug}")
    print(f"🌐 Server: {settings.host}:{settings.port}")

    # TODO: Initialize connections
    # - Elasticsearch
    # - Redis
    # - RabbitMQ (Celery)

    yield

    # Shutdown
    print("🛑 Shutting down gracefully...")
    # TODO: Close connections


def create_app() -> FastAPI:
    """
    Create and configure FastAPI application.

    Returns:
        FastAPI application instance
    """
    app = FastAPI(
        title=settings.app_name,
        description="AI Backend Service - Handles AI capabilities including document parsing, "
        "content generation, intelligent matching, and RAG-based search",
        version="1.0.0",
        docs_url=f"{settings.api_base_url}/docs",
        redoc_url=f"{settings.api_base_url}/redoc",
        openapi_url=f"{settings.api_base_url}/openapi.json",
        lifespan=lifespan,
        debug=settings.debug,
    )

    # Configure CORS
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=settings.cors_allow_credentials,
        allow_methods=settings.cors_allow_methods,
        allow_headers=settings.cors_allow_headers,
    )

    # Add GZip compression
    app.add_middleware(GZipMiddleware, minimum_size=1000)

    # TODO: Add custom middleware
    # - Request ID tracking
    # - Logging middleware
    # - Rate limiting
    # - Authentication

    # Register exception handlers
    @app.exception_handler(Exception)
    async def global_exception_handler(request, exc):
        """Global exception handler"""
        return JSONResponse(
            status_code=500,
            content={
                "success": False,
                "error": {
                    "code": "INTERNAL_SERVER_ERROR",
                    "message": "An unexpected error occurred",
                    "details": str(exc) if settings.debug else None,
                },
                "timestamp": "",  # TODO: Add timestamp
            },
        )

    # Register API routes
    app.include_router(
        health.router,
        prefix=settings.api_base_url,
        tags=["Health"],
    )

    # TODO: Register additional routers
    # app.include_router(ai.router, prefix=f"{settings.api_base_url}/ai", tags=["AI"])

    # Root endpoint
    @app.get("/")
    async def root():
        """Root endpoint"""
        return {
            "success": True,
            "data": {
                "service": settings.app_name,
                "version": "1.0.0",
                "environment": settings.app_env,
                "status": "running",
                "docs_url": f"{settings.api_base_url}/docs",
            },
        }

    return app


# Create application instance
app = create_app()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.reload,
        workers=settings.workers if not settings.reload else 1,
        log_level=settings.log_level.lower(),
    )
