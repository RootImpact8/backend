package com.example.rootimpact.domain.farm.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private DateUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 현재 날짜 반환
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    // 현재 기준일 반환 (현재 날짜 - 1, 주말인 경우 금요일로 조정)
    public static LocalDate getCurrentDate() {
        LocalDate currentDate = getToday().minusDays(1);
        return adjustForWeekend(currentDate);
    }

    // 이전 기준일 반환 (현재 기준일 - 1)
    public static LocalDate getPreviousDate() {
        return getCurrentDate().minusDays(1);
    }

    // 현재 기준일 -> 문자열(yyyy-MM-dd)
    public static String getCurrentDateStr() {
        return getCurrentDate().format(DateTimeFormatter.ISO_DATE);
    }

    // 이전 기준일 -> 문자열(yyyy-MM-dd)
    public static String getPreviousDateStr() {
        return getPreviousDate().format(DateTimeFormatter.ISO_DATE);
    }

    // 주말 -> 금요일로 날짜 조정
    private static LocalDate adjustForWeekend(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return date.minusDays(2);  // 일요일 -> 금요일
        } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return date.minusDays(1);  // 토요일 -> 금요일
        }
        return date;
    }
}
