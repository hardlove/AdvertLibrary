package com.sqm.app;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sqm.advert_helper.adv.AgreementStyle;
import com.sqm.advert_helper.adv.BaseSplashActivity;
import com.sqm.advert_helper.adv.CommonAdvertLoadHelper;
import com.thl.thl_advertlibrary.helper.NewTTExpressAdvHelper;

public class MainActivity extends BaseSplashActivity {

    @Override
    protected void goGuideActivity() {
    }

    @Override
    protected void goMainActivity() {
    }

    @Override
    protected void onUserAgreementClick() {
    }

    @Override
    protected void onPrivacyPolicyClick() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.splash_activity;
    }

    @Override
    protected AgreementStyle getAgreementStyle() {
        return AgreementStyle.STYLE_05;
    }

    @Override
    protected TextView getAdvSkipView() {
        return findViewById(R.id.tv_skip);
    }

    @Override
    protected ViewGroup getAdvContainer() {
        return findViewById(R.id.rl_content);
    }

    /*显示协议回调*/
    @Override
    protected void onShowUserAgreement() {

    }

    /*同意协议回调*/
    @Override
    protected void onUserAcceptAgreement() {

    }
}