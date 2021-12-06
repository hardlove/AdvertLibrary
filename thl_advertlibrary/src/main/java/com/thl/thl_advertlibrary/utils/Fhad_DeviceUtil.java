package com.thl.thl_advertlibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.thl.thl_advertlibrary.config.AppBuildConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.UUID;


/**
 * 手机硬件信息获取
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_DeviceUtil {

    /**
     * 获取设备名
     *
     * @return
     */
    public static String getDeviceName() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备版本
     *
     * @return
     */
    public static String getDeviceVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取设备厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备ID,如果你恢复了出厂设置，那他就会改变的。而且如果你root了手机，你也可以改变这个ID
     */
    public static String getDeviceId(Context mContext) {
        return getCustomDeviceID(mContext);
    }

    public static String getCustomDeviceID(Context mContext) {
        String device_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("my_device_id", null);
        if (TextUtils.isEmpty(device_id)) {
            device_id = UUID.randomUUID().toString();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("my_device_id", device_id).apply();
        }
        return device_id;
    }


    /**
     * imei,只有Android手机才有， IMEI号是一串15位的号码，比如像这样 359881030314356
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceIMEI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获得imsi号
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceIMSI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    /**
     * 返回本机号码,不一定能获取到
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    /**
     * 获得手机sim识别代码
     */
    @SuppressLint("MissingPermission")
    public static String getSIMNumber(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }

    /**
     * 得到设备序列号
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceSN() {
        String sn = "NO Search";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            sn = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sn;
    }


    @SuppressLint("MissingPermission")
    public static String getUniqueID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    /**
     * 得到设备mac地址
     */
    @SuppressLint("MissingPermission")
    public static String MAC(Context mContext) {
        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 得到当前系统国家和地区
     */
    public static String Country(Context mContext) {
        return mContext.getResources().getConfiguration().locale.getCountry();
    }

    /**
     * 得到当前系统语言
     */
    public static String Language(Context mContext) {
        String language = "NO Search";
        String country = mContext.getResources().getConfiguration().locale
                .getCountry();
        language = mContext.getResources().getConfiguration().locale
                .getLanguage();
        // 区分简体和繁体中文  
        if ("zh".equals(language)) {
            if ("CN".equals(country)) {
                language = "Simplified Chinese";
            } else {
                language = "Traditional Chinese";
            }
        }
        return language;
    }

    /**
     * 返回系统屏幕的高度（像素单位）
     */
    public static int Height(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 返回系统屏幕的宽度（像素单位）
     */
    public static int Width(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 返回系统屏幕（像素密度）
     */
    public static float Density(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.density;
    }
    /**
     * 返回系统屏幕的宽度（像素单位）
     */
    public static int DensityDpi(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    /**
     * 将px值转换为dip或dp值
     */
    public static int px2Dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5);
    }

    /**
     * 将dp转换成px值
     */
    public static int dip2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5);
    }

    /**
     *系统TypedValue提供的dp2px
     */
    protected int dp2px(Context context,int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
    }


    /**
     * sim 是否存在
     *
     * @return
     */
    public static boolean isSimExist(Context mContext) {
        final TelephonyManager mTelephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        int simState = mTelephonyManager.getSimState();
        return !(simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN);
    }

    /**
     * 拨打电话
     *
     * @param phone
     */
    @SuppressLint("MissingPermission")
    public static void startCall(Context mContext, String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        mContext.startActivity(intent);
    }

    /**
     * cpu 名称
     *
     * @return
     */
    public static String getCpuName() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;
    }


    /**
     * 检测某个应用是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context mContext, String packageName) {
        try {
            mContext.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 去应用市场市场页面,并调整指定应用详情页面
     */
    public static void openMarket(Context context, String marketName, String packageName) {
        if (isAppInstalled(context, marketName)) {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(marketName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您的设备没有安装该应用市场", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开qq对话框
     */
    public static void openQQ(Context context, String qq) {
        if (isAppInstalled(context, "com.tencent.mobileqq")) {
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qq));
            context.startActivity(it);
        } else {
            Toast.makeText(context, "您的设备没有安装qq", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开系统浏览器，并跳转百度，检索
     *
     * @return
     */
    public static void openBrowserToBaidu(Context context, String keyWords) {
        try {
            Uri uri = Uri.parse("http://www.baidu.com/s?&ie=utf-8&oe=UTF-8&wd=" + URLEncoder.encode(keyWords, "UTF-8"));
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }



}  