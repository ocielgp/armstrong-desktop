package com.ocielgp.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateFormatter {
    private static final String dd_MM_yyyy = "dd/MM/yyyy";
    private static final String dd_MM_yy = "dd/MM/yy";
    private static final String EEEE_dd_MMM_yyyy = "EEEE, dd/MMM/yyyy";
    private static final String yyyy_mm_dd = "yyyy-mm-dd";

    public static LocalDate plusDaysToCurrentDate(int days) {
        return LocalDate.now().plusDays(days);
    }

    public static String getDateWithDayName(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(EEEE_dd_MMM_yyyy));
    }

    public static long daysDifferenceToday(LocalDate date) {
//        return date.compareTo(LocalDate.now());
        return ChronoUnit.DAYS.between(LocalDate.now(), date);
    }

    public static int differenceBetweenDays(LocalDate firstDate, LocalDate secondDate) {
        System.out.println(firstDate.compareTo(secondDate));
        return Math.abs(firstDate.compareTo(secondDate));
    }

    public static String getDayMonthYearComplete(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(dd_MM_yyyy));
    }

    public static String getDayMonthYearShort(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(dd_MM_yy));
    }

}
