package com.zjy.north.rukuapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SunmiToobarAc;
import com.zjy.north.rukuapp.contract.QuickRukuContract;
import com.zjy.north.rukuapp.entity.RKInfo;
import com.zjy.north.rukuapp.entity.RukuInfoItem;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.SpSettings;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;
import com.zjy.north.rukuapp.entity.entity.XiaopiaoInfo;
import com.zjy.north.rukuapp.task.StorageUtils;
import com.zjy.north.rukuapp.task.TaskManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.MyDecoration;
import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.framwork.DialogUtils;
import utils.framwork.MyToast;
import utils.net.wsdelegate.ChuKuServer;

public class QuickRukuActivity extends SunmiToobarAc implements QuickRukuContract.IView, View.OnClickListener {
    protected QuickRukuContract.IPresenter mPresenter;

    private List<WaitRukuInfo> mInfos = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    RecyclerView rvDataView;
    protected EditText edCode;
    private boolean notSearch = false;
    protected boolean notChageFocus = false;
    protected String tempSID = "";
    private ShangJiaInfo currentItem;
    private boolean isShangjia;
    private String currentIp;
    private String storageID="";
    private String storageInfo;
    private String nowKuqu = "";
    private SharedPreferences spKf;
    private String saveKey_edCode = "edCode";
    protected static final int DELAY_TIME = 200;

    private Handler mHandler = new Handler();
    @Override

    public void init() {
        super.init();
        mPresenter = new QuickRukuContract.Presenter(this, this);
        edCode =  getViewInContent(R.id.activity_rk_ed_code);
        rvDataView = (RecyclerView) getViewInContent(R.id.activity_rk_recview);
        rvDataView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDataView.addItemDecoration(new MyDecoration(this));
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
        rvDataView.setAdapter(mAdapter);
        spKf = getSharedPreferences(SpSettings.PREF_KF, MODE_PRIVATE);
        Runnable getInfoRun = new Runnable() {
            @Override
            public void run() {
                currentIp = StorageUtils.getCurrentIp();
                if (currentIp.equals("")) {
                    currentIp = "4g";
                }
                if ("" .equals(storageID)) {
                    try {
                        storageInfo = StorageUtils.getStorageByIp();
                        spKf.edit().putString(SpSettings.storageKey, storageInfo).commit();
                        nowKuqu = StorageUtils.getKuquID(storageInfo);
                        storageID = StorageUtils.getStorageIDFromJson(storageInfo);
//                        Log.e("zjy", "ShangjiaActivity->run(): storageInfo==" + storageInfo+"  currentIp==" + currentIp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        TaskManager.getInstance().execute(getInfoRun);
    }

    @Override
    public void onRukuSuccess(final String mxId) {
//        enterShangjiaPage(mxId);
//        String specId = mxId + "|123";
        String specId = edCode.getText().toString();
        showToast(specId + "入库完成，等待上架");
        startSearch(specId);
    }

    public void onRukuSuccess2(final String mxId) {
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
        notSearch = true;
    }

    @Override
    public void showToast(String msg) {
        showMsgToast(msg);
    }


    @Override
    public void getCameraScanResult(String result, int code) {
        super.getCameraScanResult(result, code);
        dealWith(result);
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
        if (tempSID.equals(result)) {
            notChageFocus = true;
            tempSID = "";
        } else {
            notChageFocus = false;
        }
        mPresenter.getData2(result, storageID);
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
        onScanResult(result);
    }

    @Override
    public void onScanResult(String code) {
        super.onScanResult(code);
        dealWith(code);
    }
    public void dealWith(String code) {
            View currentFocus1 = getCurrentFocus();
            if (currentFocus1 == null) {
                startSearch(code);
                return;
            }
            if (currentFocus1 instanceof EditText) {

            } else {
                return;
            }
            EditText currentFocus = (EditText) currentFocus1;
            switch (currentFocus.getId()) {
                case R.id.item_shangjia_ed_place:
                    currentFocus.setText(null);
                    currentFocus.setText(code);
                    int counts = rvDataView.getChildCount();
                    Object mTag = currentFocus.getTag();
                    if (mTag != null) {
                        currentItem = (ShangJiaInfo) mTag;
                    }else{
                        MyApp.myLogger.writeError("shangjia error itemTag==null");
                    }
                    //                Log.e("zjy", "ShangjiaActivity->dealWithScan():childCounts ==" + counts + " view=" + mTag);
                    startShangjia(code);
                    break;
                case R.id.activity_rk_ed_code:
                    Log.e("zjy", "ShangjiaActivity->dealWithScan(): ==search" );
                    startSearch(code);
                    break;
            }
    }

    public void startShangjia(final String result) {
        tempSID = result;
        if (currentItem != null) {
            if (currentIp == null) {
                showMsgToast("还未获取到当前IP信息，请稍后");
                return;
            }
            if ("".equals(storageInfo)) {
                showMsgToast("还未获取到当前库房信息，请稍后");
                return;
            }
            if ("".equals(nowKuqu)) {
                showMsgToast("还未获取到当前库区信息，请稍后");
                return;
            }
            final String id = currentItem.getShangjiaID();

            final String description = String.format("android_%s->%s,%s->%s", currentItem.getPlace(), result, currentItem
                            .getKuqu()
                    , nowKuqu);
            loadingNoProcess("正在上架。。。");

            Runnable sjRun = new Runnable() {
                @Override
                public void run() {
                    String errMsg = "";
                    try {
                        String srcKu=currentItem.getKuqu();
                        String sjResult = ChuKuServer.Shangjia(id, result, srcKu, description, loginID, currentIp);
                        //                        String s2 = RKServer.ShangJia(id, result, kuquID, loginID);
                        if (sjResult.equals("上架成功")) {
                            currentItem.setStatus(String.format("位置变更成功：%s->%s", currentItem.getPlace(), result));
                            MyApp.myLogger.writeInfo("上架成功：" + currentItem.getCodeStr() + "," + description);
                        } else {
                            MyApp.myLogger.writeInfo("上架失败：" + currentItem.getCodeStr() + "," + description + ",结果：" + result);
                            throw new IOException(String.format("ret='%s'", sjResult));
                        }
                    } catch (final IOException e) {
                        errMsg = "上架失败：" + e.getMessage();
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        errMsg = "上架失败：" + e.getMessage();
                        e.printStackTrace();
                    }
                    final String finalErrMsg = errMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onShangjiaOk(finalErrMsg);
                        }
                    });
                }
            };
            TaskManager.getInstance().execute(sjRun);
        } else {
            MyToast.showToast(this, "请先点击上架按钮");
        }
    }

    void onShangjia2(XiaopiaoInfo info, int code) {
        if (code == 0) {

        } else {

        }
    }
    void onShangjiaOk(String errMsg) {
        String sjCode = edCode.getText().toString();
        cancelLoading();
        if (!"".equals(errMsg)) {
            showMsgDialog(errMsg);
        } else {
            mAdapter.notifyDataSetChanged();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mAdapter中也有控件获取焦点
                    //不是立马生效，获取焦点需要时间
                    // 需要延时
                    edCode.requestFocus();
                }
            }, DELAY_TIME);
            String searchId = sjCode;
            showToast("上架成功");
            startSearch(searchId);
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(saveKey_edCode, edCode.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        edCode.setText(savedInstanceState.getString(saveKey_edCode));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (notSearch) {
            notSearch = false;
            return;
        }
        String mid = edCode.getText().toString();
        if (!mid.equals("")) {
//            mInfos.clear();
//            mAdapter.notifyDataSetChanged();
//            mPresenter.getData(mid);
//            startSearch(mid);
//            dealWith(mid);
//            String code = mid;
//            View currentFocus1 = getCurrentFocus();
//            if (currentFocus1 == null) {
//                startSearch(code);
//                return;
//            }
//            if (currentFocus1 instanceof EditText) {
//
//            } else {
//                return;
//            }
//            EditText currentFocus = (EditText) currentFocus1;
//            switch (currentFocus.getId()) {
//                case R.id.item_shangjia_ed_place:
//                    currentFocus.setText(null);
//                    currentFocus.setText(code);
//                    int counts = rvDataView.getChildCount();
//                    Object mTag = currentFocus.getTag();
//                    if (mTag != null) {
//                        currentItem = (ShangJiaInfo) mTag;
//                    }else{
//                        MyApp.myLogger.writeError("shangjia error itemTag==null");
//                    }
//                    startShangjia(code);
//                    break;
//                case R.id.activity_rk_ed_code:
//                    Log.e("zjy", "ShangjiaActivity->dealWithScan(): ==search" );
//                    startSearch(code);
//                    break;
//            }
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
        try {
            pdDialog.cancel();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateData(List<WaitRukuInfo> data) {
        mInfos.addAll(data);
        rvDataView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlert(String msg) {
        showMsgDialog(msg);
    }

    @Override
    public void setPresenter(QuickRukuContract.IPresenter iPresenter) {

    }

    @Override
    public void onShangjiaDataCallback(List<ShangJiaInfo> infos) {
        ShangjiaAdapter adapter = new ShangjiaAdapter(infos,
                R.layout.item_shangjia, mContext);
        rvDataView.setAdapter(adapter);
        final List<ShangJiaInfo> sjInfos = infos;
        if (notChageFocus) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final long t2 = System.currentTimeMillis();
                View childAt2 = rvDataView.getChildAt(0);
                View itemView = rvDataView.getLayoutManager().getChildAt(0);
                rvDataView.scrollToPosition(0);
                //                            Log.e("zjy", "ShangjiaActivity->handleMessage():rvMgrGetChild==" + itemView + "\tgetChildAt" + childAt2);
                if (itemView != null) {
                    EditText viewById = itemView.findViewById(R.id.item_shangjia_ed_place);
                    viewById.setFocusable(true);
                    viewById.requestFocus();
                    currentItem = sjInfos.get(0);
                }else{

                }
            }
        }, DELAY_TIME);
    }

    static class ShangjiaAdapter extends BaseRvAdapter<ShangJiaInfo> {
        public ShangjiaAdapter(List<ShangJiaInfo> mData, int layoutId, Context mContext) {
            super(mData, layoutId, mContext);
        }

        /**
         绑定数据
         @param holder {@link BaseRvViewholder};
         @param item   item数据
         */
        @Override
        protected void convert(BaseRvViewholder holder, ShangJiaInfo item) {
            int position = holder.position;
            final ShangJiaInfo xiaopiaoInfo = item;

            TextView tv = (TextView) holder.getView(R.id.item_shangjia_tv);
            Button btnSangjia = (Button) holder.getView(R.id.item_shangjia_btn);
            TextView tvStatus = (TextView) holder.getView(R.id.item_shangjia_tv_status);
            final EditText edPlace = holder.getView(R.id.item_shangjia_ed_place);
            final QuickRukuActivity activity = (QuickRukuActivity) mContext;

            btnSangjia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edPlace.requestFocus();
                    activity.isShangjia = true;
                    activity.currentItem = xiaopiaoInfo;
                    if (activity.isV7000()) {
                        activity.V7000Scan();
                    } else if (activity.isSunmiScan()) {
                        activity.sunmiScan();
                    } else {
                        activity.startScanActivity();
                    }
                }
            });
            edPlace.setTag(item);
            //key可以定义在string.xml中，<item name="tag_shangjia" type="id">123123</item>
            //            edPlace.setTag(R.id.tag_shangjia);

            if (!"".equals(xiaopiaoInfo.getStatus())) {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(xiaopiaoInfo.getStatus());
            } else {
                tvStatus.setVisibility(View.GONE);
            }
            tv.setText(xiaopiaoInfo.toString());
            edPlace.setText(xiaopiaoInfo.getPlace());
            //            if (position == 0) {
            //                edPlace.setFocusable(true);
            //                edPlace.requestFocus();
            //                activity.currentItem = xiaopiaoInfo;
            //            }else{
            //
            //            }
        }
    }
}
