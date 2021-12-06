package com.thl.thl_advertlibrary.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

/**
 * 激励视频广告
 *
 * @author ${dell}
 * @time 2019/12/11 11
 */
public class TTAdRewardVideoHelper {
    private void showVideo(TTAdVideoListener listener) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(advertModel.getAdvert_param_1())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(1)  //奖励的数量
                .setExpressViewAcceptedSize(500, 500)//如果激励广告模板是非渲染模板，去掉这一句
                .setUserID("")//用户id,必传参数
                .setMediaExtra("") //附加参数，可选
                .setOrientation(advertModel.getWidth() > advertModel.getHeight() ? TTAdConstant.HORIZONTAL : TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d("==========", code + ",onError:" + message);
                if (isNetworkConnected(activity)) {
                    listener.onSkip();
                } else {
                    listener.onNetworkError();
                }

            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.d("==========", "加载缓存激励视频");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {

            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        AdvertUtils.showAdvertRecord(activity, advertModel);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                    }

                    @Override
                    public void onAdClose() {
                        listener.onSkip();
                    }

                    @Override
                    public void onVideoComplete() {
                        listener.onPlayComplete();
                    }

                    @Override
                    public void onVideoError() {
                        listener.onPlayComplete();
                        listener.onSkip();
                    }

                    @Override
                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {

                    }

//                    @Override
//                    public void onRewardVerify(boolean b, int i, String s) {
//                    }

                    @Override
                    public void onSkippedVideo() {
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            Log.d("==========", "下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("==========", "下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("==========", "下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("==========", "下载完成，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("==========", "安装完成，点击下载区域打开");
                    }
                });
                mttRewardVideoAd.showRewardVideoAd(activity);
//                mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            }

        });
    }

    Activity activity;
    boolean mHasShowDownloadActive;

    TTAdNative mTTAdNative;
    TTRewardVideoAd mttRewardVideoAd;
    AdvertModel advertModel;

    public TTAdRewardVideoHelper(Activity activity) {
        this.activity = activity;
        this.advertModel = AdvertUtils.searchFirstAdvertByLocation("Lock");
        if (advertModel == null) {
            return;
        }
        TTAdManager ttAdManager = TTAdConfigManager.init(activity, advertModel.getAdvert_param_0());
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        if (TTAdConfigManager.checkCanRequestPermission(activity)) {
            TTAdConfigManager.init(activity, advertModel.getAdvert_param_0()).requestPermissionIfNecessary(activity);
        }
        mTTAdNative = ttAdManager.createAdNative(activity.getApplicationContext());
    }

    /**
     * @param message  弹窗形式
     * @param listener 监听
     */
    public void showWaterModel(String message, TTAdVideoListener listener) {
        if (advertModel == null || advertModel.getAdvert_type() != 9) {
            listener.onSkip();
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setPositiveButton("去解锁", (dialog12, which) -> {
            showVideo(listener);
        });
        dialog.setNegativeButton("下次吧", null);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * @param listener 监听
     */
    public void showWaterModel(TTAdVideoListener listener) {
        if (advertModel == null || advertModel.getAdvert_type() != 9) {
            listener.onSkip();
            return;
        }
        showVideo(listener);
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public interface TTAdVideoListener {
        void onPlayComplete();//没有广告或者广告播放完成

        void onSkip();//广告页面手动关闭，跳转处理放在这里

        /*当前无网络,无法加载广告（开发者需要根据自己的业务逻辑来决定是否进行广告后的解锁操作）*/
        void onNetworkError();
    }

}
