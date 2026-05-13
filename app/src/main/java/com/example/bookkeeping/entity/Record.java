package com.example.bookkeeping.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

import java.time.YearMonth;
import java.util.Date;


@AutoValue
@Entity
public abstract class Record {
    @AutoValue.CopyAnnotations
    @PrimaryKey
    public abstract long getId();

    // 1 代表 收入
    // 0 代表 支出
    public abstract int getType();

    public abstract double getAmount();

    public abstract String getCategory();

    public abstract String getNote();

    public abstract Date getRecord_time();

    public abstract Date getCreate_time();

    public abstract Date getUpdate_time();

    public abstract YearMonth getYearmonth();

    public static Record create(long id, int type, double amount, String category,
                                String note, Date record_time, Date create_time, Date update_time, YearMonth yearmonth) {
        return builder()
                .setId(id)
                .setType(type)
                .setAmount(amount)
                .setCategory(category)
                .setNote(note)
                .setRecord_time(record_time)
                .setCreate_time(create_time)
                .setUpdate_time(update_time)
                .setYearmonth(yearmonth)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Record.Builder();
    }

    // 基于当前对象生成预先填充的 Builder
    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(long id);

        public abstract Builder setType(int type);

        public abstract Builder setAmount(double amount);

        public abstract Builder setCategory(String category);

        public abstract Builder setNote(String note);

        public abstract Builder setRecord_time(Date record_time);

        public abstract Builder setCreate_time(Date create_time);

        public abstract Builder setUpdate_time(Date update_time);

        public abstract Builder setYearmonth(YearMonth yearmonth);

        public abstract Record build();
    }
}
