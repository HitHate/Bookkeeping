package com.example.bookkeeping.service;

import androidx.lifecycle.LiveData;

import com.example.bookkeeping.entity.Search;

import java.util.List;

public interface SearchRepository {
    LiveData<List<Search>> findAll();

    void update(String keyword);

    void insert(String keyword);

    void deleteAll();

    void delete(String keyword);
}
