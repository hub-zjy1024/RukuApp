package utils.framwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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

    public void requestPermission(String[] ps) {
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
        }
    }

    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                showMsgDialog("请重启程序并允许所有授权！！！，" + msg);
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

}
