package com.example.bookkeeping.ui.total;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.ui.get.GetViewModel;

public class TotalViewModelFactory implements ViewModelProvider.Factory {

    private final RecordRepositoryImpl repository;
    public TotalViewModelFactory(RecordRepositoryImpl repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(TotalViewModel.class)){
            return (T) new TotalViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
