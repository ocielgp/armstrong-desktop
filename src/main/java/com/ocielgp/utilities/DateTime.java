package com.ocielgp.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private static final String DATETIME_MYSQL = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_WITH_DAY_NAME = "EEEE, dd/MMM/yyyy";

    public static LocalDateTime MySQLToJava(String dateTime) {
        return LocalDateTime.parse(
                dateTime,
                DateTimeFormatter.ofPattern(DATETIME_MYSQL)
        );
    }

    public static String getDateWithDayName(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_WITH_DAY_NAME));
    }
}
