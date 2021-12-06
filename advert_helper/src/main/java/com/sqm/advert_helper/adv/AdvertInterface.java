package com.sqm.advert_helper.adv;

import android.app.Application;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * 广告处理代理类接口
 */
public interface AdvertInterface extends Application.ActivityLifecycleCallbacks {
    /*获取广告加载容器*/
    ViewGroup getContainer();

    /*获取跳过广告按钮*/
    TextView getSkipView();

    /*跳过广告回调*/
    void onSkip();

    /*设置协议跳转*/
    void initSpannableString(TextView textView, String textString);

    /*首次安装是否显示协议*/
    boolean isNeedShowAgreement();

    /*如需改变协议样式*/
    AgreementStyle getAgreementStyle();

    /*显示用户协议弹框回调*/
    void onShowUserAgreement();

    /*用户同意协议回调*/
    void onUserAcceptAgreement();

    /*开始加载广告回调*/
    void onStartLoadAdvert();

}
