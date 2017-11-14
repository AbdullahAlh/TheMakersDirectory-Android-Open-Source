package com.themakersdirectory.UI;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xlethal on 7/25/16.
 */

public class InteractiveRecyclerView extends RecyclerView {
    OnBottomReachedListener mListener;

    public InteractiveRecyclerView(Context context) {
        super(context);
    }

    public InteractiveRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveRecyclerView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getChildAt(getChildCount() - 1);
        if (view != null) {
            int diff = (view.getBottom() - (getHeight() + getScrollY()));

            if (diff == 0 && mListener != null) {
                mListener.onBottomReached();
            }
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    // Getters & Setters
    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }


    /**
     * Event listener.
     */
    public interface OnBottomReachedListener {
        public void onBottomReached();
    }

}
