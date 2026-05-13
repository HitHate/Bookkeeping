package com.example.bookkeeping.ui.total;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.bookkeeping.entity.Summary;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;

public class TotalViewModel extends ViewModel {

    private LiveData<Double> get;
    private LiveData<Double> pay;
    private LiveData<Double> netIncome;

    private LiveData<Summary> summaryLiveData;


    public TotalViewModel(RecordRepositoryImpl repository) {
        get = repository.count(1);
        pay = repository.count(0);
        summaryLiveData = repository.getSummary();

        netIncome = Transformations.map(summaryLiveData, input -> input.income - input.expense);
    }

    public LiveData<Double> getGet() {
        return get;
    }
    public LiveData<Double> getPay() {
        return pay;
    }

    public LiveData<Double> getNetIncome(){
        return netIncome;
    }

}