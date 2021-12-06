package com.thl.thl_advertlibrary.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.thl.thl_advertlibrary.R;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;
import com.thl.thl_advertlibrary.utils.Fhad_TimeCount;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;


/**
 * 后台切换到前台的广告展示（穿山甲开屏广告），5s关闭
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class BackgroundAdvertDialog extends Dialog {

    AppCompatActivity activity;
    AdvertModel model;
    Fhad_TimeCount mTimeCount;//计时器
    Button fhad_iv_delete;
    RelativeLayout rl_content;

    @Override
    public void dismiss() {
        rl_content.post(() -> {
            super.dismiss();
        });
        if (mTimeCount != null) {
            mTimeCount.cancel();
            mTimeCount = null;
        }
    }

    @Override
    public void hide() {
        rl_content.post(() -> {
            super.hide();
        });
        if (mTimeCount != null) {
            mTimeCount.cancel();
            mTimeCount = null;
        }
    }
    private void startTick(int s) {
        mTimeCount = new Fhad_TimeCount(1000 * s, 1000, new Fhad_TimeCount.TimeOutCallback() {
            @Override
            public void onFinish() {
                dismiss();
            }

            @Override
            public void onTick(long st) {
                long t = st / 1000;
                fhad_iv_delete.setText(t + "s");
            }
        });
        mTimeCount.start();
    }

    public BackgroundAdvertDialog(AppCompatActivity activity) {
        super(activity);
        this.activity = activity;
        model = AdvertUtils.searchFirstAdvertByLocation("active");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fhad_splash_advert);
        rl_content = findViewById(R.id.fhad_rl_content);
        fhad_iv_delete = findViewById(R.id.fhad_iv_delete);
        fhad_iv_delete.setOnClickListener(v -> hide());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(lp);
    }


    @Override
    protected void onStart() {
        super.onStart();
        fhad_iv_delete.setVisibility(View.VISIBLE);
        if (model != null && model.getAdvert_type() == 9) {
            fhad_iv_delete.setVisibility(View.GONE);
            TTAdNative mTTAdNative = TTAdConfigManager.init(activity, model.getAdvert_param_0()).createAdNative(activity);
            //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            if (TTAdConfigManager.checkCanRequestPermission(activity)) {
                TTAdConfigManager.init(activity, model.getAdvert_param_0()).requestPermissionIfNecessary(activity);
            }
            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(model.getAdvert_param_1())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(Fhad_DeviceUtil.Width(activity), Fhad_DeviceUtil.Height(activity))//必须设置
                    .build();
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    dismiss();
                }

                @Override
                @MainThread
                public void onTimeout() {
                    dismiss();
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
                    if (ad == null) {
                        dismiss();
                        return;
                    }
                    //获取SplashView
                    View view = ad.getSplashView();
                    rl_content.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                    rl_content.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();

                    //设置SplashView的交互监听器
                    ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            AdvertUtils.clickAdvert(activity, model);
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            AdvertUtils.showAdvertRecord(activity, model);
                        }

                        @Override
                        public void onAdSkip() {
                            dismiss();
                        }

                        @Override
                        public void onAdTimeOver() {
                            dismiss();
                        }
                    });
                }
            }, 5000);//超时时间
        }
//        else if (model != null && model.getAdvert_type() == 6) {
//            new SplashAD(activity, rl_content, fhad_iv_delete, model.getAdvert_param_0(), model.getAdvert_param_1(), new SplashADListener() {
//                @Override
//                public void onADDismissed() {
//                    hide();
//                }
//
//                @Override
//                public void onNoAD(AdError error) {
//                    hide();
//                }
//
//                @Override
//                public void onADPresent() {
//                    AdvertUtils.showAdvertRecord(activity, model);
//                }
//
//                @Override
//                public void onADClicked() {
//                    AdvertUtils.clickAdvert(activity, model);
//                }
//
//                @Override
//                public void onADTick(long millisUntilFinished) {
//                    fhad_iv_delete.setText(String.valueOf(millisUntilFinished / 1000) + "s");
//                }
//            }, 0);
//
//        }
        else if (model != null) {//非sdk广告
            AdvertUtils.showAdvert(activity, model, rl_content);
            rl_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdvertUtils.clickAdvert(activity, model);
                    hide();
                }
            });
            startTick(5);
        } else {
            dismiss();
        }
    }

}
