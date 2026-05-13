package com.example.bookkeeping;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookkeeping.service.impl.SearchRepositoryImpl;
import com.example.bookkeeping.ui.pay.PayViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final SearchRepositoryImpl repository;
    public MainViewModelFactory(SearchRepositoryImpl repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
