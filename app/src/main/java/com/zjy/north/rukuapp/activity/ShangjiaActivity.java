package com.zjy.north.rukuapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SunmiScanActivity;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.SpSettings;
import com.zjy.north.rukuapp.task.CheckUtils;
import com.zjy.north.rukuapp.task.StorageUtils;
import com.zjy.north.rukuapp.task.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.MyDecoration;
import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.framwork.MyToast;
import utils.framwork.SoftKeyboardUtils;
import utils.handler.NoLeakHandler;
import utils.net.wsdelegate.ChuKuServer;
import utils.net.wsdelegate.RKServer;

public class ShangjiaActivity extends SunmiScanActivity implements NoLeakHandler.NoLeakCallback {
    private Handler mHandler = new NoLeakHandler(this);
    private List<ShangJiaInfo> sjInfos = new ArrayList<>();
    private SharedPreferences spKf;
    private String storageID = "";
    private final int GET_DATA = 0;
    private RecyclerView.Adapter mAdapter;
    private EditText edMxID;
    public static final String KuQq_ID = "kq_id";
    private String kuquID = "";
    private boolean isShangjia = false;
    private ShangJiaInfo currentItem = null;
    private String storageInfo;
    private String nowKuqu = "";
    private ProgressDialog pd;
    private Context sjContext = ShangjiaActivity.this;
    RecyclerView rv;
    RecyclerView.LayoutManager rvMgr;
    private String currentIp;

    public static final String intentName_mxID = "mxID";

    @Override

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GET_DATA:
                pdDialog.cancel();
                long time = System.currentTimeMillis();
                mAdapter.notifyDataSetChanged();

                if (mAdapter.getItemCount() > 0) {

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final long t2 = System.currentTimeMillis();
                            View childAt2 = rv.getChildAt(0);
                            View itemView = rvMgr.getChildAt(0);

                            rv.scrollToPosition(0);
                            Log.e("zjy", "ShangjiaActivity->handleMessage():rvMgrGetChild==" + itemView + "\tgetChildAt" + childAt2);
                            if (itemView != null) {
                                EditText viewById = itemView.findViewById(R.id.item_shangjia_ed_place);
                                viewById.setFocusable(true);
                                viewById.requestFocus();
                                currentItem = sjInfos.get(0);
                            }else{

                            }
                        }
                    }, 200);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sangjia);
    }

    @Override
    public void init() {
        super.init();

        Toolbar tb = getViewInContent(R.id.dyjkf_normalTb);
        tb.setTitle("上架");
        tb.setSubtitle("");
        setSupportActionBar(tb);

        rv = (RecyclerView) findViewById(R.id.activity_shangjia_rv);
        rvMgr = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(rvMgr);
        rv.addItemDecoration(new MyDecoration(this));
        Button btnScan = (Button) findViewById(R.id.shangjia_activity_btn_scancode);
        edMxID = (EditText) findViewById(R.id.shangjia_activity_ed_pid);
        Button btnSearch = (Button) findViewById(R.id.shangjia_activity_btn_search);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShangjia = false;
                if (isV7000()) {
                    V7000Scan();
                } else if (isSunmiScan()) {
                    sunmiScan();
                }else {
                    startScanActivity();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mxID = edMxID.getText().toString();
                if (mxID.equals("")) {
                    MyToast.showToast(sjContext, "请先输入明细ID");
                    return;
                }
                startSearch(mxID);
            }
        });
        mAdapter = new ShangjiaAdapter(sjInfos, R.layout.item_shangjia, this);
        rv.setAdapter(mAdapter);

        Intent intent = getIntent();
        String senderId = intent.getStringExtra(intentName_mxID);
        if (senderId != null) {
            startSearch(senderId);
        }


        spKf = getSharedPreferences(SpSettings.PREF_KF, MODE_PRIVATE);
        storageInfo = spKf.getString(SpSettings.storageKey, "");
        storageID = StorageUtils.getStorageIDFromJson(storageInfo);
        kuquID = spKf.getString(KuQq_ID, "");

        Runnable getInfoRun = new Runnable() {
            @Override
            public void run() {
                 currentIp = StorageUtils.getCurrentIp();
                if (currentIp.equals("")) {
                    currentIp = "4g";
                }
                if (storageID.equals("")) {
                    try {
                        storageInfo = StorageUtils.getStorageByIp();
                        nowKuqu = StorageUtils.getKuquID(storageInfo);
                        storageID = StorageUtils.getStorageIDFromJson(storageInfo);
                        spKf.edit().putString(SpSettings.storageKey, storageInfo).commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        TaskManager.getInstance().execute(getInfoRun);

    }

    @Override
    public void onScanResult(String code) {
        super.onScanResult(code);
        dealWithScan(code);
        MyApp.myLogger.writeInfo("searchcode=" + code);
    }

    void dealWithScan(String code) {
        EditText currentFocus = (EditText) getCurrentFocus();
        if (currentFocus == null) {
            startSearch(code);
            return;
        }
        switch (currentFocus.getId()) {
            case R.id.item_shangjia_ed_place:
                currentFocus.setText(code);
                startShangjia(code);
                break;
            case R.id.shangjia_activity_ed_pid:
                Log.e("zjy", "ShangjiaActivity->dealWithScan(): ==search" );
                startSearch(code);
                break;
        }
    }


    @Override

    public void resultBack(String result) {
        dealWithScan(result);
    }

    public void startSearch(String code) {
        edMxID.setText(code);
        sjInfos.clear();
        SoftKeyboardUtils.closeInputMethod(edMxID, this);
        getData(code);
    }

    @Override
    public void getCameraScanResult(final String result) {
        dealWithScan(result);
//        if (isShangjia) {
//            startShangjia(result);
//        } else {
//            startSearch(result);
//        }

    }

    @Override
    protected void loadingNoProcess(String msg) {
        pdDialog.setCancelable(false);
        super.loadingNoProcess(msg);
    }

    public void startShangjia(final String result) {
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
                showMsgToast("还未获取到当前库房信息，请稍后");
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
                            if (!"".equals(finalErrMsg)) {
                                showMsgDialog(finalErrMsg);
                            } else {
                                mAdapter.notifyDataSetChanged();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mAdapter中也有控件获取焦点
                                        //不是立马生效，获取焦点需要时间
                                        // 需要延时
                                        edMxID.requestFocus();
                                    }
                                }, 200);
                                MyToast.showToast(sjContext, "上架成功");
                            }
                            pdDialog.cancel();
                        }
                    });
                }
            };
            TaskManager.getInstance().execute(sjRun);
        } else {
            MyToast.showToast(this, "请先点击上架按钮");
        }
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
            EditText edPlace = holder.getView(R.id.item_shangjia_ed_place);
            final ShangjiaActivity activity = (ShangjiaActivity) mContext;
            btnSangjia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            if (!"".equals(xiaopiaoInfo.getStatus())) {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(xiaopiaoInfo.getStatus());
            } else {
                tvStatus.setVisibility(View.GONE);
            }
            tv.setText(xiaopiaoInfo.toString());
//            if (position == 0) {
//                edPlace.setFocusable(true);
//                edPlace.requestFocus();
//                activity.currentItem = xiaopiaoInfo;
//            }else{
//
//            }
        }
    }

    public void getData(final String mxID) {
        loadingNoProcess("正在搜索数据。。");
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    String balaceInfo = "";
                    String tempId = storageID;
                    if (CheckUtils.isAdmin()) {
                        tempId = "";
                    }
                    if (mxID.contains("|")||mxID.contains("-")) {
                        balaceInfo = RKServer.GetShangJiaInfo(mxID);
                    }else{
                        balaceInfo = ChuKuServer.GetStorageBlanceInfoByID(Integer.parseInt(mxID), "", tempId);
                    }
                    Log.e("zjy", "SangjiaActivity->run(): balanceInfo==" + balaceInfo + "\tstorId=" + tempId);
                    JSONObject jobj = new JSONObject(balaceInfo);
                    JSONArray jarray = jobj.getJSONArray("表");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject tj = jarray.getJSONObject(i);
                        String parno = tj.getString("型号");
                        String pid = tj.getString("单据号");
                        String time = tj.getString("入库日期");
                        String temp[] = time.split(" ");
                        if (temp.length > 1) {
                            time = temp[0];
                        }
                        time = time.replaceAll("/", "-");
                        String deptno = tj.getString("部门号");
                        String counts = tj.getString("剩余数量");
                        String factory = tj.getString("厂家");
                        String producefrom = "";
                        String pihao = tj.getString("批号");
                        String fengzhuang = tj.getString("封装");
                        String description = tj.getString("描述");
                        String place = tj.getString("位置");
                        String storageID = tempId;
                        String flag = tj.getString("SQInvoiceType");
                        String shangjiaID = tj.getString("ID");
                        String company = tj.getString("name");
                        String notes = tj.getString("备注");
                        String kuqu = tj.getString("库区");
                        String detailPID = tj.getString("明细ID");
                        ShangJiaInfo info = new ShangJiaInfo(parno, deptno, time, deptno, counts, factory,
                                producefrom, pihao, fengzhuang, description, place, notes, flag, detailPID, storageID,
                                company);
                        info.setShangjiaID(shangjiaID);
                        info.setPid(pid);
                        info.setKuqu(kuqu);
                        sjInfos.add(info);
                    }
                } catch (final NumberFormatException e) {
                    showMsgToast("仅支持纯数字条码");
                    e.printStackTrace();
                } catch (final IOException e) {
                    showMsgToast("连接服务器失败：" + e.getMessage());
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    showMsgToast("服务解析失败，" + e);
                    e.printStackTrace();
                } catch (JSONException e) {
                    showMsgToast("查询不到数据");
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(GET_DATA);
            }
        };
        TaskManager.getInstance().execute(run);
    }
}
