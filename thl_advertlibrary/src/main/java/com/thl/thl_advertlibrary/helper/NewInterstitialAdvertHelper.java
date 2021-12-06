package com.thl.thl_advertlibrary.helper;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

import java.lang.ref.WeakReference;

/**
 * @创建者: Kluas
 * @创建时间: 2021/11/13 22:35
 * @描述: 新插屏广告，4.0.2.2
 */
public class NewInterstitialAdvertHelper {
    private WeakReference<FragmentActivity> mWeakReference;
    private FragmentActivity mActivity;
    private TTAdNative mTTFullScreenVideoAdNative;
    private AdvertModel model;

    private OnAdvertCallback mCallback;
    private boolean mHasShowDownloadActive;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;
    private boolean mIsLoaded = false;

    public void setCallback(OnAdvertCallback callback) {
        mCallback = callback;
    }

    private boolean mIsInited = false;

    public interface OnAdvertCallback {

        void onDismiss();//广告消失

        void onError(int code, String msg);

    }


    public NewInterstitialAdvertHelper(WeakReference<FragmentActivity> weakReference, String key) {
        mWeakReference = weakReference;
        this.model = AdvertUtils.searchFirstAdvertByLocation(key);
        init();
    }

    public NewInterstitialAdvertHelper(WeakReference<FragmentActivity> weakReference, AdvertModel model) {
        mWeakReference = weakReference;
        this.model = model;
        init();
    }

    public void init() {
        if (null == model || null == mWeakReference) {
            if (mCallback != null) {
                mCallback.onError(0, "null params");
            }
            return;
        }
        mActivity = mWeakReference.get();
        if (null == mActivity) {
            if (mCallback != null) {
                mCallback.onError(0, "null params");
            }
            return;
        }
        if (model != null) {
            if (model.getAdvert_type() != 9) {
                return;
            }
            mIsInited = true;
            mTTFullScreenVideoAdNative = TTAdConfigManager.init(mActivity, model.getAdvert_param_0()).createAdNative(mActivity);


        }
    }

    public void loadNewInterstitialAd() {
        Log.d("=========", "loadNewInterstitialAd(), model = " + model);
        if (!mIsInited || null == model) {
            if (mCallback != null) {
                mCallback.onError(0, "init error");
            }
            return;
        }

        loadNewInterstitialAd(model.getAdvert_param_1(), 500, 500);
    }


    private void loadNewInterstitialAd(String codeId, int expressViewWidth, int expressViewHeight) {
        if (TTAdConfigManager.checkCanRequestPermission(mActivity)) {
            TTAdConfigManager.init(mActivity, model.getAdvert_param_0()).requestPermissionIfNecessary(mActivity);
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(TTAdLoadType.PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTFullScreenVideoAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {

                    @Override
                    public void onError(int i, String s) {
                        if (mCallback != null) {
                            mCallback.onError(i, s);
                        }
                        Log.e("=========", "onError : code = " + i + " ; message :" + s);
                    }

                    @Override
                    public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                        mIsLoaded = false;
                        mTTFullScreenVideoAd = ttFullScreenVideoAd;
                        if (mTTFullScreenVideoAd != null) {
                            mTTFullScreenVideoAd.showFullScreenVideoAd(mActivity, TTAdConstant.RitScenes.HOME_GIFT_BONUS, null); // HOME_GIFT_BONUS GAME_GIFT_BONUS
                            bindAdListener(mTTFullScreenVideoAd, mCallback);
                        } else {
                            Log.d("=========", "请先加载广告 ");
                        }
                    }

                    @Override
                    public void onFullScreenVideoCached() {

                    }

                    @Override
                    public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                        mIsLoaded = true;
                        Log.d("=========", "onFullScreenVideoCached() ");
                    }
                }
        );
    }

    private void bindAdListener(TTFullScreenVideoAd ad, OnAdvertCallback listener) {
        ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
            @Override
            public void onAdShow() {
                Log.d("=========", "onAdShow");
            }

            //广告的下载bar点击回调
            @Override
            public void onAdVideoBarClick() {
                Log.d("=========", "onAdVideoBarClick");
            }

            //广告关闭的回调
            @Override
            public void onAdClose() {
                Log.d("=========", "onAdClose");
                if (mCallback != null) {
                    mCallback.onDismiss();
                }
            }

            //视频播放完毕的回调
            @Override
            public void onVideoComplete() {
                Log.d("=========", "onVideoComplete");
            }

            //跳过视频播放
            @Override
            public void onSkippedVideo() {
                Log.d("=========", "onSkippedVideo");
            }
        });

        Log.d("=========", "广告类型 ：" + getAdType(ad.getFullVideoAdType()));
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Log.d("=========", "点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Log.d("=========", "下载中，点击暂停");
                }
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


    public void onDestroy() {
        if (mTTFullScreenVideoAd != null) {
            mTTFullScreenVideoAd = null;
        }
    }

}
