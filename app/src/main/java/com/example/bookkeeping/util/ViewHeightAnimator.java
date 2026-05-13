package com.example.bookkeeping.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

public class ViewHeightAnimator {

    private ValueAnimator currentAnimator;
    private static final long DEFAULT_DURATION = 300;

    public void expand(View view, int targetHeight) {
        expand(view, targetHeight, DEFAULT_DURATION);
    }

    public void expand(View view, int targetHeight, long duration) {
        cancelRunning();
        view.setVisibility(View.VISIBLE);
        currentAnimator = ValueAnimator.ofInt(0, targetHeight);
        currentAnimator.setDuration(duration);
        currentAnimator.setInterpolator(new DecelerateInterpolator());
        currentAnimator.addUpdateListener(animation -> {
            int h = (int) animation.getAnimatedValue();
            setHeight(view, h);
        });
        currentAnimator.start();
    }

    public void collapse(View view) {
        collapse(view, DEFAULT_DURATION);
    }

    public void collapse(View view, long duration) {
        cancelRunning();
        int startHeight = view.getHeight();
        currentAnimator = ValueAnimator.ofInt(startHeight, 0);
        currentAnimator.setDuration(duration);
        currentAnimator.setInterpolator(new DecelerateInterpolator());
        currentAnimator.addUpdateListener(animation -> {
            int h = (int) animation.getAnimatedValue();
            setHeight(view, h);
        });
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                setHeight(view, 0);
            }
        });
        currentAnimator.start();
    }

    public void cancelRunning() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }
    }

    public boolean isAnimating() {
        return currentAnimator != null && currentAnimator.isRunning();
    }

    private void setHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }
}