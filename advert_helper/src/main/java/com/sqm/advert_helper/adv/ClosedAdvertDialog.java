package com.sqm.advert_helper.adv;

import androidx.appcompat.app.AppCompatActivity;
import com.sqm.advert_helper.R;
import com.thl.thl_advertlibrary.dialog.BaseClosedAdvertDialog;

/**
 * Author：CL
 * 日期:2020/6/28
 * 说明：退出广告Dialog
 **/
public class ClosedAdvertDialog extends BaseClosedAdvertDialog {
    public ClosedAdvertDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initView() {
        setContentView(R.layout.advert_helper_dialog_close_adv);

        rl_content = findViewById(R.id.rl_content);
        fl_content = findViewById(R.id.fl_content);
        rl_content1 = findViewById(R.id.rl_content1);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_confirm = findViewById(R.id.bt_confirm);

    }
}
