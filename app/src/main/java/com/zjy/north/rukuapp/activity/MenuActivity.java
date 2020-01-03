package com.zjy.north.rukuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zjy.north.baidulocation.BaiduLocation;
import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SavedLoginInfoActivity;
import com.zjy.north.rukuapp.adapter.MenuActivityRvAdapter;
import com.zjy.north.rukuapp.adapter.MenuGvAdapter;
import com.zjy.north.rukuapp.entity.IntentKeys;
import com.zjy.north.rukuapp.entity.MyMenuItem;
import com.zjy.north.rukuapp.task.CheckUtils;

import java.util.ArrayList;

import utils.adapter.recyclerview.BaseItemClickListener;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.btprint.SPrinter;
import utils.common.log.LogUploader;
import utils.framwork.DialogUtils;

public class MenuActivity extends SavedLoginInfoActivity implements OnItemClickListener {
    private final String tag_Ruku = "库存标签";
    private final String tag_Print = "运单打印";
    private final String tag_Kaoqin = "考勤";
    private final String tag_Chukudan = "出库单";
    private final String tag_ChukudanPrint = "出库单打印";
    private final String tag_Viewpic = "单据图片";
    private final String tag_Panku = "盘库";
    private final String tag_CaigouTakePic = "采购拍照";
    private final String tag_ChukuCheck = "出库拍照";
    private final String tag_Admin = "特殊";
    private final String tag_Setting = "设置";
    private final String tag_shangjia = "报关数据上架";
    private final String tag_rukuShangjia = "一键上架";
    private final String tag_SHQD = "送货清单";

    private final String tag_Zbar = "TestZbar";
    private final String tag_SlideBack = "SlidebackAc";
    private final String tag_TestReupload = "图片重传";

    private GridView gv;
    private final String tag_hetong = "hetong";
    private final String tag_about = "关于";
    private final String tag_kucun_mgr = "库存管理";

    private final String tag_quickRuku = "报关数据入库";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
    }

    @Override
    public void init() {
        super.init();
        Toolbar tb = getViewInContent(R.id.dyjkf_normalTb);
        tb.setTitle("菜单");
        tb.setSubtitle("登陆人:" + MyApp.id);
        setSupportActionBar(tb);

//        gv =getViewInContent(R.id.menu_gv);
//        gv.setOnItemClickListener(this);
        addItemGV();
        final Toolbar fTb = tb ;
        BaiduLocation bloc = new BaiduLocation(this, new BaiduLocation.onReceiveLocationListener() {
            @Override
            public void onReceiveLocation(BaiduLocation.Mlocation location) {
                fTb.setSubtitle(fTb.getSubtitle() + "\t" + location.city + location.district);
            }
        });
        bloc.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按下BACK，同时没有重复
            int size = (int) MyApp.cachedThreadPool.getActiveCount() - 1;
            if (size > 0) {
                DialogUtils.getSpAlert(this, "后台还有" + size +
                        "张图片未上传完成，强制退出将导致图片上传失败", "提示").show();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void addItemGV() {
        ArrayList<MyMenuItem> data = new ArrayList<>();

        data.add(new MyMenuItem(R.drawable.menu_ruku, tag_quickRuku, "入库"));
        data.add(new MyMenuItem(R.drawable.menu_shangjia , tag_shangjia, "上架"));
        data.add(new MyMenuItem(R.drawable.menu_shangjia , tag_rukuShangjia, "上架"));
        data.add(new MyMenuItem(R.drawable.menu_kucun_edit, tag_kucun_mgr, "关于"));
        data.add(new MyMenuItem(R.drawable.menu_chuku, tag_about, "关于"));
//        data.add(new MyMenuItem(R.drawable.menu_chuku, tag_Admin, "关于"));

        if (CheckUtils.isAdmin()) {
            data.add(new MyMenuItem(R.drawable.menu_chuku, tag_Admin, "101"));
        }
        MenuGvAdapter adapter = new MenuGvAdapter(this, data, R.layout.item_menu_gv);
        RecyclerView mView = getViewInContent(R.id.activity_menu_dataview);
        MenuActivityRvAdapter adap = new MenuActivityRvAdapter(data, R.layout.item_menu_gv, mContext, new BaseItemClickListener<MyMenuItem>() {
            @Override
            public void onItemClick(BaseRvViewholder holder, MyMenuItem item) {
                MenuActivity.this.onItemClick(item);
            }
        });
        GridLayoutManager gridLayoutMgr = new GridLayoutManager(mContext, 3);
        mView.setLayoutManager(gridLayoutMgr );
        mView.setAdapter(adap);
//        gv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            startService(new Intent(this, LogUploadService.class));
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
        LogUploader uploader = new LogUploader(this);
        uploader.ScheduelUpload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPrinter printer = SPrinter.getPrinter();
        if (printer != null) {
            printer.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyMenuItem data = (MyMenuItem) parent.getItemAtPosition(position);
        onItemClick(data);
    }
    void onItemClick(MyMenuItem data ){
        String value = data.content;
        Intent intent = new Intent();
        switch (value) {
            case tag_Admin:
                //                intent.setClass(mContext, MainActivity.class);
                //                startActivity(intent);
                //                intent.setClass(mContext, TakePicActivity.class);
                //                intent.setClass(mContext, TakePic2Ac.class);
                intent.setClass(mContext, TakePic2Activity.class);

                intent.putExtra(IntentKeys.key_pid, "123456789");
                startActivity(intent);
                break;
            case tag_about:
                intent.setClass(mContext, AboutActivity.class);
                startActivity(intent);
                break;
            case tag_quickRuku:
                intent.setClass(mContext, QuickRukuActivity.class);
                startActivity(intent);
                break;
            case tag_shangjia:
                intent.setClass(mContext, ShangjiaActivity.class);
                startActivity(intent);
                break;
            case tag_rukuShangjia:
                intent.setClass(mContext, QuickRuku2Activity.class);
                startActivity(intent);
                break;
            case tag_kucun_mgr:
                intent.setClass(mContext, KucunEditActivity.class);
                startActivity(intent);
                break;

        }
    }
}
