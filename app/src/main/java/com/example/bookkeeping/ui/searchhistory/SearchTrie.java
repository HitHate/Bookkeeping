package com.example.bookkeeping.ui.searchhistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchTrie {
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        //用 Set 存储多个原文，避免覆盖 + 自动去重
        Set<String> fullTexts = new LinkedHashSet<>();
    }

    /**
     * 用历史列表重建整个 Trie
     */
    public void buildFromList(List<String> historyList) {
        root.children.clear();
        for (String word : historyList) {
            if (word != null && !word.isEmpty()) {
                insertWithAllSuffixes(word);
            }
        }
    }

    /**
     * 插入单条记录（包括所有后缀）
     */
    public void insertWithAllSuffixes(String originalText) {
        if (originalText == null || originalText.isEmpty()) return;
        for (int start = 0; start < originalText.length(); start++) {
            String suffix = originalText.substring(start);
            insertSuffix(suffix, originalText);
        }
    }

    private void insertSuffix(String suffix, String fullText) {
        TrieNode node = root;
        for (char c : suffix.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.fullTexts.add(fullText);   // 加入集合，不会有覆盖问题
    }

    /**
     * 查询前缀（实际是任意位置子串匹配）
     *
     * @param prefix 用户输入
     */
    public List<String> search(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;

        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return result;
        }
        collect(node, result);
        return result;
    }

    private void collect(TrieNode node, List<String> result) {
        if (node == null) return;
        if (node.isEnd) {
            result.addAll(node.fullTexts);
        }
        for (TrieNode child : node.children.values()) {
            collect(child, result);
        }
    }
}