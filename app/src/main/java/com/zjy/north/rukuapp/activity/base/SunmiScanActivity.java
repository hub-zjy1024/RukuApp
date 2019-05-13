package com.zjy.north.rukuapp.activity.base;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.dev.BarcodeAPI;
import com.sunmi.scanner.ScanController;

/**
 Created by 张建宇 on 2019/5/5. */
public class SunmiScanActivity extends SavedLoginInfoWithScanActivity implements ScanController.ScanListener {
    private ScanController sunmiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        super.init();
        String brand = Build.BRAND;
        scanTool = BarcodeAPI.getInstance();
        if (brand.contains("SUNMI")) {
            hasScanBtn = true;
        }
        sunmiController = new ScanController(mContext, this);
    }

    public boolean isSunmiScan() {
        String brand = Build.BRAND;
        if (brand.contains("SUNMI")) {
            return true;
        }
        return false;
    }
    @Override
    public void onScanResult(String code) {
        Log.e("zjy", "com.zjy.north.rukuapp.activity.base.SunmiScanActivity->onScanResult(): ==" + code);
    }

    public void sunmiScan(ScanController.ScanListener listener) {
        sunmiController.setmListener(listener);
        try {
            sunmiController.scan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sunmiScan(){
        sunmiController.setmListener(this);
        try {
            sunmiController.scan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sunmiController.release();
    }
}
