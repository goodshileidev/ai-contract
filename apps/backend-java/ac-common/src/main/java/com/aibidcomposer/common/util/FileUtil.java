package com.aibidcomposer.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类
 *
 * 需求编号: REQ-JAVA-COMMON-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 允许的文档文件扩展名
     */
    public static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );

    /**
     * 允许的图片文件扩展名
     */
    public static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );

    /**
     * 最大文件大小（50MB）
     */
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（小写，不带点）
     */
    public static String getExtension(String filename) {
        if (StringUtil.isBlank(filename)) {
            return "";
        }
        return FilenameUtils.getExtension(filename).toLowerCase();
    }

    /**
     * 获取文件名（不含扩展名）
     *
     * @param filename 文件名
     * @return 文件名（不含扩展名）
     */
    public static String getBaseName(String filename) {
        if (StringUtil.isBlank(filename)) {
            return "";
        }
        return FilenameUtils.getBaseName(filename);
    }

    /**
     * 判断是否为允许的文档类型
     *
     * @param filename 文件名
     * @return 是否为允许的文档类型
     */
    public static boolean isAllowedDocument(String filename) {
        String extension = getExtension(filename);
        return DOCUMENT_EXTENSIONS.contains(extension);
    }

    /**
     * 判断是否为允许的图片类型
     *
     * @param filename 文件名
     * @return 是否为允许的图片类型
     */
    public static boolean isAllowedImage(String filename) {
        String extension = getExtension(filename);
        return IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 判断文件大小是否超过限制
     *
     * @param fileSize 文件大小（字节）
     * @param maxSize  最大大小（字节）
     * @return 是否超过限制
     */
    public static boolean isFileSizeExceeded(long fileSize, long maxSize) {
        return fileSize > maxSize;
    }

    /**
     * 判断文件大小是否超过默认限制（50MB）
     *
     * @param fileSize 文件大小（字节）
     * @return 是否超过限制
     */
    public static boolean isFileSizeExceeded(long fileSize) {
        return isFileSizeExceeded(fileSize, MAX_FILE_SIZE);
    }

    /**
     * 生成唯一文件名
     * 格式: 原文件名_UUID.扩展名
     *
     * @param originalFilename 原文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        if (StringUtil.isBlank(originalFilename)) {
            return StringUtil.generateUuid();
        }

        String baseName = getBaseName(originalFilename);
        String extension = getExtension(originalFilename);
        String uuid = StringUtil.generateUuid();

        if (StringUtil.isNotBlank(extension)) {
            return baseName + "_" + uuid + "." + extension;
        } else {
            return baseName + "_" + uuid;
        }
    }

    /**
     * 生成简单唯一文件名
     * 格式: UUID.扩展名
     *
     * @param originalFilename 原文件名
     * @return 唯一文件名
     */
    public static String generateSimpleUniqueFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = StringUtil.generateUuid();

        if (StringUtil.isNotBlank(extension)) {
            return uuid + "." + extension;
        } else {
            return uuid;
        }
    }

    /**
     * 格式化文件大小
     * 例如: 1024 -> 1KB, 1048576 -> 1MB
     *
     * @param size 文件大小（字节）
     * @return 格式化后的大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param dirPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectoryIfNotExists(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists() && file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除目录（包括子文件和子目录）
     *
     * @param dirPath 目录路径
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                FileUtils.deleteDirectory(dir);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 是否复制成功
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        try {
            File source = new File(sourcePath);
            File target = new File(targetPath);
            FileUtils.copyFile(source, target);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 是否移动成功
     */
    public static boolean moveFile(String sourcePath, String targetPath) {
        try {
            File source = new File(sourcePath);
            File target = new File(targetPath);
            FileUtils.moveFile(source, target);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 保存上传的文件
     *
     * @param file       上传的文件
     * @param targetPath 目标路径
     * @return 是否保存成功
     */
    public static boolean saveUploadedFile(MultipartFile file, String targetPath) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        try {
            File targetFile = new File(targetPath);
            // 确保目标目录存在
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            file.transferTo(targetFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 读取文件内容为字符串
     *
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String readFileToString(String filePath) throws IOException {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, "UTF-8");
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  内容
     * @throws IOException IO异常
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }

    /**
     * 获取文件的MIME类型
     *
     * @param filename 文件名
     * @return MIME类型
     */
    public static String getMimeType(String filename) {
        String extension = getExtension(filename);

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "svg" -> "image/svg+xml";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    public static boolean exists(String filePath) {
        if (StringUtil.isBlank(filePath)) {
            return false;
        }
        return new File(filePath).exists();
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节），文件不存在返回-1
     */
    public static long getFileSize(String filePath) {
        if (!exists(filePath)) {
            return -1;
        }
        return new File(filePath).length();
    }
}
