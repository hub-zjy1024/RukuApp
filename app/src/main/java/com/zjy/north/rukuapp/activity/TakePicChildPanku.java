package com.zjy.north.rukuapp.activity;

import com.zjy.north.rukuapp.task.CheckUtils;

import utils.common.UploadUtils;

public class TakePicChildPanku extends TakePicActivity {

    @Override
    public String getUploadRemotePath() {
        String remotePath;
        String remoteName = UploadUtils.getPankuRemoteName(pid);
        if (CheckUtils.isAdmin()) {
            remotePath = UploadUtils.getTestPath(pid);
        } else {
            remotePath = "/" + UploadUtils.getCurrentDate() + "/pk/" + remoteName + ".jpg";
        }
        return remotePath;
    }

    @Override
    public boolean check(int len) {
        return super.check(3);
    }
}
