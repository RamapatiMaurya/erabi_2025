package com.school.erabi.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }


    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.mListener = listener;
        this.mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView == null || mListener == null || !this.mGestureDetector.onTouchEvent(e)) {
            return false;
        }
        mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        return false;
    }

    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
