package com.example.bookkeeping.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static volatile AppExecutors sInstance;
    private final ExecutorService diskIO;

    private AppExecutors() {
        diskIO = Executors.newSingleThreadExecutor();
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppExecutors();
                }
            }
        }
        return sInstance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }
}