package com.example.bookkeeping.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Random;

public class SnowFlakeHelper {

    private static final String PREFS_NAME = "worker_id_prefs";
    private static final String KEY_DATA_CENTER = "data_center_id";
    private static final String KEY_WORKER = "worker_id";

    /**
     * 获取或生成 dataCenterId（0~31）
     */
    public static long getDataCenterId(Context context) {
        return getId(context, KEY_DATA_CENTER);
    }

    /**
     * 获取或生成 workerId（0~31）
     */
    public static long getWorkerId(Context context) {
        return getId(context, KEY_WORKER);
    }

    private static long getId(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long id = prefs.getLong(key, -1L);
        if (id < 0) {
            id = new Random().nextInt(32); // 0~31
            prefs.edit().putLong(key, id).apply();
        }
        return id;
    }
}