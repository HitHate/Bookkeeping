package com.example.bookkeeping.ui.pay;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookkeeping.R;
import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.databinding.FragmentPayBinding;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.ui.insertOrupdate.InsertOrUpdateActivity;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.ui.Adapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import com.yanzhenjie.recyclerview.widget.StickyNestedScrollView;

import java.util.List;

public class PayFragment extends Fragment {

    private PayViewModel mViewModel;

    private FragmentPayBinding binding;
    private RecordRepositoryImpl repository;
    private PayViewModelFactory factory;
    private SwipeRecyclerView recyclerView;

    private StickyNestedScrollView scrollView;

    private Adapter adapter;
    private List<Object> recordList;

    public static PayFragment newInstance() {
        return new PayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPayBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = RecordRepositoryImpl.getInstance(((MyApp)(requireActivity().getApplication())).getDatabase());
        factory = new PayViewModelFactory(repository);
        adapter = new Adapter();

        mViewModel = new ViewModelProvider(requireActivity(),factory).get(PayViewModel.class);

        recyclerView = binding.showPay;
        scrollView = binding.scrollView;


        mViewModel.getFlatRecords().observe(getViewLifecycleOwner(),records -> {
            recordList = records;
            adapter.submitData(records,requireActivity());
        });


        scrollView.setNestedScrollingEnabled(false);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(requireActivity(), R.color.Gainsboro)));
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
                SwipeMenuItem editItem = new SwipeMenuItem(requireContext())
                        .setText("编辑")
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height)
                        .setTextColor(Color.WHITE)
                        .setBackgroundColor(Color.rgb(135, 206, 250));
                rightMenu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(requireContext())
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
                Intent intent = new Intent(requireActivity(), InsertOrUpdateActivity.class);
                intent.putExtra("type","edit");
                MyApp.getInstance().getShareViewModel().setRecord(record);
                requireActivity().startActivity(intent);
            }else {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(requireActivity());
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