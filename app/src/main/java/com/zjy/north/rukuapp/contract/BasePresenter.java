package com.zjy.north.rukuapp.contract;

import android.content.Context;

/**
 Created by 张建宇 on 2019/4/29. */
public abstract class BasePresenter<T> {
    public BasePresenter(Context mContext) {
        this.mContext = mContext;
    }

    protected Context mContext;
    public abstract T getProvider();
}
