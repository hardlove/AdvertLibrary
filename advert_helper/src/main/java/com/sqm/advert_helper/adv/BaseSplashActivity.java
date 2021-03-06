package com.sqm.advert_helper.adv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.SPStaticUtils;
import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseSplashActivity extends AppCompatActivity {
    private AdvManagerProxy advManagerProxy;
    protected FragmentActivity activity;
    private static final String IS_FIRST = "is_first";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isDisableGuide()) {
                        goMainActivity();
                    } else {
                        SPStaticUtils.put(IS_FIRST, false);
                        goGuideActivity();
                    }
                    break;
                case 2:
                    goMainActivity();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.activity = this;
        super.onCreate(savedInstanceState);
        if (useImmerse()) {
            setStatusBar();
        }
        setContentView(getLayoutResID());
        initView(savedInstanceState);
        initData(savedInstanceState);
        initListener();
        initAdvert();

        advManagerProxy.onActivityCreated(this, null);
        if (getAdvSkipView() != null) {
            getAdvSkipView().setVisibility(View.GONE);
        }
        if (!isTaskRoot() && getIntent() != null) {
            // TODO: 2021/1/9 ???????????????????????????APP?????????APP?????????????????????????????????????????????bug
            String action = getIntent().getAction();
            if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
            }
        }


    }


    /*?????????????????????*/
    private void initAdvert() {
        advManagerProxy = new AdvManagerProxy(this) {
            @Override
            public ViewGroup getContainer() {
                return getAdvContainer();
            }

            @Override
            public TextView getSkipView() {
                return getAdvSkipView();
            }

            @Override
            public void onSkip() {
                boolean isFirst = SPStaticUtils.getBoolean(IS_FIRST, true);
                if (isFirst) {
                    mHandler.sendEmptyMessage(1);
                } else {
                    mHandler.sendEmptyMessage(2);
                }

            }

            @Override
            public void initSpannableString(TextView textView, String textString) {
                initPrivacy(textView, textString);
            }

            @Override
            public AgreementStyle getAgreementStyle() {
                return BaseSplashActivity.this.getAgreementStyle();
            }

            @Override
            protected void onPermissionGranted() {
                super.startLoadAdvert();
            }

            @Override
            protected void onPermissionDenied() {
                super.startLoadAdvert();
            }

            @Override
            public void onStartLoadAdvert() {

            }

            /*????????????????????????*/
            @Override
            public void onShowUserAgreement() {
                BaseSplashActivity.this.onShowUserAgreement();
            }

            /*??????????????????????????????*/
            @Override
            public void onUserAcceptAgreement() {
                BaseSplashActivity.this.onUserAcceptAgreement();
            }

        };

    }


    /**
     * ?????????????????????
     *
     * @return
     */
    protected boolean useImmerse() {
        return true;
    }

    protected void setStatusBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fullScreen(false)
                .statusBarDarkFont(true)
                .init();
    }


    /*???????????????*/
    private void initPrivacy(TextView textView, String textString) {
        String user_privacy = "????????????";
        String policy = "????????????";
        Spannable spannable = new SpannableString(textString);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (!ClickUtil.isFastClick()) {
                    onUserAgreementClick();
                }

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getColorByAttr(android.R.attr.colorPrimary)); //????????????
                ds.setUnderlineText(false);
            }
        }, textString.indexOf(user_privacy), textString.indexOf(user_privacy) + user_privacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (!ClickUtil.isFastClick()) {
                    onPrivacyPolicyClick();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getColorByAttr(android.R.attr.colorPrimary)); //????????????
                ds.setUnderlineText(false);
            }
        }, textString.indexOf(policy), textString.indexOf(policy) + policy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @ColorInt
    private int getColorByAttr(@AttrRes int colorPrimary) {
        int[] attribute = new int[]{colorPrimary};
        TypedArray array = this.getTheme().obtainStyledAttributes(attribute);
        int color = array.getColor(0, Color.BLACK);
        array.recycle();
        return color;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        advManagerProxy.onActivityDestroyed(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        advManagerProxy.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        advManagerProxy.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        advManagerProxy.onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        advManagerProxy.onActivityStopped(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        advManagerProxy.onActivitySaveInstanceState(this, outState);
    }

    /*?????????????????????*/
    protected boolean isDisableGuide() {
        return true;
    }

    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    protected void initData(@Nullable Bundle savedInstanceState) {
    }

    protected void initListener() {
    }

    protected abstract int getLayoutResID();

    protected abstract AgreementStyle getAgreementStyle();

    protected abstract TextView getAdvSkipView();

    protected abstract ViewGroup getAdvContainer();

    protected abstract void goGuideActivity();

    protected abstract void goMainActivity();

    protected abstract void onPrivacyPolicyClick();

    protected abstract void onUserAgreementClick();

    protected void onUserAcceptAgreement() {
    }

    protected void onShowUserAgreement() {
    }
}
