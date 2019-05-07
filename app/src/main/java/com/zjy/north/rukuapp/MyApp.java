package com.zjy.north.rukuapp;

import android.app.Application;

import com.zjy.north.rukuapp.service.LogUploadService;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import utils.common.LogRecoder;

/**
 Created by js on 2016/12/27. */

public class MyApp extends Application implements Thread.UncaughtExceptionHandler{
    public static String id;
    public static String ftpUrl;
    public static LogRecoder myLogger;
    public static ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    @Override
    public void onCreate() {
        super.onCreate();
        final String logFileName = LogUploadService.logFileName;;
        myLogger = new LogRecoder(logFileName);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        myLogger.writeError(ex, "===[AppCrash]=====\n");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}