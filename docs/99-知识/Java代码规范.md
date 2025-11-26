---
文档类型: 知识库文档
需求编号: DOC-2025-11-003
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# Java 代码规范

**基于**: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
**适用范围**: AIBidComposer 项目的所有 Java 代码
**技术栈**: Java 17 LTS + Spring Boot 3.2.x

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.0 | claude-sonnet-4-5 | 基于 Google Java Style Guide 创建规范 |

---

## 1. 源文件基础

### 1.1 文件名
- 源文件名 = 顶级类名 + `.java`
- 顶级类名使用大驼峰命名法（UpperCamelCase）

```java
// ✅ 正确
UserService.java        // 包含 public class UserService
UserServiceImpl.java    // 包含 public class UserServiceImpl

// ❌ 错误
userService.java
user_service.java
```

### 1.2 文件编码
- **UTF-8** 编码
- **无 BOM**（Byte Order Mark）

### 1.3 特殊字符
```java
// ✅ 使用 Unicode 转义符时添加注释
String unitAbbrev = "\u03bcs"; // "μs"

// ❌ 不推荐：直接使用不可见字符
String unitAbbrev = "μs"; // 某些编辑器可能显示不正确
```

---

## 2. 源文件结构

### 2.1 文件组织顺序

```java
/*
 * Copyright 2025 AIBidComposer Team
 */

package com.aibidcomposer.service;  // 1. package 语句

// 2. import 语句（分组，按字母排序）
import com.aibidcomposer.domain.User;
import com.aibidcomposer.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// 3. 顶级类（只有一个）
/**
 * 用户服务类
 *
 * 需求编号: REQ-2025-11-001
 */
@Service
public class UserService {
    // 类内容
}
```

### 2.2 Import 语句规范

**分组顺序**（组间空行分隔）：
1. 项目自己的包
2. 第三方库（如 Spring、Apache Commons）
3. Java 标准库（java.*, javax.*）

```java
// ✅ 正确的 import 顺序
import com.aibidcomposer.domain.User;
import com.aibidcomposer.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

// ❌ 禁止使用通配符导入
import java.util.*;  // ❌ 错误
import com.aibidcomposer.domain.*;  // ❌ 错误
```

---

## 3. 格式化规范

### 3.1 大括号

**K&R 风格**（Kernighan and Ritchie）：
```java
// ✅ 正确
if (condition) {
    doSomething();
} else {
    doSomethingElse();
}

// ✅ 即使只有一行也要加大括号
if (condition) {
    return true;
}

// ❌ 错误：省略大括号
if (condition)
    return true;  // ❌ 容易引入bug

// ❌ 错误：大括号单独成行
if (condition)
{  // ❌ 不符合 K&R 风格
    doSomething();
}
```

**空块简写**：
```java
// ✅ 允许简写
void doNothing() {}

try {
    doSomething();
} catch (Exception e) {}
```

### 3.2 缩进

- **4个空格** 为一个缩进层级
- **禁止使用 Tab 字符**

```java
// ✅ 正确：4空格缩进
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
}
```

### 3.3 每行一条语句

```java
// ✅ 正确
int a = 1;
int b = 2;

// ❌ 错误
int a = 1; int b = 2;
```

### 3.4 列限制

- **每行最多 100 字符**
- 例外：
  - package 和 import 语句
  - 注释中的长URL
  - 命令行示例

### 3.5 换行规则

**在更高语法层级处换行**：
```java
// ✅ 正确：在逗号后换行
public void method(String firstParameter,
                   String secondParameter,
                   String thirdParameter) {
    // ...
}

// ✅ 正确：在运算符前换行
String message = "这是一个很长的字符串，"
    + "需要分成多行来表示，"
    + "以保持代码可读性";

// ✅ 正确：方法链换行
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

// ✅ 正确：Lambda 表达式换行
List<String> names = users.stream()
    .filter(user -> user.isActive())
    .map(User::getName)
    .collect(Collectors.toList());
```

### 3.6 空白规范

**垂直空白**（空行）：
```java
public class UserService {
    // 字段之间可以没有空行
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ✅ 方法之间必须有空行
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    public User save(User user) {
        validateUser(user);
        return userRepository.save(user);
    }
}
```

**水平空白**：
```java
// ✅ 正确
if (a == b) {          // if 和 ( 之间有空格
    c = a + b;         // 运算符两侧有空格
}

method(a, b, c);       // 逗号后有空格

// ❌ 错误
if(a==b){              // ❌ 缺少空格
    c=a+b;             // ❌ 缺少空格
}

method(a,b,c);         // ❌ 逗号后缺少空格
```

### 3.7 括号分组

```java
// ✅ 推荐：使用括号明确优先级
if ((a && b) || (c && d)) {
    // ...
}

// ⚠️ 可以但不推荐：依赖默认优先级
if (a && b || c && d) {  // 不够清晰
    // ...
}
```

---

## 4. 命名规范

### 4.1 所有标识符的通用规则

- 只使用 ASCII 字母、数字、下划线
- 使用完整的英文单词，避免缩写（除非是广泛认可的缩写）

```java
// ✅ 正确
int userCount;
String firstName;
HttpRequest request;  // HTTP 是广泛认可的缩写

// ❌ 错误
int usrCnt;           // 不要使用缩写
String fName;         // 不要使用缩写
```

### 4.2 包名

- **全小写**
- **连续的单词直接连接**（没有下划线）

```java
// ✅ 正确
package com.aibidcomposer.service;
package com.aibidcomposer.repository;
package com.aibidcomposer.controller.api;

// ❌ 错误
package com.aibidcomposer.Service;       // ❌ 不要大写
package com.aibidcomposer.user_service;  // ❌ 不要下划线
```

### 4.3 类名

- **大驼峰命名法**（UpperCamelCase）
- 通常是名词或名词短语

```java
// ✅ 正确
public class User { }
public class UserService { }
public class UserServiceImpl { }
public class ResourceNotFoundException { }

// ❌ 错误
public class user { }           // ❌ 应该大写开头
public class User_Service { }   // ❌ 不要下划线
```

**特殊类型命名**：
```java
// 测试类：被测试类名 + Test
public class UserServiceTest { }

// 抽象类：Abstract 前缀（可选）
public abstract class AbstractUserService { }

// 接口：不使用特殊前缀
public interface UserService { }  // ✅ 不要 IUserService
```

### 4.4 方法名

- **小驼峰命名法**（lowerCamelCase）
- 通常是动词或动词短语

```java
// ✅ 正确
public void sendMessage() { }
public User findById(Long id) { }
public boolean isValid() { }
public boolean hasPermission() { }

// ❌ 错误
public void SendMessage() { }      // ❌ 首字母不要大写
public User find_by_id(Long id) { } // ❌ 不要下划线
```

**常见方法命名模式**：
```java
// 查询方法
public User findById(Long id) { }
public List<User> findAll() { }
public Optional<User> findByEmail(String email) { }

// 布尔查询方法
public boolean isActive() { }
public boolean hasRole(String role) { }
public boolean canEdit() { }

// 创建/保存方法
public User create(User user) { }
public User save(User user) { }
public User update(Long id, User user) { }

// 删除方法
public void delete(Long id) { }
public void remove(Long id) { }
```

### 4.5 常量名

- **全大写**
- **单词间用下划线分隔**（CONSTANT_CASE）
- 必须是 `static final` 且内容不可变

```java
// ✅ 正确
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";
public static final List<String> ALLOWED_FORMATS =
    Collections.unmodifiableList(Arrays.asList("PDF", "DOCX"));

// ❌ 错误
public static final int maxRetryCount = 3;  // ❌ 应该全大写
public static final int MAX_RETRY_COUNT_VALUE = 3;  // ❌ 不要冗余后缀
```

### 4.6 非常量字段名

- **小驼峰命名法**（lowerCamelCase）
- 通常是名词或名词短语

```java
// ✅ 正确
private String firstName;
private List<User> activeUsers;
private final UserRepository userRepository;

// ❌ 错误
private String FirstName;       // ❌ 首字母不要大写
private String first_name;      // ❌ 不要下划线
private String m_firstName;     // ❌ 不要匈牙利命名法
```

### 4.7 参数名

- **小驼峰命名法**（lowerCamelCase）
- 避免使用单字符参数名（除了临时变量）

```java
// ✅ 正确
public User create(String username, String email) { }
public void sendEmail(String toAddress, String subject, String body) { }

// ⚠️ 仅在循环等简短上下文中可以使用单字符
for (int i = 0; i < count; i++) { }
for (User user : users) { }

// ❌ 错误
public User create(String u, String e) { }  // ❌ 参数名太短
```

### 4.8 局部变量名

- **小驼峰命名法**（lowerCamelCase）
- 可以更简短和缩写

```java
// ✅ 正确
String userName = "John";
List<User> activeUsers = findActiveUsers();

// ✅ 循环变量可以简短
for (int i = 0; i < 10; i++) { }
for (User u : users) { }

// ❌ 错误
String UserName = "John";  // ❌ 首字母不要大写
```

### 4.9 类型变量名

**单个大写字母**，可选后跟一个数字：
```java
// ✅ 正确
public class Box<T> { }
public class Pair<K, V> { }
public <T extends User> T findOne() { }

// ✅ 也可以使用有意义的名称（大驼峰）
public class ResponseEntity<ResponseType> { }
```

常见类型变量：
- `E` - Element（集合元素）
- `K` - Key（映射键）
- `V` - Value（映射值）
- `T` - Type（通用类型）
- `R` - Result（返回类型）

---

## 5. 编程实践

### 5.1 @Override 注解

**始终使用** @Override 注解：
```java
// ✅ 正确
@Override
public String toString() {
    return "User{id=" + id + ", name=" + name + "}";
}

@Override
public boolean equals(Object obj) {
    // ...
}
```

### 5.2 捕获的异常

**不要忽略异常**：
```java
// ✅ 正确：处理异常
try {
    doSomething();
} catch (SomeException e) {
    log.error("处理失败", e);
    throw new BusinessException("操作失败", e);
}

// ✅ 正确：确实不需要处理时添加注释
try {
    int number = Integer.parseInt(value);
} catch (NumberFormatException expected) {
    // 期望的异常，使用默认值
    number = 0;
}

// ❌ 错误：空 catch 块
try {
    doSomething();
} catch (Exception e) {
    // ❌ 什么都不做
}
```

### 5.3 静态成员访问

**通过类名访问**，不要通过实例：
```java
// ✅ 正确
String formatted = String.format("%s: %d", name, count);

// ❌ 错误
String text = "hello";
String formatted = text.format("%s: %d", name, count);  // ❌ 不要通过实例
```

### 5.4 Finalizers

**禁止使用 finalizers** (`finalize()` 方法)：
```java
// ❌ 禁止
@Override
protected void finalize() throws Throwable {
    // 不要使用 finalizer
}

// ✅ 使用 try-with-resources 或 AutoCloseable
try (InputStream input = new FileInputStream(file)) {
    // 使用 input
}  // 自动关闭
```

---

## 6. Javadoc 规范

### 6.1 格式

```java
/**
 * 用户服务类，提供用户相关的业务逻辑处理
 *
 * <p>该服务负责用户的查询、创建、更新、删除等操作。
 * 所有操作都会进行权限验证。
 *
 * <p>需求编号: REQ-2025-11-001
 * 实现日期: 2025-11-26
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    /**
     * 根据用户ID查询用户信息
     *
     * <p>如果用户不存在，抛出 {@link ResourceNotFoundException}。
     *
     * @param id 用户ID，不能为 null
     * @return 用户信息
     * @throws ResourceNotFoundException 当用户不存在时
     * @throws IllegalArgumentException 当 id 为 null 时
     */
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        log.debug("查询用户，ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
}
```

### 6.2 必须使用 Javadoc 的位置

- ✅ 所有 `public` 类
- ✅ 所有 `public` 和 `protected` 方法
- ⚠️ 其他方法根据需要选择性使用

### 6.3 摘要片段

- 第一句是摘要片段
- 以句号、问号或感叹号结束
- 不要以 "This method returns" 或 "This class represents" 开头

```java
// ✅ 正确
/**
 * 根据用户ID查询用户信息
 */

// ❌ 错误
/**
 * This method returns a user by ID
 */
```

---

## 7. Spring Boot 项目特定规范

### 7.1 依赖注入

**使用构造器注入**（推荐）：
```java
// ✅ 推荐：构造器注入 + @RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Lombok 自动生成构造器
}

// ⚠️ 可以但不推荐：字段注入
@Service
public class UserService {

    @Autowired  // ⚠️ 不推荐
    private UserRepository userRepository;
}
```

### 7.2 分层架构

**严格遵守分层职责**：
```java
// Controller 层：处理HTTP请求
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}

// Service 层：业务逻辑
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
}

// Repository 层：数据访问
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByStatus(UserStatus status);
}
```

### 7.3 实体类规范

```java
/**
 * 用户实体
 *
 * 需求编号: REQ-2025-11-001
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### 7.4 异常处理

```java
// 全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        log.warn("资源未找到: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .code("RESOURCE_NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("未预期的错误", ex);

        ErrorResponse error = ErrorResponse.builder()
            .code("INTERNAL_SERVER_ERROR")
            .message("系统内部错误")
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// 自定义异常
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 7.5 配置类

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

## 8. 项目特定补充规范

### 8.1 需求编号标注

**必须在以下位置标注需求编号**：
```java
/**
 * 用户管理服务
 *
 * 需求编号: REQ-2025-11-001
 * 实现日期: 2025-11-26
 */
@Service
public class UserService {

    /**
     * 根据ID查询用户
     *
     * 需求编号: REQ-2025-11-001
     */
    public User findById(Long id) {
        // 需求编号: REQ-2025-11-001 - 用户查询功能
        log.debug("查询用户，ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
}
```

### 8.2 日志规范

```java
@Service
@Slf4j  // 使用 Lombok 的 @Slf4j
public class UserService {

    public User create(User user) {
        // DEBUG：调试信息
        log.debug("创建用户: {}", user.getUsername());

        // INFO：重要业务流程
        log.info("用户注册成功，用户名: {}", user.getUsername());

        // WARN：警告但不影响流程
        log.warn("用户邮箱未验证: {}", user.getEmail());

        // ERROR：错误异常
        try {
            sendWelcomeEmail(user);
        } catch (Exception e) {
            log.error("发送欢迎邮件失败，用户: {}", user.getUsername(), e);
        }

        return userRepository.save(user);
    }
}
```

### 8.3 中文注释规范

```java
/**
 * 用户服务类
 *
 * <p>提供用户相关的业务逻辑处理，包括：
 * <ul>
 *   <li>用户查询和搜索</li>
 *   <li>用户创建和更新</li>
 *   <li>用户权限验证</li>
 * </ul>
 *
 * 需求编号: REQ-2025-11-001
 */
@Service
public class UserService {

    // ✅ 使用中文注释解释业务逻辑
    public User create(User user) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

        // 设置默认状态为待激活
        user.setStatus(UserStatus.PENDING_ACTIVATION);

        return userRepository.save(user);
    }
}
```

---

## 9. 代码审查检查清单

提交代码前，请确保：

### 格式化
- [ ] 使用 4 空格缩进（不使用 Tab）
- [ ] 每行不超过 100 字符
- [ ] 大括号使用 K&R 风格
- [ ] 正确使用空行和空格

### 命名
- [ ] 类名使用大驼峰（UpperCamelCase）
- [ ] 方法名和变量名使用小驼峰（lowerCamelCase）
- [ ] 常量使用全大写+下划线（CONSTANT_CASE）
- [ ] 包名全小写，无下划线

### 文档
- [ ] 所有 public 类都有 Javadoc
- [ ] 所有 public/protected 方法都有 Javadoc
- [ ] 类注释包含需求编号
- [ ] 重要业务逻辑有中文注释

### 编程实践
- [ ] 覆盖的方法使用 @Override
- [ ] 不忽略捕获的异常
- [ ] 使用构造器注入而非字段注入
- [ ] 遵循分层架构职责

### Spring Boot
- [ ] Controller 只处理 HTTP 请求
- [ ] Service 包含业务逻辑和事务
- [ ] Repository 只负责数据访问
- [ ] 使用统一的异常处理

### 项目特定
- [ ] 代码注释包含需求编号
- [ ] 使用中文注释解释业务逻辑
- [ ] 日志级别使用正确
- [ ] 敏感信息不记录到日志

---

## 10. 工具配置

### 10.1 IntelliJ IDEA 设置

**导入 Google Java Style**：
1. 下载 [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
2. IntelliJ IDEA → Preferences → Editor → Code Style → Java
3. 点击齿轮图标 → Import Scheme → IntelliJ IDEA code style XML
4. 选择下载的 XML 文件

**自定义调整**：
- Indent: 4 spaces（默认是2，需要改成4）
- Line length: 100（保持）

### 10.2 Checkstyle 配置

在 `pom.xml` 中添加：
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <consoleOutput>true</consoleOutput>
        <failsOnError>true</failsOnError>
    </configuration>
</plugin>
```

运行检查：
```bash
mvn checkstyle:check
```

---

## 参考资料

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Effective Java (3rd Edition)](https://www.oreilly.com/library/view/effective-java/9780134686097/) by Joshua Bloch

---

**最后更新**: 2025-11-26
**版本**: 1.0
