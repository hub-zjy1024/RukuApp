package com.zjy.north.rukuapp.activity;

import com.zjy.north.rukuapp.picupload.ContinnueFtpUploader;
import com.zjy.north.rukuapp.picupload.PicUploader;

public class TakePic2Activity extends TakePic2Ac {

    @Override
    protected PicUploader getUpLoader() {
        return new ContinnueFtpUploader(mUrl);
    }

}
