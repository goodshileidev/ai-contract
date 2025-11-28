package com.aibidcomposer.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Date Utility Class
 *
 * <p>This utility class provides date/time manipulation and formatting functionality
 * using Java 8+ Date/Time API.</p>
 *
 * <p>Features:
 * <ul>
 *   <li>Date formatting to ISO-like format</li>
 *   <li>Date/Time conversion between java.util.Date and java.time.LocalDateTime</li>
 *   <li>Time calculation and comparison</li>
 * </ul>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-001</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class DateUtil {

    /**
     * Private constructor to prevent instantiation
     */
    private DateUtil() {}

    /**
     * Standard date format pattern: yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date only pattern: yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * Time only pattern: HH:mm:ss
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * Date-time formatter for standard format
     */
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern(PATTERN_DATETIME);

    /**
     * Date formatter
     */
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern(PATTERN_DATE);

    /**
     * Formats a Date to standard string representation
     *
     * <p>This method converts a Date object to a formatted string using
     * the standard "yyyy-MM-dd HH:mm:ss" format.</p>
     *
     * @param date the date to format
     * @return formatted date string in "yyyy-MM-dd HH:mm:ss" format, or null if input is null
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Formats a LocalDateTime to standard string representation
     *
     * @param localDateTime the LocalDateTime to format
     * @return formatted date string in "yyyy-MM-dd HH:mm:ss" format, or null if input is null
     */
    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Formats a LocalDate to date string
     *
     * @param localDate the LocalDate to format
     * @return formatted date string in "yyyy-MM-dd" format, or null if input is null
     */
    public static String formatDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DATE_FORMATTER);
    }

    /**
     * Parses a date string to LocalDateTime
     *
     * @param dateTimeStr the date string in "yyyy-MM-dd HH:mm:ss" format
     * @return parsed LocalDateTime, or null if input is null
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * Parses a date string to LocalDate
     *
     * @param dateStr the date string in "yyyy-MM-dd" format
     * @return parsed LocalDate, or null if input is null
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Converts java.util.Date to java.time.LocalDateTime
     *
     * @param date the Date to convert
     * @return converted LocalDateTime, or null if input is null
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Converts java.time.LocalDateTime to java.util.Date
     *
     * @param localDateTime the LocalDateTime to convert
     * @return converted Date, or null if input is null
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Gets current date time as LocalDateTime
     *
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Gets current date as LocalDate
     *
     * @return current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Adds days to a date
     *
     * @param date the base date
     * @param days the number of days to add (can be negative)
     * @return new date with days added
     */
    public static LocalDateTime plusDays(LocalDateTime date, long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * Calculates days between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return number of days between start and end
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Checks if a date is after another date
     *
     * @param date1 first date
     * @param date2 second date
     * @return true if date1 is after date2
     */
    public static boolean isAfter(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isAfter(date2);
    }

    /**
     * Gets the start of day for a given date
     *
     * @param date the date
     * @return LocalDateTime at start of day (00:00:00)
     */
    public static LocalDateTime startOfDay(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of day for a given date
     *
     * @param date the date
     * @return LocalDateTime at end of day (23:59:59.999999999)
     */
    public static LocalDateTime endOfDay(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate().atTime(23, 59, 59, 999999999);
    }
}
