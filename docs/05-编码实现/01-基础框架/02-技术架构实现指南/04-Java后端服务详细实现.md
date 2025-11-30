---
文档类型: 架构文档
需求编号: DOC-2025-11-002
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术架构详细实现 - ⚙️ Java后端服务详细实现

### Spring Boot项目结构
```
java-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aibidcomposer/
│   │   │       ├── AibidApplication.java
│   │   │       ├── config/              # 配置类
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── RedisConfig.java
│   │   │       │   ├── WebClientConfig.java
│   │   │       │   └── JpaConfig.java
│   │   │       ├── controller/          # 控制器
│   │   │       │   ├── UserController.java
│   │   │       │   ├── ProjectController.java
│   │   │       │   ├── TemplateController.java
│   │   │       │   └── DocumentController.java
│   │   │       ├── service/             # 业务逻辑层
│   │   │       │   ├── UserService.java
│   │   │       │   ├── ProjectService.java
│   │   │       │   ├── AIServiceClient.java
│   │   │       │   └── impl/
│   │   │       ├── repository/          # 数据访问层
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── ProjectRepository.java
│   │   │       │   └── TemplateRepository.java
│   │   │       ├── entity/              # 实体类
│   │   │       │   ├── User.java
│   │   │       │   ├── Project.java
│   │   │       │   ├── Template.java
│   │   │       │   └── Document.java
│   │   │       ├── dto/                 # 数据传输对象
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── exception/           # 异常处理
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── BusinessException.java
│   │   │       ├── security/            # 安全相关
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       └── util/                # 工具类
│   │   │           ├── ResponseUtil.java
│   │   │           └── DateUtil.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
├── pom.xml
└── Dockerfile
```

### 用户服务实现
```java
// 实体类 - entity/User.java
package com.aibidcomposer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "user";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// 数据访问层 - repository/UserRepository.java
package com.aibidcomposer.repository;

import com.aibidcomposer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

// DTO - dto/request/LoginRequest.java
package com.aibidcomposer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}

// DTO - dto/response/LoginResponse.java
package com.aibidcomposer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Integer expiresIn;
    private UserResponse user;
}

// 业务逻辑层 - service/UserService.java
package com.aibidcomposer.service;

import com.aibidcomposer.dto.request.LoginRequest;
import com.aibidcomposer.dto.request.RegisterRequest;
import com.aibidcomposer.dto.response.LoginResponse;
import com.aibidcomposer.dto.response.UserResponse;
import com.aibidcomposer.entity.User;
import com.aibidcomposer.exception.BusinessException;
import com.aibidcomposer.repository.UserRepository;
import com.aibidcomposer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setCompanyId(request.getCompanyId());
        user.setRole("user");

        User savedUser = userRepository.save(user);

        // 发送验证邮件（异步）
        // emailService.sendVerificationEmail(savedUser.getEmail());

        return mapToUserResponse(savedUser);
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (!user.getIsActive()) {
            throw new BusinessException("账户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 生成JWT Token
        String accessToken = jwtTokenProvider.generateToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getExpirationTime())
            .user(mapToUserResponse(user))
            .build();
    }

    /**
     * 获取用户信息
     */
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        return mapToUserResponse(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }

        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .companyId(user.getCompanyId())
            .role(user.getRole())
            .isActive(user.getIsActive())
            .isVerified(user.getIsVerified())
            .avatarUrl(user.getAvatarUrl())
            .phone(user.getPhone())
            .department(user.getDepartment())
            .position(user.getPosition())
            .createdAt(user.getCreatedAt())
            .lastLoginAt(user.getLastLoginAt())
            .build();
    }
}

// 控制器 - controller/UserController.java
package com.aibidcomposer.controller;

import com.aibidcomposer.dto.request.LoginRequest;
import com.aibidcomposer.dto.request.RegisterRequest;
import com.aibidcomposer.dto.response.ApiResponse;
import com.aibidcomposer.dto.response.LoginResponse;
import com.aibidcomposer.dto.response.UserResponse;
import com.aibidcomposer.service.UserService;
import com.aibidcomposer.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse user = userService.register(request);
        return ResponseUtil.success(user, "注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = userService.login(request);
        return ResponseUtil.success(response, "登录成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
        @AuthenticationPrincipal String userId
    ) {
        UserResponse user = userService.getUserById(UUID.fromString(userId));
        return ResponseUtil.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse user = userService.updateUser(UUID.fromString(userId), request);
        return ResponseUtil.success(user, "更新成功");
    }
}
```

### AI服务客户端实现
```java
// AI服务客户端 - service/AIServiceClient.java
package com.aibidcomposer.service;

import com.aibidcomposer.dto.ai.DocumentAnalysisRequest;
import com.aibidcomposer.dto.ai.DocumentAnalysisResponse;
import com.aibidcomposer.dto.ai.ContentGenerationRequest;
import com.aibidcomposer.dto.ai.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceClient {

    private final WebClient webClient;

    @Value("${ai.service.url}")
    private String aiServiceUrl; // http://python-ai-service:8001

    /**
     * 分析招标文档
     */
    public DocumentAnalysisResponse analyzeTenderDocument(
        String documentId,
        String content,
        String documentType
    ) {
        log.info("调用AI服务分析文档: {}", documentId);

        DocumentAnalysisRequest request = DocumentAnalysisRequest.builder()
            .documentId(documentId)
            .documentContent(content)
            .documentType(documentType)
            .analysisOptions(List.of("requirements", "risks", "opportunities"))
            .build();

        return webClient.post()
            .uri(aiServiceUrl + "/api/v1/analyze/document")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(DocumentAnalysisResponse.class)
            .timeout(Duration.ofSeconds(60))
            .doOnError(error -> log.error("AI文档分析失败", error))
            .block();
    }

    /**
     * 生成标书内容
     */
    public ContentGenerationResponse generateBidContent(
        String projectId,
        String templateId,
        Map<String, Object> requirements,
        Map<String, Object> companyProfile
    ) {
        log.info("调用AI服务生成内容: project={}, template={}", projectId, templateId);

        ContentGenerationRequest request = ContentGenerationRequest.builder()
            .projectId(projectId)
            .templateId(templateId)
            .requirements(requirements)
            .companyProfile(companyProfile)
            .build();

        return webClient.post()
            .uri(aiServiceUrl + "/api/v1/generate/content")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ContentGenerationResponse.class)
            .timeout(Duration.ofSeconds(120))
            .doOnError(error -> log.error("AI内容生成失败", error))
            .block();
    }

    /**
     * 异步分析文档
     */
    public Mono<DocumentAnalysisResponse> analyzeTenderDocumentAsync(
        String documentId,
        String content,
        String documentType
    ) {
        log.info("异步调用AI服务分析文档: {}", documentId);

        DocumentAnalysisRequest request = DocumentAnalysisRequest.builder()
            .documentId(documentId)
            .documentContent(content)
            .documentType(documentType)
            .analysisOptions(List.of("requirements", "risks", "opportunities"))
            .build();

        return webClient.post()
            .uri(aiServiceUrl + "/api/v1/analyze/document")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(DocumentAnalysisResponse.class)
            .timeout(Duration.ofSeconds(60))
            .doOnError(error -> log.error("AI文档分析失败", error));
    }
}

// WebClient配置 - config/WebClientConfig.java
package com.aibidcomposer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024)) // 16MB
            .build();
    }
}
```
