package com.zjy.north.rukuapp.contract;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.zjy.north.rukuapp.entity.RKInfo;
import com.zjy.north.rukuapp.entity.RukuInfoItem;
import com.zjy.north.rukuapp.entity.WaitRukuInfo;
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

        void updateData(List<WaitRukuInfo> data);

        void showAlert(String msg);

        void onRukuSuccess(String mxId);

        void showToast(String msg);
    }

    public interface IPresenter {
        void getData(String code);

        void ruku(WaitRukuInfo info, String Place, String uid, String RKCount);

        void ruku(List<WaitRukuInfo> infos, String uid);
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
            dataSrc.getData(code, new DataSrc.MultiListener() {

                @Override
                public void callbackShangjia(String mxId) {
                    mView.onRukuSuccess(mxId);
                    mView.cancelLoading();
                }

                @Override
                public void callback(List<WaitRukuInfo> data) {
                    mView.updateData(data);
                    mView.cancelLoading();
                }

                @Override
                public void onError(String msg) {
                    mView.showToast(msg);
                    mView.cancelLoading();
                }
            });
        }

        @Override
        public void ruku(WaitRukuInfo info, String Place, String uid, String RKCount) {
            final String detailId = info.getId();

            mView.loading(detailId + "正在入库");
            dataSrc.ruku(info, Place, uid, RKCount, new DataSrc.MultiListener() {
                @Override
                public void callbackShangjia(String mxId) {
                    mView.onRukuSuccess(mxId);
                    mView.cancelLoading();
                }

                @Override
                public void callback(List<WaitRukuInfo> data) {

                }

                @Override
                public void onError(String msg) {
                    mView.showAlert(msg);
                    mView.cancelLoading();
                }
            });
        }

        @Override
        public void ruku(List<WaitRukuInfo> infos, String uid) {
            String sIds = "";
            for(int i=0;i<infos.size();i++) {
                sIds += "" + infos.get(i).getId() + "|";
            }
            if (sIds.endsWith("|")) {
                sIds = sIds.substring(0, sIds.length() - 1);
            }
            mView.loading(sIds + "正在入库");
            dataSrc.batchRk(infos, uid, new DataSrc.BatchListener() {
                @Override
                public void isAllFinished() {
                    mView.showAlert("已全部入库完成");
                    mView.cancelLoading();
                }

                @Override
                public void callback(List data) {

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

        interface MultiListener extends MCallback<WaitRukuInfo> {
            void callbackShangjia(String mxId);
        }

        interface BatchListener extends MCallback {
            void isAllFinished();
        }
        public void batchRk(final List<WaitRukuInfo> infos, final String uid, final
        BatchListener
                mCallBack) {
            Runnable mRun = new Runnable() {
                @Override
                public void run() {
                    String errMsg = "";
                    int errCode = 1;
                    try {
                        JSONArray mArray = new JSONArray();
                        String ids = "";
                        for (int i = 0; i < infos.size(); i++) {
                            WaitRukuInfo info = infos.get(i);
                            JSONObject rukuObj = new JSONObject();
                            String id = info.getId();
                            rukuObj.put("ID", id);
                            rukuObj.put("Place", "");
                            rukuObj.put("UserId", uid);
                            rukuObj.put("RKNumber", info.getLeftCount());
                            ids += id + "-";
                            mArray.put(rukuObj);
                        }
                        String jsonData = mArray.toString();
                        String res = RKServer.SetApplyCustomRuKu(jsonData);
                        JSONObject mobj = new JSONObject(res);
                        Log.e("zjy", "QuickRukuContract->run(): id==" + ids + ",Rukures=" + res);

                        String status = mobj.getString("status");
                        if ("200".equals(status)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCallBack.isAllFinished();
                                }
                            });
//                            Object objList = mobj.get("list");
//                            if (objList.equals( null) ) {
//                                throw new IOException("查询不到入库信息");
//                            }
//                            JSONArray list = (JSONArray) objList;
//                            if (list.length() > 0) {
//                                JSONObject mOBj = list.getJSONObject(0);
//                                final String mxId = mOBj.getString("明细ID");
//
//                            }else{
//                                throw new IOException("入库失败，ret=" + res);
//                            }
                        }else{
                            String msg = "返回异常，" + mobj.getString("message");
                            throw new IOException(msg);
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
                }};
            TaskManager.getInstance().execute(mRun);
        }
        public void ruku(WaitRukuInfo info, final String Place, final String uid, final String RKCount, final MultiListener
                mCallBack) {

            final String code = String.valueOf(info.getId());

            Runnable mRun = new Runnable() {
                @Override
                public void run() {
                    final List<RKInfo> mList = new ArrayList<>();
                    String errMsg = "";
                    int errCode = 1;
                    //                    {"ID":"0","Place":"小黑屋","UserId":"101","RKNumber":"100"}
                    try {
                        JSONObject rukuObj = new JSONObject();
                        rukuObj.put("ID", code);
                        rukuObj.put("Place", Place);
                        rukuObj.put("UserId", uid);
                        rukuObj.put("RKNumber", RKCount);
                        String json = rukuObj.toString();

                        String res = RKServer.SetCustomsRuKu(json);
                        JSONObject mobj = new JSONObject(res);
                        Log.e("zjy", "QuickRukuContract->run(): id==" + code + ",Rukures=" + res);

                        String status = mobj.getString("status");
                        if ("200".equals(status)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCallBack.callbackShangjia(code);
                                }
                            });

//                            Object objList = mobj.get("list");
//                            if (objList.equals(null) ) {
//                                throw new IOException(code + "已完成入库");
//                            }
//                            JSONArray list = (JSONArray) objList;
//                            if (list.length() > 0) {
//                                JSONObject mOBj = list.getJSONObject(0);
//                                final String mxId = mOBj.getString("明细ID");
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mCallBack.callbackShangjia(mxId);
//                                    }
//                                });
//                            }else{
//                                throw new IOException("入库失败，ret=" + res);
//                            }
                        }else{
                            String msg = "返回异常，" + mobj.getString("message");
                            throw new IOException(msg);
                        }
//                        String userINfo0 = Login.GetUserInfoByAllInfoByID("101");
//                        JSONObject mobj2 = new JSONObject(userINfo0);

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

        public static class ToastEx extends IOException {

        }
        public void getData(final String code, final MultiListener mCallBack) {

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
                        String message = mobj.getString("message");
                        if ("200".equals(status)) {
                            Object objList = mobj.get("list");
                            if (objList.equals(null)) {
                                throw new IOException("查询不到" + code + "的信息或已入库");
                            }
                            JSONArray list = (JSONArray) objList;
                            if ("已入库".equals(message)) {
                                Log.e("zjy", "QuickRukuContract->run(): ==shangjiaInfo=" + mData);
                                String sjId = "";
                                final List<WaitRukuInfo> waitList = new ArrayList<>();

                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject dObj = list.getJSONObject(i);
                                    int pid = dObj.getInt("单据号");
                                    int detailID = dObj.getInt("明细ID");
                                    sjId = detailID + "";
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

                                    //显示模拟数据
                                    String id = dObj.getString("单据号");
                                    String makeDate =rkDate;
                                    String partno = partNo;
                                    String counts =leftCount+"";
                                    String comeFrom = "";
                                    int rkLeftCont = leftCount;

                                    WaitRukuInfo mInfo = new RukuInfoItem(id, makeDate, partno, counts, factory, comeFrom, pihao);
                                    mInfo.setLeftCount(rkLeftCont);
                                    waitList.add(mInfo);
                                }
                                if (mList.size() == 0) {
                                    throw new IOException("查询不到" + code + "的上架数据");
                                }
                                final String finalSjId = sjId;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCallBack.callbackShangjia(finalSjId);
                                    }
                                });
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mCallBack.callback(waitList);
//                                    }
//                                });
                            } else if ("等待入库".equals(message)) {
                                final List<WaitRukuInfo> waitList = new ArrayList<>();

                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject dObj = list.getJSONObject(i);
                                    String id = dObj.getString("ID");
                                    String makeDate = dObj.getString("制单日期");
                                    String partno = dObj.getString("型号");
                                    String counts = dObj.getString("数量");
                                    String factory = dObj.getString("厂家");
                                    String comeFrom = dObj.getString("产地");
                                    String pihao = dObj.getString("批号");

                                    int rkLeftCont = dObj.getInt("未入库");
                                    WaitRukuInfo mInfo = new RukuInfoItem(id, makeDate, partno, counts, factory, comeFrom, pihao);
                                    mInfo.setLeftCount(rkLeftCont);
                                    waitList.add(mInfo);
                                }
                                if (waitList.size() > 0) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mCallBack.callback(waitList);
                                        }
                                    });
                                }else{
                                    throw new IOException("查询不到" + code + "的入库数据");
                                }
                            }
                        } else {
                            throw new IOException("接口返回异常，" + message);
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
