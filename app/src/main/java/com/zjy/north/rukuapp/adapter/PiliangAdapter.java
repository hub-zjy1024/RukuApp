package com.zjy.north.rukuapp.adapter;

import android.content.Context;

import com.zjy.north.rukuapp.entity.WaitRukuInfo;

import java.util.List;

import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;

/**
 Created by 张建宇 on 2019/5/9. */
public class PiliangAdapter  extends BaseRvAdapter<WaitRukuInfo> {

    public PiliangAdapter(List<WaitRukuInfo> mData, int layoutId, Context mContext) {
        super(mData, layoutId, mContext);
    }

    /**
     绑定数据
     @param holder {@link BaseRvViewholder};
     @param item   item数据
     */
    @Override
    protected void convert(BaseRvViewholder holder, WaitRukuInfo item) {

    }
}
