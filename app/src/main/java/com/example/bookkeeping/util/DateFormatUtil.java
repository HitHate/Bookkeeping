package com.example.bookkeeping.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DateFormatUtil {

    private static final ThreadLocal<SimpleDateFormat> MONTH_DAY =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("M月d日", Locale.CHINESE));
    private static final ThreadLocal<SimpleDateFormat> YEAR_MONTH_DAY =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy年M月d日", Locale.CHINESE));

    private static final ThreadLocal<SimpleDateFormat> YEAR_MONTH_DAY_MINUTE=
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-M-d H:mm", Locale.CHINESE));
    private static final ThreadLocal<SimpleDateFormat> YEAR_MONTH_DAY_SECOND =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-M-d H:mm:ss", Locale.CHINESE));

    public static String toMonthDay(Date date){

        return Objects.requireNonNull(MONTH_DAY.get()).format(date);
    }

    public static String toYearMonthDayMinute(Date date){
        return Objects.requireNonNull(YEAR_MONTH_DAY_MINUTE.get()).format(date);
    }

    public static String toYearMonthDay(Date date){
        return Objects.requireNonNull(YEAR_MONTH_DAY.get()).format(date);
    }

    public static String toYearMonthDaySecond(Date date){
        return Objects.requireNonNull(YEAR_MONTH_DAY_SECOND.get()).format(date);
    }
}
