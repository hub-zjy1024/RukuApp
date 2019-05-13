package com.zjy.north.rukuapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.ToobarAc;

import utils.framwork.ZjyPermissionChecker;

public class MainActivity extends ToobarAc {
    ZjyPermissionChecker checker;
    public static final String[] nPermissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        super.init();
        mToobar.setTitle("权限检查");
        checker = new ZjyPermissionChecker(this);
        boolean b = checker.requestPermission(nPermissions);
        if (b) {
            entryMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            checker.onRequestPermissionsResult(requestCode, permissions, grantResults);
            entryMain();
        } catch (Exception e) {
            showMsgDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    void entryMain() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        CharSequence title = menuItem.getTitle();
        String strTitle = title.toString();
        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                showMsgToast("点击了-" + strTitle);
                break;
            case R.id.action_share:
                showMsgToast("点击了-" + strTitle);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public String setTitle() {
        return "这是测试Toolbar";
    }

}
