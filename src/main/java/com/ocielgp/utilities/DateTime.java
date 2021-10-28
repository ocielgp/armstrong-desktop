package com.ocielgp.utilities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTime {
    private static final String DATETIME_MYSQL = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_MYSQL = "y-MM-dd";
    private static final String DATE_WITH_DAY_NAME = "EEEE, dd/MMMM/yyyy";
    private static final String DATE = "dd/MM/y";
    private static final String DATE_SHORT = "dd/MM/yyyy";

    public static LocalDateTime MySQLToJava(String dateTime) {
        return LocalDateTime.parse(
                dateTime,
                DateTimeFormatter.ofPattern(DATETIME_MYSQL)
        );
    }

    public static String JavaToMySQLDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DATETIME_MYSQL));
    }

    public static String JavaToMySQLDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_MYSQL));
    }

    public static boolean isToday(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_MYSQL, Locale.getDefault())).equals(LocalDate.now().toString());
    }

    public static String getDateWithDayName(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_WITH_DAY_NAME, Locale.getDefault()));
    }

    public static String getDateShort(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_SHORT, Locale.getDefault()));
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
