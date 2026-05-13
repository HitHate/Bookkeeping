package com.example.bookkeeping.service.impl;


import androidx.lifecycle.LiveData;

import com.example.bookkeeping.application.MyApp;

import com.example.bookkeeping.dao.SearchDAO;
import com.example.bookkeeping.database.AppDatabase;
import com.example.bookkeeping.entity.Search;
import com.example.bookkeeping.service.SearchRepository;
import com.example.bookkeeping.util.AppExecutors;

import java.util.Date;
import java.util.List;

public class SearchRepositoryImpl implements SearchRepository {

    private static volatile SearchRepositoryImpl repository;
    private final SearchDAO searchDAO;
    private SearchRepositoryImpl(AppDatabase appDatabase) {
        searchDAO = appDatabase.searchDAO();
    }

    public static SearchRepositoryImpl getInstance(AppDatabase appDatabase){
        if (repository == null){
            synchronized (SearchRepositoryImpl.class){
                if (repository == null){
                    repository = new SearchRepositoryImpl(appDatabase);
                }
            }
        }
        return repository;
    }
    @Override
    public LiveData<List<Search>> findAll() {
        return searchDAO.findAll();
    }

    @Override
    public void update(String keyword) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                searchDAO.update(keyword,new Date());
            }
        });
    }

    @Override
    public void insert(String keyword) {
        long id = MyApp.getInstance().getIdGenerator().nextId();
        Search search = Search.create(id,keyword,new Date());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                searchDAO.insert(search);
            }
        });
    }

    @Override
    public void deleteAll() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                searchDAO.deleteAll();
            }
        });

    }

    public void delete(String keyword){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                searchDAO.delete(keyword);
            }
        });
    }
}
