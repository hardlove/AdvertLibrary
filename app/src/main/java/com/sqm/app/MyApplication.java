package com.sqm.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.thl.thl_advertlibrary.config.AdvertConfig;
import com.thl.thl_advertlibrary.config.AppBuildConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppBuildConfig.init(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, "huawei");

        //广告初始化
        AdvertConfig.GENERAL_HOST_BUSS = false? AppConfig.GENERAL_HOST_BUSS : AppConfig.GENERAL_HOST_BUSS_TEST;
        AdvertConfig.appName = Utils.getApp().getResources().getString(R.string.app_name);
        AdvertConfig.initAdvert(this, "");
    }
}
