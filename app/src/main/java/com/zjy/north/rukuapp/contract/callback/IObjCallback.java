package com.zjy.north.rukuapp.contract.callback;

/**
 * Created by 张建宇 on 2019/8/6.
 */
public interface IObjCallback<T> {
    void onError(String msg);

    void callback(T obj);
}
