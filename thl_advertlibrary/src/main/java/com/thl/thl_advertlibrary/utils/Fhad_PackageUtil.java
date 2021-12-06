package com.thl.thl_advertlibrary.utils;

import android.content.Context;

import com.thl.thl_advertlibrary.config.AppBuildConfig;

/**
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_PackageUtil {

    private static final String TAG = Fhad_PackageUtil.class.getName();

    /**
     * 获取包名
     * @param mContext
     * @return
     */
    public static String getPackageName(Context mContext) {
//        String packageName = null;
//        try {
//            packageName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).packageName;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
//        return packageName;
        return AppBuildConfig.getInstance().getPackageName();
    }

    /**
     * 版本名
     * @param mContext
     * @return
     */
    public static String getVersionName(Context mContext) {
//        String versionName = null;
//        try {
//            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
//        return versionName;
        return AppBuildConfig.getInstance().getVersionName();
    }

    /**
     * 获取版本号
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
//        int versionCode = -1;
//        try {
//            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
//        return versionCode;
        return AppBuildConfig.getInstance().getVersionCode();
    }

    /**
     * 获取渠道名
     * @param mContext
     * @return
     */
    public static String getChannelName(Context mContext,String channelBand) {
//        String sourceChannel = null;
//        try {
//            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
//            if (info != null && info.metaData != null) {
//                Object value = info.metaData.get(channelBand);
//                if (value != null) {
//                    sourceChannel = value.toString();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
//        return sourceChannel;
        return AppBuildConfig.getInstance().getChannel();
    }
}
