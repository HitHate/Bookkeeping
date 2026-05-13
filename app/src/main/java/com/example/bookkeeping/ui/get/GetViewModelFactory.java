package com.example.bookkeeping.ui.get;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;

public class GetViewModelFactory implements ViewModelProvider.Factory {

    private final RecordRepositoryImpl repository;
    public GetViewModelFactory(RecordRepositoryImpl repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(GetViewModel.class)){
            return (T) new GetViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
