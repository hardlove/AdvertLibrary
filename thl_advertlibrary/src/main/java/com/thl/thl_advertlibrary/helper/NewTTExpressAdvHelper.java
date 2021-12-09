package com.thl.thl_advertlibrary.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.ComponentActivity;
import androidx.annotation.StringDef;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author：CL
 * 日期:2021/12/07
 * 说明：新插屏广告
 **/
public class NewTTExpressAdvHelper implements LifecycleObserver {
    private static final String TAG = "NewTTExpressAdvHelper";
    private ComponentActivity activity;
    private TTExpressAdvListener mListener;
    private String advert_location;
    int expressViewWidth;
    int expressViewHeight;
    private TTFullScreenVideoAd ttFullScreenVideoAd;


    /*多样式-首页*/
    public static final String TYPE_MAIN = "mainadmulti";
    /*多样式-退出*/
    public static final String TYPE_QUIT = "quitmulti";
    /*多样式-拉起*/
    public static final String TYPE_ACTIVE = "activemulti";
    /*多样式-完成*/
    public static final String TYPE_FINISH = "finishmulti";
    /*多样式-新插屏*/
    public static final String TYPE_NEW = "newinsert";


    @StringDef({TYPE_MAIN, TYPE_QUIT, TYPE_ACTIVE, TYPE_FINISH,TYPE_NEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AdvTypeConfig {
    }


    public NewTTExpressAdvHelper(ComponentActivity activity, String location) {
        this(activity, location, -1, -1);
    }


    /**
     * @param activity
     * @param advert_location 插屏广告位：chapin1 或 chapin2
     * @param width           期望宽度 单位 dp
     * @param height          期望高度 单位 dp
     */
    public NewTTExpressAdvHelper(ComponentActivity activity, String advert_location, int width, int height) {
        this.activity = activity;
        this.advert_location = advert_location;
        if (width <= 0 || height <= 0) {
            this.expressViewWidth = px2dp((float) (getScreenWidth() * 0.75));
            this.expressViewHeight = this.expressViewWidth * 3 / 2;
        }
        activity.getLifecycle().addObserver(this);
    }

    public void showAdvert(TTExpressAdvListener listener) {
        init(listener);
    }

    private void init(TTExpressAdvListener listener) {
        this.mListener = listener;
        AdvertModel model = AdvertUtils.searchFirstAdvertByLocation(advert_location);
        if (model == null) {
            Log.e(TAG, "未获取到新插屏广告配置，广告位：" + advert_location);
            if (mListener != null) {
                mListener.onSkip();
            }
            return;
        }

        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity);

        Log.d(TAG, "新插屏广告期望尺寸,width:" + expressViewWidth + "  height:" + expressViewHeight);


        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(model.getAdvert_param_1())
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(TTAdLoadType.PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();

        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "请求新插屏广告失败,code:" + code + "  message:" + message);
                if (mListener != null) {
                    if (isNetworkConnected(activity)) {
                        mListener.onSkip();
                    } else {
                        mListener.onNetworkError();
                    }

                }
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                NewTTExpressAdvHelper.this.ttFullScreenVideoAd = ttFullScreenVideoAd;
                if (ttFullScreenVideoAd != null) {
                    ttFullScreenVideoAd.showFullScreenVideoAd(activity, TTAdConstant.RitScenes.HOME_GIFT_BONUS, null);
                    ttFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            if (mListener != null) {
                                mListener.onAdShow();
                            }
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            if (mListener != null) {
                                mListener.onAdClicked();
                            }
                        }

                        @Override
                        public void onAdClose() {
                            if (mListener != null) {
                                mListener.onAdDismiss();
                                mListener.onSkip();
                            }
                        }

                        //视频播放完毕的回调
                        @Override
                        public void onVideoComplete() {

                        }

                        //跳过视频播放
                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                    Log.d("=========", "广告类型 ：" + getAdType(ttFullScreenVideoAd.getFullVideoAdType()));
                    ttFullScreenVideoAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {
                            Log.d("=========", "点击开始下载");
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d("=========", "下载中，点击暂停");
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d("=========", "下载暂停，点击继续");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d("=========", "下载失败，点击重新下载");
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            Log.d("=========", "安装完成，点击图片打开");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            Log.d("=========", "点击安装");
                        }
                    });
                }

            }

            @Override
            public void onFullScreenVideoCached() {

            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {

            }
        });


    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public interface TTExpressAdvListener {
        default void onAdShow() {
        }

        default void onAdDismiss() {
        }

        default void onAdClicked() {
        }

        void onSkip();//广告页面手动关闭，跳转处理放在这里

        /*当前无网络,无法加载广告（开发者需要根据自己的业务逻辑来决定是否进行广告后的解锁操作）*/
        void onNetworkError();

    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        if (ttFullScreenVideoAd != null) {

        }
        activity.getLifecycle().removeObserver(this);
    }

    public int px2dp(final float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    private String getAdType(int type) {
        switch (type) {

            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通全屏视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable全屏视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;

            case TTAdConstant.AD_TYPE_LIVE:
                return "直播流，type=" + type;
        }

        return "未知类型+type=" + type;
    }
}
