package com.zjy.north.rukuapp.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.zjy.north.rukuapp.activity.base.SunmiScanActivity;
import com.zjy.north.rukuapp.contract.QuickRukuContract;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;

import java.util.ArrayList;
import java.util.List;

/**
 Created by 张建宇 on 2019/5/9. */
public class PiliangRkActivity extends SunmiScanActivity implements QuickRukuContract.IView, View.OnClickListener {
    private QuickRukuContract.IPresenter mPresenter;

    private List<WaitRukuInfo> mInfos = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    private EditText edCode;

    /**
     Called when a view has been clicked.
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    public void loading(String msg) {

    }

    @Override
    public void cancelLoading() {
        pdDialog.show();
    }

    @Override
    public void updateData(List<WaitRukuInfo> data) {
        pdDialog.show();
    }

    @Override
    public void showAlert(String msg) {

    }

    @Override
    public void onRukuSuccess(String mxId) {

    }

    @Override
    public void onRukuSuccess2(String inputCode, String place) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void onShangjiaDataCallback(List<ShangJiaInfo> infos) {

    }

    /**
     当BaseView为Fragment时，在Activity中初始化Presenter，并传递到Fragment中，
     */
    @Override
    public void setPresenter(QuickRukuContract.IPresenter iPresenter) {

    }

}
