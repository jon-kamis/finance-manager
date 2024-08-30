package com.kamis.financemanager.util;

import org.hibernate.query.sqm.TemporalUnit;

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

    /**
     * Returns the last moment of the previous day
     * @param date The starting date
     * @return The last moment of the previous day prior to date
     */
    public static Date getEndOfPreviousDay(Date date) {
        return Date.from(LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant());
    }

    /**
     * Returns the last moment of the given date
     * @param date The date to get the last moment of
     * @return A date representing the last moment of the passed date
     */
    public static Date getEndOfDay(Date date) {
        return Date.from(LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusNanos(1).toInstant());
    }

    /**
     * Returns the first moment of the next date
     * @param date the starting date
     * @return A date that is the first moment of the day directly following date
     */
    public static Date getStartOfNextDay(Date date) {
        return Date.from(LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
