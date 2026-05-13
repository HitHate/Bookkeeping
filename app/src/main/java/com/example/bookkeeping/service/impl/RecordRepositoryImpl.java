package com.example.bookkeeping.service.impl;

import androidx.lifecycle.LiveData;

import com.example.bookkeeping.dao.RecordDAO;
import com.example.bookkeeping.database.AppDatabase;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.entity.Summary;
import com.example.bookkeeping.service.RecordRepository;
import com.example.bookkeeping.util.AppExecutors;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordRepositoryImpl implements RecordRepository {
    private static volatile RecordRepositoryImpl repository;
    private final RecordDAO recordDAO;
    private RecordRepositoryImpl(AppDatabase appDatabase) {
        recordDAO = appDatabase.recordDAO();
    }

    public static RecordRepositoryImpl getInstance(AppDatabase appDatabase){
        if (repository == null){
            synchronized (RecordRepositoryImpl.class){
                if (repository == null){
                    repository = new RecordRepositoryImpl(appDatabase);
                }
            }
        }
        return repository;
    }

    @Override
    public void insert(Record record) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                recordDAO.insert(record);
            }
        });
    }

    @Override
    public void update(Record record) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                recordDAO.update(record);
            }
        });
    }

    @Override
    public void delete(Record record) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                recordDAO.delete(record);
            }
        });
    }

    @Override
    public LiveData<List<Record>> selectGet() {
        return recordDAO.selectGet();
    }

    @Override
    public LiveData<List<Record>> selectPay() {
        return recordDAO.selectPay();
    }

    @Override
    public LiveData<List<Record>> selectAll() {
        return recordDAO.selectAll();
    }

    public double Difference(double get, double pay){
        return get-pay;
    }

    @Override
    public LiveData<Double> count(int type) {
        return recordDAO.count(type);
    }

    @Override
    public LiveData<Summary> getSummary() {
        return recordDAO.summary();
    }

    @Override
    public List<Object> searchRecords(String keyword,String type) {
        if(type.equals("全部")){
            return buildFlatList(recordDAO.searchRecords(keyword));
        }else if(type.equals("收入")){
            return buildFlatList(recordDAO.searchRecordByType(keyword,1));
        } else{
            return buildFlatList(recordDAO.searchRecordByType(keyword,0));
        }
    }

    public List<Object> buildFlatList(List<Record> sortedRecords) {
        LinkedHashMap<YearMonth, List<Record>> grouped = new LinkedHashMap<>();
        for (Record record : sortedRecords) {
            YearMonth ym = record.getYearmonth();
            grouped.computeIfAbsent(ym, k -> new ArrayList<>()).add(record);
        }

        List<Object> flatList = new ArrayList<>();
        for (Map.Entry<YearMonth, List<Record>> entry : grouped.entrySet()) {
            flatList.add(entry.getKey());       // 组头：YearMonth
            flatList.addAll(entry.getValue());  // 子项：Record
        }
        return flatList;
    }
}
