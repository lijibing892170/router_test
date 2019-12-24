package com.ljb.router_test;

import android.app.Application;

import com.zhx.lib_router_lrouter.lrouter.LRouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LRouter.init(this);
    }
}
