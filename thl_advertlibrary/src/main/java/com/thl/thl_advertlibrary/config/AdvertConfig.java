package com.thl.thl_advertlibrary.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.thl.thl_advertlibrary.BuildConfig;
import com.thl.thl_advertlibrary.activity.Fhad_BaseSplashActivity;
import com.thl.thl_advertlibrary.dialog.BackgroundAdvertDialog;
import com.thl.thl_advertlibrary.helper.NewTTExpressAdvHelper;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.network.bean.FreeTimeModel;
import com.thl.thl_advertlibrary.network.bean.UrlInterceptModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_PackageUtil;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ${dell}
 * @time 2019/12/31 18
 */
public class AdvertConfig {

    public static String GENERAL_HOST_BUSS = "";//域名
    public static String appName = "";//应用名
    public static int advertFreeTime = 5000;//应用离开超过advertFreeTime，回来显示广告
    public static FreeTimeModel freeTimeModel;

    protected boolean isRunInBackground;
    protected int appCount;
    protected long leaveAppTime;//应用进入后台的时刻

    protected BackgroundAdvertDialog advertDialog;
    private static AdvertConfig advertConfig;
    private boolean expressAdvIsShowing;
    private static final boolean useExpressAdv = true;//后台拉起广告是否使用插屏类型

    public static AdvertConfig initAdvert(Application application, String dbName) {
        LitePalDB litePalDB;
        if (TextUtils.isEmpty(dbName)) {
            LitePal.initialize(application);
            litePalDB = new LitePalDB("fhad_advert", 10);
        } else {
            litePalDB = LitePalDB.fromDefault(dbName);
        }
        litePalDB.addClassName(AdvertModel.class.getName());
        litePalDB.addClassName(UrlInterceptModel.class.getName());
        LitePal.use(litePalDB);
        // 重要：适配安卓P，如果WebView使用多进程，添加如下代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(application);
            // 填入应用自己的包名
            if (!Fhad_PackageUtil.getPackageName(application).equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
        if (advertConfig == null) {
            synchronized (AdvertConfig.class) {
                if (advertConfig == null) {
                    advertConfig = new AdvertConfig();
                    advertConfig.initBackgroundCallBack(application);
                    advertConfig.appCount = 0;
                    advertConfig.leaveAppTime = 0;
                    advertConfig.isRunInBackground = false;
                }
            }
        }
        return advertConfig;
    }

    /**
     * activity 生命周期监听
     */
    public void initBackgroundCallBack(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
                if (isRunInBackground) {
                    //应用从后台回到前台 需要做的操作
                    back2App(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
                if (appCount == 0) {
                    //应用进入后台 需要做的操作
                    leaveApp(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    /**
     * 从后台回到前台需要执行的逻辑
     *
     * @param activity
     */
    protected void back2App(Activity activity) {
        isRunInBackground = false;
        if (System.currentTimeMillis() - leaveAppTime > advertFreeTime) {//超过十秒回来弹广告


            if (!activity.isFinishing() && activity instanceof AppCompatActivity && !isFilter(activity)) {
                if (!(activity instanceof Fhad_BaseSplashActivity)) {
                    if (useExpressAdv) {
                        if (!expressAdvIsShowing) {
                            expressAdvIsShowing = true;
                            NewTTExpressAdvHelper ttExpressAdvHelper = new NewTTExpressAdvHelper((ComponentActivity) activity, NewTTExpressAdvHelper.TYPE_ACTIVE);
                            ttExpressAdvHelper.showAdvert(new NewTTExpressAdvHelper.TTExpressAdvListener() {
                                @Override
                                public void onSkip() {
                                    expressAdvIsShowing = false;
                                }

                                @Override
                                public void onNetworkError() {
                                    expressAdvIsShowing = false;
                                }
                            });
                        }
                    } else {
                        AdvertModel model = AdvertUtils.searchFirstAdvertByLocation("active");
                        if (model != null) {
                            advertDialog = new BackgroundAdvertDialog((AppCompatActivity) activity);
                            advertDialog.show();
                        }
                    }


                }
            }
        }
    }

    /**
     * 过滤开屏页面，避免与后台拉起广告重叠显示
     *
     * @param activityNames
     */
    public static void setFilterActivity(Class<?>... activityNames) {
        if (activityNames != null) {
            for (Class<?> aClass : activityNames) {
                filterActivityNames.add(aClass.getName());
            }
        }
    }

    private static Set<String> filterActivityNames = new HashSet<>();

    private boolean isFilter(Activity activity) {
        boolean filter = filterActivityNames.contains(activity.getClass().getName());
        if (BuildConfig.DEBUG) {
            Log.d("后台拉起", "当前Activity:" + activity + " 是否被过滤：" + filter);
        }
        return filter;
    }


    /**
     * 离开应用 压入后台或者退出应用
     *
     * @param activity
     */
    protected void leaveApp(Activity activity) {
        try {
            isRunInBackground = true;
            leaveAppTime = System.currentTimeMillis();
            if (advertDialog != null && advertDialog.isShowing()) {
                advertDialog.dismiss();
            }
        } catch (Exception e) {
            boolean destroyed = activity == null || activity.isDestroyed();
            Log.e("退到后台，关闭广告。", " activity is Destroy:" + destroyed, e);
        }
    }

    public static String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
