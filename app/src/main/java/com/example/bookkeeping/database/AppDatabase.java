package com.example.bookkeeping.database;

import android.content.Context;

import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.bookkeeping.dao.RecordDAO;
import com.example.bookkeeping.dao.SearchDAO;
import com.example.bookkeeping.entity.RecordFts;
import com.example.bookkeeping.entity.Search;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.util.Converters;
import com.example.bookkeeping.util.YearMonthConverter;


@Database(entities = {Search.class, Record.class, RecordFts.class} , version = 1 ,exportSchema = false)
@TypeConverters({Converters.class, YearMonthConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase db;
    public static AppDatabase getInstance(Context context){
        if (db == null){
            synchronized (AppDatabase.class){
                if (db == null){
                    db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"Bookkeeping.db").build();
                }
            }
        }
        return db;
    }

    public abstract RecordDAO recordDAO();
    public abstract SearchDAO searchDAO();
}
