package com.zjy.north.rukuapp.contract;

import android.content.Context;

import com.zjy.north.rukuapp.entity.WaitRukuInfo;

import java.util.List;

/**
 Created by 张建宇 on 2019/5/9. */
public class PiliangRukuContract {
    public interface IPresenter extends QuickRukuContract.IPresenter {
        void ruku(List<WaitRukuInfo> infos, String uid);
    }
    public static class Presenter implements IPresenter {
        private Context mContext;
        QuickRukuContract.IView mView;

        @Override
        public void getData(String code) {

        }

        @Override
        public void ruku(WaitRukuInfo info, String Place, String uid, String RKCount) {

        }

        @Override
        public void ruku(List<WaitRukuInfo> infos, String uid) {

        }
    }



}
