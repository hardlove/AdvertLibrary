package com.thl.thl_advertlibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTDislikeDialogAbstract;
import com.bytedance.sdk.openadsdk.dislike.TTDislikeListView;
import com.thl.thl_advertlibrary.R;

/**
 * Create by hanweiwei on 14/12/2018
 */
public class AdvertDislikeDialog extends TTDislikeDialogAbstract {
    private OnDislikeItemClick mOnDislikeItemClick;

    public AdvertDislikeDialog(@NonNull Context context) {
        super(context);
    }

    public AdvertDislikeDialog setOnDislikeItemClick(OnDislikeItemClick onDislikeItemClick) {
        mOnDislikeItemClick = onDislikeItemClick;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TTDislikeListView lv_dislike_custom = findViewById(R.id.fhad_dislike_custom);
        lv_dislike_custom.post(new Runnable() {
            @Override
            public void run() {
                mOnDislikeItemClick.onItemClick();
                dismiss();
            }
        });
        getWindow().setDimAmount(0.0f);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fhad_dialog_dislike;
    }

    @Override
    public int[] getTTDislikeListViewIds() {
        return new int[]{R.id.fhad_dislike_custom};
    }


    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return null;
    }


    public interface OnDislikeItemClick {
        void onItemClick();
    }

}

