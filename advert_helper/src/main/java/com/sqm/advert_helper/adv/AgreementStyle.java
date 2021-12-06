package com.sqm.advert_helper.adv;

import com.sqm.advert_helper.R;

/**
 * Author：CL
 * 日期:2020/9/1
 * 说明：
 **/
public enum AgreementStyle {
    STYLE_01("style01", R.layout.advert_helper_dialog_agreement_01),
    STYLE_02("style02", R.layout.advert_helper_dialog_agreement_02),
    STYLE_03("style03", R.layout.advert_helper_dialog_agreement_03),
    STYLE_04("style04", R.layout.advert_helper_dialog_agreement_04),
    STYLE_05("style05", R.layout.advert_helper_dialog_agreement_05),
    STYLE_06("style06", R.layout.advert_helper_dialog_agreement_06),
    STYLE_07("style07", R.layout.advert_helper_dialog_agreement_07),
    ;
    private int layoutID;
    private String name;

    AgreementStyle(String name, int layoutID) {
        this.name = name;
        this.layoutID = layoutID;
    }

    public int getLayoutID() {
        return layoutID;
    }

    public String getName() {
        return name;
    }
}
