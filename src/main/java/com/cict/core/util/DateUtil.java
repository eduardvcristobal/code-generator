package com.cict.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Handles the conversion of {@link Date} to a new date and time API of Java 8.
 * Following are some of the new classes that belongs that API:
 * TODO - really messy class that needs a thorough clean/remodel.
 * <p>
 * {@link LocalDateTime}
 * {@link LocalDate}
 * {@link OffsetDateTime}
 * {@link OffsetTime}
 */
public class DateUtil {

    /**
     * The default time-zone offset to use.
     */
    private final static ZoneOffset zoneOffset = ZoneOffset.UTC;

    /**
     * Converts {@link ZonedDateTime} to legacy {@link Date}.
     */
    public static Date toDate(ZonedDateTime zdt) {
        return Date.from(zdt.toInstant());
    }

    /**
     * Converts {@link LocalDate} to legacy {@link Date}.
     */
    public static Date toDate(LocalDate ld) {
        return Date.from(ld.atStartOfDay().atZone(zoneOffset).toInstant());
    }

    /**
     * Converts legacy {@link Date} to {@link ZonedDateTime}.
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), zoneOffset);
    }

    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * Converts legacy {@link Date} to {@link String} value of {@link ZonedDateTime}.
     */
    public static String toZuluTimestamp(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatter.format(toZonedDateTime(date));
    }


    public static String toTimestamp(Date date, ZoneId zoneId) {
        return toTimestamp(date, zoneId, "");
    }

    public static String toTimestamp(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static String toTimestamp(Date date, ZoneId zoneId, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.isEmpty() ? "yyyy-MM-dd HH:mm:ss" : pattern);
        return formatter.format(toZonedDateTime(date, zoneId));
    }

    /**
     * Converts legacy {@link Date} to {@link Date}.
     */
    public static LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneOffset).toLocalDate();
    }

    /**
     * Converts {@link LocalDate} to string.
     */
    public static String toDateString(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    /**
     * Converts legacy {@link Date} to {@link Date}.
     */
    public static LocalDate toLocalDate(String date) {
        if (date.matches("\\d{4}-\\d{2}")) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return LocalDate.parse(date);
    }

    /**
     * Converts the string date to date.
     */
    public static Date convertToDate(String strDate, String pattern) {

        if (strDate.isEmpty()) {
            return null;
        }

        LocalDate ld = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(pattern));
        LocalDate fd = LocalDate.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth());
        return Date.from(Instant.ofEpochMilli(fd.toEpochDay()));
    }

    /**
     * Converts string to date with specific format.
     */
    public static Date convertToDate(String dateValue) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(dateValue);
    }

    /**
     * Converts date to string with specific format.
     */
    public static String convertToDate(Date date) {
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        return newFormat.format(date);
    }

    public static String convertToDate(Date date, String pattern) {
        SimpleDateFormat newFormat = new SimpleDateFormat(pattern);
        return newFormat.format(date);
    }

    public static String convertFromInstant(String instantString, String PATTERN_FORMAT) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(ZoneId.systemDefault());
        Instant instant = Instant.parse(instantString);
        String formattedInstant = formatter.format(instant);

        return formattedInstant;
    }


    /**
     * Returns the greater of the two dates or which ever is not-null if one of them
     * is null.
     */
    public static Date max(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            return date1.compareTo(date2) < 0 ? date2 : date1;
        }
    }

    public void getDates() {
        Month month = Month.valueOf("March".toUpperCase());

        System.out.printf("For the month of %s:%n", month);
        LocalDate date = Year.now().atMonth(month).atDay(1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        Month mi = date.getMonth();
        while (mi == month) {
            System.out.printf("%s%n", date);
            date = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            mi = date.getMonth();
        }
    }
}