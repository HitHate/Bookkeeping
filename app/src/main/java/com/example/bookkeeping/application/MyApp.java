package com.example.bookkeeping.application;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.bookkeeping.database.AppDatabase;
import com.example.bookkeeping.model.ShareViewModel;
import com.example.bookkeeping.util.SnowFlake;
import com.example.bookkeeping.util.SnowFlakeHelper;
import com.github.gzuliyujiang.dialog.DialogConfig;
import com.github.gzuliyujiang.dialog.DialogStyle;

public class MyApp extends Application implements ViewModelStoreOwner {
    private SnowFlake idGenerator;
    private AppDatabase database;
    private ViewModelStore viewModelStore;
    private ShareViewModel shareViewModel;

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        viewModelStore = new ViewModelStore();
        shareViewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        DialogConfig.setDialogStyle(DialogStyle.One);
        idGenerator = new SnowFlake(SnowFlakeHelper.getDataCenterId(this), SnowFlakeHelper.getWorkerId(this), this);
        database = AppDatabase.getInstance(this);
    }

    public static MyApp getInstance() {
        return instance;
    }

    public ShareViewModel getShareViewModel(){
        return shareViewModel;
    }
    public SnowFlake getIdGenerator() {
        return idGenerator;
    }

    public AppDatabase getDatabase(){
        return database;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}
