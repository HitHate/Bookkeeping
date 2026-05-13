package com.example.bookkeeping.service;

import androidx.lifecycle.LiveData;

import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.entity.Summary;

import java.util.List;

public interface RecordRepository {

    void insert(Record record);
    void update(Record record);
    void delete(Record record);
    LiveData<List<Record>> selectGet();
    LiveData<List<Record>> selectPay();
    LiveData<List<Record>> selectAll();
    LiveData<Double> count(int type);
    LiveData<Summary> getSummary();
    List<Object> searchRecords(String keyword,String type);
}
