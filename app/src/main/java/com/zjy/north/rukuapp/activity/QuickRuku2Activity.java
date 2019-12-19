package com.zjy.north.rukuapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;

import java.util.ArrayList;
import java.util.List;

import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.framwork.MyToast;

/**
 * Created by 张建宇 on 2019/12/13.
 */
public class QuickRuku2Activity extends QuickRukuActivity {

    private WaitRukuInfo currentItem;

    private String newPlace = "";
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInfos = new ArrayList<>();
    }

    @Override
    public String setTitle() {
        return "一键入库上架";
    }

    private String mScanResult = "";


    private List<WaitRukuInfo> mInfos;

    @Override
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
                if (currentItem == null) {
                    super.dealWith(code);
                    return;
                }
                currentFocus.setText(null);
                currentFocus.setText(code);
                int counts = rvDataView.getChildCount();
                Object mTag = currentFocus.getTag();
                if (mTag != null) {
                    currentItem = (WaitRukuInfo) mTag;
                }else{
                    Log.e("zjy", "QuickRuku2Activity->dealWith(): nullTag==");

                    MyApp.myLogger.writeError("shangjia error itemTag==null");
                }
                //                Log.e("zjy", "ShangjiaActivity->dealWithScan():childCounts ==" + counts + " view=" + mTag);
//                startShangjia(code);
                String place = code;
                String leftCount = "" + currentItem.getLeftCount();
                newPlace = code;
                mPresenter.ruku2(currentItem, place, loginID, leftCount);
                break;
            case R.id.activity_rk_ed_code:
                super.dealWith(code);
                break;
        }


    }

    @Override
    void onShangjiaOk(String errMsg) {
//        super.onShangjiaOk(errMsg);
        String sjCode = edCode.getText().toString();
        cancelLoading();
        if (!"".equals(errMsg)) {
            showMsgDialog(errMsg);
        } else {
            RecyclerView.Adapter mAdapter = rvDataView.getAdapter();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
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
        }
    }

    @Override
    public void onRukuSuccess2(String inputCode, String place) {
        String specId = edCode.getText().toString();
        edCode.requestFocus();
        RecyclerView.Adapter mada = rvDataView.getAdapter();
        if (mada != null) {
            String newPlace = place;
            currentItem.setStatus("位置变化：" +currentItem.getPlace()  + "->" + newPlace);
            currentItem.setPlace(newPlace);
            mada.notifyDataSetChanged();
        }
        showToast(currentItem.getId() + "入库完成,并已上架");
        tempSID = specId;
    }

    @Override
    public void onRukuSuccess(String mxId) {
//        super.onRukuSuccess(mxId);
        String specId = edCode.getText().toString();
        edCode.requestFocus();
        RecyclerView.Adapter mada = rvDataView.getAdapter();
        if (mada != null) {
            currentItem.setStatus("位置变化：" +currentItem.getPlace()  + "->" + newPlace);
            currentItem.setPlace(newPlace);
            mada.notifyDataSetChanged();
        }

        showToast(currentItem.getId() + "入库完成,并已上架");
        tempSID = specId;
    }

    @Override
    public void onShangjiaDataCallback(List<ShangJiaInfo> infos) {
        currentItem = null;
        super.onShangjiaDataCallback(infos);
    }

    @Override
    public void updateData(final List<WaitRukuInfo> data) {
        Log.e("zjy", "QuickRuku2Activity->updateData(): newDate==");
        Ruku2Adapter adapter = new Ruku2Adapter(data,
                R.layout.item_shangjia, mContext);
        rvDataView.setAdapter(adapter);
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
                    currentItem = data.get(0);
                }else{

                }
            }
        }, DELAY_TIME);
    }


    static class Ruku2Adapter extends BaseRvAdapter<WaitRukuInfo> {

        public WaitRukuInfo nowInfo;
        public Ruku2Adapter(List<WaitRukuInfo> mData, int layoutId, Context mContext) {
            super(mData, layoutId, mContext);
        }

        @Override
        protected void convert(BaseRvViewholder holder, WaitRukuInfo item) {
            int position = holder.position;
            final WaitRukuInfo xiaopiaoInfo = item;
            TextView tv = (TextView) holder.getView(R.id.item_shangjia_tv);
            Button btnSangjia = (Button) holder.getView(R.id.item_shangjia_btn);
            TextView tvStatus = (TextView) holder.getView(R.id.item_shangjia_tv_status);
            final EditText edPlace = holder.getView(R.id.item_shangjia_ed_place);
            QuickRuku2Activity activity = null;
            if (mContext instanceof QuickRuku2Activity) {
                activity = (QuickRuku2Activity) mContext;
            }

            final QuickRuku2Activity finalActivity = activity;
            btnSangjia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edPlace.requestFocus();
                    if(finalActivity==null){
                        MyToast.showToast(mContext, "程序异常,请退出程序");
                        return;
                    }
                    finalActivity.currentItem = xiaopiaoInfo;
                    if (finalActivity.isV7000()) {
                        finalActivity.V7000Scan();
                    } else if (finalActivity.isSunmiScan()) {
                        finalActivity.sunmiScan();
                    } else {
                        finalActivity.startScanActivity();
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
        }
    }
}
