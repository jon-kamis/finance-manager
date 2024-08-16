package com.kamis.financemanager.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    /**
     * Returns a Java Util Date object from a date string
     * @param dateString The date string to parse. Expects format 'yyyy-mm-dd'
     * @return A Java Util Date Object parsed from the given string
     */
    public static Date getDate(String dateString) {
        return Date.from(LocalDate.parse(dateString).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Returns a Java Util Date object from a date time string
     * @param dateTimeString The date string to parse. Expects format 'yyyy-mm-ddT00:00:00'
     * @return A Java Util Date Object parsed from the given string
     */
    public static Date getDateTime(String dateTimeString) {
        return Date.from(LocalDateTime.parse(dateTimeString).atZone(ZoneId.systemDefault()).toInstant());
    }
}
