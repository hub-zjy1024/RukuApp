package com.zjy.north.rukuapp.contract;

import android.content.Context;
import android.os.Handler;

import com.zjy.north.rukuapp.entity.RKInfo;
import com.zjy.north.rukuapp.task.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.net.wsdelegate.RKServer;

/**
 Created by 张建宇 on 2019/5/7. */
public class QuickRukuContract {
    public interface IView extends BaseView<IPresenter> {
        void loading(String msg);

        void cancelLoading();

        void updateData(List<RKInfo> data);

        void showAlert(String msg);
    }

    public interface IPresenter {
        void getData(String code);
    }

    public static class Presenter implements IPresenter {
        private Context mContext;
        private DataSrc dataSrc;
        IView mView;

        public Presenter(Context mContext, IView mView) {
            this.mContext = mContext;
            this.mView = mView;
            dataSrc = new DataSrc(mContext);
        }

        public void getData(String code) {
            mView.loading("正在搜索" + code);
            dataSrc.getData(code, new DataSrc.Listener() {

                @Override
                public void callback(List<RKInfo> data) {
                    mView.updateData(data);
                    mView.cancelLoading();
                }

                @Override
                public void onError(String msg) {
                    mView.showAlert(msg);
                    mView.cancelLoading();
                }
            });
        }
    }

    private static class DataSrc {
        private Context mContext;
        private android.os.Handler mHandler = new Handler();

        public DataSrc(Context mContext) {
            this.mContext = mContext;
        }

        interface Listener extends MCallback<RKInfo> {
        }

        public void getData(final String code, final Listener mCallBack) {

            Runnable mRun = new Runnable() {
                @Override
                public void run() {
                    final List<RKInfo> mList = new ArrayList<>();
                    String errMsg = "";
                    int errCode = 1;
                    try {
                        String mData = RKServer.GetApplyCustomInfo(code);

                        JSONObject mobj = new JSONObject(mData);
                        String status = mobj.getString("status");
                        if ("200".equals(status)) {
                            JSONArray list = mobj.getJSONArray("list");
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject dObj = list.getJSONObject(i);
                                int pid = dObj.getInt("单据号");
                                int detailID = dObj.getInt("明细ID");
                                String partNo = dObj.getString("型号");
                                String factory = dObj.getString("厂家");
                                String pihao = dObj.getString("批号");
                                String description = dObj.getString("描述");
                                String fengzhuang = dObj.getString("封装");
                                int leftCount = dObj.getInt("剩余数量");
                                String rkDate = dObj.getString("入库日期");
                                String FormID = dObj.getString("FormID");
                                String kpType = dObj.getString("开票类型");
                                String kpCompName = dObj.getString("开票公司");
                                String pidType = dObj.getString("单据类型");
                                RKInfo info = new RKInfo(partNo, factory, pihao, fengzhuang, description, pid, detailID,
                                        leftCount,
                                        rkDate, FormID, kpType, kpCompName, pidType);
                                mList.add(info);
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCallBack.callback(mList);
                                }
                            });
                        } else if ("400".equals(status)) {

                        }
                        errCode = 0;
                    } catch (IOException e) {
                        errMsg = e.getMessage();
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        errMsg = e.getMessage();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        errMsg = e.getMessage();
                        e.printStackTrace();
                    }
                    if (errCode == 1) {
                        final String finalErrMsg = errMsg;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallBack.onError(finalErrMsg);
                            }
                        });

                    }
                }
            };
            TaskManager.getInstance().execute(mRun);
        }
    }
}
