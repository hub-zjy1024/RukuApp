package com.zjy.north.rukuapp.contract;

import android.util.Log;

import com.zjy.north.rukuapp.task.TaskManager;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import utils.net.wsdelegate.ChuKuServer;

/**
 * Created by 张建宇 on 2020/1/2.
 */
public class QuickRK2Contract {
    public interface IView2 extends KucunEditContract.IView {
        void onUpdate2(boolean isOk, String id, String newPihao);
    }

    public static class Present extends KucunEditContract.Presenter {
        public Present(IView2 mView) {
            super(mView);
        }

        //        IView2 iview;
        //
        //        public Present(KucunEditContract.IView mView, IView2 iview) {
        //            super(mView);
        //            this.iview = iview;
        //        }

        @Override
        public void setPihao(final String detailId, final String oldph, final String newph,
                             final String uid) {
            final int progressId = mView.preLoading("正在修改批号");
            Runnable mRun = new Runnable() {
                @Override
                public void run() {

                    String errMsg = "未知错误";
                    try {
                        //                        int iPid = Integer.parseInt(detailId);
//                        String setRes = ChuKuServer.SetPiHaoByApplyCustomsLocal(detailId, oldph, newph, uid);
                        String setRes = ChuKuServer.SetPiHaoByApplyCustoms(detailId, oldph, newph, uid);
                        if ("成功".endsWith(setRes)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mView.cancelLoading(progressId);
                                    IView2 realView;
                                    try {
//                                        if (mView instanceof IView2) {
//                                        }
                                        realView = (IView2) mView;
                                        realView.onUpdate2(true, detailId, newph);
                                    } catch (Throwable e) {
                                        Log.e("zjy", "QuickRK2Contract->run(): onUpdate2 ERR==", e);
                                    }
                                }
                            });
                            return;
                        } else {
                            throw new Exception("返回有误," + setRes);
                        }
                    } catch (final IOException e) {
                        errMsg = "查询失败," + e.getMessage();
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        errMsg = "查询失败:xmlParse," + e.getMessage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errMsg = "查询不到相关信息," + e.getMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errMsg = "其他," + e.getMessage();
                    }
                    final String finalErrMsg = errMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.cancelLoading(progressId);
                            mView.showError(finalErrMsg);
                        }
                    });
                }
            };
            TaskManager.getInstance().execute(mRun);
        }
    }
}
