package com.thl.thl_advertlibrary.config;

import android.content.Context;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdConfigManager {

    private static TTAdManager ttAdManager;

    public static TTAdManager init(Context context, String appId){
        if (ttAdManager==null){
            synchronized (TTAdConfigManager.class){
                if (ttAdManager==null){
                    TTAdConfig config=  new TTAdConfig.Builder()
                            .appId(appId)
                            .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                            .appName(AdvertConfig.appName)
                            .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                            .allowShowNotify(true) //是否允许sdk展示通知栏提示
                            .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                            .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                            .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                            .supportMultiProcess(true)
                            .needClearTaskReset()
                            .build();
                    // context 必须是application context
                    TTAdSdk.init(context.getApplicationContext(), config, new TTAdSdk.InitCallback() {
                        @Override
                        public void success() {
                            Log.d("广告初始化", "穿山甲广告初始化成功~~~ 穿山甲SDKVersion:" + ttAdManager.getSDKVersion() + "  当前线程是否是主线程:" + (Looper.myLooper() == Looper.getMainLooper()));

                        }

                        @Override
                        public void fail(int i, String s) {
                            Log.d("广告初始化", "穿山甲广告初始化失败。msg：" + s);
                        }
                    });
                    ttAdManager = TTAdSdk.getAdManager();
                }
            }
        }
        return ttAdManager;
    }

    /**
     * 检查是否可以申请权限
     * @return
     */
    public static boolean checkCanRequestPermission(Context context) {
        long lastTime = PreferenceManager.getDefaultSharedPreferences(context).getLong("last_request_permission_time", 0);
        boolean flag = System.currentTimeMillis() - lastTime > 48 * 60 * 60 * 1000;
        if (flag) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("last_request_permission_time", System.currentTimeMillis()).apply();
            Log.d(TAG, "可以申请权限。。。。");
        } else {
            Log.d(TAG, "不可以申请权限。。。。");
        }
        return flag;
    }

    private static final String TAG = "TTAdConfigManager";
}
