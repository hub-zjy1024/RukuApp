package com.zjy.north.rukuapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import utils.adapter.recyclerview.BaseRvViewholder;

/**
 Created by 张建宇 on 2019/5/8. */
public abstract class RvAdapterWithPoi<T> extends RecyclerView.Adapter<BaseRvViewholder> {
    protected List<T> mData;
    protected int layoutId;
    protected Context mContext;

    public void onBindViewHolder(BaseRvViewholder holder, int position) {
        T tData = mData.get(position);
        holder.position = position;
        convert2(holder, tData, position);
    }

    @Override
    public BaseRvViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        //        View item = LayoutInflater.from(mContext).inflate(layoutId, null);
        View item = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        return new BaseRvViewholder(item);
    }
    /**
     绑定数据
     @param holder {@link utils.adapter.recyclerview.BaseRvViewholder};
     @param item   item数据
     */
    protected abstract void convert2(BaseRvViewholder holder, T item, int poi);

    @Override
    public long getItemId(int position) {
        return mData == null ? 0 : mData.size();
    }

}
