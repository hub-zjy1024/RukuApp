package com.zjy.north.rukuapp.entity;

/**
 Created by 张建宇 on 2019/5/7. */
public class RKInfo extends BaseFileds {
    //    {
    //        "单据号": 2504900,
    //            "明细ID": 2793458,
    //            "剩余数量": 0,
    //            "入库日期": "2019-04-26T23:21:34.117",
    //            "FormID": "Customs-390729",
    //            "开票类型": "增值税票",
    //            "开票公司": "杭州比一比电子科技有限公司",
    //            "单据类型": "国外采购"
    //    }
    private int pid;
    private int detailID;
    private int leftCount;
    private String rkDate;
    private String FormID;
    private String kpType;
    private String kpCompName;

    private String pidType;

    public RKInfo(int pid) {
        this.pid = pid;
    }

    public RKInfo(String partNo, String factory, String pihao, String fengzhuang, String description, int pid) {
        super(partNo, factory, pihao, fengzhuang, description);
        this.pid = pid;
    }

    public RKInfo(String partNo, String factory, String pihao, String fengzhuang, String description, int pid, int detailID,
                  int leftCount, String rkDate, String formID, String kpType, String kpCompName, String pidType) {
        super(partNo, factory, pihao, fengzhuang, description);
        this.pid = pid;
        this.detailID = detailID;
        this.leftCount = leftCount;
        this.rkDate = rkDate;
        FormID = formID;
        this.kpType = kpType;
        this.kpCompName = kpCompName;
        this.pidType = pidType;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getDetailID() {
        return detailID;
    }

    public void setDetailID(int detailID) {
        this.detailID = detailID;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public String getRkDate() {
        return rkDate;
    }

    public void setRkDate(String rkDate) {
        this.rkDate = rkDate;
    }

    public String getFormID() {
        return FormID;
    }

    public void setFormID(String formID) {
        FormID = formID;
    }

    public String getKpType() {
        return kpType;
    }

    public void setKpType(String kpType) {
        this.kpType = kpType;
    }

    public String getKpCompName() {
        return kpCompName;
    }

    public void setKpCompName(String kpCompName) {
        this.kpCompName = kpCompName;
    }

    public String getPidType() {
        return pidType;
    }

    public void setPidType(String pidType) {
        this.pidType = pidType;
    }

    @Override
    public String toString() {
        return
                        "单据号='" + pid + '\'' + "\t" +"明细ID='" + detailID + '\'' + "\n" +
                        "剩余数量='" + leftCount + '\'' + "\n" +
                        "入库日期='" + rkDate + '\'' + "\n" +
                        "FormID='" + FormID + '\'' + "\n" +
                        "开票类型='" + kpType + '\'' + "\n" +
                        "开票公司='" + kpCompName + '\'' + "\n" +
                        "单据类型='" + pidType + '\'' + "\n" +
                        "型号='" + partNo + '\'' + "\n" +
                        "厂家='" + factory + '\'' + "\n" +
                        "批号='" + pihao + '\'' + "\t" +"封装='" + fengzhuang + '\'' + "\n" +
                        "描述='" + description + '\'' + "\n" +
                        "";
    }
}
