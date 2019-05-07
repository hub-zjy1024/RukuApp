package com.zjy.north.rukuapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.scanner.ScanController;
import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.SunmiScanActivity;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.SpSettings;
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
import utils.handler.NoLeakHandler;
import utils.net.wsdelegate.ChuKuServer;
import utils.net.wsdelegate.RKServer;

public class ShangjiaActivity extends SunmiScanActivity implements NoLeakHandler.NoLeakCallback {
    private Handler mHandler = new NoLeakHandler(this);
    private List<ShangJiaInfo> sjInfos = new ArrayList<>();
    private SharedPreferences spKf;
    private String storageID = "";
    private final int GET_DATA = 0;
    private RVAdapter mAdapter;
    private EditText edMxID;
    public static final String KuQq_ID = "kq_id";
    private String kuquID = "";
    private boolean isShangjia = false;
    private ShangJiaInfo currentItem = null;
    private String storageInfo;
    private ProgressDialog pd;
    private Context sjContext = ShangjiaActivity.this;

    @Override

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GET_DATA:
                pd.cancel();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sangjia);
        RecyclerView rv = (RecyclerView) findViewById(R.id.activity_shangjia_rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.addItemDecoration(new MyDecoration(this));
        Button btnScan = (Button) findViewById(R.id.shangjia_activity_btn_scancode);
        edMxID = (EditText) findViewById(R.id.shangjia_activity_ed_pid);
        Button btnSearch = (Button) findViewById(R.id.shangjia_activity_btn_search);
        pd = new ProgressDialog(this);
        pd.setMessage("");
        pd.setTitle("提示");
        pd.setCancelable(false);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShangjia = false;

                String brand = Build.BRAND;
                if (brand.contains("V7000")) {
                    V7000Scan();
                } else if (brand.contains("SUNMI")) {
                    ((ShangjiaActivity) mContext).sunmiScan(new ScanController.ScanListener() {
                        @Override
                        public void onScanResult(String code) {
                            getCameraScanResult(code);
                        }
                    });
                }else{
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
        spKf = getSharedPreferences(SpSettings.PREF_KF, MODE_PRIVATE);
        storageInfo = spKf.getString(SpSettings.storageKey, "");
        storageID = StorageUtils.getStorageIDFromJson(storageInfo);

        kuquID = spKf.getString(KuQq_ID, "");
        mAdapter = new RVAdapter(sjInfos, R.layout.item_shangjia, this);
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onScanResult(String code) {
        super.onScanResult(code);
    }

    public void shangjia(String code) {
        try {
            String mdata = RKServer.GetApplyCustomInfo(code);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        //        服务：RKServer
        //                接口：GetApplyCustomInfo
        //                参数：codes
        //                参数格式：390729|1254125|123456
    }

    @Override

    public void resultBack(String result) {
        getCameraScanResult(result);
    }

    public void startSearch(String code) {
        edMxID.setText(code);
        sjInfos.clear();
        getData(code);
    }

    @Override
    public void getCameraScanResult(final String result) {
        if (isShangjia) {
            startShangjia(result);
        } else {
            startSearch(result);
        }
    }

    public void startShangjia(final String result) {
        if (currentItem != null) {
            final String id = currentItem.getShangjiaID();
            String newKuqu = getKuquID(storageInfo);
            final String description = String.format("android_%s->%s,%s->%s", currentItem.getPlace(), result, currentItem
                            .getKuqu()
                    , newKuqu);
            pd.setMessage("正在上架。。。");
            pd.show();
            Runnable sjRun = new Runnable() {
                @Override
                public void run() {
                    String errMsg = "";
                    try {
                        String currentIp = StorageUtils.getCurrentIp();
                        if (currentIp.equals("")) {
                            throw new IOException("获取IP地址失败");
                        }
                        String sjResult = ChuKuServer.Shangjia(id, result, kuquID, description, loginID, currentIp);
                        if (sjResult.equals("上架成功")) {
                            currentItem.setStatus(String.format("位置变更成功：%s->%s", currentItem.getPlace(), result));
                            MyApp.myLogger.writeInfo("上架成功：" + currentItem.getCodeStr() + "," + description);
                        } else {
                            MyApp.myLogger.writeInfo("上架失败：" + currentItem.getCodeStr() + "," + description + ",结果：" + result);
                            throw new IOException(String.format("返回结果=''", sjResult));
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
                                MyToast.showToast(sjContext, "上架成功");
                            }
                            pd.cancel();
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

        @Override
        protected void convert(BaseRvViewholder holder, ShangJiaInfo item) {
            final ShangJiaInfo xiaopiaoInfo = item;

            TextView tv = (TextView) holder.getView(R.id.item_shangjia_tv);
            Button btnSangjia = (Button) holder.getView(R.id.item_shangjia_btn);
            TextView tvStatus = (TextView) holder.getView(R.id.item_shangjia_tv_status);

            btnSangjia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ShangjiaActivity activity = (ShangjiaActivity) mContext;
                    activity.isShangjia = true;
                    activity.currentItem = xiaopiaoInfo;
                    String brand = Build.BRAND;
                    if (brand.contains("V7000")) {
                        activity.V7000Scan();
                    } else if (brand.contains("SUNMI")) {
                        activity.sunmiScan(new ScanController.ScanListener() {
                            @Override
                            public void onScanResult(String code) {
                                activity.startShangjia(code);
                            }
                        });
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

        }
    }

    static class RVAdapter extends RecyclerView.Adapter<RvHolder> {

        private List<ShangJiaInfo> xpInfos;
        private int layoutID;
        private Context mContext;

        private RVAdapter(List<ShangJiaInfo> xpInfos, int layoutID, Context mContext) {
            this.xpInfos = xpInfos;
            this.layoutID = layoutID;
            this.mContext = mContext;
        }

        @Override
        public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(layoutID, null);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                        .WRAP_CONTENT);
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            v.setLayoutParams(layoutParams);
            return new RvHolder(v);
        }

        @Override
        public void onBindViewHolder(RvHolder holder, int position) {
            final ShangJiaInfo xiaopiaoInfo = xpInfos.get(position);
            holder.btnSangjia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ShangjiaActivity activity = (ShangjiaActivity) mContext;
                    activity.isShangjia = true;
                    activity.currentItem = xiaopiaoInfo;
                    String brand = Build.BRAND;
                    if (brand.contains("V7000")) {
                        activity.V7000Scan();
                    } else if (brand.contains("SUNMI")) {
                        activity.sunmiScan(new ScanController.ScanListener() {
                            @Override
                            public void onScanResult(String code) {
                                activity.startShangjia(code);
                            }
                        });
                    } else {
                        activity.startScanActivity();
                    }
                }
            });
            if (!"".equals(xiaopiaoInfo.getStatus())) {
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setText(xiaopiaoInfo.getStatus());
            } else {
                holder.tvStatus.setVisibility(View.GONE);
            }
            holder.tv.setText(xiaopiaoInfo.toString());
        }


        @Override
        public int getItemCount() {
            return xpInfos.size();
        }
    }

    static class RvHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus;
        public TextView tv;
        public Button btnSangjia;

        public RvHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_shangjia_tv);
            btnSangjia = (Button) itemView.findViewById(R.id.item_shangjia_btn);
            tvStatus = (TextView) itemView.findViewById(R.id.item_shangjia_tv_status);

        }
    }

    public void getData(final String mxID) {
        pd.setMessage("正在搜索数据。。");
        pd.show();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    if (storageID.equals("")) {
                        String info = StorageUtils.getStorageByIp();
                        storageID = StorageUtils.getStorageIDFromJson(info);
                        spKf.edit().putString(SpSettings.storageKey, info).commit();
                    }
                    String balaceInfo = ChuKuServer.GetStorageBlanceInfoByID(Integer.parseInt(mxID), "", storageID);
                    Log.e("zjy", "SangjiaActivity->run(): balanceInfo==" + balaceInfo);
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
                        String storageID = ShangjiaActivity.this.storageID;
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


    public static String getKuquID(String storageInfo) {
        return StorageUtils.getStorageInfo(storageInfo, "ChildStorageID");
    }
}
