package com.thl.thl_advertlibrary.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.thl.thl_advertlibrary.R;
import com.thl.thl_advertlibrary.helper.InterctionAdvertHelper;

import androidx.appcompat.app.AppCompatActivity;


/**
 * 插屏广告
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class InteractionAdvertDialog extends Dialog {

    AppCompatActivity activity;

    OnDialogCallback callback;
    RelativeLayout rl_content;
    ImageView iv_delete;

    public interface OnDialogCallback {

        void onDialogDismiss(AppCompatActivity activity);
    }

    public void setCallback(OnDialogCallback callback) {
        this.callback = callback;
    }

    @Override
    public void dismiss() {
        rl_content.removeAllViews();
        helper.initAdvert();
        super.dismiss();
        if (callback != null) {
            callback.onDialogDismiss(activity);
        }
    }


    InterctionAdvertHelper helper;

    public InteractionAdvertDialog(AppCompatActivity activity) {
        this(activity, "cancel");
    }


    public InteractionAdvertDialog(AppCompatActivity activity, String channel) {
        super(activity);
        this.activity = activity;

        helper = new InterctionAdvertHelper(activity, channel);

        helper.setListener(new InterctionAdvertHelper.OnIntercrionAdvertListener() {

            @Override
            public void advertDismiss() {
                dismiss();
            }

            @Override
            public void onAdvertGetFinish() {

            }

        });
        helper.initAdvert();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fhad_interction_advert);
        rl_content = findViewById(R.id.fhad_rl_content);
        iv_delete = findViewById(R.id.fhad_iv_delete);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        iv_delete.setVisibility(View.GONE);
        iv_delete.setOnClickListener(v -> dismiss());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                iv_delete.setVisibility(View.VISIBLE);
                helper.render(InteractionAdvertDialog.this,rl_content);//嵌入页面时，直接调用；嵌入弹窗时，在弹窗显示时，调用
            }
        });
    }
}
