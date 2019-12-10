package com.zjy.north.rukuapp.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.net.HttpUtils;
import utils.net.wsdelegate.ChuKuServer;

/**
 Created by 张建宇 on 2018/4/17. */
public class StorageUtils {
    public static String getCurrentIp() {
        String url = "http://172.16.6.101:802/ErpV5IP.asp";
        try {
            return HttpUtils.create(url).getBodyString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Map<String, Object> getStorageAndIp() throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        String bodyString = "";
        String info = "";
        String url = "http://172.16.6.101:802/ErpV5IP.asp";
        try {
            String ip = HttpUtils.create(url).getBodyString();
            map.put("ip", ip);
            bodyString = ChuKuServer.GetStoreRoomIDByIP(ip);
            map.put("StorInfo", bodyString);
        } catch (IOException e) {
            e.printStackTrace();
            info = e.getMessage();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (bodyString.equals("")) {
            throw new IOException("获取库房ID出错：" + info);
        }
        return map;
    }
    public static String getStorageByIp() throws IOException {
        //        GetStoreRoomIDByIP
        String ip = StorageUtils.getCurrentIp();
        if ("".equals(ip)) {
            throw new IOException("获取当前IP失败");
        }
        String bodyString = "";
        String info = "";
        try {
            bodyString = ChuKuServer.GetStoreRoomIDByIP(ip);
        } catch (IOException e) {
            info = e.getMessage();
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (bodyString.equals("")) {
            throw new IOException("获取库房ID出错：" + info);
        }
        return bodyString;
    }

    public static String getStorageIDFromJson(String info) {
        return getStorageInfo(info, "StoreRoomID");
    }
    public static String getStorageInfo(String info, String tag) {
        String storageID = "";
        try {
            JSONObject obj = new JSONObject(info);
            JSONArray table = obj.getJSONArray("表");
            storageID = table.getJSONObject(0).getString(tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return storageID;
    }

    public static String getKuquID(String storageInfo) {
        return StorageUtils.getStorageInfo(storageInfo, "ChildStorageID");
    }

}
