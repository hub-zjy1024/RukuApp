package com.zjy.north.rukuapp.activity.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import utils.framwork.DialogUtils;
import utils.framwork.MyToast;

/**
 Created by 张建宇 on 2019/3/20. */
public abstract class BaseMActivity extends AppCompatActivity {
    protected Context mContext;
    protected ProgressDialog pdDialog;

    private boolean isPaused = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

    }

    protected void loadingNoProcess(String msg) {
        pdDialog.setTitle("加载中。。。");
        pdDialog.setMessage(msg);
        String s;
        pdDialog.show();
//        if (!isPaused) {
//            pdDialog.show();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        pdDialog = new ProgressDialog(mContext);
        init();
        setListeners();
    }

    public <T extends View> T getViewInContent(@IdRes int resId) {
        return (T) findViewById(resId);
    }


    public abstract void init();

    public abstract void setListeners();

    public void setOnClickListener(View.OnClickListener listener, @IdRes int id) {
        View v = getViewInContent(id);
        v.setOnClickListener(listener);
    }

    public void showMsgDialog(String msg) {
        showMsgDialog(msg, "提示");
    }

    public void showMsgToast(String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            MyToast.showToast(mContext, msg);
        } else {
            final String fMSg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyToast.showToast(mContext, fMSg);
                }
            });
        }
    }

    public final void openActivity(Class ac) {
        Intent mIntent = new Intent(this, ac);
        startActivity(mIntent);
    }

    public void showMsgDialog(final String msg, final String title) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            DialogUtils.getSpAlert(mContext, msg, title).show();
        } else {
            final String fMSg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.getSpAlert(mContext, fMSg, title).show();
                }
            });
        }
    }
}
