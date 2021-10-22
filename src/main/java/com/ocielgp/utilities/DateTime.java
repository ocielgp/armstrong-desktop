package com.ocielgp.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTime {
    private static final String DATETIME_MYSQL = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_WITH_DAY_NAME = "EEEE, dd/MMM/yyyy";
    private static final String DATE_SHORT = "dd/MM/yyyy";

    public static LocalDateTime MySQLToJava(String dateTime) {
        return LocalDateTime.parse(
                dateTime,
                DateTimeFormatter.ofPattern(DATETIME_MYSQL)
        );
    }

    public static String getDateWithDayName(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_WITH_DAY_NAME, new Locale("es", "MX")));
    }

    public static String getDateShort(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_SHORT, new Locale("es", "MX")));
    }

    public static String getEndDate(long months) {
        return getDateWithDayName(
                LocalDateTime.now().plusMonths(months)
        );
    }

    public static long getDaysLeft(String dateTime) {
        return Duration.between(
                LocalDateTime.now(),
                MySQLToJava(dateTime)
        ).toDays();
    }

    public static long getDaysLeft(LocalDateTime localDateTime) {
        return Duration.between(LocalDateTime.now(), localDateTime).toDays();
    }
}
