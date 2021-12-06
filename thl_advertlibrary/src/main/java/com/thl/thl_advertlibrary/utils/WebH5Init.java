package com.thl.thl_advertlibrary.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建者:Du jiang
 * @创建时间: 2021/8/24 9:56
 * @描述:
 */
public class WebH5Init {
    public  static String mRefer;
    public static String mChannel;
    public static String mPkg;
    public static String mVersionName;
    public static String mBrand;
    public static int mVersionCode;

//   判断是否已经初始化，避免重复初始化
    private static boolean isInited = false ;
//    url和域名映射，暂时不用
    private static Map<String,String> webSites = new HashMap<>();
    /**
     * 初始化App相关的参数，用于后续调用处理，在Application中调用
     * @param channel
     * @param pkg
     * @param versionName
     * @param versionCode
     * @param brand
     */
    public static void init(String channel,String pkg,String versionName,int versionCode,String brand){
        if (!isInited) {
            mChannel = channel;
            mPkg = pkg;
            mVersionName = versionName;
            mVersionCode = versionCode;
            mBrand = brand;
            isInited = true;
        }
    }


    /**
     * 初始化App相关的参数，用于后续调用处理，在Application中调用
     * @param channel  渠道
     * @param pkg    包名
     */
    public static void init(String channel,String pkg){
        if (!isInited) {
            mChannel = channel;
            mPkg = pkg;
            isInited = true;
        }
    }

    /**
     * 初始化App相关的参数，用于后续调用处理，在Application中调用
     * @param refer 微信支付的域名
     * @param channel
     * @param pkg
     * @param versionName
     * @param versionCode
     * @param brand
     */
    @Deprecated
    public static void init(String refer,String channel,String pkg,String versionName,int versionCode,String brand){
        if (!isInited) {
            mRefer = refer;
            mChannel = channel;
            mPkg = pkg;
            mVersionName = versionName;
            mVersionCode = versionCode;
            mBrand = brand;
            isInited = true;
        }
    }


    /**
     * 初始化App相关的参数，用于后续调用处理，在Application中调用
     * @param channel  渠道
     * @param pkg    包名
     * @param refer  微信支付的域名
     */

    @Deprecated
    public static void init(String refer,String channel,String pkg){
        if (!isInited) {
            mRefer = refer;
            mChannel = channel;
            mPkg = pkg;
            isInited = true;
        }
    }

}
