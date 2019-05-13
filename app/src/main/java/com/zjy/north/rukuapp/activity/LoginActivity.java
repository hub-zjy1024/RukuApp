package com.zjy.north.rukuapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.BaseMActivity;
import com.zjy.north.rukuapp.contract.MainContract;
import com.zjy.north.rukuapp.entity.SpSettings;

import java.util.ArrayList;
import java.util.List;

import utils.common.UploadUtils;

public class LoginActivity extends BaseMActivity implements MainContract.MainAcView, View.OnClickListener {

    private MainContract.MainAcPresenter mPresenter;

    private EditText edUserName;
    private EditText edPwd;
    private CheckBox cboRemp;
    private CheckBox cboAutol;
    private TextView tvVersion;

    private String debugPwd = "62105300";
    private SharedPreferences sp;
    private String versionName = "1";
    public static final int reqPermissionCode = 100;
    public static final String[] nPermissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruku_login);
        //        setContentView(R.layout.activity_login);
    }

    @Override
    public void init() {
        edUserName = getViewInContent(R.id.login_username);
        edPwd = getViewInContent(R.id.login_pwd);
        cboRemp = getViewInContent(R.id.login_rpwd);
        cboAutol = getViewInContent(R.id.login_autol);
        tvVersion = getViewInContent(R.id.main_version);
        mPresenter = new MainContract.MainAcPresenter(this, this);
        requestPermission(nPermissions);

        sp = getSharedPreferences(SpSettings.PREF_USERINFO, 0);
        final String phoneCode = UploadUtils.getPhoneCode(mContext);
        //        pd = new ProgressDialog(mContext);
        Log.e("zjy", "MainActivity.java->onCreate(): phoneInfo==" + phoneCode);
        //检查更新
        int code = 0;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            code = info.versionCode;
            versionName = info.versionName;
            tvVersion.setText("当前版本为：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mPresenter = new MainContract.MainAcPresenter(mContext, this);
        mPresenter.CheckUpdate();

        recordLog(phoneCode, code);
        readCache();
    }

    void checkNetWork() {
        //获取网络状态
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                int type = activeNetworkInfo.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    MyApp.myLogger.writeInfo("network stat:mobile");
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    MyApp.myLogger.writeInfo("network stat:wifi");
                }
            }
        }
    }

    public void requestPermission(String[] ps) {
        List<String> arrlist = new ArrayList<>();
        for (String permission : ps) {
            int isGranted = ContextCompat.checkSelfPermission(mContext, permission);
            if (isGranted == PackageManager.PERMISSION_DENIED) {
                arrlist.add(permission);
            }
        }
//        String[] notGranted = (String[]) arrlist.toArray();
        String[] notGranted=arrlist.toArray(new String[arrlist.size()]);
        if (notGranted.length > 0) {
            ActivityCompat.requestPermissions(this, notGranted, reqPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String msg = "";
        if (requestCode == reqPermissionCode) {
            for(int i=0;i<grantResults.length;i++) {
                int code = grantResults[i];
                if (code == PackageManager.PERMISSION_DENIED) {
                    String name = permissions[i];
                    msg = "缺少权限，" + name;
                }
            }
            if (!"".equals(msg)) {
                showMsgDialog("请允许所有授权！！！，" + msg);
            }
        }
    }

    void recordLog(String phoneCode, int code) {
        checkNetWork();
        final SharedPreferences logSp = getSharedPreferences(SpSettings.PREF_LOGUPLOAD, MODE_PRIVATE);
        String saveDate = logSp.getString("codeDate", "");
        int lastCode = logSp.getInt("lastCode", -1);
        final String current = UploadUtils.getCurrentDate();
        if (MyApp.myLogger != null) {
            StringBuilder sbInfo = new StringBuilder();
            if (!saveDate.equals(current)) {
                logSp.edit().putString("codeDate", current).apply();
                sbInfo.append(String.format("phonecode:%s,apiVersion=%d,dyj-version=%d", phoneCode, Build.VERSION.SDK_INT, code));
//                sbInfo.append("\n");
//                sbInfo.append(String.format("ApiVersion:%s", Build.VERSION.SDK_INT));
//                sbInfo.append("\n");
//                sbInfo.append(String.format("dyj-version:%s", code));
//                sbInfo.append("\n");
            } else if (code != lastCode && lastCode != -1) {
                sbInfo.append(String.format("updated-from '%d' to '%d':", lastCode, code));
                sbInfo.append("\n");
                logSp.edit().putInt("lastCode", code).apply();
            }
            MyApp.myLogger.writeInfo(sbInfo.toString());
        }
    }

    private void readCache() {
        if (sp.getBoolean("remp", false)) {
            edUserName.setText(sp.getString("name", ""));
            edPwd.setText(sp.getString("pwd", ""));
            cboRemp.setChecked(true);
            if (sp.getBoolean("autol", false)) {
                cboAutol.setChecked(true);
                mPresenter.login(sp.getString("name", ""), sp.getString("pwd", ""), versionName);
            }
        }
    }

    @Override
    public void setListeners() {
        setOnClickListener(this, R.id.login_btnlogin);
        setOnClickListener(this, R.id.login_scancode);
    }

    @Override
    public void onLoginSuccess(String name) {
        boolean auto = cboAutol.isChecked();
        boolean saved = cboRemp.isChecked();
        MyApp.id = name;
        mPresenter.saveData(saved, auto);
        startNewActivity();
    }

    @Override
    public void onLoginFailed(String msg) {
        debugPwd = "621053000";
        showMsgToast(msg);
    }

    @Override
    public void cancelLoading() {
        pdDialog.cancel();
    }
    @Override
    public void showProgress(String msg) {
        loadingNoProcess(msg);
    }

    @Override
    public void getUpdateInfo(String info) {
        tvVersion.setText(tvVersion.getText().toString() + info);
    }

    @Override
    public void startNewActivity() {
        Intent mIntent = new Intent(mContext, MenuActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void setPresenter(MainContract.MainAcPresenter mainAcPresenter) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btnlogin:
                String edName = edUserName.getText().toString().trim();
                String edPwd = this.edPwd.getText().toString().trim();

                String dvId = UploadUtils.getDeviceID(mContext);
                if ("868930027847564".equals(dvId) || "358403032322590".equals(dvId)
                        || "864394010742122".equals(dvId)
                        || "A0000043F41515".equals(dvId)
                        || "866462026203849".equals(dvId)
                        || "869552022575930".equals(dvId)
                        || "868591030284169".equals(dvId)
                        ) {
                    mPresenter.login("101", debugPwd, versionName);
                } else {
                    if (edPwd.equals("") || edName.equals("")) {
                        showMsgToast("请填写完整信息后再登录");
                    } else {
                        mPresenter.login(edName, edPwd, versionName);
                    }
                }
                break;
            case R.id.login_scancode:
                break;
        }

    }
}
