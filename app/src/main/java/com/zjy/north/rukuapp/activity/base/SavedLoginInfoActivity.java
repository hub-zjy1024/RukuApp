package com.zjy.north.rukuapp.activity.base;

import android.os.Bundle;
import android.util.Log;

import com.zjy.north.rukuapp.MyApp;

/**
 Created by 张建宇 on 2019/4/29. */
public class SavedLoginInfoActivity  extends BaseMActivity {
    protected String loginID = MyApp.id;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("uid", loginID);
            outState.putString("ftpUrl", MyApp.ftpUrl);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_saved_login_info);
        if (savedInstanceState != null) {
            loginID = savedInstanceState.getString("uid");
            MyApp.id = loginID;
            MyApp.ftpUrl = savedInstanceState.getString("ftpUrl");
        }
        Log.e("zjy", getClass().getName() + "->supper->onCreate(): nowID==" + loginID);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }
}
