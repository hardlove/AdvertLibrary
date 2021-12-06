package com.thl.thl_advertlibrary.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.thl.thl_advertlibrary.helper.InterctionAdvertHelper;
import com.thl.thl_advertlibrary.network.Fhad_HttpMethodUtils;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

import org.litepal.LitePal;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


/**
 * 退出应用广告,插屏广告
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public abstract class BaseClosedAdvertDialog extends Dialog {

    protected RelativeLayout rl_content;
    protected FrameLayout fl_content;
    protected RelativeLayout rl_content1;
    protected Button bt_cancel;
    protected Button bt_confirm;

    AppCompatActivity activity;
    InterctionAdvertHelper helper;

    public BaseClosedAdvertDialog(AppCompatActivity activity) {
        super(activity);
        this.activity = activity;
        helper = new InterctionAdvertHelper(activity, "quit");

        helper.setListener(new InterctionAdvertHelper.OnIntercrionAdvertListener() {

            @Override
            public void advertDismiss() {
                dismiss();
                Fhad_HttpMethodUtils.CopyStringToClipboard(activity);
                activity.finish();
            }

            @Override
            public void onAdvertGetFinish() {
//              helper.render(rl_content);//嵌入页面时，直接调用；嵌入弹窗时，在弹窗显示时，调用
            }

        });
        helper.initAdvert();
    }

    @Override
    public void dismiss() {
        rl_content.removeAllViews();
        helper.initAdvert();
        super.dismiss();
    }

    public abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setDimAmount(0f);
        fl_content.setVisibility(View.GONE);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                fl_content.setVisibility(View.VISIBLE);
                helper.render(BaseClosedAdvertDialog.this, rl_content);//嵌入页面时，直接调用；嵌入弹窗时，在弹窗显示时，调用
            }
        });
        List<AdvertModel> advertModels = LitePal.where("advert_location =? and is_open =?", "quit", "1").find(AdvertModel.class);
        if (advertModels != null && advertModels.size() > 1) {
            AdvertModel advertModel = advertModels.get(1);
            AdvertUtils.showAdvert(activity, advertModel, rl_content1);
            rl_content1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdvertUtils.clickAdvert(activity, advertModel);
                    dismiss();
                }
            });
        } else {
            rl_content1.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) fl_content.getLayoutParams();
            layoutParams.topMargin = 0;
            fl_content.setLayoutParams(layoutParams);
        }
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fhad_HttpMethodUtils.CopyStringToClipboard(activity);
                dismiss();
                activity.finish();
            }
        });
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

}
