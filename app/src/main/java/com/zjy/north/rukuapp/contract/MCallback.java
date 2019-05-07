package com.zjy.north.rukuapp.contract;

import java.util.List;

/**
 Created by 张建宇 on 2019/5/7. */
public interface MCallback<T> {
    void callback(List<T> data);

    void onError(String msg);
}
