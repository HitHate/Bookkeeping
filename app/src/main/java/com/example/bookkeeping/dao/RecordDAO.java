package com.example.bookkeeping.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.entity.Summary;

import java.util.List;

@Dao
public interface RecordDAO {

    @Query("SELECT * FROM Record WHERE type = 1 ORDER BY yearmonth DESC , create_time DESC")
    LiveData<List<Record>> selectGet();

    @Query("SELECT * FROM Record WHERE type = 0 ORDER BY yearmonth DESC , create_time DESC")
    LiveData<List<Record>> selectPay();

    @Query("SELECT * FROM Record ORDER BY yearmonth DESC , create_time DESC")
    LiveData<List<Record>> selectAll();
    @Insert
    void insert(Record record);
    @Update
    void update(Record record);
    @Delete
    void delete(Record record);
    @Query("SELECT SUM(amount) FROM Record WHERE type = :type")
    LiveData<Double> count(int type);

    @Query("SELECT " +
            "COALESCE(SUM(CASE WHEN type = 1 THEN amount ELSE 0 END), 0) AS income, " +
            "COALESCE(SUM(CASE WHEN type = 0 THEN amount ELSE 0 END), 0) AS expense " +
            "FROM Record")
    LiveData<Summary> summary();

    @Query("SELECT Record.* FROM record_fts JOIN Record ON record_fts.rowid = Record.id " +
            "WHERE record_fts MATCH :query")
    List<Record> searchRecords(String query);

    @Query("SELECT Record.* FROM record_fts JOIN Record ON record_fts.rowid = Record.id " +
            "WHERE record_fts MATCH :query AND Record.type = :type")
    List<Record> searchRecordByType(String query, int type);
}
