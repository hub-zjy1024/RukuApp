package utils.framwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.zjy.north.rukuapp.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 Created by 张建宇 on 2019/5/5. */
public class ZjyPermissionChecker {
    static final int reqPermissionCode = 100;
    private Activity mAc;

    public ZjyPermissionChecker(Activity mAc) {
        this.mAc = mAc;
    }

    public boolean requestPermission(String[] ps) {
        List<String> arrlist = new ArrayList<>();
        for (String permission : ps) {
            int isGranted = ContextCompat.checkSelfPermission(mAc, permission);
            if (isGranted == PackageManager.PERMISSION_DENIED) {
                arrlist.add(permission);
            }
        }
        String[] notGranted = arrlist.toArray(new String[arrlist.size()]);
        if (notGranted.length > 0) {
            ActivityCompat.requestPermissions(mAc, notGranted, reqPermissionCode);
        }else{
            return true;
        }
        return false;
    }

    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws
            Exception {
        String msg = "";
        if (requestCode == reqPermissionCode) {
            for (int i = 0; i < grantResults.length; i++) {
                int code = grantResults[i];
                if (code == PackageManager.PERMISSION_DENIED) {
                    String name = permissions[i];
                    msg = "缺少权限，" + name;
                }
            }
            if (!"".equals(msg)) {
                throw new Exception("请重启程序并允许所有授权！！！，" + msg);
            }
        }
    }

    private void showMsgDialog(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mAc);
        builder.setTitle("警告");
        builder.setMessage(errMsg);
        builder.setCancelable(true);
        builder.create().show();
    }


    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", mAc.getPackageName());
            mAc.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", mAc.getPackageName());
                mAc.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                mAc.startActivity(getAppDetailSettingIntent());
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            mAc.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            mAc.startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            mAc.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            mAc.startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String pkName = mAc.getPackageName();
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",pkName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",pkName);
        }
        return localIntent;
    }
}
