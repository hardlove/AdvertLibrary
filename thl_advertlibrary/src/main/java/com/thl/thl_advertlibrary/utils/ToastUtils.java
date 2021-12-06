package com.thl.thl_advertlibrary.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * author: Lenovo
 * created on: 2019/10/11 17:51
 * description:
 */
public class ToastUtils {

    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
