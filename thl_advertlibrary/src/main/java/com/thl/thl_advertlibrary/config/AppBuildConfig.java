package com.thl.thl_advertlibrary.config;

import android.os.Build;

import com.thl.thl_advertlibrary.utils.WebH5Init;

import java.util.Objects;

/**
 * Author：CL
 * 日期:2021/6/10
 * 说明：APP基本信息
 * 代替通过PackageManager获取时检索用户应用列表，规避上架收集用户信息问题
 **/
public class AppBuildConfig {
    private final String packageName;
    private final String versionName;
    private final int versionCode;
    private final String channel;

    private static AppBuildConfig instance;

    public static AppBuildConfig getInstance() {
        if (instance == null) {
            throw new NullPointerException("请先初始化！");
        }
        return instance;
    }

    public static void init(String packageName, String versionName, int versionCode, String channel) {
        if (instance == null) {
            instance = new AppBuildConfig(packageName, versionName, versionCode, channel);
            WebH5Init.init(channel, packageName, versionName, versionCode, Build.BRAND);
        }
    }

    private AppBuildConfig(String packageName, String versionName, int versionCode, String channel) {
        Objects.requireNonNull(packageName, "请指定包名！");
        Objects.requireNonNull(versionName, "请指定版本名！");
        Objects.requireNonNull(channel, "请指定应用渠道!");
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.channel = channel;
    }


    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getChannel() {
        return channel;
    }
}
