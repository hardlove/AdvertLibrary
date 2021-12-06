package com.thl.thl_advertlibrary.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;

import com.thl.thl_advertlibrary.R;
import com.thl.thl_advertlibrary.config.AdvertConfig;


/**
 * 退出应用广告,插屏广告
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class AgreementDialog extends Dialog {

    public interface OnAgreementDialogListener {
        void onConfirm();

        void onCancel();

        void initSpannableString(TextView textView, String textString);
    }

    OnAgreementDialogListener listener;

    private @LayoutRes
    int layoutResID;  // 协议弹窗

    public AgreementDialog(Context activity, @LayoutRes int layoutResID, OnAgreementDialogListener listener) {
        super(activity);
        this.listener = listener;
        this.layoutResID = layoutResID;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        String textString = String.format(getContext().getString(R.string.fhad_app_agreement)
                , AdvertConfig.appName, AdvertConfig.appName, AdvertConfig.appName, AdvertConfig.appName);
        TextView textView = findViewById(R.id.fhad_tv_agreement);
        TextView fhad_tv_agree = findViewById(R.id.fhad_tv_agree);
        TextView fhad_tv_dis_agree = findViewById(R.id.fhad_tv_dis_agree);

        listener.initSpannableString(textView, textString);

        fhad_tv_agree.setOnClickListener(v -> {
            CheckBox checkBox = findViewById(R.id.checkbox);
            if (checkBox != null && (!checkBox.isChecked())) {
                Toast.makeText(getContext(), "请阅读并同意《用户协议》和《隐私政策》后使用！", Toast.LENGTH_SHORT).show();
                return;
            }

            listener.onConfirm();
            dismiss();
        });
        fhad_tv_dis_agree.setOnClickListener(v -> {
            listener.onCancel();
            dismiss();
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

}
