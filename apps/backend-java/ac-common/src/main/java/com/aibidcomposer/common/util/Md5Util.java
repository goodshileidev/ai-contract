package com.aibidcomposer.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 Utility Class
 *
 * <p>This utility class provides MD5 hashing functionality for string data.
 * It generates 32-character hexadecimal MD5 hash values.</p>
 *
 * <p>Features:
 * <ul>
 *   <li>Generates MD5 hash for input strings</li>
 *   <li>Uses UTF-8 encoding for byte conversion</li>
 *   <li>Returns uppercase hexadecimal representation</li>
 *   <li>Thread-safe static methods</li>
 * </ul>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-003</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class Md5Util {

    /**
     * Private constructor to prevent instantiation
     */
    private Md5Util() {}

    /**
     * Hexadecimal digits for byte-to-hex conversion (0-9, A-F)
     */
    private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Generates MD5 hash value for the input string
     *
     * <p>This method takes a string input and returns its MD5 hash value
     * as a 32-character hexadecimal string in uppercase format.</p>
     *
     * <p>Implementation details:
     * <ul>
     *   <li>Uses UTF-8 encoding for string-to-byte conversion</li>
     *   <li>Processes the input through Java's MessageDigest MD5 algorithm</li>
     *   <li>Converts the resulting byte array to hexadecimal string</li>
     *   <li>Uses uppercase hexadecimal characters (0-9, A-F)</li>
     * </ul>
     * </p>
     *
     * @param str the input string to hash
     * @return 32-character uppercase hexadecimal MD5 hash string
     * @throws RuntimeException if MD5 algorithm is not available
     */
    public static String md5(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            // Get MessageDigest instance for MD5 algorithm
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            // Update digest with UTF-8 encoded bytes of the input string
            mdInst.update(str.getBytes(StandardCharsets.UTF_8));

            // Get the resulting hash bytes
            byte[] md = mdInst.digest();

            // Convert byte array to hexadecimal string
            int j = md.length;
            char[] result = new char[j * 2];
            int k = 0;

            for (byte b : md) {
                // Extract high 4 bits and convert to hex (shift right 4 bits, mask with 0xf)
                result[k++] = HEX_DIGITS[b >>> 4 & 0xf];
                // Extract low 4 bits and convert to hex (mask with 0xf)
                result[k++] = HEX_DIGITS[b & 0xf];
            }

            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * Generates MD5 hash value for the input string (lowercase)
     *
     * @param str the input string to hash
     * @return 32-character lowercase hexadecimal MD5 hash string
     * @throws RuntimeException if MD5 algorithm is not available
     */
    public static String md5Lowercase(String str) {
        String hash = md5(str);
        return hash != null ? hash.toLowerCase() : null;
    }

    /**
     * Verifies if a string matches a given MD5 hash
     *
     * @param str the original string
     * @param md5Hash the MD5 hash to compare with
     * @return true if the string's MD5 hash matches the provided hash
     */
    public static boolean verify(String str, String md5Hash) {
        if (str == null || md5Hash == null) {
            return false;
        }
        String hash = md5(str);
        return md5Hash.equalsIgnoreCase(hash);
    }
}
