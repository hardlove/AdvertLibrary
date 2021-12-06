package com.thl.thl_advertlibrary.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Author：CL
 * 日期:2020/12/16
 * 说明：插屏广告
 **/
public class TTExpressAdvHelper implements LifecycleObserver {
    private static final String TAG = "TTExpressAdvHelper";
    private ComponentActivity activity;
    private TTExpressAdvListener mListener;
    private String advert_location;
    int expressViewWidth;
    int expressViewHeight;
    private List<TTNativeExpressAd> mTTAdList;

    /**
     * 使用 chapin1 号插屏广告位
     *
     * @param activity
     */
    public TTExpressAdvHelper(ComponentActivity activity) {
        this(activity, "chapin1", -1, -1);
//        this(activity, "quit",-1,-1);
    }

    public TTExpressAdvHelper(ComponentActivity activity, String location) {
        this(activity, location, -1, -1);
    }


    /**
     * @param activity
     * @param advert_location 插屏广告位：chapin1 或 chapin2
     * @param width           期望宽度 单位 dp
     * @param height          期望高度 单位 dp
     */
    public TTExpressAdvHelper(ComponentActivity activity, String advert_location, int width, int height) {
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
            Log.e(TAG, "未获取到插屏广告配置，广告位：chapin1");
            if (mListener != null) {
                mListener.onSkip();
            }
            return;
        }

        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity);

        Log.d(TAG, "插屏广告期望尺寸,width:" + expressViewWidth + "  height:" + expressViewHeight);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(model.getAdvert_param_1()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .build();
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {

            //请求广告失败
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "请求插屏广告失败,code:" + code + "  message:" + message);
                if (mListener != null) {
                    if (isNetworkConnected(activity)) {
                        mListener.onSkip();
                    } else {
                        mListener.onNetworkError();
                    }

                }
            }

            //请求广告成功
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                Log.e(TAG, "请求插屏广告成功" + " list size:" + (list != null ? list.size() : 0));
                if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

                mTTAdList = list;
                for (TTNativeExpressAd mTTAd : list) {
                    mTTAd.render();
                    mTTAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                        //广告关闭回调
                        @Override
                        public void onAdDismiss() {
                            Log.d(TAG, "插屏广告,onAdDismiss~~");
                            if (mListener != null) {
                                mListener.onAdDismiss();
                                mListener.onSkip();
                            }

                        }

                        //广告点击回调
                        @Override
                        public void onAdClicked(View view, int type) {
                            if (mListener != null) {
                                mListener.onAdClicked();
                            }
                            Log.d(TAG, "插屏广告,onAdClicked~~");
                        }

                        //广告展示回调
                        @Override
                        public void onAdShow(View view, int type) {

                            Log.d(TAG, "插屏广告,onAdShow~~");
                            if (mListener != null) {
                                mListener.onAdShow();
                            }
                        }

                        //广告渲染失败回调
                        @Override
                        public void onRenderFail(View view, String msg, int code) {
                            Log.d(TAG, "插屏广告,onRenderFail~~");
                            if (mListener != null) {
                                mListener.onSkip();
                            }
                        }

                        //广告渲染成功回调
                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            Log.d(TAG, "插屏广告,onRenderSuccess~~");
                            if (activity == null || activity.isFinishing() || activity.isDestroyed())
                                return;
                            //在渲染成功回调时展示广告，提升体验
                            mTTAd.showInteractionExpressAd(activity);

                        }
                    });
                }

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
        if (mTTAdList != null && mTTAdList.size() > 0) {
            for (TTNativeExpressAd expressAd : mTTAdList) {
                expressAd.destroy();
            }
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
}
