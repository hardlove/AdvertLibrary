package com.thl.thl_advertlibrary.utils;

import android.util.Log;

import com.thl.thl_advertlibrary.BuildConfig;


public class Lg {
    //    private static final String TAG =Lg.class.getSimpleName();
    private static final String TAG ="******";

    public static boolean DEBUG =true;

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
            Log.e(tag, msg);
    }

    //   测试模式
    public static void dd(String msg) {
        d(TAG, msg);
    }

    public static void dd(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void de(String msg) {
        e(TAG, msg);
    }

    public static void de(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }


    public static void error( int code , String msg) {
        Log.e(TAG, "onError ,code :"+code+" , msg :"+msg);
    }

}
