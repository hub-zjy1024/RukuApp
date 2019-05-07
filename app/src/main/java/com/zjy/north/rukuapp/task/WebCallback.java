package com.zjy.north.rukuapp.task;

/**
 Created by 张建宇 on 2017/8/17. */

public interface WebCallback<T> {
     void errorCallback(Throwable e);

     void okCallback(T obj);

     void otherCallback(Object obj);

}
