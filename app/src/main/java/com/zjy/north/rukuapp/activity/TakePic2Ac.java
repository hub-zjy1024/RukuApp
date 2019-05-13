package com.zjy.north.rukuapp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.task.CheckUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import utils.common.MyFileUtils;
import utils.common.UploadUtils;
import utils.dbutils.PicUploadDB;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;

/**
 Created by 张建宇 on 2019/5/10. */
public class TakePic2Ac extends TakePicActivity {
    private final static int FTP_CONNECT_FAIL = 3;
    private final static int PICUPLOAD_SUCCESS = 0;
    private final static int PICUPLOAD_ERROR = 1;
    private final static int PICUPLOAD_ERROR_NO_SD = 4;
    private LinearLayout llResult;
    PicUploadDB picDb;


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PICUPLOAD_ERROR:
                btn_takepic.setEnabled(true);
                toolbar.setVisibility(View.GONE);
                break;
            case PICUPLOAD_SUCCESS:
                Object obj = msg.obj;
                final TextView textView = (TextView) obj;
                String nowTag = textView.getTag().toString();
                textView.setText("图片:" + nowTag + "上传完成 OK···");
                picHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llResult.removeView(textView);
                    }
                }, 2000);

                showMsgToast( "上传成功，后台剩余图片：" + (MyApp.cachedThreadPool.getActiveCount() - 1));
                break;
            case FTP_CONNECT_FAIL:
                showMsgToast( "连接ftp服务器失败，请检查网络");
                break;
            case PICUPLOAD_ERROR_NO_SD:
                showMsgToast( "sd卡不存在，不可用后台上传");
                btn_commit.setEnabled(false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        llResult = getViewInContent(R.id.take_pic2_result_containner);
        picDb = new PicUploadDB(this);
    }

    @Override
    public void init() {
        super.init();
    }

    void saveTOSD(File upFile, InputStream in) {

        File dyjImgDir = upFile.getParentFile();
        if (!dyjImgDir.exists()) {
            dyjImgDir.mkdirs();
        }
        byte[] mBit = new byte[1024 * 50];
        int len = 0;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(upFile);
            while ((len = in.read(mBit)) != -1) {
                fos.write(mBit, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override

    public void upLoadPic(final int cRotate, final byte[] picData) {
        if (!beforeCommit()) {
            return;
        }
        //载入水印图
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);

        final TextView textView = initStatuView();

        final NotifyMgr notifyer = new NotifyMgr(this, pid);
        final UpLoadeLogger uploadLogger = new UpLoadeLogger();
        Runnable tempThread = new Runnable() {
            @Override
            public void run() {
                uploadLogger.setStartTime();
                notifyer.sendNotify();
                String insertPath = "";
                boolean isStop = false;
                int counts = 0;
                String ftpStr = FTPUtils.getMainStr();
                InputStream transformedImg = null;
                String tempFile = UploadUtils.getRandomNumber(6);
                File   upFile = new File(Environment.getExternalStorageDirectory(), "dyj_img/" + tempFile + ".jpg");
                try {
                    transformedImg = getTransformedImg(cRotate, picData, bitmap);
                    saveTOSD(upFile, transformedImg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError error) {
                    error.printStackTrace();
                    picHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showMsgToast("请选择合适的尺寸，重新拍摄");
                            showSizeChoiceDialog(parameters);
                        }
                    });
                    return;
                }
                while (!isStop) {
                    String mUrl = FtpManager.mainAddress;
                    String uploadTag = "CKTZ";
                    String remotePath = "";
                    String remoteName = UploadUtils.getChukuRemoteName(pid);
                    String notifyName = remoteName.substring(remoteName.lastIndexOf("_") + 1);


                    String msg = "";
                    FileInputStream inSrc = null;
                    try {
                         inSrc = new FileInputStream(upFile);
                        boolean upSuccess = false;
                        remotePath = getUploadRemotePath();
                        insertPath = UploadUtils.createInsertPath(mUrl, remotePath);
//                        Log.e("zjy", "TakePic2Activity->run(): InsertPath==" + insertPath);
                        String type = getUploadFlag();
                        mUploader.upload(pid, transformedImg, remotePath, loginID, cid + "", did + "", remoteName,
                                type,   insertPath);
                        upLoadSuccess(notifyer, remoteName, uploadLogger, textView);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (inSrc != null) {
                            try {
                                inSrc.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                MyApp.myLogger.writeError(getClass().getName() + ", close Error:" + notifyName +
                                        "-" + e1.getMessage());

                            }
                        }
                        String tempMsg = counts + ":" + e.getMessage();
                        String fName = upFile.getName();
                        MyApp.myLogger.writeError(getClass().getName()+",upload Exception:" + fName +
                                "-" + tempMsg);
                        notifyer.chageMsg(notifyName + "上传失败，正在重新上传", 0);
                        updateFailedInfo(textView);
                    }
                    counts++;
                    uploadLogger.tryTime = counts;
                    if (counts >= 3) {
                        upLoadFailed(notifyer, textView);
//                        try {
                            // 记录失败图片
                        // PicUploadInfo upInfo = new PicUploadInfo(upFile.getAbsolutePath(), pid,
//                            upInfo.okcount = 0;
//                            upInfo.ftpurl = ftpStr;
//                            int res = picDb.insertRecord(upInfo);
//                            if (res != 1) {
//                                MyApp.myLogger.writeError("reupload insert=" + res);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    }
                }
            }
        };
        MyApp.cachedThreadPool.execute(tempThread);
    }

    void updateFailedInfo(final TextView textView) {
        final String tag = textView.getTag().toString();
        picHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("图片:" + tag + "重新上传中....");
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void upLoadFailed(NotifyMgr notifyMgr, final TextView textView) {
        notifyMgr.cancelNotify();

        picHandler.post(new Runnable() {
            @Override
            public void run() {
                String nowTag = textView.getTag().toString();
                textView.setText("图片:" + nowTag + ",多次上传失败，请检查网络");
                picHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llResult.removeView(textView);
                    }
                }, 8000);
            }
        });
    }

    void upLoadSuccess(NotifyMgr notifyer, String remoteName, UpLoadeLogger mLogger, TextView textView) {

        double totalTime = mLogger.getTotalTime();
        double runTime = mLogger.getRunTime();
        int counts = mLogger.tryTime;
        int watiTime = (int) (totalTime - runTime);
        if (watiTime > 1) {
            MyApp.myLogger.writeBug(remoteName + ",Task Wait SoLong");
        }
        String strCounts = ",counts=";
        if (counts > 0) {
            strCounts += counts;
        } else {
            strCounts = "";
        }
        double checkRate = 1.6;
        String msg = "";
        if (runTime > checkRate) {
            msg = String.format("takepic2 finish %s,time=%f/%f wait=%d %s", remoteName, runTime, totalTime, watiTime, strCounts);
        } else {
            msg = String.format("takepic2 finish %s, time<%f,", remoteName, checkRate);
        }
        MyApp.myLogger.writeInfo(msg);
        Log.e("zjy", "TakePic2Activity->run(): uploadLog=" + msg);

        notifyer.cancelNotify();

        Message message = Message.obtain(picHandler, PICUPLOAD_SUCCESS);
        message.obj = textView;
        message.sendToTarget();
    }
    @Override
    public void getUrlAndFtp() {
        super.getUrlAndFtp();
        mUploader = getUpLoader();
    }

    public TextView initStatuView() {
        Date date = new Date();
        int minute = date.getMinutes();
        int ss = date.getSeconds();
        String upTime = minute + ":" + ss;
        final TextView textView = new TextView(mContext);
        textView.setBackgroundColor(getResources().getColor(R.color.color_tv_result_transparent));
        textView.setText("图片:" + upTime + "正在上传");
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        float fontSize = 18;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView.setTag(upTime);

        llResult.addView(textView);
        return textView;
    }

    @Override
    public boolean beforeCommit() {
        String errMsg = "";
        if (tempBytes == null) {
            errMsg = "当前程序出现错误,请重新进入";
        }
        final File sFile = MyFileUtils.getFileParent();
        if (sFile == null) {
            errMsg = "无法获取存储路径，请换用普通拍照功能";
        }
        if (kfFTP == null || "".equals(kfFTP)) {
            if (!CheckUtils.isAdmin()) {
                errMsg = "读取上传地址失败，请重启程序";
            }
        }
        if(!errMsg.equals("")){
            showMsgToast(errMsg  );
            return false;
        }
        mCamera.startPreview();
        auto.start();

        isPreview = true;
        toolbar.setVisibility(View.GONE);
        btn_takepic.setEnabled(true);

        return true;
    }


    static class UpLoadeLogger {
        long createTime;
        long startTime;

        int tryTime;

        public UpLoadeLogger() {
            this.createTime = System.currentTimeMillis();
        }

        public void setStartTime() {
            startTime = System.currentTimeMillis();
        }

        public double getRunTime() {
            double totalTime = (double) (System.currentTimeMillis() - startTime) / 1000;
            return totalTime;
        }

        public double getTotalTime() {
            double totalTime = (double) (System.currentTimeMillis() - createTime) / 1000;
            return totalTime;
        }
    }

    static class NotifyMgr {
        private static SparseArray<String> map = new SparseArray<>();

        private NotificationManager mgr;

        NotificationCompat.Builder builder;
        private int finalId;

        public NotifyMgr(Context mContext, String pid) {
            builder = new NotificationCompat.Builder(mContext);
            mgr = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.appicon);
            builder.setContentTitle("上传" + pid + "的图片").setSmallIcon(R.mipmap.appicon)
                    .setContentText("图片正在上传").setProgress(100, 0, false).setLargeIcon(largeIcon);
        }

        public void chageMsg(String msg, int
                progress) {
            builder.setProgress(100, progress, false)
                    .setSubText(msg);
        }

        public void cancelNotify() {
            mgr.cancel(finalId);
            map.remove(finalId);
        }


        public void sendNotify() {
            int id = (int) (Math.random() * 1000000);
            while (true) {
                if (map.get(id) != null) {
                    id = (int) (Math.random() * 1000000);
                } else {
                    map.put(id, String.valueOf(id));
                    break;
                }
            }
            finalId = id;
            mgr.notify(finalId, builder.build());
        }
    }
}
