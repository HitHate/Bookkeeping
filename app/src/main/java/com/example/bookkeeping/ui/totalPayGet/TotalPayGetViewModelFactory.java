package com.example.bookkeeping.ui.totalPayGet;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.ui.get.GetViewModel;

public class TotalPayGetViewModelFactory implements ViewModelProvider.Factory {
    private final RecordRepositoryImpl repository;
    public TotalPayGetViewModelFactory(RecordRepositoryImpl repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(TotalPayGetViewModel.class)){
            return (T) new TotalPayGetViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
