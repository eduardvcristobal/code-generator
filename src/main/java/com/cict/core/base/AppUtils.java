package com.cict.core.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AppUtils {
    public static boolean isDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isLocalDateTime(String value) {
        try {
            LocalDateTime.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseLocalDate(String value) {
        return LocalDate.parse(value);
    }

    public static LocalDateTime parseLocalDateTime(String value) {
        return LocalDateTime.parse(value);
    }
}
