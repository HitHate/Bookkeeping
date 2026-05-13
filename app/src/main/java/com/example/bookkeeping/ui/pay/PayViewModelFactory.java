package com.example.bookkeeping.ui.pay;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.ui.get.GetViewModel;

public class PayViewModelFactory implements ViewModelProvider.Factory {

    private final RecordRepositoryImpl repository;
    public PayViewModelFactory(RecordRepositoryImpl repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(PayViewModel.class)){
            return (T) new PayViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
