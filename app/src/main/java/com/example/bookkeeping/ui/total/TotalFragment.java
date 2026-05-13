package com.example.bookkeeping.ui.total;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookkeeping.R;

import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.databinding.FragmentTotalBinding;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;

public class TotalFragment extends Fragment {

    private TotalViewModel mViewModel;

    private FragmentTotalBinding binding;

    private TotalViewModelFactory modelFactory;

    private RecordRepositoryImpl repository;

    private TextView get;
    private TextView pay;
    private TextView netIncome;
    public static TotalFragment newInstance() {
        return new TotalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTotalBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = RecordRepositoryImpl.getInstance(((MyApp)(requireActivity().getApplication())).getDatabase());
        modelFactory = new TotalViewModelFactory(repository);
        mViewModel = new ViewModelProvider(this,modelFactory).get(TotalViewModel.class);

        get = binding.get;
        pay = binding.pay;
        netIncome = binding.netincome;

        mViewModel.getGet().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double value) {
                if (value == null) {
                    get.setText("0");
                } else {
                    get.setText(String.valueOf(value));
                }
            }
        });

        mViewModel.getPay().observe(getViewLifecycleOwner(), new Observer<Double>(){
            @Override
            public void onChanged(Double value) {
                if (value == null) {
                    pay.setText("0");
                } else {
                    pay.setText(String.valueOf(value));
                }
            }
        });

        mViewModel.getNetIncome().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                if (aDouble == null) {
                    netIncome.setText("0");
                } else {
                    if(aDouble >= 0){
                        netIncome.setTextColor(Color.GREEN);
                        netIncome.setText(String.valueOf(aDouble));
                    }else {
                        netIncome.setTextColor(Color.RED);
                        netIncome.setText(String.valueOf(aDouble));
                    }
                }

            }
        });
    }
}