package com.zjy.north.rukuapp.entity;

/**
 Created by 张建宇 on 2019/5/9. */
public class RukuInfoItem extends WaitRukuInfo {

    private boolean isCheckd = false;

    public RukuInfoItem(String id, String makeDate, String partno, String counts, String factory, String comeFrom, String
            pihao, boolean isCheckd) {
        super(id, makeDate, partno, counts, factory, comeFrom, pihao);
        this.isCheckd = isCheckd;
    }

    public RukuInfoItem(String id, String makeDate, String partno, String counts, String factory, String comeFrom, String pihao) {
        super(id, makeDate, partno, counts, factory, comeFrom, pihao);
    }

    public boolean isCheckd() {
        return isCheckd;
    }

    public void setCheckd(boolean checkd) {
        isCheckd = checkd;
    }

}
