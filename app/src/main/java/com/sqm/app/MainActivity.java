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
    public void goGuideActivity() {
    }

    @Override
    public void goMainActivity() {
    }

    @Override
    public void onUserAgreementClick() {
    }

    @Override
    public void onPrivacyPolicyClick() {
    }

    @Override
    public int getLayoutResID() {
        return R.layout.splash_activity;
    }

    @Override
    public AgreementStyle getAgreementStyle() {
        return AgreementStyle.STYLE_05;
    }

    @Override
    public TextView getAdvSkipView() {
        return findViewById(R.id.tv_skip);
    }

    @Override
    public ViewGroup getAdvContainer() {
        return findViewById(R.id.rl_content);
    }

    /*显示协议回调*/
    @Override
    public void onShowUserAgreement() {

    }

    /*同意协议回调*/
    @Override
    public void onUserAcceptAgreement() {

    }
}