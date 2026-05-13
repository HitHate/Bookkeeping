package com.example.bookkeeping.ui.insertOrupdate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookkeeping.R;
import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.databinding.ActivityInsertOrUpdateBinding;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.util.DateFormatUtil;
import com.github.gzuliyujiang.wheelpicker.DatimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnDatimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity;
import com.github.gzuliyujiang.wheelpicker.widget.DatimeWheelLayout;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

public class InsertOrUpdateActivity extends AppCompatActivity {

    private ActivityInsertOrUpdateBinding binding;
    private String type;
    private TextView id;
    private TextView or;
    private AppCompatSpinner spinner;
    private EditText amount;
    private EditText category;
    private TextView recordtime;
    private ImageButton picker;
    private EditText note;
    private Button cancel;
    private Button submit;
    private RecordRepositoryImpl repository;
    private int newType;
    private Date newRecordTime;
    private double newAmount;
    private String newCategory;
    private String newNote;
    private YearMonth newYm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInsertOrUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = RecordRepositoryImpl.getInstance(((MyApp) (this.getApplication())).getDatabase());
        id = binding.id;
        or = binding.or;
        spinner = binding.type;
        amount = binding.amount;
        category = binding.category;
        recordtime = binding.recordtime;
        picker = binding.timepicker;
        note = binding.note;
        cancel = binding.cancel;
        submit = binding.submit;

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("insert")) {
            initInsert();
        } else if (type.equals("edit")) {
            initEdit();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initEdit() {
        Record record = MyApp.getInstance().getShareViewModel().getRecord();

        newRecordTime = record.getRecord_time();
        newYm = record.getYearmonth();
        newAmount = record.getAmount();
        newNote = record.getNote();
        newCategory = record.getCategory();

        id.setText("收支单号:" + record.getId());
        or.setText("编辑");
        spinner.setSelection(record.getType());
        amount.setText(String.valueOf(record.getAmount()));
        category.setText(String.valueOf(record.getCategory()));
        recordtime.setText(DateFormatUtil.toYearMonthDayMinute(record.getRecord_time()));
        note.setText(record.getNote());
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYearMonthDayTime(view,record);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newType = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    amount.setError("不能为空");
                    submit.setEnabled(false);
                    return;
                }
                newAmount = Double.parseDouble(String.valueOf(editable).trim());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amount.setError(null);
                submit.setEnabled(true);
            }
        });
        category.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    newCategory = record.getCategory();
                }else {
                    newCategory = editable.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    newNote = record.getNote();
                }else {
                    newNote = editable.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(InsertOrUpdateActivity.this);
                normalDialog.setTitle("编辑");
                normalDialog.setMessage("确定您的编辑？");
                normalDialog.setPositiveButton("确定", (dialog, which) -> {
                    Record newRecord = record.toBuilder()
                            .setType(newType)
                            .setAmount(newAmount)
                            .setCategory(newCategory)
                            .setNote(newNote)
                            .setRecord_time(newRecordTime)
                            .setYearmonth(newYm)
                            .setUpdate_time(new Date())
                            .build();
                    repository.update(newRecord);
                    finish();
                });
                normalDialog.setNegativeButton("取消", (dialog, which) -> {
                });
                normalDialog.show();
            }
        });
    }

    private void initInsert() {
        Record record = MyApp.getInstance().getShareViewModel().getRecord();

        newRecordTime = new Date();
        newYm = YearMonth.now();
        newNote = "默认备注";
        newCategory = "默认分类";
        or.setText("新增");
        submit.setEnabled(false);
        amount.setError("不能为空");
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYearMonthDayTime(view,record);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newType = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    amount.setError("不能为空");
                    submit.setEnabled(false);
                    return;
                }
                newAmount = Double.parseDouble(String.valueOf(editable).trim());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amount.setError(null);
                submit.setEnabled(true);
            }
        });
        category.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    newCategory = "默认分类";
                }else {
                    newCategory = editable.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    newNote = "默认备注";
                }else {
                    newNote = editable.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = MyApp.getInstance().getIdGenerator().nextId();
                Record newRecord = Record.create(id,newType,newAmount,newCategory,newNote,newRecordTime,new Date(),new Date(),newYm);
                repository.insert(newRecord);
                finish();
            }
        });
    }

    public void onYearMonthDayTime(View view,Record record) {
        Calendar calendar = Calendar.getInstance();
        DatimePicker picker = new DatimePicker(this);
        DatimeEntity beginEntity = new DatimeEntity();
        DateEntity begin = new DateEntity();
        begin.setYear(2000);
        begin.setMonth(1);
        begin.setDay(1);
        beginEntity.setDate(begin);
        final DatimeWheelLayout wheelLayout = picker.getWheelLayout();
        wheelLayout.setResetWhenLinkage(false,false);
        picker.setOnDatimePickedListener(new OnDatimePickedListener() {
            @Override
            public void onDatimePicked(int year, int month, int day, int hour, int minute, int second) {
                calendar.set(year,(month-1),day,hour,minute,second);
                newRecordTime = calendar.getTime();
                newYm = YearMonth.of(year,month);
                recordtime.setText(DateFormatUtil.toYearMonthDayMinute(newRecordTime));
            }
        });
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
        wheelLayout.setTimeMode(TimeMode.HOUR_24_NO_SECOND);
        wheelLayout.setRange(beginEntity,DatimeEntity.now(),DatimeEntity.now());
        wheelLayout.setDateLabel("年", "月", "日");
        wheelLayout.setTimeLabel("时", "分", "秒");
        picker.show();
    }
}