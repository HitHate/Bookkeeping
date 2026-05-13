package com.example.bookkeeping.model;

import androidx.lifecycle.ViewModel;

import com.example.bookkeeping.entity.Record;

public class ShareViewModel extends ViewModel {

    private Record record;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
