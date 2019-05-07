package com.zjy.north.rukuapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SunmiScanActivity;
import com.zjy.north.rukuapp.contract.QuickRukuContract;
import com.zjy.north.rukuapp.entity.RKInfo;

import java.util.ArrayList;
import java.util.List;

import utils.MyDecoration;
import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;

public class QuickRukuActivity extends SunmiScanActivity implements QuickRukuContract.IView,View.OnClickListener {
    private QuickRukuContract.IPresenter mPresenter;

    private List<RKInfo> mInfos = new ArrayList<>();
    private QrkAdapter mAdapter;
    private EditText edCode;

    @Override

    public void init() {
        super.init();
        Toolbar tb = getViewInContent(R.id.dyjkf_normalTb);
        tb.setTitle("快速入库");
        tb.setSubtitle("");
        setSupportActionBar(tb);
        mPresenter = new QuickRukuContract.Presenter(this, this);
        edCode =  getViewInContent(R.id.activity_rk_ed_code);
        RecyclerView rv = (RecyclerView) getViewInContent(R.id.activity_rk_recview);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.addItemDecoration(new MyDecoration(this));
        mAdapter = new QrkAdapter(mInfos, R.layout.item_shangjia, this);
        rv.setAdapter(mAdapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_rk_btn_scan:
                break;
            case R.id.activity_rk_btn_search:
                mInfos.clear();
                mAdapter.notifyDataSetChanged();
                String code = edCode.getText().toString();
                if (code.equals("")) {
                    showMsgToast("请先输入查询号码");
                    return;
                }
                mPresenter.getData(code);
                break;
        }

    }
    @Override
    public void setListeners() {
        super.setListeners();
        setOnClickListener(this, R.id.activity_rk_btn_scan);
        setOnClickListener(this, R.id.activity_rk_btn_search);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_ruku);
    }

    static class QrkAdapter extends BaseRvAdapter<RKInfo> {
        public QrkAdapter(List<RKInfo> mData, int layoutId, Context mContext) {
            super(mData, layoutId, mContext);
        }

        @Override
        protected void convert(BaseRvViewholder holder, RKInfo item) {

        }
    }

    @Override
    public void loading(String msg) {
        loadingNoProcess(msg);
    }

    @Override
    public void cancelLoading() {
        pdDialog.cancel();
    }

    @Override
    public void updateData(List<RKInfo> data) {
        mInfos.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlert(String msg) {
        showMsgDialog(msg);
    }

    @Override
    public void setPrinter(QuickRukuContract.IPresenter iPresenter) {

    }
}
