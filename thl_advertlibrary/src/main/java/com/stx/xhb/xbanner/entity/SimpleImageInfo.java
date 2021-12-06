package com.stx.xhb.xbanner.entity;

public class SimpleImageInfo extends SimpleBannerInfo {
    private String mUrl;
    public SimpleImageInfo(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public String getXBannerUrl() {
        return mUrl;
    }
}
