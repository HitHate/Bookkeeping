package com.example.bookkeeping;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bookkeeping.entity.Search;
import com.example.bookkeeping.service.impl.SearchRepositoryImpl;
import com.example.bookkeeping.ui.searchhistory.SearchTrie;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final SearchRepositoryImpl repository;
    private final SearchTrie trie = new SearchTrie();
    private LiveData<List<Search>> history;
    private List<Search> cachedHistoryList = new ArrayList<>();
    private List<String> keywordList = new ArrayList<>();
    public final MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Search>> suggestionsLiveData = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final long DEBOUNCE_DELAY = 200;
    private final Observer<List<Search>> historyObserver = this::updateTrie;
    private final Observer<String> queryObserver = input -> {
        if (debounceRunnable != null) handler.removeCallbacks(debounceRunnable);
        debounceRunnable = () -> {
            if (input == null || input.trim().isEmpty()) {
                suggestionsLiveData.postValue(cachedHistoryList);
            } else {
                suggestionsLiveData.postValue(getHistory(input.trim()));
            }
        };
        handler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);
    };

    public MainViewModel(SearchRepositoryImpl repository) {
        this.repository = repository;
        history = repository.findAll();
        history.observeForever(historyObserver);
        queryLiveData.observeForever(queryObserver);
    }

    private void updateTrie(List<Search> historyList) {
        this.cachedHistoryList = historyList;
        keywordList = new ArrayList<>();
        for (Search h : historyList) {
            keywordList.add(h.getKeyword());
        }
        trie.buildFromList(keywordList);

        String currentQuery = queryLiveData.getValue();
        if (currentQuery == null || currentQuery.trim().isEmpty()) {
            suggestionsLiveData.postValue(cachedHistoryList);
        }
    }

    private List<Search> getHistory(String query) {
        List<String> matchedKeywords = trie.search(query);
        List<Search> result = new ArrayList<>();
        for (Search h : cachedHistoryList) {
            if (matchedKeywords.contains(h.getKeyword())) {
                result.add(h);
            }
        }
        return result;
    }

    public LiveData<List<Search>> getSuggestions() {
        return suggestionsLiveData;
    }

    public void onQueryChanged(String newText) {
        queryLiveData.setValue(newText);
    }

    public void saveSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        if (keywordList.contains(keyword)) {
            repository.update(keyword);
        } else {
            repository.insert(keyword);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        history.removeObserver(historyObserver);
        queryLiveData.removeObserver(queryObserver);
        if (debounceRunnable != null) handler.removeCallbacks(debounceRunnable);
    }
}
