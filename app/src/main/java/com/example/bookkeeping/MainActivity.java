package com.example.bookkeeping;

import android.content.Intent;
import android.os.Bundle;


import com.example.bookkeeping.application.MyApp;
import com.example.bookkeeping.database.AppDatabase;

import com.example.bookkeeping.ui.insertOrupdate.InsertOrUpdateActivity;
import com.example.bookkeeping.service.impl.RecordRepositoryImpl;
import com.example.bookkeeping.service.impl.SearchRepositoryImpl;
import com.example.bookkeeping.ui.searchhistory.SearchHistoryPanel;
import com.example.bookkeeping.ui.searchresult.ResultActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.AppLaunchChecker;
import androidx.drawerlayout.widget.DrawerLayout;


import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bookkeeping.databinding.ActivityMainBinding;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private View dimOverlay;
    private SearchHistoryPanel historyPanel;
    private SearchView searchView;
    private AppCompatSpinner spinner;
    private MainViewModel mainViewModel;
    private MainViewModelFactory modelFactory;
    private SearchRepositoryImpl repository;
    private String[] items = {"全部","收入","支出"};
    private String select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppLaunchChecker.onActivityCreate(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarActivityMain.toolbar);
        NavigationView navigationView = binding.navView;

        navigationView.setItemIconTintList(null);

        dimOverlay = binding.appBarActivityMain.dimOverlay;
        searchView = binding.appBarActivityMain.toolbar.findViewById(R.id.search);

        repository = SearchRepositoryImpl.getInstance((MyApp.getInstance()).getDatabase());
        modelFactory = new MainViewModelFactory(repository);
        mainViewModel = new ViewModelProvider(this,modelFactory).get(MainViewModel.class);

        mainViewModel.getSuggestions().observe(this, suggestions -> {
            historyPanel.setData(suggestions);
        });;

        historyPanel = binding.appBarActivityMain.history.historyPanel;

        // 点击遮罩关闭面板
        dimOverlay.setOnClickListener(v -> {
            collapseHistoryPanel();
            searchView.clearFocus();
        });


        historyPanel.setOnHistoryClickListener(keyword -> {
            searchView.setQuery(keyword,false);
        });

        historyPanel.setOnDeleteClickListener(new SearchHistoryPanel.OnDeleteClickListener() {
            @Override public void onDelete(String keyword) {
                repository.delete(keyword);
                searchView.setQuery("",false);
            }
            @Override public void onClearAll() {
                repository.deleteAll();
            }
        });



        drawer = binding.drawerLayout;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.TotalPayGet,R.id.Total,R.id.Get,R.id.Pay).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        binding.appBarActivityMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InsertOrUpdateActivity.class);
                intent.putExtra("type","insert");
                startActivity(intent);
            }
        });
    }

    public void expandHistoryPanel() {
        historyPanel.expand();
        dimOverlay.setVisibility(View.VISIBLE);
    }

    // 收起时隐藏遮罩
    public void collapseHistoryPanel() {
        historyPanel.collapse();
        dimOverlay.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initSearchView(menu);
        initSpinner(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.select) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initSearchView(Menu menu){
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // 先设置数据（含可见性切换）// 再展开面板
                    expandHistoryPanel();
                } else {
                    collapseHistoryPanel();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    mainViewModel.saveSearch(query);
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("type",select);
                    intent.putExtra("keyword",query);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.onQueryChanged(newText);
                return true;
            }
        });
    }

    public void initSpinner(Menu menu){
        spinner = (AppCompatSpinner)menu.findItem(R.id.select).getActionView();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_toolbar,
                items
        );
        // 设置下拉列表的布局（可使用系统布局或自定义）
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setBackground(null);

        spinner.setPadding(0, 0, 0, 0);

            // 4. 调整下拉框垂直偏移，避免遮住 Toolbar
        int toolbarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height); // 需事先定义
        spinner.setDropDownVerticalOffset(toolbarHeight);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 未选择时的处理
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}