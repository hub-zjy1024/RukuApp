package com.zjy.north.rukuapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zjy.north.rukuapp.BuildConfig;
import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.contract.KucunEditContract;
import com.zjy.north.rukuapp.contract.QuickRK2Contract;
import com.zjy.north.rukuapp.entity.ShangJiaInfo;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;
import com.zjy.north.rukuapp.entity.entity.XiaopiaoInfo;

import java.util.ArrayList;
import java.util.List;

import utils.adapter.recyclerview.BaseRvAdapter;
import utils.adapter.recyclerview.BaseRvViewholder;
import utils.framwork.DialogUtils;
import utils.framwork.MyToast;

/**
 * Created by 张建宇 on 2019/12/13.
 * {@link MenuActivity#tag_rukuShangjia}
 *
 */
public class QuickRuku2Activity extends QuickRukuActivity implements QuickRK2Contract.IView2 {

    private WaitRukuInfo currentItem;

    private String newPlace = "";
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInfos = new ArrayList<>();
        presenter = new QuickRK2Contract.Present(this);
        if (BuildConfig.DEBUG) {
            EditText edText = getViewInContent(R.id.activity_rk_ed_code);
            edText.setText("A640320|2959883|3252270##TJA1050T/CM,118|5000|TH|NXP||640320|MTM0OS4w");
        }
    }

    private   KucunEditContract.Presenter presenter;
    AlertDialog editDialog;
    private View focusedView = null;

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

    public void showEditView(final XiaopiaoInfo xiaopiaoInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // TODO: 2019/11/18 添加弹窗内容，执行修改操作，创建itemLayout
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.dialog_kucun_edit, null, false);
        builder.setTitle("修改批号：" + xiaopiaoInfo.getPid());
        Button btnModify = itemView.findViewById(R.id.dialog_kucun_btn_update);
        final EditText editText = itemView.findViewById(R.id.dialog_kucun_ed_partno);
        final TextView tvOldPartno = itemView.findViewById(R.id.dialog_kucun_tv_oldpartno);
        final String oldpihao = xiaopiaoInfo.getPihao();
        tvOldPartno.setText(oldpihao);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPihao = editText.getText().toString();
                presenter.setPihao(xiaopiaoInfo.getCodeStr(), oldpihao, tempPihao, loginID);
            }
        });
        builder.setView(itemView);
        editDialog = builder.create();
        editDialog.show();
    }

    public void showPopMenu(final XiaopiaoInfo xiaopiaoInfo, View anchorView) {
//        PopupMenu menu = new PopupMenu(mContext, anchorView);
//        menu.inflate(R.menu.quick_rk_item_more);
//        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_quick_rk_modify_pihao:
//                        showEditView(xiaopiaoInfo);
//                        break;
//                }
//                return false;
//            }
//        });
//        menu.show();
        PopupWindow mOperatesView = new PopupWindow(mContext);
        LinearLayout temp = new LinearLayout(mContext);
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.popup_quick_rk_more, temp, false);
        View viewById = itemView.findViewById(R.id.popup_quick_rk_modify);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusedView = getCurrentFocus();
                showEditView(xiaopiaoInfo);
            }
        });
        mOperatesView.setContentView(itemView);
        mOperatesView.setOutsideTouchable(true);
        mOperatesView.showAsDropDown(anchorView, -20, 10);
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

    @Override
    public void onDataCallback(List<XiaopiaoInfo> datas) {

    }

    @Override
    public void onUpdate(boolean isOk, String dpid) {
    }

    @Override
    public void showMsgDialog(String msg) {
        Dialog mdialog = DialogUtils.getSpAlert(mContext, msg, "提示");
        mdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (focusedView != null) {
                    focusedView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            focusedView.requestFocus();
                        }
                    }, DELAY_TIME);
                }
            }
        });
        mdialog.show();
    }

    public void onUpdate2(boolean isOk, String id, String newPihao) {
        if (isOk) {
            showMsgDialog("批号已修改完成");
            if (editDialog != null) {
                editDialog.cancel();
            }
            RecyclerView.Adapter adapter = rvDataView.getAdapter();
            if (adapter instanceof Ruku2Adapter) {
                Ruku2Adapter qrk2Adapter = (Ruku2Adapter) adapter;
                WaitRukuInfo itemByItemID = qrk2Adapter.getItemByItemID(id);
                if (itemByItemID != null) {
                    itemByItemID.setPihao(newPihao);
                    qrk2Adapter.notifyDataSetChanged();
                }
            }
            //            presenter.startSearch(dpid, partno, storageID);
        }
    }

    @Override
    public void showError(String msg) {
        showMsgDialog(msg);
    }

    @Override
    public int preLoading(String msg) {
        return showProgressWithID(msg);
    }

    @Override
    public void cancelLoading(int id) {
        cancelDialogById(id);
    }



    static class Ruku2Adapter extends BaseRvAdapter<WaitRukuInfo> {

        static class ItemListener implements View.OnClickListener{
            WaitRukuInfo xiaopiaoInfo;

            QuickRuku2Activity finalActivity;
            BaseRvViewholder holder;

            public ItemListener(WaitRukuInfo mInfo, QuickRuku2Activity finalActivity,
                                BaseRvViewholder holder) {
                this.xiaopiaoInfo = mInfo;
                this.finalActivity = finalActivity;
                this.holder = holder;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.item_shangjia_btn:
                        EditText edPlace = holder.getView(R.id.item_shangjia_ed_place);
                        edPlace.requestFocus();
                        if(finalActivity==null){
                            MyToast.showToast(finalActivity, "程序异常,请退出程序");
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
                        break;
                    case R.id.item_shangjia_modify_batchno:
                        String pihao = xiaopiaoInfo.getPihao();
                        XiaopiaoInfo tempInfo = new XiaopiaoInfo();
                        tempInfo.setPihao(pihao);
                        tempInfo.setPid(xiaopiaoInfo.getId());
                        tempInfo.setCodeStr(xiaopiaoInfo.getId());
                        if (finalActivity != null) {
//                            finalActivity.showEditView(tempInfo);
                            View mView = holder.getView(R.id.item_shangjia_modify_batchno);
                            finalActivity.showPopMenu(tempInfo,mView );
                        }
                        break;
                }
            }
        }
        public WaitRukuInfo nowInfo;
        public Ruku2Adapter(List<WaitRukuInfo> mData, int layoutId, Context mContext) {
            super(mData, layoutId, mContext);
        }

        public WaitRukuInfo getItemByItemID(String searchId) {
            if (mData != null) {
                for (WaitRukuInfo info : mData) {
                    if (searchId.equals(info.getId())) {
                        return info;
                    }
                }
            }
            return null;
        }
        @Override
        protected void convert(BaseRvViewholder holder, WaitRukuInfo item) {
            int position = holder.position;
            final WaitRukuInfo xiaopiaoInfo = item;
            TextView tv = (TextView) holder.getView(R.id.item_shangjia_tv);
            TextView tvStatus = (TextView) holder.getView(R.id.item_shangjia_tv_status);
            final EditText edPlace = holder.getView(R.id.item_shangjia_ed_place);
            QuickRuku2Activity activity = null;
            if (mContext instanceof QuickRuku2Activity) {
                activity = (QuickRuku2Activity) mContext;
            } else {
                throw new IllegalArgumentException(getClass() + " 构造方法传入的context必须为QuickRuku2Activity ");
            }

            final QuickRuku2Activity finalActivity = activity;
            ItemListener mListner = new ItemListener(xiaopiaoInfo, finalActivity, holder);
            holder.setOnclick(R.id.item_shangjia_btn, mListner);
            View btn = holder.getView(R.id.item_shangjia_modify_batchno);
            btn.setVisibility(View.VISIBLE);
            holder.setOnclick(R.id.item_shangjia_modify_batchno, mListner);
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
