package com.zjy.north.rukuapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SunmiToobarAc;
import com.zjy.north.rukuapp.contract.QuickRukuContract;
import com.zjy.north.rukuapp.entity.RKInfo;
import com.zjy.north.rukuapp.entity.RukuInfoItem;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;

import java.util.ArrayList;
import java.util.List;

import utils.MyDecoration;
import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.framwork.DialogUtils;

public class QuickRukuActivity extends SunmiToobarAc implements QuickRukuContract.IView, View.OnClickListener {
    private QuickRukuContract.IPresenter mPresenter;

    private List<WaitRukuInfo> mInfos = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    private EditText edCode;

    @Override

    public void init() {
        super.init();
        mPresenter = new QuickRukuContract.Presenter(this, this);
        edCode =  getViewInContent(R.id.activity_rk_ed_code);
        RecyclerView rv = (RecyclerView) getViewInContent(R.id.activity_rk_recview);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.addItemDecoration(new MyDecoration(this));
        mAdapter = new QrkAdapter2(mInfos, R.layout.item_quick_rk, this, new QrkAdapter2.ItemClickListener() {
            @Override
            public void onItemCLick(WaitRukuInfo info) {
                String place = "";
                String counts = "" + info.getLeftCount();
                mPresenter.ruku(info, place, loginID, counts);
            }
        }, new QrkAdapter2.ItemClickListener() {
            @Override
            public void onItemCLick(final WaitRukuInfo info) {
                int counts = 0;
                try {
                    counts = info.getLeftCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AlertDialog mDialog;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("修改入库数量");
                final View itemView = LayoutInflater.from(mContext).inflate(R.layout.dialog_rk_modify_count, null);
                final EditText edCounts = itemView.findViewById(R.id.dialog_rk_ed_counts);
                edCounts.setText(counts + "");
                final int finalCounts = counts;
                mBuilder.setPositiveButton("取消", null);
                mBuilder.setNegativeButton("入库", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inCounts = edCounts.getText().toString();
                        if ("".equals(inCounts.trim())) {
                            showMsgToast("数量不能为空");
                            return;
                        }
                        int in = Integer.parseInt(inCounts);
                        if (in > finalCounts) {
                            showMsgToast("超过最大可入库数量" + finalCounts);
                            return;
                        }
                        String place = "";
                        String counts = in + "";
                        mPresenter.ruku(info, place, loginID, counts);
                    }
                });
                mBuilder.setView(itemView);
                mDialog = mBuilder.show();
            }
        });
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onRukuSuccess(final String mxId) {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(mContext);
        mDialogBuilder.setTitle("提示");
        DialogUtils.getSpAlert(mContext, "已入库，是否上架 " + mxId, "提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterShangjiaPage(mxId);
            }
        }, "是", null, "否").show();
    }

    public void enterShangjiaPage(String mxId) {
        Intent mIntent = new Intent(mContext, ShangjiaActivity.class);
        mIntent.putExtra(ShangjiaActivity.intentName_mxID, mxId);
        startActivity(mIntent);
    }

    @Override
    public void showToast(String msg) {
        showMsgToast(msg);
    }


    @Override
    public void getCameraScanResult(String result, int code) {
        super.getCameraScanResult(result, code);
        edCode.setText(result);
        //扫码返回操作的id已经在onResume中处理了
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_rk_btn_scan:
                startScanActivity();
                break;
            case R.id.activity_rk_btn_search:
                String code = edCode.getText().toString();
                if (code.equals("")) {
                    showMsgToast("请先输入查询号码");
                    return;
                }
                startSearch(code);
                break;
            case R.id.activity_rk_batch_commit:
//                isPiliang
                piliang();
                break;
            case R.id.activity_rk_batch_selectAll:
                QrkAdapter2 tAdapter = (QrkAdapter2) mAdapter;
                tAdapter.selectAll();
                break;
            case R.id.activity_rk_batch_deselectAll:
                QrkAdapter2 tAdapter2 = (QrkAdapter2) mAdapter;
                tAdapter2.deselectAll();
                break;
        }

    }

    void startSearch(String result) {
        edCode.setText(result);
        mInfos.clear();
        mAdapter.notifyDataSetChanged();
        mPresenter.getData(result);
    }

    void piliang(){
        List<WaitRukuInfo> rkDatas = new ArrayList<>();
        for(int i=0;i<mInfos.size();i++) {
            RukuInfoItem mInfo = (RukuInfoItem) mInfos.get(i);
            if (mInfo.isCheckd()) {
                rkDatas.add(mInfo);
            }
        }
        if (rkDatas.size() == 0) {
            showToast("没有待入库数据");
            return;
        }
        mPresenter.ruku(rkDatas, loginID);
    }

    @Override
    public void resultBack(String result) {
        super.resultBack(result);
        startSearch(result);
    }

    @Override
    public void onScanResult(String code) {
        super.onScanResult(code);
        startSearch(code);
    }

    @Override
    public void setListeners() {
        super.setListeners();
        setOnClickListener(this, R.id.activity_rk_btn_scan);
        setOnClickListener(this, R.id.activity_rk_btn_search);

        setOnClickListener(this, R.id.activity_rk_batch_commit);
        setOnClickListener(this, R.id.activity_rk_batch_selectAll);
        setOnClickListener(this, R.id.activity_rk_batch_deselectAll);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_ruku);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("zjy", "com.zjy.north.rukuapp.activity.QuickRukuActivity->onPause(): ==");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mid = edCode.getText().toString();
        if (!mid.equals("")) {
//            mInfos.clear();
//            mAdapter.notifyDataSetChanged();
//            mPresenter.getData(mid);
            startSearch(mid);
        }
        Log.e("zjy", "com.zjy.north.rukuapp.activity.QuickRukuActivity->onResume(): ==");
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        CharSequence title = menuItem.getTitle();
        String strTitle = title.toString();
        switch (menuItem.getItemId()) {
            case R.id.action_rk_piliang:
//                showMsgToast("点击了-" + strTitle);
                View viewInContent = getViewInContent(R.id.activity_rk_batch_toolbar);
                QrkAdapter2 adapter2 = (QrkAdapter2) mAdapter;

                if (adapter2.isPiliang) {
                    viewInContent.setVisibility(View.GONE);
                }else{
                    viewInContent.setVisibility(View.VISIBLE);
                }
                adapter2.isPiliang = !adapter2.isPiliang;
                adapter2.selectNone();
                break;
            case R.id.action_share:
                showMsgToast("点击了-" + strTitle);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quickrk_meue, menu);
        return true;
    }

    @Override
    public String setTitle() {
        return "快速入库";
    }


    static class QrkAdapter2 extends BaseRvAdapter<WaitRukuInfo> {
        QrkAdapter2.ItemClickListener rkListener;
        QrkAdapter2.ItemClickListener cgCountsListener;

        public QrkAdapter2(List<WaitRukuInfo> mData, int layoutId, Context mContext, ItemClickListener rkListener) {
            super(mData, layoutId, mContext);
            this.rkListener = rkListener;
        }

        public QrkAdapter2(List<WaitRukuInfo> mData, int layoutId, Context mContext, ItemClickListener rkListener,
                           ItemClickListener cgCountsListener) {
            super(mData, layoutId, mContext);
            this.rkListener = rkListener;
            this.cgCountsListener = cgCountsListener;
        }

        interface ItemClickListener {
            void onItemCLick(WaitRukuInfo info);
        }
        @Override
        protected void convert(BaseRvViewholder holder, final WaitRukuInfo item) {
            holder.setText(R.id.item_rk_tv, item.toString());
            holder.setOnclick(R.id.item_rk_btn_modify_count, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cgCountsListener.onItemCLick(item);
                }
            });
            holder.setOnclick(R.id.item_rk_btn_ruku, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rkListener != null) {
                        rkListener.onItemCLick(item);
                    }
                }
            });
            View itemView = holder.getItemView();

            final CheckBox cboCheck = holder.getView(R.id.item_rk_cbo);
            final View btnCgCount = holder.getView(R.id.item_rk_btn_modify_count);
            final View btnRk = holder.getView(R.id.item_rk_btn_ruku);

            int stat = View.VISIBLE;
            if (isPiliang) {
                cboCheck.setVisibility(View.VISIBLE);
                stat = View.GONE;
            }else{
                cboCheck.setVisibility(View.GONE);
            }
            btnRk.setVisibility(stat);
            btnCgCount.setVisibility(stat);

            final RukuInfoItem nItem = (RukuInfoItem) item;

            cboCheck.setChecked( nItem.isCheckd());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nItem.setCheckd(!nItem.isCheckd());
                    cboCheck.setChecked(nItem.isCheckd());
                }
            });
            cboCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nItem.setCheckd(!nItem.isCheckd());
                }
            });
        }

        boolean isPiliang = false;

        public void selectNone() {
            for(int i=0;i<mData.size();i++) {
                RukuInfoItem item = (RukuInfoItem) mData.get(i);
                item.setCheckd(false);
            }
            notifyDataSetChanged();
        }
        public void selectAll(){
            for(int i=0;i<mData.size();i++) {
                RukuInfoItem item = (RukuInfoItem) mData.get(i);
                item.setCheckd(true);
            }
            notifyDataSetChanged();
        }
        public void deselectAll(){
            for(int i=0;i<mData.size();i++) {
                RukuInfoItem item = (RukuInfoItem) mData.get(i);
                item.setCheckd(!item.isCheckd());
            }
            notifyDataSetChanged();
        }
    }
    static class QrkAdapter extends BaseRvAdapter<RKInfo> {
        ItemClickListener mListener;
        interface ItemClickListener {
            void onItemCLick(RKInfo info);
        }

        public QrkAdapter(List<RKInfo> mData, int layoutId, Context mContext, ItemClickListener mListener) {
            super(mData, layoutId, mContext);
            this.mListener = mListener;
        }

        public QrkAdapter(List<RKInfo> mData, int layoutId, Context mContext) {
            super(mData, layoutId, mContext);
        }

        @Override
        protected void convert(BaseRvViewholder holder, final RKInfo item) {
//            View view = holder.getView(R.id.item_rk_tv);
            holder.setText(R.id.item_rk_tv, item.toString());
            holder.setOnclick(R.id.item_rk_btn_ruku, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemCLick(item);
                    }
                }
            });
        }
    }

    @Override
    public void loading(String msg) {
        loadingNoProcess(msg);
    }

    @Override
    public void cancelLoading() {
        pdDialog.cancel();
    }

    @Override
    public void updateData(List<WaitRukuInfo> data) {
        mInfos.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlert(String msg) {
        showMsgDialog(msg);
    }

    @Override
    public void setPresenter(QuickRukuContract.IPresenter iPresenter) {

    }
}
