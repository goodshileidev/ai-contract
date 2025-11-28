package com.aibidcomposer.common.biz.service;

import com.aibidcomposer.common.biz.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件存储服务
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件
     *
     * @param file       文件
     * @param bucketName 桶名称
     * @return 文件访问路径
     * @throws Exception 异常
     */
    public String uploadFile(MultipartFile file, String bucketName) throws Exception {
        // 确保桶存在
        createBucketIfNotExists(bucketName);

        // 生成唯一文件名
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        log.info("文件上传成功: bucket={}, fileName={}", bucketName, fileName);
        return fileName;
    }

    /**
     * 上传文件到默认桶
     *
     * @param file 文件
     * @return 文件访问路径
     * @throws Exception 异常
     */
    public String uploadFile(MultipartFile file) throws Exception {
        return uploadFile(file, minioConfig.getBucketName());
    }

    /**
     * 下载文件
     *
     * @param fileName   文件名
     * @param bucketName 桶名称
     * @return 文件流
     * @throws Exception 异常
     */
    public InputStream downloadFile(String fileName, String bucketName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 下载文件（从默认桶）
     *
     * @param fileName 文件名
     * @return 文件流
     * @throws Exception 异常
     */
    public InputStream downloadFile(String fileName) throws Exception {
        return downloadFile(fileName, minioConfig.getBucketName());
    }

    /**
     * 删除文件
     *
     * @param fileName   文件名
     * @param bucketName 桶名称
     * @throws Exception 异常
     */
    public void deleteFile(String fileName, String bucketName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
        log.info("文件删除成功: bucket={}, fileName={}", bucketName, fileName);
    }

    /**
     * 删除文件（从默认桶）
     *
     * @param fileName 文件名
     * @throws Exception 异常
     */
    public void deleteFile(String fileName) throws Exception {
        deleteFile(fileName, minioConfig.getBucketName());
    }

    /**
     * 获取文件预览URL（临时访问链接）
     *
     * @param fileName   文件名
     * @param bucketName 桶名称
     * @param expires    过期时间（秒）
     * @return 预览URL
     * @throws Exception 异常
     */
    public String getPresignedObjectUrl(String fileName, String bucketName, int expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 获取文件预览URL（从默认桶，默认1小时过期）
     *
     * @param fileName 文件名
     * @return 预览URL
     * @throws Exception 异常
     */
    public String getPresignedObjectUrl(String fileName) throws Exception {
        return getPresignedObjectUrl(fileName, minioConfig.getBucketName(), 3600);
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName   文件名
     * @param bucketName 桶名称
     * @return 是否存在
     */
    public boolean fileExists(String fileName, String bucketName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断文件是否存在（默认桶）
     *
     * @param fileName 文件名
     * @return 是否存在
     */
    public boolean fileExists(String fileName) {
        return fileExists(fileName, minioConfig.getBucketName());
    }

    /**
     * 创建桶（如果不存在）
     *
     * @param bucketName 桶名称
     * @throws Exception 异常
     */
    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("创建MinIO桶: {}", bucketName);
        }
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return uuid + extension;
    }
}
