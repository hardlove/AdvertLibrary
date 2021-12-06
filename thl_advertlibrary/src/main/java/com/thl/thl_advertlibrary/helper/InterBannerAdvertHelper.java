package com.thl.thl_advertlibrary.helper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
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
 * 内容横幅
 * @author ${dell}
 * @time 2020/1/7 15
 */
public class InterBannerAdvertHelper {

    public interface OnIntercrionAdvertListener {

        void initSuccess(View view);//广告初始化完成,如果需要立即显示，可以重写此方法

        void advertDismiss();//广告消失

        void advertClick();//广告点击

    }


    TTNativeExpressAd mTTAd;
    boolean mHasShowDownloadActive;//是否正在下载
    AppCompatActivity activity;
    AdvertModel model;
    OnIntercrionAdvertListener listener;

    /**
     * 展示banner广告
     *
     * @param activity
     * @param model
     */
    public InterBannerAdvertHelper(AppCompatActivity activity, AdvertModel model) {
        this.activity = activity;
        this.model = model;
    }

    public InterBannerAdvertHelper setListener(OnIntercrionAdvertListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 展示banner广告
     */
    public void initAdvert(RelativeLayout rl_content) {
        if (model == null) {
            rl_content.setVisibility(View.GONE);
            return;
        }
        if (model.getAdvert_type() != 9) {
            if (listener != null) {
                listener.initSuccess(null);
            }
            return;
        }
        rl_content.post(new Runnable() {
            @Override
            public void run() {
                if (rl_content==null){
                    return;
                }

                TTAdNative mTTAdNative = TTAdConfigManager.init(activity, model.getAdvert_param_0()).createAdNative(activity);
                //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
                if (TTAdConfigManager.checkCanRequestPermission(activity)) {
                    TTAdConfigManager.init(activity, model.getAdvert_param_0()).requestPermissionIfNecessary(activity);
                }
                float width = rl_content.getWidth();
                if (width == 0) {
                    width = Fhad_DeviceUtil.Width(activity);
                }
                width = Fhad_DeviceUtil.px2Dip(activity, width);
                int height = (int) (width * model.getHeight() / model.getWidth());
                //step4:创建广告请求参数AdSlot,具体参数含义参考文档
                AdSlot adSlot = new AdSlot.Builder()
                        .setCodeId(model.getAdvert_param_1()) //广告位id
                        .setSupportDeepLink(true)
                        .setAdCount(1) //请求广告数量为1到3条
                        .setExpressViewAcceptedSize(width, height) //期望模板广告view的size,单位dp
                        .setImageAcceptedSize(640, 320)//这个参数设置即可，不影响模板广告的size
                        .build();
                //step5:请求广告，对请求回调的广告作渲染处理
                mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                    @Override
                    public void onError(int code, String message) {
                        rl_content.setVisibility(View.GONE);
                        Log.d("=========", model.getAid() + ",banner 加载失败：" + code + ",message:" + message);
                    }

                    @Override
                    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                        if (ads == null || ads.size() == 0) {
                            rl_content.setVisibility(View.GONE);
                            return;
                        }
                        mTTAd = ads.get(0);
                        bindAdListener(rl_content,mTTAd, listener);
                        mTTAd.render();//广告渲染
                    }
                });
            }
        });
    }

    /**
     * 展示banner广告
     */
    public void showAdvert(RelativeLayout rl_content,View view) {
        if (model != null && model.getAdvert_type() != 9) {//非sdk广告
            rl_content.setVisibility(View.VISIBLE);
            AdvertUtils.showAdvert(activity, model, rl_content);
            rl_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdvertUtils.clickAdvert(activity, model);
                    if (listener != null) {
                        listener.advertClick();
                    }
                }
            });
        } else if (view != null) {
            if (view.getParent()==null){
                rl_content.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                rl_content.addView(view, params);
                AdvertUtils.showAdvertRecord(activity, model);
            }
        } else {
            rl_content.setVisibility(View.GONE);
            rl_content.post(() -> {
                if (listener != null) {
                    listener.advertDismiss();
                }
            });
        }
    }

    private void bindAdListener(RelativeLayout rl_content,TTNativeExpressAd ad, OnIntercrionAdvertListener listener) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {

            @Override
            public void onAdClicked(View view, int type) {
                AdvertUtils.clickAdvert(activity, model);
            }

            @Override
            public void onAdShow(View view, int type) {
                AdvertUtils.showAdvertRecord(activity, model);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                rl_content.setVisibility(View.GONE);
                Log.d("=========", "渲染失败:" + msg + "， code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                listener.initSuccess(view);
            }
        });
        //dislike设置
        ad.setDislikeDialog(new AdvertDislikeDialog(activity)
                .setOnDislikeItemClick(new AdvertDislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick() {
                        rl_content.removeAllViews();
                        rl_content.setVisibility(View.GONE);
                    }
                }));
//        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
//            @Override
//            public void onSelected(int position, String value) {
//                if (position==0){
//                    rl_content.removeAllViews();
//                }
//                //用户选择不喜欢原因后，移除广告展示
//                Log.d("=========", "onSelected" + position + "， value:" + value);
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d("=========", "onCancel" );
//            }
//        });
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            }

            @Override
            public void onInstalled(String fileName, String appName) {
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            }
        });
    }

}
