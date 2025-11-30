# Python FastAPI AI 服务任务详细计划 - AI-003 - AI-003: 企业能力库向量化 - 二级任务 3.1: 产品服务向量化 - 4) Python后端

##### 向量化服务
```python
# app/services/ai/vectorization_service.py
from typing import List, Dict, Any, Optional
from llama_index.embeddings import OpenAIEmbedding
from app.services.ai.elasticsearch_store import ElasticsearchVectorStore
from app.core.config import settings
import httpx
import logging

logger = logging.getLogger(__name__)

class VectorizationService:
    """向量化服务
    需求编号: REQ-AI-003
    """

    def __init__(self):
        self.embedding_model = OpenAIEmbedding(
            api_key=settings.OPENAI_API_KEY,
            model="text-embedding-ada-002"
        )
        self.es_store = ElasticsearchVectorStore()
        self.java_client = httpx.AsyncClient(base_url="http://backend-java:8080")

    async def vectorize_product(
        self,
        capability_id: str,
        organization_id: str
    ) -> Dict[str, Any]:
        """
        向量化产品能力

        Args:
            capability_id: 能力ID
            organization_id: 组织ID

        Returns:
            向量化结果
        """
        try:
            # 1. 从Java服务获取能力详情
            logger.info(f"Fetching capability {capability_id} from Java service")
            response = await self.java_client.get(f"/api/v1/capabilities/{capability_id}")
            response.raise_for_status()
            capability = response.json()['data']

            # 2. 构建向量化文本
            vectorize_text = self._build_vectorize_text(capability)

            # 3. 生成向量
            logger.info(f"Generating embedding for capability {capability_id}")
            embedding = await self.embedding_model.aget_text_embedding(vectorize_text)

            # 4. 存储到Elasticsearch
            logger.info(f"Storing embedding to Elasticsearch for {capability_id}")
            await self.es_store.add_document(
                doc_id=capability_id,
                embedding=embedding,
                metadata={
                    'capability_id': capability_id,
                    'organization_id': organization_id,
                    'capability_type': 'product',
                    'name': capability['name'],
                    'description': capability['description'],
                    'features': capability.get('features', []),
                    'advantages': capability.get('advantages', []),
                    'technology_stack': capability.get('technologyStack', []),
                    'tags': capability.get('tags', []),
                    'is_active': capability.get('isActive', True),
                    'vectorized_at': datetime.utcnow().isoformat()
                },
                index_name="capabilities"
            )

            # 5. 回调Java服务更新状态
            await self._callback_java_service(capability_id, "completed")

            logger.info(f"Vectorization completed for capability {capability_id}")

            return {
                'capability_id': capability_id,
                'status': 'completed',
                'embedding_dim': len(embedding),
                'vectorized_at': datetime.utcnow().isoformat()
            }

        except Exception as e:
            logger.error(f"Vectorization failed for capability {capability_id}: {str(e)}")

            # 回调失败状态
            await self._callback_java_service(capability_id, "failed", str(e))

            raise

    def _build_vectorize_text(self, capability: Dict[str, Any]) -> str:
        """
        构建向量化文本

        将产品的多个字段合并成一段完整的描述文本，用于生成向量
        """
        parts = []

        # 1. 名称和分类
        parts.append(f"产品名称：{capability['name']}")
        if capability.get('category'):
            parts.append(f"分类：{capability['category']}")

        # 2. 描述
        parts.append(f"描述：{capability['description']}")

        # 3. 功能特性
        if capability.get('features'):
            features_text = "、".join(capability['features'])
            parts.append(f"功能特性：{features_text}")

        # 4. 优势
        if capability.get('advantages'):
            advantages_text = "、".join(capability['advantages'])
            parts.append(f"优势：{advantages_text}")

        # 5. 应用场景
        if capability.get('applicationScenarios'):
            scenarios_text = "、".join(capability['applicationScenarios'])
            parts.append(f"应用场景：{scenarios_text}")

        # 6. 技术栈
        if capability.get('technologyStack'):
            tech_text = "、".join(capability['technologyStack'])
            parts.append(f"技术栈：{tech_text}")

        # 合并所有部分
        return "\n".join(parts)

    async def _callback_java_service(
        self,
        capability_id: str,
        status: str,
        error_message: Optional[str] = None
    ):
        """回调Java服务更新向量化状态"""
        try:
            await self.java_client.post(
                "/api/v1/capabilities/vectorize/callback",
                json={
                    'capability_id': capability_id,
                    'status': status,
                    'error_message': error_message,
                    'vectorized_at': datetime.utcnow().isoformat()
                }
            )
        except Exception as e:
            logger.error(f"Callback to Java service failed: {str(e)}")

    async def close(self):
        """关闭资源"""
        await self.java_client.aclose()
        await self.es_store.close()
```

##### Celery异步任务
```python
# app/tasks/vectorization_tasks.py
from celery import Task
from app.tasks.celery_app import celery_app
from app.services.ai.vectorization_service import VectorizationService
import logging

logger = logging.getLogger(__name__)

@celery_app.task(bind=True, max_retries=3)
def vectorize_product_task(self: Task, capability_id: str, organization_id: str):
    """
    产品向量化异步任务
    需求编号: REQ-AI-003
    """
    logger.info(f"Starting vectorization task for capability {capability_id}")

    try:
        service = VectorizationService()
        result = await service.vectorize_product(capability_id, organization_id)

        logger.info(f"Vectorization task completed: {result}")
        return result

    except Exception as e:
        logger.error(f"Vectorization task failed: {str(e)}")

        # 重试机制
        raise self.retry(exc=e, countdown=60 * (self.request.retries + 1))
```

##### RabbitMQ消费者
```python
# app/consumers/vectorization_consumer.py
import pika
import json
from app.tasks.vectorization_tasks import vectorize_product_task
from app.core.config import settings
import logging

logger = logging.getLogger(__name__)

class VectorizationConsumer:
    """
    向量化消息消费者
    需求编号: REQ-AI-003
    """

    def __init__(self):
        # RabbitMQ连接
        credentials = pika.PlainCredentials(
            settings.RABBITMQ_USER,
            settings.RABBITMQ_PASSWORD
        )
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=settings.RABBITMQ_HOST,
                port=settings.RABBITMQ_PORT,
                credentials=credentials
            )
        )
        self.channel = self.connection.channel()

        # 声明交换机
        self.channel.exchange_declare(
            exchange='ai.vectorize.exchange',
            exchange_type='topic',
            durable=True
        )

        # 声明队列
        self.channel.queue_declare(
            queue='ai.vectorize.product.queue',
            durable=True
        )

        # 绑定队列到交换机
        self.channel.queue_bind(
            exchange='ai.vectorize.exchange',
            queue='ai.vectorize.product.queue',
            routing_key='vectorize.product'
        )

    def callback(self, ch, method, properties, body):
        """处理消息"""
        try:
            message = json.loads(body.decode('utf-8'))
            logger.info(f"Received vectorization message: {message}")

            capability_id = message['capability_id']
            organization_id = message['organization_id']

            # 提交Celery任务
            vectorize_product_task.delay(capability_id, organization_id)

            # 确认消息
            ch.basic_ack(delivery_tag=method.delivery_tag)

        except Exception as e:
            logger.error(f"Message processing failed: {str(e)}")
            # 拒绝消息并重新入队
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

    def start(self):
        """开始消费"""
        logger.info("Starting vectorization consumer...")
        self.channel.basic_qos(prefetch_count=1)
        self.channel.basic_consume(
            queue='ai.vectorize.product.queue',
            on_message_callback=self.callback
        )
        self.channel.start_consuming()

    def stop(self):
        """停止消费"""
        self.channel.stop_consuming()
        self.connection.close()
```

##### API接口
```python
# app/api/v1/vectorization.py
from fastapi import APIRouter, HTTPException, BackgroundTasks
from pydantic import BaseModel, Field
from typing import List, Optional
from app.services.ai.vectorization_service import VectorizationService
from app.tasks.vectorization_tasks import vectorize_product_task

router = APIRouter(prefix="/vectorize", tags=["向量化"])

class BatchVectorizeRequest(BaseModel):
    """批量向量化请求"""
    capability_ids: List[str] = Field(..., description="能力ID列表")
    capability_type: str = Field(..., description="能力类型：product|service|case")

class BatchVectorizeResponse(BaseModel):
    """批量向量化响应"""
    task_count: int = Field(..., description="提交的任务数量")
    task_ids: List[str] = Field(..., description="任务ID列表")

@router.post("/batch", response_model=BatchVectorizeResponse)
async def batch_vectorize(
    request: BatchVectorizeRequest,
    background_tasks: BackgroundTasks
):
    """
    批量向量化
    需求编号: REQ-AI-003
    """
    task_ids = []

    for capability_id in request.capability_ids:
        # 提交Celery任务
        task = vectorize_product_task.delay(
            capability_id=capability_id,
            organization_id="auto"  # 从上下文获取
        )
        task_ids.append(task.id)

    return BatchVectorizeResponse(
        task_count=len(task_ids),
        task_ids=task_ids
    )
```

**验证标准**:
- [ ] RabbitMQ消息能正确消费
- [ ] Celery任务能正确执行
- [ ] 向量生成成功并存储到Elasticsearch
- [ ] 回调Java服务状态更新成功
- [ ] 异常情况能正确重试
