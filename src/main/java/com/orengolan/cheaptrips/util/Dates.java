package com.orengolan.cheaptrips.util;

import org.joda.time.*;
import org.springframework.lang.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The {@code Dates} class provides utility methods for working with dates and times,
 * including conversions between different date representations and formatting.
 *
 * Key Features:
 * - Conversion between {@code LocalDate}, {@code LocalDateTime}, and {@code Date} objects.
 * - Methods for obtaining the current date and time in various formats.
 * - Parsing string representations of dates into {@code LocalDateTime}.
 * - Handling time zones and formatting patterns for date representations.
 *
 * Example Usage:
 * This class is used across the CheapTrips application to manage dates and times consistently.
 * It includes methods for converting between different date types, parsing date strings,
 * and obtaining the current date and time in UTC.
 *
 * Note: The class uses the Joda-Time library for enhanced date and time handling capabilities.
 * It includes methods for converting between Joda-Time and standard Java date representations.
 */
public class Dates {
    public static SimpleDateFormat shortDate = new SimpleDateFormat("yyyy-MM-dd");
    public static TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Jerusalem");
    public static SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy/MM");

    public Dates() {
    }

    public static String getCurMonth() {
        Date date = new Date();
        return monthFormatter.format(date);
    }

    public static String dateToStr(@Nullable LocalDate date) {
        return date == null ? null : shortDate.format(date);
    }

    public static Date atUtc(LocalDateTime date) {
        return atUtc(date, TIME_ZONE);
    }

    public static Date atUtc(LocalDateTime date, TimeZone zone) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTimeZone(zone);
        calendar.set(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());//convert from locatDateTime to Calender time
        calendar.set(Calendar.HOUR_OF_DAY, date.getHourOfDay());
        calendar.set(Calendar.MINUTE, date.getMinuteOfHour());
        calendar.set(Calendar.SECOND, date.getSecondOfMinute());
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date atUtc(@Nullable LocalDate date) {
        return atUtc(date, TIME_ZONE);
    }

    public static Date atUtc(@Nullable LocalDate date, TimeZone zone) {
        return date == null ? null : atUtc(date.toLocalDateTime(LocalTime.MIDNIGHT), zone);
    }

    public static LocalDateTime atLocalTime(Date date) {
        return atLocalTime(date, TIME_ZONE);
    }

    public static LocalDateTime atLocalTime(Date date, TimeZone zone) {
        if (date == null) return null;
        java.time.LocalDateTime localDate = OffsetDateTime.ofInstant(date.toInstant(), zone.toZoneId()).toLocalDateTime();
        Calendar c = Calendar.getInstance();
        c.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        c.set(Calendar.HOUR_OF_DAY, localDate.getHour());
        c.set(Calendar.MINUTE, localDate.getMinute());
        c.set(Calendar.SECOND, localDate.getSecond());
        c.set(Calendar.MILLISECOND, 0);
        LocalDateTime res = LocalDateTime.fromCalendarFields(c);
        return res;
    }

    public static Date nowUTC() {
        return DateTime.now().withZone(DateTimeZone.UTC).toDate();
    }

    public static String getFullDateTime() {
        return DateTime.now().withZone(DateTimeZone.UTC).toDateTimeISO().toString();
    }

    public static boolean equals(@Nullable Date date1, @Nullable Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        } else {
            return Objects.equals(date1, date2);
        }
    }
    public static LocalDateTime parseStringToLocalDateTime(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = formatter.parse(dateString);
        return atLocalTime(date);
    }
    public static LocalDateTime parseStringToLocalDate(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);
        return atLocalTime(date);
    }


}
