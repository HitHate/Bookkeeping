package com.example.bookkeeping.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.ui.detail.RecordDetailActivity;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.util.DateFormatUtil;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_NO_HEADER = 0;
    public static final int VIEW_TYPE_EMPTY = -1;
    private static final Object EMPTY_ITEM = new Object();
    private Activity activity;

    public static final DiffUtil.ItemCallback<Object> DIFF_CALLBACK = new DiffUtil.ItemCallback<Object>() {
        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            if(oldItem == EMPTY_ITEM && newItem == EMPTY_ITEM) return true;
            if(oldItem instanceof YearMonth && newItem instanceof YearMonth){
                return oldItem.equals(newItem);
            }else if(oldItem instanceof Record && newItem instanceof Record){
                return ((Record)oldItem).getId() == ((Record)newItem).getId();
            }
            return false;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            if (oldItem == EMPTY_ITEM && newItem == EMPTY_ITEM) return true;
            return oldItem.equals(newItem);
        }
    };
    private final AsyncListDiffer<Object> differ = new AsyncListDiffer<>(this,DIFF_CALLBACK);


    public void submitData(List<Object> list,Activity activity){
        this.activity = activity;
        if (list == null || list.isEmpty()) {
            // 提交一个只包含空占位符的列表
            differ.submitList(Collections.singletonList(EMPTY_ITEM));
        } else {
            differ.submitList(list);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_TYPE_HEADER){
            View view = inflater.inflate(R.layout.item_header,parent,false);
            return new HeaderViewHolder(view);
        }else if(viewType == VIEW_TYPE_NO_HEADER){
            View view = inflater.inflate(R.layout.item_content,parent,false);
            return new ContentViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.empty_view,parent,false);
            return new RecyclerView.ViewHolder(view) {};
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long start = System.currentTimeMillis();

        Object object = differ.getCurrentList().get(position);

        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder)holder).bind((YearMonth) object);
        }else if(holder instanceof ContentViewHolder){
            ((ContentViewHolder) holder).bind((Record) object);
        }
        Log.d("Perf", "我是在Holder中submitList took " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public int getItemCount() {

        return differ.getCurrentList().size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = differ.getCurrentList().get(position);
        if (item == EMPTY_ITEM) {
            return VIEW_TYPE_EMPTY;
        }
        return item instanceof YearMonth ? VIEW_TYPE_HEADER : VIEW_TYPE_NO_HEADER;
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.year_month);
        }
        public void bind(YearMonth yearMonth){
            String string = yearMonth.getYear()+"年"+yearMonth.getMonthValue()+"月";
            date.setText(string);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{
        TextView note;
        TextView recordTime;
        TextView updateTime;
        TextView amount;
        TextView category;
        TextView type;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.note);
            recordTime = itemView.findViewById(R.id.recordtime);
            updateTime = itemView.findViewById(R.id.updatetime);
            amount = itemView.findViewById(R.id.amount);
            category = itemView.findViewById(R.id.category);
            type = itemView.findViewById(R.id.type);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Record record = (Record) differ.getCurrentList().get(getLayoutPosition());
                    MyApp.getInstance().getShareViewModel().setRecord(record);
                    Intent intent = new Intent(activity, RecordDetailActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        public void bind(Record record) {
            note.setText(record.getNote());
            String rT = DateFormatUtil.toMonthDay(record.getRecord_time());
            recordTime.setText(rT);
            String uT = DateFormatUtil.toYearMonthDay(record.getUpdate_time());
            updateTime.setText(uT);
            category.setText(record.getCategory());
            if(record.getType() == 1){
                amount.setTextColor(ContextCompat.getColor(amount.getContext(), R.color.gold));
                amount.setText(String.valueOf("+"+record.getAmount()));
                type.setText("收入");
            }else if(record.getType() == 0){
                amount.setTextColor(Color.RED);
                amount.setText(String.valueOf("-"+record.getAmount()));
                type.setText("支出");
            }
        }
    }
}
