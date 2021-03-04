package com.ocielgp.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("");
    private static final String dd_MM_yyyy = "dd/MM/yyyy";
    private static final String EEEE_dd_MMM_yyyy = "EEEE, dd/MMM/yyyy";
    private static final String yyyy_mm_dd = "yyyy-mm-dd";

    public static String getDateWithDayName(LocalDate date) {
        formatter = DateTimeFormatter.ofPattern(EEEE_dd_MMM_yyyy);
        return date.format(formatter);
    }

    public static int differenceBetweenDays(LocalDate firstDate, LocalDate secondDate) {
        return Math.abs(firstDate.compareTo(secondDate));
    }

}
