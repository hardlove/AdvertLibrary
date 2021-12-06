package com.thl.thl_advertlibrary.activity;

import static android.widget.RelativeLayout.ALIGN_PARENT_END;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.thl.thl_advertlibrary.R;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.dialog.AgreementDialog;
import com.thl.thl_advertlibrary.network.Fhad_HttpMethodUtils;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.network.bean.Fhad_BaseCallBack;
import com.thl.thl_advertlibrary.network.bean.Fhad_BaseModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_TimeCount;
import com.thl.thl_advertlibrary.utils.UserAgreementHelper;
import com.thl.thl_advertlibrary.view.CountdownView;

import org.litepal.LitePal;

import java.util.List;

/**
 * 闪屏页父activity
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public abstract class Fhad_BaseSplashActivity extends AppCompatActivity {
    protected RelativeLayout rl_content;
    protected Button bt_confirm;
    Fhad_TimeCount mTimeCount;//计时器

    boolean advertShowing = false;//广告是否正在显示
    boolean advertLoading = false;//广告是否正在加载
    boolean isActivityState = false;//页面是否激活
    boolean isSkipIng = false;//页面是否正在跳转

    int skipTime = 0;
    private TTAdNative mTTAdNative;

    //初始化view
    public abstract void initView();

    //使用次数
    public abstract int numberUsed();

    //首次安装是否显示协议
    public boolean showAgreement() {
        boolean accept = UserAgreementHelper.isAcceptedUserAgreement(this);
        return !accept;
    }

    //跳过
    public abstract void skip();

    //设置协议跳转
    public abstract void initSpannableString(TextView textView, String textString);

    // 协议弹窗布局
    public int initAgreement() {
        return R.layout.fhad_dialog_agreement;
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(this);
        initView();
        advertLoading = true;
        isActivityState=true;
        if (showAgreement()) {
            new AgreementDialog(this, initAgreement(), new AgreementDialog.OnAgreementDialogListener() {
                @Override
                public void onConfirm() {
                    //记录已同意用户协议
                    UserAgreementHelper.setAcceptedUserAgreementState(Fhad_BaseSplashActivity.this, true);

                    updateAdvert();
                    onUserAcceptAgreement();
                }

                @Override
                public void onCancel() {
                    if (checkCanLoadAdvert()) {
                        negative();
                    } else {
                        advertLoading = false;
                        advertShowing = false;
                        //删掉广告数据库中的广告数据
                        LitePal.deleteAll(AdvertModel.class);
                        //未同意协议不得加载广告，直接跳过
                        skipOverImmediately();
                    }
                }

                @Override
                public void initSpannableString(TextView textView, String textString) {
                    Fhad_BaseSplashActivity.this.initSpannableString(textView, textString);
                }
            }).show();
        } else {
            if (checkCanLoadAdvert()) {
                updateAdvert();
            } else {
                advertLoading = false;
                advertShowing = false;
                //删掉广告数据库中的广告数据
                LitePal.deleteAll(AdvertModel.class);
                //未同意协议不得加载广告，直接跳过
                skipOverImmediately();
            }

        }
    }

    public abstract void onUserAcceptAgreement();


    public void negative() {
        updateAdvert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Fhad_BaseSplashActivity", "onResume:"+isActivityState);
        if (!isActivityState) {
            isActivityState=true;
            skipOverImmediately();
        }
    }

    public void updateAdvert() {
        if (!checkCanLoadAdvert()) {
            return;
        }
        Fhad_HttpMethodUtils.searchAdvert(this, "open", new Fhad_BaseCallBack<Fhad_BaseModel<List<AdvertModel>>>() {
            @Override
            public void success(Fhad_BaseModel<List<AdvertModel>> result) {
                AdvertModel advertModel = null;
                if (!(result.getData() == null || result.getData().isEmpty())) {
//                    LitePal.saveAll(result.getData());
                    for (AdvertModel model : result.getData()) {
                        if (model.getIs_open() == 1) {
                            advertModel = model;
                            break;
                        }
                    }
                }
                showAdvert(advertModel);
            }

            @Override
            public void onFailed(Exception e) {
                super.onFailed(e);
                showAdvert(null);
            }
        });
    }

    public void showAdvert(AdvertModel model) {
        advertLoading = false;
        bt_confirm.setOnClickListener(v -> {
            skipOverByTime();
        });
        if (model != null) {
            if (model.getAdvert_type() == 6) {//广点通
                advertShowing = true;
                showAdGDT(model);
            } else if (model.getAdvert_type() == 9) {//穿山甲
                bt_confirm.setVisibility(View.GONE);
                advertShowing = true;
                mTTAdNative = TTAdConfigManager.init(this, model.getAdvert_param_0()).createAdNative(this);
                if (TTAdConfigManager.checkCanRequestPermission(this)) {
                    TTAdConfigManager.init(this, model.getAdvert_param_0()).requestPermissionIfNecessary(this);
                }
                showAdChuanshanjia(model);
            } else {//非sdk广告
                advertShowing = true;
                AdvertUtils.showAdvert(this, model, rl_content);
                startTick(5);
                rl_content.setOnClickListener(v -> AdvertUtils.clickAdvert(Fhad_BaseSplashActivity.this, model));
            }
        } else {
            advertShowing = false;
            skipOverImmediately();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Fhad_BaseSplashActivity", "onPause");
        isActivityState = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Fhad_BaseSplashActivity", "onStop");
        isActivityState = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityState = false;
        stopTick();
    }

    private void stopTick() {
        if (mTimeCount != null) {
            mTimeCount.cancel();
        }
    }

    private void startTick(int s) {
        mTimeCount = new Fhad_TimeCount(1000 * s, 1000, new Fhad_TimeCount.TimeOutCallback() {
            @Override
            public void onFinish() {
                advertShowing = false;
                skipOverImmediately();
            }

            @Override
            public void onTick(long st) {
                long t = st / 1000;
                bt_confirm.setText(t + "s");
            }
        });
        mTimeCount.start();
    }

    /**
     * 立即跳过
     */
    public void skipOverImmediately() {
        skipTime = 0;
        skipOver(false);
    }

    /**
     * 延迟800ms跳过
     */
    public void skipOverByTime() {
        skipTime = 800;
        skipOver(true);
    }

    /**
     * 跳过，如果广告正在显示或页面已经暂停都不能跳转
     */
    public void skipOver(boolean isClick) {
        boolean canSkip=isActivityState&&(!advertLoading)&&(!isSkipIng);
        if (canSkip){
            if (!advertShowing||isClick){//广告结束||或者点击跳过
                isSkipIng = true;
                rl_content.postDelayed(() -> {
                    skip();
                    isSkipIng = false;
                }, skipTime);
            }
        }
    }

    private void showAdChuanshanjia(AdvertModel model) {
        rl_content.post(() -> {
            if (rl_content == null) {
                advertShowing = false;
                skipOverImmediately();
                return;
            }
            int height = rl_content.getHeight();
            int width = rl_content.getWidth();
            if (height == 0 || width == 0) {
                height = rl_content.getMeasuredHeight();
                width = rl_content.getMeasuredWidth();
            }

            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(model.getAdvert_param_1())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(width, height)//必须设置
                    .build();
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    Log.d("Fhad_BaseSplashActivity", "onError");
                    advertShowing = false;
                    skipOverImmediately();
                }

                @Override
                @MainThread
                public void onTimeout() {
                    Log.d("Fhad_BaseSplashActivity", "onTimeout");
                    advertShowing = false;
                    skipOverImmediately();
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
                    if (ad == null) {
                        Log.d("Fhad_BaseSplashActivity", "onSplashAdLoad");
                        advertShowing = false;
                        skipOverImmediately();
                        return;
                    }
                    //获取SplashView
                    View view = ad.getSplashView();
                    rl_content.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                    rl_content.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                    useCustomCountdownButton(rl_content, true, ad);
                    //设置SplashView的交互监听器
                    ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            AdvertUtils.clickAdvert(Fhad_BaseSplashActivity.this, model);
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            AdvertUtils.showAdvertRecord(Fhad_BaseSplashActivity.this, model);
                        }

                        @Override
                        public void onAdSkip() {
                            Log.d("Fhad_BaseSplashActivity", "onAdSkip");
                            skipOverByTime();
                        }

                        @Override
                        public void onAdTimeOver() {
                            Log.d("Fhad_BaseSplashActivity", "onAdTimeOver");
                            advertShowing = false;
                            skipOverImmediately();
                        }
                    });
                }
            }, 5000);//超时时间
        });
    }

    private void showAdGDT(AdvertModel model) {
        advertShowing = false;
        skipOverImmediately();
//
//        new SplashAD(Fhad_BaseSplashActivity.this, rl_content, bt_confirm, model.getAdvert_param_0(), model.getAdvert_param_1(), new SplashADListener() {
//            @Override
//            public void onADDismissed() {
//                advertShowing = false;
//                skipOverByTime();
//            }
//
//            @Override
//            public void onNoAD(AdError error) {
//                advertShowing = false;
//                skipOverImmediately();
//            }
//
//            @Override
//            public void onADPresent() {
//                AdvertUtils.showAdvertRecord(Fhad_BaseSplashActivity.this, model);
//            }
//
//            @Override
//            public void onADClicked() {
//                AdvertUtils.clickAdvert(Fhad_BaseSplashActivity.this, model);
//            }
//
//            @Override
//            public void onADTick(long millisUntilFinished) {
//                bt_confirm.setText(String.valueOf(millisUntilFinished / 1000) + "s");
//            }
//        }, 0);

    }


    /*加载广告是否需要用户同意协议的开关*/
    private final boolean advertNeedAcceptAgreement = true;//需要用户同意协议后才能加载广告

    /*检查是否可以加载广告*/
    private boolean checkCanLoadAdvert() {
        if (advertNeedAcceptAgreement) {
            //必须同意协议后才能加载广告
            return UserAgreementHelper.isAcceptedUserAgreement(this);
        } else {
            //无需同意协议也可加载广告
            return true;
        }

    }

    private void useCustomCountdownButton(ViewGroup advContainer, boolean isUseCustomCountdownButton, final TTSplashAd ad) {
        if (isUseCustomCountdownButton) {
            //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
            ad.setNotAllowSdkCountdown();
            CountdownView countdownView = new CountdownView(this);

            if (advContainer instanceof FrameLayout) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.rightMargin = 20;
                layoutParams.topMargin = 20;
                layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
                countdownView.setLayoutParams(layoutParams);
            } else if (advContainer instanceof RelativeLayout) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(ALIGN_PARENT_END);
                params.rightMargin = 20;
                params.topMargin = 20;
                countdownView.setLayoutParams(params);
            } else {
                throw new RuntimeException("广告容器请使用FrameLayout 或 RelativeLayout !");
            }


            countdownView.startCountDown();
            //*************************使用点睛样式***********************************
            //设置在点击按钮时调用点睛样式
            countdownView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.startClickEye();
                    skipOverImmediately();
                }
            });
            //设置在倒计时结束时调用点睛样式
            countdownView.setCountdownListener(new CountdownView.CountdownListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd() {
                    ad.startClickEye();
                    skipOverImmediately();

                }

                @Override
                public void onPause() {

                }
            });
            //************************************************************

            advContainer.addView(countdownView);
        }
    }
}
