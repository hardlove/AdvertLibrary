package com.sqm.advert_helper.adv;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Objects;


/**
 * 广告处理代理类
 */
public abstract class AdvManagerProxy implements AdvertInterface {
    private AdvManagerAbstract advManager;

    public AdvManagerProxy(FragmentActivity context) {
        this.advManager = new AdvManagerAbstract(context) {
            @Override
            public ViewGroup getContainer() {
                return Objects.requireNonNull(AdvManagerProxy.this.getContainer(), "不能为空，请实现getContainer()");
            }

            @Override
            public TextView getSkipView() {
                return AdvManagerProxy.this.getSkipView();
            }

            @Override
            public void onSkip() {
                AdvManagerProxy.this.onSkip();
            }

            @Override
            public void initSpannableString(TextView textView, String textString) {
                AdvManagerProxy.this.initSpannableString(textView, textString);
            }

            @Override
            public boolean isNeedShowAgreement() {
                return AdvManagerProxy.this.isNeedShowAgreement();
            }

            @Override
            public AgreementStyle getAgreementStyle() {
                return AdvManagerProxy.this.getAgreementStyle();
            }


            @Override
            protected void onPermissionDenied() {
                AdvManagerProxy.this.onPermissionDenied();
            }

            @Override
            public void onPermissionGranted() {
                AdvManagerProxy.this.onPermissionGranted();
            }

            @Override
            public void onShowUserAgreement() {
                AdvManagerProxy.this.onShowUserAgreement();
            }

            @Override
            public void onUserAcceptAgreement() {
                AdvManagerProxy.this.onUserAcceptAgreement();
            }

            @Override
            public void onStartLoadAdvert() {

            }
        };
    }

    protected void onPermissionGranted() {
        startLoadAdvert();
    }

    protected void onPermissionDenied() {
//        //权限拒绝后，默认直接加载广告。如需修改执行逻辑，可重该方法
        startLoadAdvert();
//        ToastUtils.showShort("您还有权限未授权，请授权后再操作！");
    }

    /*开始加载广告*/
    protected final void startLoadAdvert() {
        advManager.startLoadAdvert();
        onStartLoadAdvert();
    }

    /*开始加载广告回调*/
    public abstract void onStartLoadAdvert();


    /*用户同意协议回调*/
    public abstract void onUserAcceptAgreement();

    /**
     * 如需改变协议样式，重写改方法
     *
     * @return
     */
    @Override
    public AgreementStyle getAgreementStyle() {
        return AgreementStyle.STYLE_01;
    }


    /*获取广告加载次数*/
    public int getLoadAdvCount() {
        return advManager.getAdvLoadCount();
    }

    @Override
    public boolean isNeedShowAgreement() {
        return true;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        advManager.onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        advManager.onActivityStarted(activity);

    }

    @Override
    public void onActivityResumed(Activity activity) {
        advManager.onActivityResumed(activity);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        advManager.onActivityPaused(activity);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        advManager.onActivityStopped(activity);

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        advManager.onActivitySaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        advManager.onActivityDestroyed(activity);
    }
}
