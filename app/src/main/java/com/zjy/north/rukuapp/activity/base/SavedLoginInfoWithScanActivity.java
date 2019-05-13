package com.zjy.north.rukuapp.activity.base;

import android.os.Bundle;
import android.util.Log;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.zxing.activity.BaseScanActivity;

import utils.handler.NoLeakHandler;

/**
 Created by 张建宇 on 2019/5/5. */
public class SavedLoginInfoWithScanActivity extends BaseScanActivity implements NoLeakHandler.NoLeakCallback {
    protected String loginID = MyApp.id;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("uid", loginID);
            outState.putString("ftpUrl", MyApp.ftpUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            loginID = savedInstanceState.getString("uid");
            MyApp.id = loginID;
            MyApp.ftpUrl = savedInstanceState.getString("ftpUrl");
        }
        Log.e("zjy",getClass().getName()+"->getSavedId->onCreate(): nowID==" + loginID);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void setListeners() {

    }
}
