package com.zjy.north.rukuapp.entity;

/**
 Created by 张建宇 on 2017/5/3. */

public class PreChukuDetailInfo {
    private String partNo;
    private String fengzhuang;
    private String pihao;
    private String factory;
    private String description;
    private String notes;
    private String place;
    private String counts;
    private String leftCounts;
    private String proLevel;
    private String initialDate;
    private String detailID;

    public String getProLevel() {
        return proLevel;
    }

    public void setProLevel(String proLevel) {
        this.proLevel = proLevel;
    }

    public String getDetailID() {
        return detailID;
    }

    public void setDetailID(String detailID) {
        this.detailID = detailID;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public PreChukuDetailInfo() {
    }

    public PreChukuDetailInfo(String partNo, String fengzhuang, String pihao, String factory, String description, String notes, String place, String counts, String leftCounts) {
        this.partNo = partNo;
        this.fengzhuang = fengzhuang;
        this.pihao = pihao;
        this.factory = factory;
        this.description = description;
        this.notes = notes;
        this.place = place;
        this.counts = counts;
        this.leftCounts = leftCounts;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getFengzhuang() {
        return fengzhuang;
    }

    public void setFengzhuang(String fengzhuang) {
        this.fengzhuang = fengzhuang;
    }

    public String getPihao() {
        return pihao;
    }

    public void setPihao(String pihao) {
        this.pihao = pihao;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        if (notes == null) {
            notes = "";
        }
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public String getLeftCounts() {
        return leftCounts;
    }

    public void setLeftCounts(String leftCounts) {
        this.leftCounts = leftCounts;
    }

    @Override
    public String toString() {
        return
                "型号=" + partNo +"\n"+
                "封装=" + fengzhuang +"\n"+
                "批号=" + pihao + "\n"+
                "厂家=" + factory + "\n"+
                "描述=" + description + "\n"+
                "备注=" + notes + "\n"+
                "位置=" + place + "\n"+
                "数量=" + counts + "\n"+
                "剩余数量=" + leftCounts ;
    }
}
