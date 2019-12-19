package com.zjy.north.rukuapp.entity;

/**
 Created by 张建宇 on 2019/5/8.
 */
public class WaitRukuInfo {
    //    String id = dObj.getString("ID");
    //    String makeDate = dObj.getString("制单日期");
    //    String partno = dObj.getString("型号");
    //    String counts = dObj.getString("数量");
    //    String factory = dObj.getString("厂家");
    //    String comeFrom = dObj.getString("产地");
    //    String pihao = dObj.getString("批号");
    private String id;
    private String makeDate;
    private String partno;
    private String counts;
    private String factory;
    private String comeFrom;
    private String pihao;
    private int leftCount;
    private String status;
    private String place;

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public WaitRukuInfo(String id, String makeDate, String partno, String counts, String factory, String comeFrom, String pihao) {
        this.id = id;
        this.makeDate = makeDate;
        this.partno = partno;
        this.counts = counts;
        this.factory = factory;
        this.comeFrom = comeFrom;
        this.pihao = pihao;
    }

    @Override
    public String toString() {
        return
                "id='" + id + '\'' + "\n" +
                        "制单日期='" + makeDate + '\'' + "\n" +
                        "型号='" + partno + '\'' + "\n" +
                        "数量='" + counts + '\'' + "\t" + "厂家='" + factory + '\'' + "\n" +
                        "产地='" + comeFrom + '\'' + "\t" + "批号='" + pihao + '\'' + "\n" +
                        "位置='" + place+"'"+
                        "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMakeDate() {
        return makeDate;
    }

    public void setMakeDate(String makeDate) {
        this.makeDate = makeDate;
    }

    public String getPartno() {
        return partno;
    }

    public void setPartno(String partno) {
        this.partno = partno;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getPihao() {
        return pihao;
    }

    public void setPihao(String pihao) {
        this.pihao = pihao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
