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
 * @author ${dell}
 * @time 2020/1/2 14
 */
public class BannerAdvertHelper {

    /**
     * 展示banner广告
     *
     * @param context
     * @param rl_content
     * @param model
     */
    public void showAdvertModelItem(AppCompatActivity context, RelativeLayout rl_content, AdvertModel model) {
        if (model==null){
            return;
        }
        if (model.getAdvert_type() == 9) {
            rl_content.post(new Runnable() {
                @Override
                public void run() {
                    if (rl_content==null){
                        return;
                    }

                    TTAdNative mTTAdNative = TTAdConfigManager.init(context, model.getAdvert_param_0()).createAdNative(context);
                    //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
                    float width = rl_content.getWidth();
                    if (width == 0) {
                        width = Fhad_DeviceUtil.Width(context);
                    }
                    width = Fhad_DeviceUtil.px2Dip(context, width);
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
                            Log.d("=========", model.getAid() + ",banner 加载失败：" + code + ",message:" + message);
                        }

                        @Override
                        public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                            if (ads == null || ads.size() == 0) {
                                return;
                            }
                            TTNativeExpressAd mTTAd = ads.get(0);
                            bindAdListener(context, rl_content, mTTAd, model);
                            mTTAd.render();
                        }
                    });
                }
            });
        } else {
            AdvertUtils.showAdvert(context, model, rl_content);
        }
    }

    private boolean mHasShowDownloadActive = false;

    private void bindAdListener(AppCompatActivity context, RelativeLayout rl_content, TTNativeExpressAd ad, AdvertModel model) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {

            @Override
            public void onAdClicked(View view, int type) {
                AdvertUtils.clickAdvert(context, model);
            }

            @Override
            public void onAdShow(View view, int type) {
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.d("=========", "渲染失败:" + msg + "， code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //view是携带叉号，而且去不掉的
                //返回view的宽高 单位 dp
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                rl_content.addView(view, params);
            }
        });

//        //dislike设置
//        ad.setDislikeCallback(this, new TTAdDislike.DislikeInteractionCallback() {
//            @Override
//            public void onSelected(int position, String value) {
//                //用户选择不喜欢原因后，移除广告展示
//                rl_content.removeAllViews();
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
        ad.setDislikeDialog(new AdvertDislikeDialog(context)
                .setOnDislikeItemClick(new AdvertDislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick() {
                        rl_content.removeAllViews();
                    }
                }));
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
