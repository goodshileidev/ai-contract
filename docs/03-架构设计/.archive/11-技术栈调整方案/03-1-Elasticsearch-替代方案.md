---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术栈调整方案 - 1️⃣ Elasticsearch 替代方案

### 1.1 可行性分析

**✅ 完全可行，且有明显优势**

#### Elasticsearch 8.x 核心能力

**向量搜索能力** (替代Pinecone):
```yaml
特性:
  - kNN向量搜索: 支持余弦相似度、点积、L2距离
  - 稠密向量: dense_vector字段类型，支持最多2048维
  - 近似最近邻: HNSW算法，性能优异
  - 混合检索: 向量搜索 + 全文搜索 + 过滤条件
  - 向量维度: 支持OpenAI embedding (1536维)

性能:
  - 百万级向量: 亚秒级检索
  - 十亿级向量: 秒级检索 (分片优化)
  - 支持实时索引
```

**知识图谱替代方案**:
```yaml
方案1: ES + 关系建模 (推荐)
  优势:
    - 使用嵌套文档存储实体关系
    - 利用ES强大的聚合功能分析关系
    - 全文搜索 + 关系查询一体化
  局限:
    - 不支持复杂图遍历 (如多跳路径)
    - 图算法能力弱

方案2: ES + Neo4j混合 (高级需求)
  使用场景:
    - ES: 向量检索 + 文档搜索 + 简单关系
    - Neo4j: 复杂图分析 (如果需要)
  建议: 先用方案1，按需升级方案2

方案3: ES + Graph插件
  - ES提供Graph探索功能
  - 适合关系发现和可视化
```

#### 架构优势

```yaml
统一技术栈优势:
  运维简化:
    - 只需维护一个搜索引擎
    - 统一的监控和告警
    - 降低基础设施成本

  性能提升:
    - 减少网络跳转 (无需Pinecone API调用)
    - 本地部署，延迟更低
    - 统一的缓存策略

  功能增强:
    - 全文搜索 + 向量搜索无缝结合
    - 强大的聚合分析
    - 实时搜索建议

  成本优化:
    - 无需Pinecone订阅费用
    - 可自建集群
    - 灵活扩展
```

### 1.2 Elasticsearch 架构设计

#### 索引设计

**1. 标书文档索引**
```json
PUT /bid_documents
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "index": {
      "similarity": {
        "default": {
          "type": "BM25"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "document_id": { "type": "keyword" },
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "content": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "content_vector": {
        "type": "dense_vector",
        "dims": 1536,
        "index": true,
        "similarity": "cosine"
      },
      "sections": {
        "type": "nested",
        "properties": {
          "section_id": { "type": "keyword" },
          "title": { "type": "text" },
          "content": { "type": "text" },
          "content_vector": {
            "type": "dense_vector",
            "dims": 1536,
            "index": true,
            "similarity": "cosine"
          }
        }
      },
      "metadata": {
        "properties": {
          "project_id": { "type": "keyword" },
          "created_at": { "type": "date" },
          "tags": { "type": "keyword" },
          "status": { "type": "keyword" }
        }
      }
    }
  }
}
```

**2. 企业能力知识库索引**
```json
PUT /enterprise_capabilities
{
  "mappings": {
    "properties": {
      "capability_id": { "type": "keyword" },
      "name": { "type": "text", "analyzer": "ik_max_word" },
      "description": { "type": "text", "analyzer": "ik_max_word" },
      "description_vector": {
        "type": "dense_vector",
        "dims": 1536,
        "index": true,
        "similarity": "cosine"
      },
      "category": { "type": "keyword" },
      "tags": { "type": "keyword" },

      // 关系建模 - 替代知识图谱
      "related_projects": {
        "type": "nested",
        "properties": {
          "project_id": { "type": "keyword" },
          "project_name": { "type": "text" },
          "similarity_score": { "type": "float" }
        }
      },
      "required_by": {
        "type": "nested",
        "properties": {
          "requirement_id": { "type": "keyword" },
          "requirement_text": { "type": "text" },
          "match_score": { "type": "float" }
        }
      },
      "related_personnel": {
        "type": "nested",
        "properties": {
          "person_id": { "type": "keyword" },
          "name": { "type": "text" },
          "role": { "type": "keyword" }
        }
      }
    }
  }
}
```

**3. 案例库索引**
```json
PUT /project_cases
{
  "mappings": {
    "properties": {
      "case_id": { "type": "keyword" },
      "project_name": { "type": "text", "analyzer": "ik_max_word" },
      "description": { "type": "text", "analyzer": "ik_max_word" },
      "description_vector": {
        "type": "dense_vector",
        "dims": 1536,
        "index": true,
        "similarity": "cosine"
      },
      "technologies": { "type": "keyword" },
      "industry": { "type": "keyword" },
      "scale": { "type": "keyword" },
      "success_metrics": {
        "type": "object",
        "properties": {
          "budget": { "type": "float" },
          "duration_months": { "type": "integer" },
          "team_size": { "type": "integer" }
        }
      }
    }
  }
}
```

#### 混合检索实现

**语义搜索 + 全文搜索 + 过滤**
```python
from elasticsearch import AsyncElasticsearch

class HybridSearchService:
    def __init__(self):
        self.es = AsyncElasticsearch(
            hosts=["http://elasticsearch:9200"],
            basic_auth=("elastic", settings.ES_PASSWORD)
        )

    async def hybrid_search(
        self,
        query_text: str,
        query_vector: list[float],
        filters: dict = None,
        top_k: int = 10
    ):
        """
        混合检索：向量搜索 + 全文搜索 + 业务过滤
        """
        search_body = {
            "size": top_k,
            "query": {
                "bool": {
                    "should": [
                        # 向量搜索
                        {
                            "script_score": {
                                "query": {"match_all": {}},
                                "script": {
                                    "source": "cosineSimilarity(params.query_vector, 'content_vector') + 1.0",
                                    "params": {"query_vector": query_vector}
                                }
                            }
                        },
                        # 全文搜索
                        {
                            "multi_match": {
                                "query": query_text,
                                "fields": ["title^2", "content"],
                                "type": "best_fields",
                                "boost": 0.5
                            }
                        }
                    ],
                    # 业务过滤
                    "filter": self._build_filters(filters) if filters else []
                }
            },
            "_source": ["document_id", "title", "content", "metadata"],
            "highlight": {
                "fields": {
                    "content": {
                        "fragment_size": 150,
                        "number_of_fragments": 3
                    }
                }
            }
        }

        response = await self.es.search(
            index="bid_documents",
            body=search_body
        )

        return self._process_results(response)

    def _build_filters(self, filters: dict) -> list:
        filter_clauses = []

        if "project_id" in filters:
            filter_clauses.append({
                "term": {"metadata.project_id": filters["project_id"]}
            })

        if "status" in filters:
            filter_clauses.append({
                "terms": {"metadata.status": filters["status"]}
            })

        if "date_range" in filters:
            filter_clauses.append({
                "range": {
                    "metadata.created_at": {
                        "gte": filters["date_range"]["from"],
                        "lte": filters["date_range"]["to"]
                    }
                }
            })

        return filter_clauses
```

#### 关系查询实现

**使用嵌套查询模拟图关系**
```python
class RelationshipQueryService:
    async def find_matching_capabilities(
        self,
        requirement_text: str,
        requirement_vector: list[float],
        min_score: float = 0.7
    ) -> list[dict]:
        """
        查找匹配需求的企业能力（类似知识图谱的关系查询）
        """
        search_body = {
            "size": 20,
            "query": {
                "script_score": {
                    "query": {
                        "bool": {
                            "should": [
                                {
                                    "match": {
                                        "description": {
                                            "query": requirement_text,
                                            "boost": 0.3
                                        }
                                    }
                                },
                                {
                                    "nested": {
                                        "path": "required_by",
                                        "query": {
                                            "match": {
                                                "required_by.requirement_text": requirement_text
                                            }
                                        },
                                        "score_mode": "max"
                                    }
                                }
                            ]
                        }
                    },
                    "script": {
                        "source": """
                            double vectorScore = cosineSimilarity(params.query_vector, 'description_vector') + 1.0;
                            return vectorScore * 10;
                        """,
                        "params": {"query_vector": requirement_vector}
                    }
                }
            },
            "min_score": min_score * 20,  # 调整阈值
            # 聚合：分析能力分布
            "aggs": {
                "by_category": {
                    "terms": {"field": "category"}
                },
                "by_tags": {
                    "terms": {"field": "tags", "size": 20}
                }
            }
        }

        response = await self.es.search(
            index="enterprise_capabilities",
            body=search_body
        )

        return self._enrich_with_relations(response)

    async def _enrich_with_relations(self, response):
        """
        查询相关项目和人员（模拟图遍历）
        """
        results = []
        for hit in response["hits"]["hits"]:
            capability = hit["_source"]

            # 获取相关项目详情
            if "related_projects" in capability:
                project_ids = [p["project_id"] for p in capability["related_projects"]]
                projects = await self._get_projects_by_ids(project_ids)
                capability["related_projects_detail"] = projects

            results.append({
                "capability": capability,
                "score": hit["_score"],
                "relevance": hit["_score"] / 20  # 归一化到0-1
            })

        return results
```

### 1.3 部署架构

**Elasticsearch 集群配置**
```yaml
# docker-compose.yml
version: '3.8'

services:
  elasticsearch-master:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: es-master
    environment:
      - node.name=es-master
      - cluster.name=aibidcomposer-cluster
      - discovery.seed_hosts=es-node1,es-node2
      - cluster.initial_master_nodes=es-master
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
      - xpack.security.enabled=true
      - xpack.security.http.ssl.enabled=false
      - xpack.license.self_generated.type=basic
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - es-master-data:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - elastic

  elasticsearch-node1:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: es-node1
    environment:
      - node.name=es-node1
      - cluster.name=aibidcomposer-cluster
      - discovery.seed_hosts=es-master,es-node2
      - cluster.initial_master_nodes=es-master
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
      - xpack.security.enabled=true
    volumes:
      - es-node1-data:/usr/share/elasticsearch/data
    networks:
      - elastic

  elasticsearch-node2:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: es-node2
    environment:
      - node.name=es-node2
      - cluster.name=aibidcomposer-cluster
      - discovery.seed_hosts=es-master,es-node1
      - cluster.initial_master_nodes=es-master
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
      - xpack.security.enabled=true
    volumes:
      - es-node2-data:/usr/share/elasticsearch/data
    networks:
      - elastic

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    container_name: kibana
    ports:
      - 5601:5601
    environment:
      - ELASTICSEARCH_HOSTS=http://es-master:9200
      - ELASTICSEARCH_USERNAME=kibana_system
      - ELASTICSEARCH_PASSWORD=${KIBANA_PASSWORD}
    networks:
      - elastic
    depends_on:
      - elasticsearch-master

volumes:
  es-master-data:
  es-node1-data:
  es-node2-data:

networks:
  elastic:
    driver: bridge
```

**Kubernetes部署**
```yaml
# elasticsearch-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: elasticsearch
  namespace: aibidcomposer
spec:
  serviceName: elasticsearch
  replicas: 3
  selector:
    matchLabels:
      app: elasticsearch
  template:
    metadata:
      labels:
        app: elasticsearch
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
        resources:
          limits:
            memory: 4Gi
            cpu: 2
          requests:
            memory: 2Gi
            cpu: 1
        ports:
        - containerPort: 9200
          name: rest
        - containerPort: 9300
          name: inter-node
        volumeMounts:
        - name: data
          mountPath: /usr/share/elasticsearch/data
        env:
        - name: cluster.name
          value: aibidcomposer-cluster
        - name: node.name
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: discovery.seed_hosts
          value: "elasticsearch-0.elasticsearch,elasticsearch-1.elasticsearch,elasticsearch-2.elasticsearch"
        - name: cluster.initial_master_nodes
          value: "elasticsearch-0,elasticsearch-1,elasticsearch-2"
        - name: ES_JAVA_OPTS
          value: "-Xms2g -Xmx2g"
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 100Gi
```

---
