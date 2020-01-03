package com.zjy.north.rukuapp.adapter;

import android.content.Context;
import android.view.View;

import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.entity.entity.XiaopiaoInfo;

import java.util.List;

import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;

/**
 * Created by 张建宇 on 2019/11/18.
 */
public class KuCunEditAdapter extends BaseRvAdapter<XiaopiaoInfo> {

    public KuCunEditAdapter(List<XiaopiaoInfo> mData, int layoutId, Context mContext,
                            OnItemClickListener mListener) {
        super(mData, layoutId, mContext);
        this.mListener = mListener;
    }

    public interface OnItemClickListener {
        void onClick(XiaopiaoInfo item);
    }

    private OnItemClickListener mListener;
    @Override
    protected void convert(BaseRvViewholder holder, final XiaopiaoInfo item) {
        holder.setText(R.id.item_kucun_edit_list_tv_info, item.toString());
        holder.setOnclick(R.id.item_kucun_edit_list_btn_modify, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(item);
                }
            }
        });
    }
}
