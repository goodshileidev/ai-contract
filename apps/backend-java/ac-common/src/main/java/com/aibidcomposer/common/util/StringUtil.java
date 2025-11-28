package com.aibidcomposer.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * 需求编号: REQ-JAVA-COMMON-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
public class StringUtil {

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 生成UUID（不带横线）
     *
     * @return UUID字符串
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成UUID（带横线）
     *
     * @return UUID字符串
     */
    public static String generateUuidWithDash() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断字符串是否为空（null、空字符串、空格）
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * 驼峰转下划线
     * 例如: userName -> user_name
     *
     * @param camelCase 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToSnake(String camelCase) {
        if (isBlank(camelCase)) {
            return camelCase;
        }
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    /**
     * 下划线转驼峰
     * 例如: user_name -> userName
     *
     * @param snakeCase 下划线字符串
     * @return 驼峰字符串
     */
    public static String snakeToCamel(String snakeCase) {
        if (isBlank(snakeCase)) {
            return snakeCase;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = snakeCase.split("_");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                result.append(part.toLowerCase());
            } else {
                result.append(capitalize(part));
            }
        }

        return result.toString();
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * 首字母小写
     *
     * @param str 字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    /**
     * 判断字符串是否匹配正则表达式
     *
     * @param str   字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        if (isBlank(str) || isBlank(regex)) {
            return false;
        }
        return Pattern.matches(regex, str);
    }

    /**
     * 判断是否为邮箱格式
     *
     * @param email 邮箱字符串
     * @return 是否为邮箱格式
     */
    public static boolean isEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return matches(email, emailRegex);
    }

    /**
     * 判断是否为手机号格式（中国大陆）
     *
     * @param phone 手机号字符串
     * @return 是否为手机号格式
     */
    public static boolean isPhone(String phone) {
        String phoneRegex = "^1[3-9]\\d{9}$";
        return matches(phone, phoneRegex);
    }

    /**
     * 判断是否为身份证号格式（中国大陆）
     *
     * @param idCard 身份证号字符串
     * @return 是否为身份证号格式
     */
    public static boolean isIdCard(String idCard) {
        // 18位身份证号码正则表达式
        String idCardRegex = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";
        return matches(idCard, idCardRegex);
    }

    /**
     * 脱敏手机号
     * 例如: 13812345678 -> 138****5678
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String desensitizePhone(String phone) {
        if (isBlank(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 脱敏身份证号
     * 例如: 110101199001011234 -> 110101********1234
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String desensitizeIdCard(String idCard) {
        if (isBlank(idCard) || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 脱敏邮箱
     * 例如: test@example.com -> t***@example.com
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String desensitizeEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 1) {
            return email;
        }

        return username.charAt(0) + "***@" + domain;
    }

    /**
     * 集合转字符串
     * 例如: [1, 2, 3] -> "1,2,3"
     *
     * @param collection 集合
     * @param separator  分隔符
     * @return 字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return StringUtils.join(collection, separator);
    }

    /**
     * 字符串转数组
     * 例如: "1,2,3" -> ["1", "2", "3"]
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 数组
     */
    public static String[] split(String str, String separator) {
        if (isBlank(str)) {
            return new String[0];
        }
        return str.split(separator);
    }

    /**
     * 截取字符串，超出部分用省略号表示
     *
     * @param str    字符串
     * @param length 最大长度
     * @return 截取后的字符串
     */
    public static String ellipsis(String str, int length) {
        if (isBlank(str) || str.length() <= length) {
            return str;
        }
        return str.substring(0, length) + "...";
    }

    /**
     * 移除字符串中的所有空格
     *
     * @param str 字符串
     * @return 移除空格后的字符串
     */
    public static String removeSpaces(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.replaceAll("\\s+", "");
    }

    /**
     * 判断字符串是否包含中文
     *
     * @param str 字符串
     * @return 是否包含中文
     */
    public static boolean containsChinese(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches(".*[\\u4e00-\\u9fa5]+.*");
    }

    /**
     * 获取字符串的字节长度（UTF-8编码）
     *
     * @param str 字符串
     * @return 字节长度
     */
    public static int getByteLength(String str) {
        if (isBlank(str)) {
            return 0;
        }
        return str.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
    }
}
