package com.zjy.north.rukuapp.activity.base;

import android.graphics.Bitmap;

import com.zjy.north.rukuapp.picupload.PicUploader;

/**
 Created by 张建宇 on 2019/5/13. */
public abstract class BasePicTake extends BaseMActivity {
    public abstract String getUploadRemotePath();
    protected abstract PicUploader getUpLoader();

    public abstract void upLoadPic(final int cRotate, final byte[] picData);

    public abstract void upLoadPic(Bitmap mPic);

}
