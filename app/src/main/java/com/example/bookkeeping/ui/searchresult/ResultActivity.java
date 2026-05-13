package com.example.bookkeeping.ui.searchresult;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bookkeeping.R;
import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.databinding.ActivityResultBinding;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.ui.Adapter;
import com.example.bookkeeping.ui.insertOrupdate.InsertOrUpdateActivity;
import com.example.bookkeeping.util.AppExecutors;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import com.yanzhenjie.recyclerview.widget.StickyNestedScrollView;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private RecordRepositoryImpl repository;
    private SwipeRecyclerView recyclerView;
    private StickyNestedScrollView scrollView;
    private Adapter adapter;
    private TextView textView;
    private List<Object> recordList;
    private String type;
    private String keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        keyword = intent.getStringExtra("keyword");



        adapter = new Adapter();
        repository = RecordRepositoryImpl.getInstance((MyApp.getInstance()).getDatabase());
        textView = binding.keyword;
        recyclerView = binding.result;
        scrollView = binding.scrollView;
        textView.setText(keyword);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                recordList = repository.searchRecords(keyword,type);
                runOnUiThread(() -> {
                    adapter.submitData(recordList, ResultActivity.this);
                });
            }
        });


        scrollView.setNestedScrollingEnabled(false);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(ResultActivity.this, R.color.Gainsboro)));
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setOnItemMenuClickListener(listener);
        recyclerView.setAdapter(adapter);

    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            int type = adapter.getItemViewType(position);
            if(type == Adapter.VIEW_TYPE_NO_HEADER){
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
                SwipeMenuItem editItem = new SwipeMenuItem(ResultActivity.this)
                        .setText("编辑")
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height)
                        .setTextColor(Color.WHITE)
                        .setBackgroundColor(Color.rgb(135, 206, 250));
                rightMenu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(ResultActivity.this)
                        .setText("删除")
                        .setWidth(width)
                        .setTextSize(15)
                        .setHeight(height)
                        .setBackgroundColor(Color.RED)
                        .setTextColor(Color.WHITE);
                rightMenu.addMenuItem(deleteItem);

            }
        }
    };

    private OnItemMenuClickListener listener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
            menuBridge.closeMenu();
            Record record = (Record) recordList.get(adapterPosition);
            if(menuBridge.getPosition() == 0){
                Intent intent = new Intent(ResultActivity.this, InsertOrUpdateActivity.class);
                intent.putExtra("type","edit");
                MyApp.getInstance().getShareViewModel().setRecord(record);
                startActivity(intent);
            }else {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(ResultActivity.this);
                normalDialog.setTitle("删除？");
                normalDialog.setMessage("确定删除这条账目?");
                normalDialog.setPositiveButton("确定", (dialog, which) -> {
                    repository.delete(record);
                });
                normalDialog.setNegativeButton("取消", (dialog, which) -> {
                });
                normalDialog.show();
            }

        }
    };
}