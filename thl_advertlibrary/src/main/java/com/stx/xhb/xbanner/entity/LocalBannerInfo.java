package com.stx.xhb.xbanner.entity;

import androidx.annotation.DrawableRes;

public class LocalBannerInfo extends SimpleBannerInfo {
    @DrawableRes
    private int bannerRes;

    public LocalBannerInfo(int bannerRes) {
        this.bannerRes = bannerRes;
    }

    public LocalBannerInfo(int bannerRes, Object tag) {
        this.bannerRes = bannerRes;
        this.tag = tag;
    }

    @Override
    public Integer getXBannerUrl() {
        return bannerRes;
    }

    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
