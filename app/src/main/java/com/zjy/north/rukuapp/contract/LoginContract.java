package com.zjy.north.rukuapp.contract;

import android.content.Context;

/**
 Created by 张建宇 on 2019/4/29. */
public class LoginContract {
    public interface ILoginView extends BaseView<IPresenter> {
        void loading(String msg);
    }

    public interface IPresenter {
        void login(String name, String password);

        void updateVersion();
    }

    public static class Presenter extends BasePresenter<LoginDs> implements IPresenter {
        private LoginDs mDataSrc;

        public Presenter(Context mContext) {
            super(mContext);
            mDataSrc = new LoginDs(mContext);
        }

        @Override
        public void login(String name, String password) {

        }

        @Override
        public void updateVersion() {

        }

        @Override
        public LoginDs getProvider() {
            return new LoginDs(mContext);
        }
    }

    private static class LoginDs {
        private Context mContext;
        public LoginDs(Context mContext) {
            this.mContext = mContext;
        }
    }
}
