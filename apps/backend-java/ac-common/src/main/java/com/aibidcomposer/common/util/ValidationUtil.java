package com.aibidcomposer.common.util;

import com.aibidcomposer.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * 需求编号: REQ-JAVA-COMMON-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
public class ValidationUtil {

    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 手机号正则表达式（中国大陆）
     */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 身份证号正则表达式（中国大陆18位）
     */
    private static final Pattern ID_CARD_PATTERN =
            Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");

    /**
     * 密码强度正则表达式（至少8位，包含字母和数字）
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");

    /**
     * URL正则表达式
     */
    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);

    /**
     * IPv4地址正则表达式
     */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    /**
     * 验证对象
     *
     * @param validator 验证器
     * @param object    要验证的对象
     * @param groups    验证组
     * @param <T>       对象类型
     * @throws ValidationException 验证失败时抛出
     */
    public static <T> void validate(Validator validator, T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            throw new ValidationException("验证失败", errors);
        }
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否为有效邮箱
     */
    public static boolean isValidEmail(String email) {
        if (StringUtil.isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式（中国大陆）
     *
     * @param phone 手机号
     * @return 是否为有效手机号
     */
    public static boolean isValidPhone(String phone) {
        if (StringUtil.isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证身份证号格式（中国大陆18位）
     *
     * @param idCard 身份证号
     * @return 是否为有效身份证号
     */
    public static boolean isValidIdCard(String idCard) {
        if (StringUtil.isBlank(idCard)) {
            return false;
        }

        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }

        // 验证校验码
        return validateIdCardChecksum(idCard);
    }

    /**
     * 验证身份证号校验码
     *
     * @param idCard 身份证号
     * @return 校验码是否正确
     */
    private static boolean validateIdCardChecksum(String idCard) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.getNumericValue(idCard.charAt(i)) * weights[i];
        }

        int index = sum % 11;
        char expectedCheckCode = checkCodes[index];
        char actualCheckCode = Character.toUpperCase(idCard.charAt(17));

        return expectedCheckCode == actualCheckCode;
    }

    /**
     * 验证密码强度
     * 规则：至少8位，包含字母和数字
     *
     * @param password 密码
     * @return 是否为强密码
     */
    public static boolean isStrongPassword(String password) {
        if (StringUtil.isBlank(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证URL格式
     *
     * @param url URL
     * @return 是否为有效URL
     */
    public static boolean isValidUrl(String url) {
        if (StringUtil.isBlank(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证IPv4地址格式
     *
     * @param ip IPv4地址
     * @return 是否为有效IPv4地址
     */
    public static boolean isValidIpv4(String ip) {
        if (StringUtil.isBlank(ip)) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证字符串长度
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 长度是否在范围内
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证数值范围
     *
     * @param value 数值
     * @param min   最小值
     * @param max   最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值范围
     *
     * @param value 数值
     * @param min   最小值
     * @param max   最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值范围
     *
     * @param value 数值
     * @param min   最小值
     * @param max   最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 验证字符串是否只包含字母
     *
     * @param str 字符串
     * @return 是否只包含字母
     */
    public static boolean isAlpha(String str) {
        if (StringUtil.isBlank(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z]+$");
    }

    /**
     * 验证字符串是否只包含数字
     *
     * @param str 字符串
     * @return 是否只包含数字
     */
    public static boolean isNumeric(String str) {
        if (StringUtil.isBlank(str)) {
            return false;
        }
        return str.matches("^\\d+$");
    }

    /**
     * 验证字符串是否只包含字母和数字
     *
     * @param str 字符串
     * @return 是否只包含字母和数字
     */
    public static boolean isAlphanumeric(String str) {
        if (StringUtil.isBlank(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * 验证中文姓名格式
     *
     * @param name 姓名
     * @return 是否为有效中文姓名
     */
    public static boolean isValidChineseName(String name) {
        if (StringUtil.isBlank(name)) {
            return false;
        }
        // 2-4个中文字符
        return name.matches("^[\\u4e00-\\u9fa5]{2,4}$");
    }

    /**
     * 验证银行卡号格式（简单验证）
     *
     * @param cardNumber 银行卡号
     * @return 是否为有效银行卡号
     */
    public static boolean isValidBankCard(String cardNumber) {
        if (StringUtil.isBlank(cardNumber)) {
            return false;
        }
        // 13-19位数字
        return cardNumber.matches("^\\d{13,19}$");
    }

    /**
     * 验证社会信用代码（统一社会信用代码）
     *
     * @param code 社会信用代码
     * @return 是否为有效社会信用代码
     */
    public static boolean isValidSocialCreditCode(String code) {
        if (StringUtil.isBlank(code)) {
            return false;
        }
        // 18位字母或数字
        return code.matches("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");
    }

    /**
     * 验证经度
     *
     * @param longitude 经度
     * @return 是否为有效经度（-180 到 180）
     */
    public static boolean isValidLongitude(double longitude) {
        return isInRange(longitude, -180.0, 180.0);
    }

    /**
     * 验证纬度
     *
     * @param latitude 纬度
     * @return 是否为有效纬度（-90 到 90）
     */
    public static boolean isValidLatitude(double latitude) {
        return isInRange(latitude, -90.0, 90.0);
    }

    /**
     * 抛出验证异常（单个字段）
     *
     * @param field   字段名
     * @param message 错误信息
     * @throws ValidationException 验证异常
     */
    public static void throwValidationError(String field, String message) {
        Map<String, String> errors = new HashMap<>();
        errors.put(field, message);
        throw new ValidationException("验证失败", errors);
    }

    /**
     * 抛出验证异常（多个字段）
     *
     * @param errors 错误信息映射
     * @throws ValidationException 验证异常
     */
    public static void throwValidationErrors(Map<String, String> errors) {
        if (errors != null && !errors.isEmpty()) {
            throw new ValidationException("验证失败", errors);
        }
    }

    /**
     * 验证必填参数
     *
     * @param value     参数值
     * @param fieldName 字段名
     * @throws ValidationException 验证异常
     */
    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throwValidationError(fieldName, fieldName + "不能为空");
        }
    }

    /**
     * 验证必填字符串参数
     *
     * @param value     参数值
     * @param fieldName 字段名
     * @throws ValidationException 验证异常
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (StringUtil.isBlank(value)) {
            throwValidationError(fieldName, fieldName + "不能为空");
        }
    }
}
