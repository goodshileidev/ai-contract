---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术栈调整方案 - 2️⃣ Java Spring Boot + Python 混合架构

### 2.1 架构设计

**✅ 推荐架构：微服务分离**

```yaml
服务划分:
  Spring Boot服务 (业务核心):
    职责:
      - 用户认证授权
      - 项目管理
      - 文档管理 (CRUD)
      - 组织管理
      - 权限控制
      - 审批流程
      - 数据持久化
    技术栈:
      - Spring Boot 3.x
      - Spring Security
      - Spring Data JPA
      - PostgreSQL
      - Redis
      - JWT

  Python AI服务 (AI能力):
    职责:
      - LLM调用 (OpenAI, Anthropic等)
      - 文档向量化
      - 语义搜索
      - 需求分析
      - 内容生成
      - 质量审查
      - Elasticsearch交互
    技术栈:
      - FastAPI
      - LlamaIndex
      - OpenAI SDK
      - Elasticsearch Python客户端
```

**服务通信架构**
```
┌─────────────────┐
│   前端 (React)   │
│  Ant Design Pro │
└────────┬────────┘
         │
         ├─────────────────┐
         │                 │
         ▼                 ▼
┌──────────────────┐  ┌──────────────────┐
│  Spring Boot服务  │  │   Python AI服务   │
│  (数据管理)       │◄─┤   (AI能力)       │
│                  │  │                  │
│  - 用户/项目     │  │  - LLM调用       │
│  - 文档CRUD      │  │  - 向量化        │
│  - 权限控制      │  │  - 语义搜索      │
│  - 审批流程      │  │  - 内容生成      │
└────────┬─────────┘  └────────┬─────────┘
         │                     │
         ├─────────────────────┤
         │                     │
         ▼                     ▼
┌──────────────────┐  ┌──────────────────┐
│   PostgreSQL     │  │  Elasticsearch   │
│   (业务数据)      │  │  (向量+搜索)      │
└──────────────────┘  └──────────────────┘
         │
         ▼
┌──────────────────┐
│      Redis       │
│  (缓存+会话)      │
└──────────────────┘
```

### 2.2 服务接口设计

**Spring Boot → Python AI服务调用**

**Spring Boot RestTemplate配置**
```java
// config/AiServiceConfig.java
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Bean
    public RestTemplate aiServiceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 设置超时
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);

        restTemplate.setRequestFactory(factory);

        // 添加拦截器
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("X-Service-Auth", aiServiceToken);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}

// service/AiServiceClient.java
@Service
@Slf4j
public class AiServiceClient {

    @Autowired
    private RestTemplate aiServiceRestTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    /**
     * 分析招标文件需求
     */
    public RequirementAnalysisResult analyzeRequirements(String documentId) {
        String url = aiServiceUrl + "/api/ai/analyze-requirements";

        Map<String, Object> request = Map.of(
            "document_id", documentId,
            "analysis_depth", "detailed"
        );

        try {
            ResponseEntity<RequirementAnalysisResult> response =
                aiServiceRestTemplate.postForEntity(
                    url,
                    request,
                    RequirementAnalysisResult.class
                );

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to analyze requirements for document: {}", documentId, e);
            throw new AiServiceException("需求分析失败", e);
        }
    }

    /**
     * 生成标书内容
     */
    public ContentGenerationResult generateContent(ContentGenerationRequest request) {
        String url = aiServiceUrl + "/api/ai/generate-content";

        try {
            ResponseEntity<ContentGenerationResult> response =
                aiServiceRestTemplate.postForEntity(
                    url,
                    request,
                    ContentGenerationResult.class
                );

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to generate content", e);
            throw new AiServiceException("内容生成失败", e);
        }
    }

    /**
     * 语义搜索相似案例
     */
    public List<ProjectCase> searchSimilarCases(String requirementText, int topK) {
        String url = aiServiceUrl + "/api/ai/search-similar-cases";

        Map<String, Object> request = Map.of(
            "query", requirementText,
            "top_k", topK
        );

        try {
            ResponseEntity<SearchResult> response =
                aiServiceRestTemplate.postForEntity(
                    url,
                    request,
                    SearchResult.class
                );

            return response.getBody().getCases();

        } catch (Exception e) {
            log.error("Failed to search similar cases", e);
            return Collections.emptyList();
        }
    }
}
```

**Python AI服务端点**
```python
# app/main.py
from fastapi import FastAPI, Depends, HTTPException
from app.services.llm_service import LLMService
from app.services.search_service import SearchService
from app.middleware.auth import verify_service_token

app = FastAPI(title="AI Service for AIBidComposer")

@app.post("/api/ai/analyze-requirements")
async def analyze_requirements(
    request: RequirementAnalysisRequest,
    _: None = Depends(verify_service_token)
):
    """
    分析招标文件需求
    供Spring Boot服务调用
    """
    llm_service = LLMService()

    # 从Elasticsearch获取文档
    document = await search_service.get_document(request.document_id)

    # 使用LLM分析
    analysis_result = await llm_service.analyze_requirements(
        document_content=document.content,
        depth=request.analysis_depth
    )

    return {
        "requirements": analysis_result.requirements,
        "key_points": analysis_result.key_points,
        "technical_specs": analysis_result.technical_specs
    }

@app.post("/api/ai/generate-content")
async def generate_content(
    request: ContentGenerationRequest,
    _: None = Depends(verify_service_token)
):
    """
    生成标书内容
    """
    llm_service = LLMService()

    # 检索相关案例
    similar_cases = await search_service.search_similar_cases(
        request.requirement_text,
        top_k=5
    )

    # 生成内容
    generated_content = await llm_service.generate_bid_content(
        requirement=request.requirement_text,
        context=similar_cases,
        section_type=request.section_type
    )

    return {
        "content": generated_content,
        "references": similar_cases,
        "tokens_used": llm_service.get_tokens_used()
    }

@app.post("/api/ai/search-similar-cases")
async def search_similar_cases(
    request: SearchRequest,
    _: None = Depends(verify_service_token)
):
    """
    语义搜索相似案例
    """
    search_service = SearchService()

    # 向量化查询
    query_vector = await search_service.embed_text(request.query)

    # 混合搜索
    results = await search_service.hybrid_search(
        query_text=request.query,
        query_vector=query_vector,
        index="project_cases",
        top_k=request.top_k
    )

    return {
        "cases": results,
        "total": len(results)
    }
```

### 2.3 数据一致性策略

**事件驱动架构**
```java
// Spring Boot - 发布事件
@Service
public class DocumentService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AiServiceClient aiServiceClient;

    @Transactional
    public Document createDocument(CreateDocumentRequest request) {
        // 1. 保存文档到PostgreSQL
        Document document = documentRepository.save(
            new Document(request)
        );

        // 2. 发布事件：异步索引到Elasticsearch
        eventPublisher.publishEvent(
            new DocumentCreatedEvent(document.getId(), document.getContent())
        );

        return document;
    }
}

// 事件监听器
@Component
@Slf4j
public class DocumentEventListener {

    @Autowired
    private AiServiceClient aiServiceClient;

    @Async
    @EventListener
    public void handleDocumentCreated(DocumentCreatedEvent event) {
        try {
            // 调用Python AI服务索引文档
            aiServiceClient.indexDocument(
                event.getDocumentId(),
                event.getContent()
            );

            log.info("Document indexed: {}", event.getDocumentId());

        } catch (Exception e) {
            log.error("Failed to index document: {}", event.getDocumentId(), e);
            // 可以重试或记录到死信队列
        }
    }
}
```

**Python AI服务 - 索引处理**
```python
@app.post("/api/ai/index-document")
async def index_document(
    request: IndexDocumentRequest,
    _: None = Depends(verify_service_token)
):
    """
    索引文档到Elasticsearch
    """
    search_service = SearchService()

    # 1. 文档分块
    chunks = search_service.chunk_document(
        request.content,
        chunk_size=500,
        chunk_overlap=50
    )

    # 2. 生成embeddings
    embeddings = await search_service.embed_batch(
        [chunk.text for chunk in chunks]
    )

    # 3. 索引到Elasticsearch
    await search_service.index_document_chunks(
        document_id=request.document_id,
        chunks=chunks,
        embeddings=embeddings
    )

    return {"status": "indexed", "chunks_count": len(chunks)}
```

### 2.4 Spring Boot 项目结构

```
spring-boot-service/
├── src/main/java/com/aibidcomposer/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java
│   │   ├── RedisConfig.java
│   │   └── AiServiceConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ProjectController.java
│   │   ├── DocumentController.java
│   │   ├── TemplateController.java
│   │   └── ApprovalController.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── ProjectService.java
│   │   ├── DocumentService.java
│   │   ├── AiServiceClient.java       # AI服务调用客户端
│   │   └── SearchService.java         # 搜索服务(委托给Python)
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── DocumentRepository.java
│   │   └── TemplateRepository.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Project.java
│   │   ├── Document.java
│   │   └── Template.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateProjectRequest.java
│   │   │   ├── ContentGenerationRequest.java
│   │   │   └── SearchRequest.java
│   │   └── response/
│   │       ├── ProjectResponse.java
│   │       ├── DocumentResponse.java
│   │       └── AiAnalysisResponse.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── AiServiceException.java
│   └── Application.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/migration/         # Flyway迁移脚本
│       ├── V1__init_schema.sql
│       └── V2__add_indexes.sql
└── pom.xml
```

**核心依赖 (pom.xml)**
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.0</version>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>

    <!-- Flyway (数据库迁移) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---
