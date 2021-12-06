package com.stx.xhb.xbanner.entity;


import com.thl.thl_advertlibrary.network.bean.AdvertModel;

public class BannerImageInfo extends SimpleBannerInfo {

    private AdvertModel bannerRes;

    public BannerImageInfo(AdvertModel bannerRes) {
        this.bannerRes = bannerRes;
    }

    @Override
    public AdvertModel getXBannerUrl() {
        return bannerRes;
    }

}