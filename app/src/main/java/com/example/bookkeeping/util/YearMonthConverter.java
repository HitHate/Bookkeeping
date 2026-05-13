package com.example.bookkeeping.util;

import androidx.room.TypeConverter;
import java.time.YearMonth;

public class YearMonthConverter {
    @TypeConverter
    public static Integer fromYearMonth(YearMonth ym) {
        return ym == null ? null : ym.getYear() * 100 + ym.getMonthValue();
    }

    @TypeConverter
    public static YearMonth toYearMonth(Integer value) {
        if (value == null) return null;
        return YearMonth.of(value / 100, value % 100);
    }
}