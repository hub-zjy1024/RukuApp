package com.zjy.north.rukuapp.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.BaseMActivity;
import com.zjy.north.rukuapp.contract.MainContract;

import utils.framwork.ZjyPermissionChecker;

public class MainActivity extends BaseMActivity implements MainContract.MainAcView {
    ZjyPermissionChecker checker;
    public static final String[] nPermissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
         checker = new ZjyPermissionChecker(this);
        checker.requestPermission(nPermissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void login(int code, String name, String pwd, String msg) {

    }

    @Override
    public void showProgress(String msg) {
        loadingNoProcess(msg);
    }

    @Override
    public void getUpdateInfo(String info) {

    }

    @Override
    public void startNewActivity() {

    }

    @Override
    public void setPrinter(MainContract.MainAcPresenter mainAcPresenter) {

    }
}
