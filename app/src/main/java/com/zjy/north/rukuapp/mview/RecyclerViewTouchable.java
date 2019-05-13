package com.zjy.north.rukuapp.mview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zjy.north.rukuapp.R;

/**
 Created by 张建宇 on 2019/5/9. */
public class RecyclerViewTouchable extends RecyclerView{
    public RecyclerViewTouchable(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewTouchable(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewTouchable(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        ItemTouchHelper helper;
//        helper.attachToRecyclerView(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取当前位置的的ItemView
                ViewGroup childViewUnder = (ViewGroup) findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null) {
                    int childCount = childViewUnder.getChildCount();
                    if (childViewUnder.getId() == R.id.item_shangjia_tv) {
//                        super.dispatchTouchEvent(e);
//                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }
}
