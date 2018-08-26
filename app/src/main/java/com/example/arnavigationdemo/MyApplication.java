package com.example.arnavigationdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;


/**
 * Created by ming on 2018/7/17.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SDKInitializer.initialize(getApplicationContext());
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
