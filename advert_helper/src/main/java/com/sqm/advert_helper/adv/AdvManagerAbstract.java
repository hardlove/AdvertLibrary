package com.sqm.advert_helper.adv;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sqm.advert_helper.BuildConfig;
import com.sqm.advert_helper.R;
import com.thl.thl_advertlibrary.config.TTAdConfigManager;
import com.thl.thl_advertlibrary.network.Fhad_HttpMethodUtils;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.network.bean.Fhad_BaseModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;
import com.thl.thl_advertlibrary.utils.Fhad_TimeCount;
import com.thl.thl_advertlibrary.utils.UserAgreementHelper;
import com.thl.thl_advertlibrary.view.CountdownView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

import static android.widget.RelativeLayout.ALIGN_PARENT_END;

/**
 * 广告处理管理者抽象类
 */
public abstract class AdvManagerAbstract implements AdvertInterface {

    private static final String TAG = "AdvManagerAbstract";
    private FragmentActivity context;


    protected ViewGroup rl_content;
    protected TextView bt_confirm;
    Fhad_TimeCount mTimeCount;//计时器

    boolean advertShowing;//广告正在显示
    boolean isActivityPaused;//页面是否已经暂停，暂停的状态下不能跳转

    int skipTime = 200;
    private TTAdNative mTTAdNative;
    private int loadCount;/*广告加载次数*/
    private boolean accept;
    private boolean isShowingArgeement;/*正在显示协议弹窗*/
    private boolean agreementCompleted;//是否协议授权完成
    private boolean isSkiped;//是否已经跳过，防止多次触发

    private long startTime;
    private long responseTime;
    private long renderStartTime;
    private long showTime;


    /**
     * @param context
     */
    public AdvManagerAbstract(@NonNull FragmentActivity context) {
        this.context = context;
        this.loadCount = SPStaticUtils.getInt(Constant.ADV_LOAD_COUNT, 0);
    }


    //使用次数
    public int getAdvLoadCount() {
        return this.loadCount;
    }


    private void stopTick() {
        if (mTimeCount != null) {
            mTimeCount.cancel();
            mTimeCount.onFinish();
        }
    }

    private void startTick(int s) {
        mTimeCount = new Fhad_TimeCount(1000L * s, 1000, new Fhad_TimeCount.TimeOutCallback() {
            @Override
            public void onFinish() {
                advertShowing = false;//广告展示完；
                skipOver();
            }

            @Override
            public void onTick(long st) {
                long t = st / 1000;
                if (bt_confirm != null) {
                    bt_confirm.setText(t + "s");
                }
            }
        });
        mTimeCount.start();
    }

    /**
     * 立即跳过
     */
    public void skipOverImmediately() {
        isActivityPaused = false;
        advertShowing = false;
        skipTime = 0;
        skipOver();
    }

    /**
     * 延时跳过
     *
     * @param mills
     */
    public void skipOverDelay(int mills) {
        skipTime = mills;
        skipOver();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("延时%s跳过", mills));
        }

    }

    public synchronized void skipOver() {
        if (!isSkiped) {
            isSkiped = true;
            rl_content.postDelayed(() -> {
                onSkip();
            }, skipTime);
        }
    }

    private void showAdChuanshanjia(AdvertModel model) {
        try {
            if (BuildConfig.DEBUG) {
                renderStartTime = System.currentTimeMillis();
                Log.e(TAG, "开始渲染穿山甲广告，renderStartTime:" + renderStartTime);
            }
            rl_content.post(() -> {
                int height = rl_content.getHeight();
                int width = rl_content.getWidth();

                if (width == 0 || height == 0) {
                    width = ScreenUtils.getScreenWidth();
                    height = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(75);
                }
                /**
                 * 版本要求：3901及以上版本
                 * 【新增】AdSlot新增广告配置接口，用于控制下载APP前是否弹出二次确认弹窗(适用所有广告类型)
                 * AdSlot.Builder.setDownloadType(@TTAdConstant.DOWNLOAD_TYPE int downloadType)，传入的值为：
                 * public static final int DOWNLOAD_TYPE_NO_POPUP = 0;// 对于应用的下载不做特殊处理；
                 * public static final int DOWNLOAD_TYPE_POPUP = 1;// 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                 */

                //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
                AdSlot adSlot = new AdSlot.Builder()
                        .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_NO_POPUP)
                        .setCodeId(model.getAdvert_param_1())
                        .setSupportDeepLink(true)
                        .setImageAcceptedSize(width, height)//必须设置
                        .build();
                //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
                mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                    @Override
                    @MainThread
                    public void onError(int code, String message) {
                        skipOverImmediately();
                    }

                    @Override
                    @MainThread
                    public void onTimeout() {
                        skipOverImmediately();
                    }

                    @Override
                    @MainThread
                    public void onSplashAdLoad(TTSplashAd ad) {
                        if (ad == null) {
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
                                AdvertUtils.clickAdvert((AppCompatActivity) context, model);
                            }

                            @Override
                            public void onAdShow(View view, int type) {
                                AdvertUtils.showAdvertRecord(context, model);
                                if (BuildConfig.DEBUG) {
                                    showTime = System.currentTimeMillis();
                                    Log.e(TAG, "渲染穿山甲广告成功，显示广告，showTime:" + showTime + " 加载广告总时间:" + (showTime - startTime) + " 毫秒");
                                }
                            }

                            @Override
                            public void onAdSkip() {
//                                skipOverImmediately();
                                skipOverDelay(1200);
                            }

                            @Override
                            public void onAdTimeOver() {
                                advertShowing = false;
                                skipOver();
                            }
                        });
                    }
                }, 5000);//超时时间
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "加载穿山甲广告错误，直接跳过。。。", e);
            }
            rl_content.post(this::skipOverImmediately);

        }
    }

    private void showAdGDT(AdvertModel model) {
        skipOverImmediately();
//
//        new SplashAD(Fhad_BaseSplashActivity.this, rl_content, bt_confirm, model.getAdvert_param_0(), model.getAdvert_param_1(), new SplashADListener() {
//            @Override
//            public void onADDismissed() {
//                advertShowing = false;
//                skipOver();
//            }
//
//            @Override
//            public void onNoAD(AdError error) {
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


    /*更新广告数据*/
    public void updateAdvert(boolean onlyUpdateForPreloading) {
        if (!checkCanLoadAdvert()) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "未同意用户协议,无法加载广告数据！");
            }
            return;
        }

        if (BuildConfig.DEBUG) {
            startTime = System.currentTimeMillis();
            Log.e(TAG, "开始更新广告数据,startTime:" + startTime + "  onlyUpdateForPreloading:" + onlyUpdateForPreloading);
        }
        Fhad_HttpMethodUtils.updateAdvert(context, new StringCallback() {

            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "更新广告失败   updateAdvert~~~~~~~", e);
                }
                LitePal.deleteAll(AdvertModel.class);
                if (!onlyUpdateForPreloading) {
                    if (!isPageDestroyed()) {
                        skipOverImmediately();
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (BuildConfig.DEBUG) {
                        responseTime = System.currentTimeMillis();
                        Log.e(TAG, "更新广告数据成功,responseTime:" + responseTime + "  获取广告数据时长：" + (responseTime - startTime) + " 毫秒");
                        Log.d(TAG, "更新广告成功   response~~~~~~~" + response);
                    }

                    Fhad_BaseModel<List<AdvertModel>> result = new Gson().fromJson(response, new TypeToken<Fhad_BaseModel<List<AdvertModel>>>() {
                    }.getType());
                    LitePal.deleteAll(AdvertModel.class);
                    LitePal.saveAll(result.getData());

                    if (!isPageDestroyed()) {
                        if (onlyUpdateForPreloading) {
                            onlyUpdateForPreloading();
                        } else {
                            //执行加载广告
                            performShowAdvert();
                        }

                    }

                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "更新数据解析错误~~~~", e);
                    }
                    if (!isPageDestroyed()) {
                        if (!onlyUpdateForPreloading) {
                            skipOverImmediately();
                        }
                    }
                }

            }
        });
    }

    private boolean isPageDestroyed() {
        return context == null || context.isFinishing() || context.isDestroyed();
    }

    /**
     * 广告预加载
     */
    protected void onlyUpdateForPreloading() {
        AdvertModel model = AdvertUtils.searchFirstAdvertByLocation("open");
        if (model != null) {
            int height = rl_content.getHeight();
            int width = rl_content.getWidth();
            if (width == 0 || height == 0) {
                width = ScreenUtils.getScreenWidth();
                height = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(75);
            }
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(model.getAdvert_param_1())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(width, height)//必须设置
                    .build();
            TTAdNative ttAdNative = TTAdConfigManager.init(context, model.getAdvert_param_0()).createAdNative(context);
            ttAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onTimeout() {
                }

                @Override
                public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                }
            }, 2000);
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "未找到广告加载数据，无法进行广告预加载处理");
            }
        }

    }


    /**
     * 如果可以申请权限，则先申请权限，然后再展示广告。避免权限弹框和广告同时展示
     */
    public void performShowAdvert() {
        boolean canRequest = TTAdConfigManager.checkCanRequestPermission(context);
        if (canRequest) {
            PermissionUtils.permission(getAdvLoadPermission())
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            showAdvert();
                        }

                        @Override
                        public void onDenied() {
                            showAdvert();
                        }
                    }).request();
        } else {
            showAdvert();
        }
    }


    /*显示广告*/
    public void showAdvert() {
        if (context == null || context.isFinishing() || context.isDestroyed()) {
            Log.e(TAG, "页面已经销毁~~~~~~~~~~");
            return;
        }
        AdvertModel model = AdvertUtils.searchFirstAdvertByLocation("open");
        if (model != null) {
            Log.d(TAG, "加载开屏广告~~~~~~");
            SPStaticUtils.put(Constant.ADV_LOAD_COUNT, ++loadCount);
            skipTime = 0;
            if (model.getAdvert_type() == 6) {//广点通
                advertShowing = true;
                showAdGDT(model);
            } else if (model.getAdvert_type() == 9) {//穿山甲
                if (bt_confirm != null) {
                    bt_confirm.setVisibility(View.GONE);
                }
                advertShowing = true;
                mTTAdNative = TTAdConfigManager.init(context, model.getAdvert_param_0()).createAdNative(context);
                if (TTAdConfigManager.checkCanRequestPermission(context)) {
                    TTAdConfigManager.init(context, model.getAdvert_param_0()).requestPermissionIfNecessary(context);
                }
                showAdChuanshanjia(model);
            } else {//非sdk广告
                AdvertUtils.showAdvert(context, model, (RelativeLayout) rl_content);
                startTick(5);
                advertShowing = true;
                rl_content.setOnClickListener(v -> AdvertUtils.clickAdvert((AppCompatActivity) context, model));
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "未获取到广告数据，直接跳过广告加载。。。");
            }
            skipOverImmediately();
        }
    }

    private void showAgreement() {
        String textString = String.format(context.getString(R.string.advert_helper_app_agreement), context.getString(R.string.app_name));
        AgreementStyle style = getAgreementStyle();
        if (style == null) {
            throw new NullPointerException("请指定协议样式，重写AdvManagerProxy的getAgreementStyle()方法");
        }
        View content = LayoutInflater.from(context).inflate(style.getLayoutID(), null, false);
        TextView textView = content.findViewById(R.id.fhad_tv_agreement);
        Button btnOK = content.findViewById(R.id.btn_ok);
        Button btnCancel = content.findViewById(R.id.btn_cancel);
        CheckBox checkBox = content.findViewById(R.id.checkbox);
        initSpannableString(textView, textString);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(content);


        builder.setCancelable(false);
        AlertDialog dialog = builder.show();
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (displayMetrics.widthPixels * 0.80);
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        }

        btnOK.setOnClickListener(v -> {
            if (checkBox != null && (!checkBox.isChecked())) {
                ToastUtils.showShort("请阅读并同意《用户协议》和《隐私政策》后使用！");
                return;
            }
            dialog.dismiss();

            isShowingArgeement = false;
            agreementCompleted = true;
            UserAgreementHelper.setAcceptedUserAgreementState(context, true);
            checkPermission();
            //用户已接受app使用协议
            onUserAcceptAgreement();
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                isShowingArgeement = false;
                agreementCompleted = true;
                checkPermission();
            }
        });

        onShowUserAgreement();
    }

    /*初始化广告显示相关的控件*/
    private void initAdvertViews() {
        rl_content = Objects.requireNonNull(getContainer(), "广告加载容器不能为空，请指定");
        bt_confirm = getSkipView();
        if (bt_confirm != null) {
            bt_confirm.setOnClickListener(v -> skipOverImmediately());
        }

    }
    //==============================================================================================


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        accept = UserAgreementHelper.isAcceptedUserAgreement(activity);
        initAdvertViews();

        // TODO: 2020/7/15 Step: 1 检查是否需要显示协议
        if (isNeedShowAgreement() && !accept) {
            showAgreement();
            isShowingArgeement = true;
        }


    }

    @Override
    public void onActivityStarted(Activity activity) {
        // TODO: Step：2 检查权限是否开启
        if (!isShowingArgeement) {
            checkPermission();

        }

    }


    /**
     * 穿山甲广告加载前申请的权限
     */
    protected String[] initPermissions() {
        //不再主动申请广告所需权限,加载广告时再去申请
//        return new String[]{PermissionConstants.PHONE, PermissionConstants.LOCATION, PermissionConstants.STORAGE};
        return null;
    }

    /**
     * 穿山甲广告加载时申请的权限
     */
    @NonNull
    protected String[] getAdvLoadPermission() {
        return new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    /*检查权限*/
    private void checkPermission() {
        //如果没有申请权限，直接授权通过
        if (ObjectUtils.isEmpty(initPermissions())) {
            onPermissionGranted();
            return;
        }
        if (!TTAdConfigManager.checkCanRequestPermission(context)) {
            // TODO: 2021/6/8 不能再申请权限 ，直接走同意流程
            onPermissionGranted();
            return;
        }
        PermissionUtils
                .permission(initPermissions())
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        onPermissionGranted();
                    }

                    @Override
                    public void onDenied() {
                        onPermissionDenied();
                    }
                })
                .request();

        if (getAdvLoadCount() == 0 && agreementCompleted && checkCanLoadAdvert()) {
            // TODO: 2020/11/22 首次启动APP预加载广告（注意：确保网络请求在用户同意协议后）
            updateAdvert(true);
        }

    }

    /*获取到所有权限*/
    protected void onPermissionGranted() {
        startLoadAdvert();
    }

    /*权限拒绝*/
    protected void onPermissionDenied() {
        startLoadAdvert();
    }

    /*开始加载广告*/
    protected void startLoadAdvert() {
        boolean accept = checkCanLoadAdvert();
        if (!accept) {
            //删掉广告数据库中的广告数据
            LitePal.deleteAll(AdvertModel.class);
            //未同意协议不得加载广告，直接跳过
            skipOverImmediately();
            Log.e(TAG, "未同意用户协议，跳过广告！");
        } else {
            if (!advertShowing) {
                startTime = System.currentTimeMillis();
                if (isNewVersion()) {//新版本
                    //更新记录的版本号
                    SPStaticUtils.put(Constant.LAST_APP_VERSION_CODE, BuildConfig.VERSION_CODE);
                    updateAdvert(false);//如何是新的版本，直接更新广告数据，避免上架检查时出现广告
                } else {
                    // TODO: Step:3 权限已通过，初始化广告
                    AdvertModel model = AdvertUtils.searchFirstAdvertByLocation("open");
                    if (model == null) {
                        // TODO: 2020/7/15 还没有广告数据，获取广告数据
                        updateAdvert(false);
                    } else {
                        // TODO: 2020/7/15 已有广告数据，直接显示
                        performShowAdvert();
                    }
                }
            }
        }

    }

    /*是否是新的版本*/
    private boolean isNewVersion() {
        int lastVersion = SPStaticUtils.getInt(Constant.LAST_APP_VERSION_CODE, 0);
        return BuildConfig.VERSION_CODE != lastVersion;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isActivityPaused = true;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        stopTick();
        updateAdvert(false);

        context = null;//防止内存泄露
    }

    /*加载广告是否需要用户同意协议的开关*/
    private final boolean advertNeedAcceptAgreement = true;//需要用户同意协议后才能加载广告

    /*检查是否可以加载广告*/
    private boolean checkCanLoadAdvert() {
        if (advertNeedAcceptAgreement) {
            //必须同意协议后才能加载广告
            return UserAgreementHelper.isAcceptedUserAgreement(context);
        } else {
            //无需同意协议也可加载广告
            return true;
        }

    }

    private void useCustomCountdownButton(ViewGroup advContainer, boolean isUseCustomCountdownButton, final TTSplashAd ad) {
        if (isUseCustomCountdownButton) {
            //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
            ad.setNotAllowSdkCountdown();
            CountdownView countdownView = new CountdownView(context);

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
                    Log.d(TAG, "开始开屏倒计时。。。。");

                }

                @Override
                public void onEnd() {
                    Log.d(TAG, "开屏倒计时结束。。。。");
                    ad.startClickEye();
                    skipOverImmediately();

                }

                @Override
                public void onPause() {
                    Log.d(TAG, "开屏倒计时暂停。。。。");
                }
            });
            //************************************************************

            advContainer.addView(countdownView);
        }
    }
}
