package com.thl.thl_advertlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author：CL
 * 日期:2021/7/27
 * 说明：用户协议记录
 **/
public class UserAgreementHelper {
    public static final String IS_ACCEPT_AGREEMENT = "is_accept_agreement";

    public static boolean isAcceptedUserAgreement(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(IS_ACCEPT_AGREEMENT, false);
    }

    public static void setAcceptedUserAgreementState(Context context, boolean accepted) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(IS_ACCEPT_AGREEMENT, accepted).apply();
    }
}
