package com.thl.thl_advertlibrary.helper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
//import com.thl.thl_advertlibrary.dialog.DislikeDialog;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;

import java.util.List;

/**
 * @author ${dell}
 * @time 2020/1/7 15
 */
public class InterctionChuanshanjiaAdvertHelper {

    AppCompatActivity activity;


    TTNativeExpressAd mTTAd;
    boolean mHasShowDownloadActive;//是否正在下载
    private TTAdNative mTTAdNative;

    /**
     * 展示
     *
     * @param activity
     * @param model
     */
    public InterctionChuanshanjiaAdvertHelper(AppCompatActivity activity, AdvertModel model) {
        this.activity = activity;
        this.model = model;
    }

    AdvertModel model;
    OnIntercrionAdvertListener listener;

    /**
     * 展示banner广告
     */
    public void showAdvert() {

    }

    public InterctionChuanshanjiaAdvertHelper setListener(OnIntercrionAdvertListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 展示banner广告
     */
    public void initAdvert() {
        if (model == null) {
            return;
        }
        if (model.getAdvert_type() != 9) {
            if (listener != null) {
                listener.initSuccess();
            }
            return;
        }

        mTTAdNative = TTAdConfigManager.init(activity, model.getAdvert_param_0()).createAdNative(activity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdConfigManager.init(activity, model.getAdvert_param_0()).requestPermissionIfNecessary(activity);
//        float screenWidth = Fhad_DeviceUtil.Width(activity) / Fhad_DeviceUtil.Density(activity);  // 屏幕宽度(dp)
//        int width = (int) screenWidth * 7 / 10;
//        int height = (int) ((float) width * model.getHeight() / model.getWidth());
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
//        AdSlot adSlot = new AdSlot.Builder()
////                .setCodeId("914351305") //广告位id
//                .setCodeId(model.getAdvert_param_1()) //广告位id
//                .setSupportDeepLink(true)
//                .setAdCount(1) //请求广告数量为1到3条
////                .setExpressViewAcceptedSize(200, 300) //期望模板广告view的size,单位dp
//                .setExpressViewAcceptedSize(width, height) //期望模板广告view的size,单位dp
//                .setImageAcceptedSize(600, 900)//这个参数设置即可，不影响模板广告的size
//                .build();
//        //step5:请求广告，对请求回调的广告作渲染处理
//        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
//            @Override
//            public void onError(int code, String message) {
//                Log.d("=========", "load error : " + code + ", " + message);
//            }
//
//            @Override
//            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
//                if (ads == null || ads.size() == 0) {
//                    return;
//                }
//                mTTAd = ads.get(0);
//                bindAdListener(mTTAd, listener);
//                mTTAd.render();//广告渲染
//            }
//        });
    }
//
    public interface OnIntercrionAdvertListener {

        void initSuccess();//广告初始化完成,如果需要立即显示，可以重写此方法

        void advertDismiss();//广告消失

        void advertClick();//广告点击

    }
//
    private void bindAdListener(TTNativeExpressAd ad, OnIntercrionAdvertListener listener) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
//                Log.d("=========", "广告关闭 ");
                if(listener != null){
                    listener.advertDismiss();
                }
            }

            @Override
            public void onAdClicked(View view, int type) {
//                Log.d("=========", "广告被点击 ");
                AdvertUtils.clickAdvert(activity, model);
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.d("=========", "广告展示 ");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.d("=========", "onRenderFail ");
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.d("=========", "渲染成功：rExpressView:" + width + "， height:" + height);
                //返回view的宽高 单位 dp
//                mTTAd.showInteractionExpressAd(activity);//直接渲染到activity里
                mTTAd.showInteractionExpressAd(activity);
                listener.initSuccess();
            }
        });

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
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

    public void loadExpressAd(int code) {
        switch (code) {
            case 1:
                loadExpressAd(model.getAdvert_param_1(), 300, 300);
                break;
            case 2:
                loadExpressAd(model.getAdvert_param_1(), 300, 450);
                break;
            case 3:
                loadExpressAd(model.getAdvert_param_1(), 450, 300);
                break;
        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d("", "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd,listener);
                mTTAd.render();
            }
        });
    }


//    private void bindAdListener(TTNativeExpressAd ad) {
//        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
//            @Override
//            public void onAdDismiss() {
////                ToastUtils.showLong( "广告关闭");
////                finish();
//            }
//
//            @Override
//            public void onAdClicked(View view, int type) {
////                ToastUtils.showLong(  "广告被点击");
//            }
//
//            @Override
//            public void onAdShow(View view, int type) {
////                ToastUtils.showLong(  "广告展示");
//            }
//
//            @Override
//            public void onRenderFail(View view, String msg, int code) {
////                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
////                TToast.show(mContext, msg + " code:" + code);
//            }
//
//            @Override
//            public void onRenderSuccess(View view, float width, float height) {
////                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
//                //返回view的宽高 单位 dp
////                TToast.show(mContext, "渲染成功");
//                mTTAd.showInteractionExpressAd(activity);
//
//            }
//        });
//        bindDislike(ad, false);
//        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
//            return;
//        }
//        ad.setDownloadListener(new TTAppDownloadListener() {
//            @Override
//            public void onIdle() {
////                ToastUtils.showLong( "点击开始下载");
//            }
//
//            @Override
//            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
////                    ToastUtils.showLong("下载中，点击暂停", Toast.LENGTH_LONG);
//                }
//            }
//
//            @Override
//            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
////                ToastUtils.showLong( "下载暂停，点击继续", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
////                ToastUtils.showLong( "下载失败，点击重新下载", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onInstalled(String fileName, String appName) {
////                ToastUtils.showLong("安装完成，点击图片打开", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
////                ToastUtils.showLong( "点击安装", Toast.LENGTH_LONG);
//            }
//        });
//    }

//    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
//        if (customStyle) {
//            //使用自定义样式
//            List<FilterWord> words = ad.getFilterWords();
//            if (words == null || words.isEmpty()) {
//                return;
//            }
//
//            final DislikeDialog dislikeDialog = new DislikeDialog(this.activity, words);
//            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
//                @Override
//                public void onItemClick(FilterWord filterWord) {
//                    //屏蔽广告
//                }
//            });
//            ad.setDislikeDialog(dislikeDialog);
//            return;
//        }
//        //使用默认模板中默认dislike弹出样式
//        ad.setDislikeCallback(this.activity, new TTAdDislike.DislikeInteractionCallback() {
//            @Override
//            public void onSelected(int position, String value) {
//            }
//
//            @Override
//            public void onCancel() {
////                ToastUtils.showLong( "点击取消 ");
//            }
//
//            @Override
//            public void onRefuse() {
////                ToastUtils.showLong("您已成功提交反馈，请勿重复提交哦！",3);
//            }
//
//        });
//    }
    public  void onDestroy() {
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }

}
