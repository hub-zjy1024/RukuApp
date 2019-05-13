package com.zjy.north.rukuapp.activity;

import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.task.CheckUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import utils.common.UploadUtils;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;
import utils.net.wsdelegate.MartStock;
import utils.net.wsdelegate.WebserviceUtils;

/**
 Created by 张建宇 on 2019/5/10. */
public class CaigouTakePic extends TakePicActivity {

    public boolean getInsertResultMain(String remoteName, String insertPath) throws IOException, XmlPullParserException {
        String result = MartStock.InsertSSCGPicInfo(WebserviceUtils.WebServiceCheckWord, cid,
                did, Integer.parseInt(loginID), pid, remoteName, insertPath, "SCCG");
        MyApp.myLogger.writeInfo("takepic insert:" + remoteName + "=" + result);
        return "操作成功".equals(result);
    }

    public String getUploadRemotePath() {
        String remoteName;
        String remotePath;
        remoteName = UploadUtils.createSCCGRemoteName(pid);
        remotePath = UploadUtils.getCaigouRemoteDir(remoteName + ".jpg");
        return remotePath;
    }

    public void getUrlAndFtp() {
        mUrl = FTPUtils.DB_HOST;
        if (CheckUtils.isAdmin()) {
            mUrl = FtpManager.mainAddress;
        }
    }
}
