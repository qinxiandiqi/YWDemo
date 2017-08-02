package com.qinxiandiqi.ywdemo;

import android.app.Application;

/**
 * Created by Jianan on 2017/8/2.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IM.getIM().init(this);
    }
}
