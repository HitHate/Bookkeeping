package com.example.bookkeeping.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookkeeping.entity.Search;

import java.util.Date;
import java.util.List;

@Dao
public interface SearchDAO {

    @Query("SELECT * FROM Search ORDER BY search_time DESC")
    LiveData<List<Search>> findAll();

    @Query("UPDATE Search SET search_time = :time WHERE keyword = :keyword")
    void update(String keyword, Date time);

    @Insert
    void insert(Search search);

    @Query("DELETE FROM Search")
    void deleteAll();

    @Query("DELETE FROM Search WHERE keyword = :keyword")
    void delete(String keyword);
}
