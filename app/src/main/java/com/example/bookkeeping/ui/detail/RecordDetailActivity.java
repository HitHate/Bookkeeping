package com.example.bookkeeping.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookkeeping.R;
import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.databinding.ActivityRecordDetailBinding;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.util.DateFormatUtil;


public class RecordDetailActivity extends AppCompatActivity {
    private TextView id;
    private TextView type;
    private TextView amount;
    private TextView category;
    private TextView recordtime;
    private TextView createtime;
    private TextView updatetime;
    private TextView note;
    private ActivityRecordDetailBinding binding;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRecordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        record = MyApp.getInstance().getShareViewModel().getRecord();
        Log.d("tag",record.getNote());
        id = binding.id;
        type = binding.type;
        amount = binding.amount;
        category = binding.category;
        recordtime = binding.recordtime;
        createtime = binding.createtime;
        updatetime = binding.updatetime;
        note = binding.note;
        init();
    }

    public void init(){
        id.setText(String.valueOf(record.getId()));
        type.setText(record.getType() == 1?"收入":"支出");
        amount.setText(String.valueOf(record.getAmount()));
        category.setText(record.getCategory());
        recordtime.setText(DateFormatUtil.toYearMonthDayMinute(record.getRecord_time()));
        createtime.setText(DateFormatUtil.toYearMonthDaySecond(record.getCreate_time()));
        updatetime.setText(DateFormatUtil.toYearMonthDaySecond(record.getUpdate_time()));
        note.setText(record.getNote());
    }
}