package com.thl.thl_advertlibrary.helper;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.dialog.AdvertDislikeDialog;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * 插屏广告 展示帮助类
 * @author ${dell}
 * @time 2020/1/7 15
 */
public class InterctionAdvertHelper {

    public interface OnIntercrionAdvertListener {

        void advertDismiss();//广告消失/加载失败（//穿山甲广告点击会自动关闭广告）

        void onAdvertGetFinish();//广告请求完成；

    }

    TTNativeExpressAd mTTAd;
    boolean mHasShowDownloadActive;//是否正在下载

    AppCompatActivity activity;
    AdvertModel model;
    OnIntercrionAdvertListener listener;
    float percentageWidth=7/10f;//广告的宽占屏幕的百分比

    /**
     *
     * @param activity
     * @param channelId 广告位id
     */
    public InterctionAdvertHelper(AppCompatActivity activity, String channelId) {
        this.activity = activity;
        this.model = AdvertUtils.searchFirstAdvertByLocation(channelId);
    }

    public InterctionAdvertHelper setPercentageWidth(float percentageWidth) {
        this.percentageWidth = percentageWidth;
        return this;
    }

    public InterctionAdvertHelper setListener(OnIntercrionAdvertListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 请求穿山甲广告控制
     */
    public void initAdvert() {
        if (model == null||model.getAdvert_type() != 9) {
            listener.onAdvertGetFinish();
            return;
        }

        TTAdNative mTTAdNative = TTAdConfigManager.init(activity, model.getAdvert_param_0()).createAdNative(activity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        if (TTAdConfigManager.checkCanRequestPermission(activity)) {
            TTAdConfigManager.init(activity, model.getAdvert_param_0()).requestPermissionIfNecessary(activity);
        }
        float screenWidth = Fhad_DeviceUtil.Width(activity) / Fhad_DeviceUtil.Density(activity);  // 屏幕宽度(dp)
        int width = (int)(screenWidth * percentageWidth);
        int height = (int) ((float) width * model.getHeight() / model.getWidth());
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId("914351305") //广告位id
                .setCodeId(model.getAdvert_param_1()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
//                .setExpressViewAcceptedSize(200, 300) //期望模板广告view的size,单位dp
                .setExpressViewAcceptedSize(width, height) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(600, 900)//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                listener.onAdvertGetFinish();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads != null || ads.size() > 0) {
                    mTTAd = ads.get(0);
                }
                listener.onAdvertGetFinish();
            }
        });
    }

    /**
     * 渲染广告，嵌入dialog
     * @param dialog
     * @param rl_content
     */
    public void render(Dialog dialog,RelativeLayout rl_content) {
        if (model != null && model.getAdvert_type() != 9) {//非sdk广告
            AdvertUtils.showAdvert(activity, model, rl_content);
            rl_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdvertUtils.clickAdvert(activity, model);
                }
            });
        } else if (mTTAd != null&&model.getAdvert_type() == 9) {
            dialog.hide();
            bindAdListener(mTTAd,listener);
            mTTAd.render();//广告渲染
        } else {
            listener.advertDismiss();
        }
    }

    /**
     * 渲染广告，嵌入activity
     * @param rl_content
     */
    public void render(RelativeLayout rl_content) {
        if (model != null && model.getAdvert_type() != 9) {//非sdk广告
            rl_content.setVisibility(View.VISIBLE);
            AdvertUtils.showAdvert(activity, model, rl_content);
            rl_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdvertUtils.clickAdvert(activity, model);
                }
            });
        } else if (model.getAdvert_type() == 9&&mTTAd != null) {
            rl_content.setVisibility(View.GONE);
            bindAdListener(mTTAd,listener);
            mTTAd.render();//广告渲染
        } else {
            rl_content.setVisibility(View.GONE);
            listener.advertDismiss();
        }
    }


    private void bindAdListener(TTNativeExpressAd ad, OnIntercrionAdvertListener listener) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
                Log.d("=========", "广告关闭 ");
                listener.advertDismiss();
            }

            @Override
            public void onAdClicked(View view, int type) {
                Log.d("=========", "广告被点击 ");
                AdvertUtils.clickAdvert(activity, model);
                listener.advertDismiss();
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.d("=========", "广告展示 ");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.d("=========", "渲染失败：onRenderFail:" + msg + "， code:" + code);
                listener.advertDismiss();
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.d("=========", "渲染成功：rExpressView:" + width + "， height:" + height);
                //返回view的宽高 单位 dp
                if (!activity.isFinishing()){
                    mTTAd.showInteractionExpressAd(activity);//直接渲染到activity里
                }
            }
        });
        ad.setDislikeDialog(new AdvertDislikeDialog(activity)
                .setOnDislikeItemClick(new AdvertDislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick() {
                        activity.finish();
                    }
                }));
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

}
