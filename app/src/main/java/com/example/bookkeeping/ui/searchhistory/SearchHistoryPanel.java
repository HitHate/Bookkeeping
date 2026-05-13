package com.example.bookkeeping.ui.searchhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.Search;
import com.example.bookkeeping.util.DateFormatUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryPanel extends ConstraintLayout {

    private ValueAnimator currentAnimator;
    private static final long ANIM_DURATION = 300;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private Button clearAllBtn;

    private HistoryAdapter adapter;
    private List<Search> currentData = new ArrayList<>();

    private OnHistoryClickListener historyClickListener;
    private OnDeleteClickListener deleteClickListener;

    private static final int ITEM_HEIGHT_DP = 50;   // 根据实际 item 调整
    private static final int RESERVE_CONTENT_DP = 80;

    public SearchHistoryPanel(Context context) {
        super(context);
        init(context);
    }

    public SearchHistoryPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchHistoryPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 加载布局（使用 test.xml 的内容，文件名你自己调整）
        LayoutInflater.from(context).inflate(R.layout.history_panel_layout, this, true);

        // 绑定视图
        recyclerView = findViewById(R.id.records);
        emptyTextView = findViewById(R.id.emptyHint);
        clearAllBtn = findViewById(R.id.deleteAllBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setClipToPadding(false);
        recyclerView.setVerticalScrollBarEnabled(false);

        clearAllBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) deleteClickListener.onClearAll();
        });

        adapter = new HistoryAdapter(
                keyword -> { if (historyClickListener != null) historyClickListener.onHistoryClick(keyword); },
                keyword -> { if (deleteClickListener != null) deleteClickListener.onDelete(keyword); }
        );
        recyclerView.setAdapter(adapter);

        // 初始隐藏
        setVisibility(GONE);

    }

    // ---------- 公共方法 ----------

    /**
     * 外部传入数据列表，内部会自动切换空状态和列表
     */
    public void setData(List<Search> data) {
        currentData.clear();
        if (data != null) currentData.addAll(data);
        adapter.submitList(new ArrayList<>(currentData));

        boolean isEmpty = currentData.isEmpty();
        recyclerView.setVisibility(isEmpty ? GONE : VISIBLE);
        emptyTextView.setVisibility(isEmpty ? VISIBLE : GONE);

        // 如果当前面板已展开，调整高度以适应新数据
        if (isExpanded()) {
            requestHeightAdjustment();
        }
    }

    /**
     * 展开面板，带动画
     */
    public void expand() {
        if (isAnimating() || getVisibility() == VISIBLE) return;

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = 0;
        setLayoutParams(lp);
        setVisibility(VISIBLE);

        post(() -> {
            int target = calculateTargetHeight();
            if (target <= 0) target = 1;   // 避免动画高度为 0
            animateHeight(0, target);
        });
    }

    /**
     * 收起面板，带动画
     */
    public void collapse() {
        if (isAnimating() || getVisibility() != VISIBLE) return;
        animateHeight(getHeight(), 0, () -> setVisibility(GONE));
    }

    public boolean isExpanded() {
        return getVisibility() == VISIBLE;
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.historyClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // ---------- 动画逻辑 ----------

    private void animateHeight(int from, int to) {
        animateHeight(from, to, null);
    }

    private void animateHeight(int from, int to, @Nullable Runnable onEnd) {
        cancelAnimator();
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.setDuration(ANIM_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            int h = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = h;
            setLayoutParams(params);
        });
        if (onEnd != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) { onEnd.run(); }
            });
        }
        anim.start();
        currentAnimator = anim;
    }

    private void cancelAnimator() {
        if (currentAnimator != null && currentAnimator.isRunning()) currentAnimator.cancel();
    }

    private boolean isAnimating() {
        return currentAnimator != null && currentAnimator.isRunning();
    }

    private void requestHeightAdjustment() {
        // 已展开状态下，数据改变后重新计算高度并平滑过渡
        if (!isExpanded() || isAnimating()) return;
        int target = calculateTargetHeight();
        if (target <= 0) target = 1;
        int current = getHeight();
        if (current == target) return;
        animateHeight(current, target);
    }

    // ---------- 高度计算 ----------

    private int calculateTargetHeight() {
        int headerHeight = measureViewHeight(clearAllBtn);

        int contentHeight;
        if (adapter.getItemCount() > 0) {
            contentHeight = adapter.getItemCount() * dpToPx(ITEM_HEIGHT_DP)
                    + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();
        } else {
            // 重新测量空 TextView 以其实际高度（含 padding）
            emptyTextView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            contentHeight = emptyTextView.getMeasuredHeight();
        }

        int desired = headerHeight + contentHeight;
        int maxAvailable = getMaxAvailableHeight();
        return Math.min(desired, maxAvailable);
    }

    private int measureViewHeight(View view) {
        if (view == null) return 0;
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    private int getMaxAvailableHeight() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = 0;
        statusBarHeight = getRootWindowInsets().getSystemWindowInsetTop();
        int reserve = dpToPx(RESERVE_CONTENT_DP);
        return screenHeight - statusBarHeight - reserve;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ---------- 接口 ----------
    public interface OnHistoryClickListener {
        void onHistoryClick(String keyword);
    }

    public interface OnDeleteClickListener {
        void onDelete(String keyword);
        void onClearAll();
    }

    // ---------- 适配器（不变，但需适配 history_item.xml） ----------
    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        private final List<Search> items = new ArrayList<>();
        private final OnItemClickListener clickListener;
        private final OnItemDeleteListener deleteListener;


        HistoryAdapter(OnItemClickListener click, OnItemDeleteListener del) {
            this.clickListener = click;
            this.deleteListener = del;
        }

        void submitList(List<Search> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Search search = items.get(position);
            holder.textView.setText(search.getKeyword());
            holder.time.setText(DateFormatUtil.toYearMonthDay(search.getSearch_time()));
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onClick(search.getKeyword());
            });
            holder.deleteBtn.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(search.getKeyword());
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            TextView time;
            ImageButton deleteBtn;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.history);
                time = itemView.findViewById(R.id.time);
                deleteBtn = itemView.findViewById(R.id.delete);
            }
        }

        interface OnItemClickListener { void onClick(String keyword); }
        interface OnItemDeleteListener { void onDelete(String keyword); }
    }
}