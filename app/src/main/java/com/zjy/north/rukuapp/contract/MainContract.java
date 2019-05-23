package com.zjy.north.rukuapp.contract;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.entity.SpSettings;
import com.zjy.north.rukuapp.task.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.common.UpdateClient;
import utils.net.wsdelegate.Login;
import utils.net.wsdelegate.MartService;
import utils.net.wsdelegate.WebserviceUtils;

/**
 Created by 张建宇 on 2018/11/16. */
public class MainContract {
    public interface MainAcViewWithScan extends MainAcView{
        public void codeLogin(String code);
    }
    public interface MainAcView extends BaseView<MainAcPresenter> {

        void onLoginSuccess(String name);

        void onLoginFailed(String msg);

        void showProgress(String msg);

        void cancelLoading();

        void getUpdateInfo(String info);

        void startNewActivity();
    }
    interface MainCallBack{
        void backRes(String res);

    }
     static class MainAcDataSrc{
         private UpdateClient mClient;
         private SharedPreferences spUserinfo;
         private Context mContext;
         public MainAcDataSrc(Context mContext) {
             this.mContext = mContext;
             this.mClient = new UpdateClient(mContext);
             spUserinfo = mContext.getSharedPreferences(SpSettings.PREF_USERINFO, 0);
         }

         private String lName;
         private String lPwd;


         public void getLoginResult(final String version, final String name, final String pwd, final MainCallBack callBack) {

             new Thread() {
                 @Override
                 public void run() {
                     try {
                         boolean enable = mClient.checkVersionAvailable();
                         if (!enable) {
                             throw new IOException("请更新到最新版本！！！");
                         }
                         String deviceID = WebserviceUtils.DeviceID + "," +
                                 WebserviceUtils.DeviceNo;
                         String wcfResult = MartService.AndroidLogin("", name, pwd, deviceID, version);
                         lName = name;
                         lPwd = pwd;
                         callBack.backRes(wcfResult);
                     } catch (IOException e) {
                         e.printStackTrace();
                         String msg = e.getMessage();
                         if (e.getCause() != null) {
                             msg+= e.getMessage();
                         }
                         callBack.backRes("IO:" + msg);
                     } catch (XmlPullParserException e) {
                         callBack.backRes("xml:"+e.getMessage());
                         e.printStackTrace();
                     }
                 }
             }.start();
         }


         private void getUserInfoDetail(final String uid) {
             new Thread() {
                 @Override
                 public void run() {
                     boolean success = false;
                     while (!success) {
                         try {
                             Map<String, Object> result = getUserInfo(uid);
                             spUserinfo.edit().putInt("cid", (int) result.get("cid")).putInt("did",
                                     (int) result.get("did")).
                                     putString("oprName", (String) result.get("oprName"))
                                     .putString("limitCorp", (String) result.get("limitCorp"))
                                     .apply();
                             success = true;
                         } catch (IOException | XmlPullParserException e) {
                             e.printStackTrace();
                         } catch (JSONException e) {
                             e.printStackTrace();
                         } catch (NumberFormatException e) {
                             e.printStackTrace();
                         }
                     }
                 }
             }.start();
         }

         private Map<String, Object> getUserInfo(String uid) throws IOException,
                 XmlPullParserException, JSONException {
             String wcfResult = Login.GetUserInfoByUID("", uid);
             JSONObject object = new JSONObject(wcfResult);
             JSONArray jarr = object.getJSONArray("表");
             JSONObject info = jarr.getJSONObject(0);
             String cid = info.getString("CorpID");
             String did = info.getString("DeptID");
             String name = info.getString("Name");
             HashMap<String, Object> result = new HashMap<>();
             result.put("cid", Integer.parseInt(cid));
             result.put("did", Integer.parseInt(did));
             result.put("oprName", name);
             result.put("limitCorp",info.getString("LimitInvoiceCorpOfBill"));
             return result;
         }

         void codeLogin(String code, final MainCallBack mCallback) {

             Runnable updateRun = new Runnable() {
                 @Override
                 public void run() {
                     //                     登录操作...返回结果
                     String result = "";
                     mCallback.backRes(result);
                 }
             };
             TaskManager.getInstance().execute(updateRun);
         }
         void saveData(boolean isAuto, boolean isSaved) {
             if (!spUserinfo.getString("name", "").equals(lName)) {
                 spUserinfo.edit().clear().apply();
                 //登录成功之后调用，获取相关信息
                 SharedPreferences.Editor edit = spUserinfo.edit();
                 edit.putString("name", lName);
                 edit.putString("pwd", lPwd);
                 edit.apply();
                 getUserInfoDetail(lName);
             } else {
                 String limitCorp = spUserinfo.getString("limitCorp", "");
                 Log.e("zjy", "Nh_MainActivity->handleMessage(): SavedLimitCorp==" + limitCorp);
                 if (limitCorp.equals("")) {
                     getUserInfoDetail(lName);
                 }
                 MyApp.ftpUrl = spUserinfo.getString("ftp", "");
             }
             //是否记住密码
             ifSavePwd(isSaved, isAuto, lName, lPwd);
         }

         private void ifSavePwd(boolean saveOrNot, boolean auto, String name, String pwd) {
             if (saveOrNot) {
                 SharedPreferences.Editor editor = spUserinfo.edit();
                 editor.putString("name", name);
                 editor.putString("pwd", pwd);
                 editor.putBoolean("remp", saveOrNot);
                 editor.putBoolean("autol", auto);
                 editor.apply();
             } else {
                 spUserinfo.edit().clear().commit();
             }
         }

         void startUpdate(final MainCallBack callBack) {
             mClient = new UpdateClient(mContext) {
                 @Override
                 public void getUpdateInfo(HashMap<String, String> map) {
                     String sCode = map.get("code");
                     final String sContent = map.get("content");
                     final String sDate = map.get("date");

                     String info = "更新说明:\n";
                     info += "更新时间:" + sDate + "\n";
                     info += "更新内容:" + sContent;
                     callBack.backRes(info);
                 }
             };

             Runnable updateRun = new Runnable() {
                 @Override
                 public void run() {
                     mClient.startUpdate();
                 }
             };
             TaskManager.getInstance().execute(updateRun);
         }
    }
    public static class MainAcPresenter{
        private Handler mHandler = new Handler();
        private MainAcDataSrc dataSrc;
        private MainAcView mView;
        private Context mContext;
        public MainAcPresenter(Context mContext, MainAcView mView) {
            this.dataSrc = new MainAcDataSrc(mContext);
            this.mContext = mContext;
            this.mView = mView;
        }

        public void login(final String name, final String pwd, String version) {
            mView.showProgress("正在登录");
            dataSrc.getLoginResult(version, name, pwd, new MainCallBack() {
                @Override
                public void backRes(String s) {
                    final String wcfResult = s;
                    String[] resArray = wcfResult.split("-");
                    String retMsg = "";
                    int retCode = 0;
                    if (resArray[0].equals("SUCCESS")) {
                        retCode = 1;
                    } else {
                        retMsg = "登录失败," + wcfResult;
                    }
                    final int finalRetCode = retCode;
                    final String finalRetMsg = retMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalRetCode == 0) {
                                mView.onLoginFailed(finalRetMsg);
                            }else{
                                mView.onLoginSuccess(name);
                                mHandler.removeCallbacksAndMessages(null);
                            }
                            mView.cancelLoading();
                        }
                    });
                }
            });
        }

        public void saveData(boolean isAuto, boolean isSaved) {
            dataSrc.saveData(isAuto, isSaved);
        }

        public void codeLogin(String code) {
            dataSrc.codeLogin(code, new MainCallBack() {
                @Override
                public void backRes(String res) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            });
        }

        public void CheckUpdate() {
            dataSrc.startUpdate(new MainCallBack() {
                @Override
                public void backRes(final String res) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.getUpdateInfo(res);
                        }
                    });
                }
            });
        }
    }
}
