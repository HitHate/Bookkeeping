package com.example.bookkeeping.ui.get;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookkeeping.database.AppDatabase;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.util.AppExecutors;

import java.io.Closeable;
import java.util.List;

public class GetViewModel extends ViewModel {
    private final LiveData<List<Record>> source;
    private final MediatorLiveData<List<Object>> flatRecords = new MediatorLiveData<>();
    private final AppExecutors executors;

    public GetViewModel(@NonNull RecordRepositoryImpl repository) {
        source = repository.selectGet();
        executors = AppExecutors.getInstance();
        flatRecords.addSource(source,records -> {
            if (records == null) return;
            // 切换到后台线程执行分组
            executors.diskIO().execute(() -> {
                List<Object> flatList = repository.buildFlatList(records); // 你的分组方法
                flatRecords.postValue(flatList);
            });
        });
    }

    public LiveData<List<Object>> getFlatRecords(){
        return flatRecords;
    }
}