package com.aibidcomposer.common.constant;

/**
 * API Constants Interface
 *
 * <p>This interface defines common constants used throughout the AIBidComposer platform.
 * Constants are organized into nested interfaces by category for better organization.</p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-013</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public interface ApiConstants {

    /**
     * Common field names used across the application
     */
    interface Fields {
        /**
         * User ID field name
         */
        String USER_ID = "userId";

        /**
         * User object key in thread local or session
         */
        String USER_OBJECT_KEY = "USER_OBJECT";

        /**
         * Organization ID field name
         */
        String ORGANIZATION_ID = "organizationId";

        /**
         * Project ID field name
         */
        String PROJECT_ID = "projectId";

        /**
         * Document ID field name
         */
        String DOCUMENT_ID = "documentId";

        /**
         * Template ID field name
         */
        String TEMPLATE_ID = "templateId";

        /**
         * Token field name in headers
         */
        String TOKEN = "token";

        /**
         * Authorization header name
         */
        String AUTHORIZATION = "Authorization";

        /**
         * Bearer token prefix
         */
        String BEARER_PREFIX = "Bearer ";

        /**
         * Request ID for tracing
         */
        String REQUEST_ID = "requestId";
    }

    /**
     * Date/Time format patterns
     */
    interface Formats {
        /**
         * Date format: yyyy-MM-dd
         */
        String DATE = "yyyy-MM-dd";

        /**
         * DateTime format: yyyy-MM-dd HH:mm
         */
        String DATETIME_MINUTE = "yyyy-MM-dd HH:mm";

        /**
         * DateTime format: yyyy-MM-dd HH:mm:ss
         */
        String DATETIME = "yyyy-MM-dd HH:mm:ss";

        /**
         * DateTime format with milliseconds: yyyy-MM-dd HH:mm:ss.SSS
         */
        String DATETIME_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";

        /**
         * Time format: HH:mm:ss
         */
        String TIME = "HH:mm:ss";

        /**
         * ISO 8601 format
         */
        String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    }

    /**
     * Common separators
     */
    interface Separators {
        /**
         * Comma separator
         */
        String COMMA = ",";

        /**
         * Colon separator
         */
        String COLON = ":";

        /**
         * Semicolon separator
         */
        String SEMICOLON = ";";

        /**
         * Pipe separator
         */
        String PIPE = "|";

        /**
         * At symbol
         */
        String AT = "@";

        /**
         * Underscore
         */
        String UNDERSCORE = "_";

        /**
         * Hyphen/dash
         */
        String HYPHEN = "-";

        /**
         * Slash
         */
        String SLASH = "/";
    }

    /**
     * HTTP-related constants
     */
    interface Http {
        /**
         * Default page number for pagination
         */
        int DEFAULT_PAGE_NO = 1;

        /**
         * Default page size for pagination
         */
        int DEFAULT_PAGE_SIZE = 20;

        /**
         * Maximum page size for pagination
         */
        int MAX_PAGE_SIZE = 100;

        /**
         * Request timeout in seconds
         */
        int REQUEST_TIMEOUT = 30;

        /**
         * HTTP method: GET
         */
        String METHOD_GET = "GET";

        /**
         * HTTP method: POST
         */
        String METHOD_POST = "POST";

        /**
         * HTTP method: PUT
         */
        String METHOD_PUT = "PUT";

        /**
         * HTTP method: DELETE
         */
        String METHOD_DELETE = "DELETE";

        /**
         * HTTP method: PATCH
         */
        String METHOD_PATCH = "PATCH";
    }

    /**
     * Cache-related constants
     */
    interface Cache {
        /**
         * Default cache TTL in seconds (1 hour)
         */
        long DEFAULT_TTL = 3600L;

        /**
         * Short cache TTL in seconds (5 minutes)
         */
        long SHORT_TTL = 300L;

        /**
         * Long cache TTL in seconds (24 hours)
         */
        long LONG_TTL = 86400L;

        /**
         * User cache key prefix
         */
        String USER_PREFIX = "user:";

        /**
         * Project cache key prefix
         */
        String PROJECT_PREFIX = "project:";

        /**
         * Document cache key prefix
         */
        String DOCUMENT_PREFIX = "document:";

        /**
         * Template cache key prefix
         */
        String TEMPLATE_PREFIX = "template:";

        /**
         * Token cache key prefix
         */
        String TOKEN_PREFIX = "token:";
    }

    /**
     * Security-related constants
     */
    interface Security {
        /**
         * JWT secret key (should be loaded from config)
         */
        String JWT_SECRET_PLACEHOLDER = "${jwt.secret}";

        /**
         * JWT expiration time in milliseconds (24 hours)
         */
        long JWT_EXPIRATION = 86400000L;

        /**
         * Refresh token expiration in milliseconds (7 days)
         */
        long REFRESH_TOKEN_EXPIRATION = 604800000L;

        /**
         * Password minimum length
         */
        int PASSWORD_MIN_LENGTH = 8;

        /**
         * Password maximum length
         */
        int PASSWORD_MAX_LENGTH = 64;

        /**
         * Maximum login attempts before account lock
         */
        int MAX_LOGIN_ATTEMPTS = 5;

        /**
         * Account lock duration in minutes
         */
        int ACCOUNT_LOCK_DURATION = 30;
    }

    /**
     * File storage constants
     */
    interface Storage {
        /**
         * Maximum file size in bytes (50MB)
         */
        long MAX_FILE_SIZE = 52428800L;

        /**
         * Allowed document file extensions
         */
        String[] DOCUMENT_EXTENSIONS = {".pdf", ".doc", ".docx", ".txt", ".md"};

        /**
         * Allowed image file extensions
         */
        String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".svg"};

        /**
         * Default bucket name for documents
         */
        String DOCUMENT_BUCKET = "documents";

        /**
         * Default bucket name for templates
         */
        String TEMPLATE_BUCKET = "templates";

        /**
         * Default bucket name for avatars
         */
        String AVATAR_BUCKET = "avatars";
    }

    /**
     * Regular expression patterns
     */
    interface Patterns {
        /**
         * Email pattern
         */
        String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        /**
         * Phone number pattern (China mobile)
         */
        String PHONE = "^1[3-9]\\d{9}$";

        /**
         * Username pattern (alphanumeric and underscore, 3-20 chars)
         */
        String USERNAME = "^[a-zA-Z0-9_]{3,20}$";

        /**
         * Password pattern (at least one letter and one number, 8+ chars)
         */
        String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";

        /**
         * UUID pattern
         */
        String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    }

    /**
     * System default values
     */
    interface Defaults {
        /**
         * Default language
         */
        String LANGUAGE = "zh-CN";

        /**
         * Default timezone
         */
        String TIMEZONE = "Asia/Shanghai";

        /**
         * Default encoding
         */
        String ENCODING = "UTF-8";

        /**
         * System user ID (for system-generated operations)
         */
        String SYSTEM_USER_ID = "00000000-0000-0000-0000-000000000000";
    }
}
