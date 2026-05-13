package com.example.bookkeeping.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
@Entity
public abstract class Search {

    @AutoValue.CopyAnnotations
    @PrimaryKey
    public abstract long getId();

    public abstract String getKeyword();

    public abstract Date getSearch_time();

    public static Search create(long id , String keyword, Date search_time){
        return new AutoValue_Search(id,keyword,search_time);

    }
}
