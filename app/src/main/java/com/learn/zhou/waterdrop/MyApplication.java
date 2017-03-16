package com.learn.zhou.waterdrop;

import android.app.Application;

import com.fanyu.litelibrary.lite;

/**
 * Created by Administrator on 2017/3/15.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        lite.Is.init(this);
    }
}
