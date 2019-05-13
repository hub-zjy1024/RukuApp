package com.zjy.north.rukuapp.activity;

import com.zjy.north.rukuapp.picupload.FtpUploader;
import com.zjy.north.rukuapp.picupload.PicUploader;

/**
 Created by 张建宇 on 2019/5/11. */
public class FtpStraigtTakePic2 extends TakePic2Ac {

    @Override
    protected PicUploader getUpLoader() {
        return  new FtpUploader(mUrl);
    }
}
