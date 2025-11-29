# Java Spring Boot 服务任务详细计划 - JAVA-001 Part3 - 1.5: 用户个人信息管理 - 1.5.3 Java后端

**验证清单**:
- [ ] UserService业务逻辑扩展完成
- [ ] UserController API扩展完成
- [ ] 文件上传功能集成MinIO
- [ ] 密码验证逻辑正确
- [ ] 单元测试覆盖率>80%

#### Service层扩展

```java
// backend-java/src/main/java/com/aibidcomposer/service/UserService.java (扩展)
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    // ... 前面的依赖和方法 ...

    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    /**
     * 获取用户个人资料
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        log.info("Getting profile for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        UserProfileResponse response = UserProfileResponse.from(user);

        // 获取用户角色
        Set<Role> roles = roleService.getUserRoles(userId);
        response.setRoles(roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet()));

        // 获取用户权限
        Set<String> permissions = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        response.setPermissions(permissions);

        // TODO: 获取统计信息（项目数、文档数）

        return response;
    }

    /**
     * 更新用户个人资料
     */
    public void updateUserProfile(UUID userId, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 检查邮箱唯一性
        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("邮箱已被使用");
        }

        // 更新字段
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // 邮箱变更后需要重新验证
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
            if (!request.getPhone().equals(user.getPhone())) {
                user.setPhoneVerified(false); // 手机变更后需要重新验证
            }
        }

        if (request.getSettings() != null) {
            user.setSettings(request.getSettings());
        }

        userRepository.save(user);
        log.info("Profile updated successfully for user: {}", userId);
    }

    /**
     * 修改密码
     */
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);

        // 验证新密码和确认密码一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("两次输入的密码不一致");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 验证当前密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            throw new ValidationException("当前密码不正确");
        }

        // 检查新密码不能与旧密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getHashedPassword())) {
            throw new ValidationException("新密码不能与当前密码相同");
        }

        // 更新密码
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(UUID userId, MultipartFile file) {
        log.info("Uploading avatar for user: {}", userId);

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("只能上传图片文件");
        }

        // 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ValidationException("文件大小不能超过5MB");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 上传到MinIO
        String avatarUrl = fileStorageService.uploadFile(
                file,
                "avatars",
                userId.toString()
        );

        // 删除旧头像（如果存在且不是默认头像）
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().contains("default")) {
            fileStorageService.deleteFile(user.getAvatarUrl());
        }

        // 更新用户头像URL
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        log.info("Avatar uploaded successfully for user: {}", userId);
        return avatarUrl;
    }

    /**
     * 获取登录历史
     * TODO: 需要实现登录历史记录功能
     */
    @Transactional(readOnly = true)
    public Page<LoginHistoryResponse> getLoginHistory(UUID userId, Pageable pageable) {
        log.info("Getting login history for user: {}", userId);

        // TODO: 从登录历史表查询
        // 暂时返回空列表
        return Page.empty(pageable);
    }
}
```

#### Controller层扩展

```java
// backend-java/src/main/java/com/aibidcomposer/controller/UserController.java (扩展)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    // ... 前面的依赖和方法 ...

    /**
     * 获取当前用户个人资料
     */
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @CurrentUser UserPrincipal currentUser
    ) {
        UserProfileResponse profile = userService.getUserProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        userService.updateUserProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "资料更新成功"));
    }

    /**
     * 修改密码
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "密码修改成功"));
    }

    /**
     * 上传头像
     */
    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam("file") MultipartFile file
    ) {
        String avatarUrl = userService.uploadAvatar(currentUser.getId(), file);

        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", avatarUrl);

        return ResponseEntity.ok(ApiResponse.success(result, "头像上传成功"));
    }

    /**
     * 获取登录历史
     */
    @GetMapping("/me/login-history")
    public ResponseEntity<ApiResponse<PagedResponse<LoginHistoryResponse>>> getLoginHistory(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "loginAt"));

        Page<LoginHistoryResponse> history = userService.getLoginHistory(currentUser.getId(), pageable);

        PagedResponse<LoginHistoryResponse> pagedResponse = new PagedResponse<>(
                history.getContent(),
                history.getTotalElements(),
                page,
                pageSize,
                history.getTotalPages()
        );

        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "获取成功"));
    }
}
```

#### 文件存储服务

```java
// backend-java/src/main/java/com/aibidcomposer/service/FileStorageService.java
package com.aibidcomposer.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件存储服务（MinIO）
 * 需求编号: REQ-JAVA-001
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param folder 文件夹
     * @param fileName 文件名
     * @return 文件URL
     */
    public String uploadFile(MultipartFile file, String folder, String fileName) {
        try {
            // 确保bucket存在
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String objectName = folder + "/" + fileName + "-" + UUID.randomUUID() + extension;

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // 返回文件URL
            String fileUrl = endpoint + "/" + bucketName + "/" + objectName;
            log.info("File uploaded successfully: {}", fileUrl);

            return fileUrl;

        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL提取objectName
            String objectName = fileUrl.replace(endpoint + "/" + bucketName + "/", "");

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            log.info("File deleted successfully: {}", fileUrl);

        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            // 删除失败不抛出异常，只记录日志
        }
    }
}
```
