package com.example.bookkeeping.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.FtsOptions;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

@AutoValue
@Fts4(contentEntity = Record.class, tokenizer = FtsOptions.TOKENIZER_ICU)
@Entity(tableName = "record_fts")
public abstract class RecordFts {

    @AutoValue.CopyAnnotations
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    public abstract long getRowid();// 雪花ID，对应底层 INTEGER 主键

    @AutoValue.CopyAnnotations
    @ColumnInfo(name = "note")
    public abstract String getNote();

    @AutoValue.CopyAnnotations
    @ColumnInfo(name = "category")
    public abstract String getCategory();

    public static RecordFts create(long rowid, String note, String category) {
        return new AutoValue_RecordFts(rowid, note, category);
    }
}