package com.sqm.advert_helper.adv;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ObjectUtils;
import com.sqm.advert_helper.R;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.BannerImageInfo;
import com.stx.xhb.xbanner.entity.LocalImageInfo;
import com.stx.xhb.xbanner.entity.SimpleBannerInfo;
import com.thl.thl_advertlibrary.helper.BannerAdvertHelper;
import com.thl.thl_advertlibrary.helper.InterBannerAdvertHelper;
import com.thl.thl_advertlibrary.helper.NewInterstitialAdvertHelper;
import com.thl.thl_advertlibrary.helper.TTAdRewardVideoHelper;
import com.thl.thl_advertlibrary.helper.TTAdVideoHelper;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.utils.AdvertUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：CL
 * 日期:2020/08/11
 * 说明：通用广告加载帮助类
 **/
public class CommonAdvertLoadHelper {
    private static final String TAG = "CommonAdvertLoadHelper";

    /**
     * 加载内容横幅广告
     * 使用时忽略配置的横幅尺寸类型
     * 广告位分别对应后台配置对横幅1,横幅2,...,横幅8尺寸
     *
     * @param activity
     * @param advContainer
     */
    public static void loadContentBanner(AppCompatActivity activity, RelativeLayout advContainer) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing() || advContainer == null) {
            return;
        }
        AdvertModel model = AdvertUtils.searchFirstAdvertByLocation("neirong1");
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong2");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong3");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong4");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong5");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong6");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong7");
        }
        if (model == null) {
            model = AdvertUtils.searchFirstAdvertByLocation("neirong8");
        }
        loadContentBanner(activity, advContainer, model);
    }


    /**
     * 加载内容横幅广告
     * model 指定尺寸的内容横幅广告数据 横幅1,横幅2,...,横幅8（neirong1,...,neirong2,...,neirong8）
     *
     * @param activity
     * @param advContainer
     */
    public static void loadContentBanner(AppCompatActivity activity, RelativeLayout advContainer, AdvertModel model) {
        if (model == null) {
            advContainer.setVisibility(View.GONE);
            Log.e(TAG, "未找到内容横幅广告，无法加载！！！");
            return;
        }
        InterBannerAdvertHelper advertHelper = new InterBannerAdvertHelper(activity, model);
        advertHelper.setListener(new InterBannerAdvertHelper.OnIntercrionAdvertListener() {
            @Override
            public void initSuccess(View view) {
                try {
                    advertHelper.showAdvert(advContainer, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void advertDismiss() {
            }

            @Override
            public void advertClick() {
            }
        });
        advertHelper.initAdvert(advContainer);//展示广告
    }

    /**
     * 播放全屏视频
     *
     * @param activity
     * @param ttAdVideoListener
     */
    public static void showTTFullScreenVideoAd(Activity activity, @Nullable String message, TTAdVideoHelper.TTAdVideoListener ttAdVideoListener) {
        ViewGroup rootView = activity.findViewById(android.R.id.content);

        Object tag = rootView.getTag(R.id.advert_helper_ttfullscreen_video_helper_id);
        TTAdVideoHelper ttAdVideoHelper;
        if (tag instanceof TTAdVideoHelper) {
            ttAdVideoHelper = (TTAdVideoHelper) tag;
            Log.d(TAG, "找到TTAdVideoHelper, TTAdVideoHelper:" + ttAdVideoHelper.hashCode());
        } else {
            ttAdVideoHelper = new TTAdVideoHelper(activity);
            rootView.setTag(R.id.advert_helper_ttfullscreen_video_helper_id, ttAdVideoHelper);
            Log.d(TAG, "未找到TTAdVideoHelper, 初始化新的TTAdVideoHelper:" + ttAdVideoHelper.hashCode());
        }

        if (TextUtils.isEmpty(message)) {
            /*不显示弹窗，直接加载广告*/
            ttAdVideoHelper.showAdvertModel(ttAdVideoListener);
            return;
        }
        ttAdVideoHelper.showAdvertModel(message, ttAdVideoListener);
    }

    /**
     * 播放视频激励广告
     *
     * @param activity
     * @param message           弹窗提示内容
     * @param ttAdVideoListener
     */
    public static void showRewardedVideoAdv(Activity activity, @Nullable String message, TTAdRewardVideoHelper.TTAdVideoListener ttAdVideoListener) {
        ViewGroup rootView = activity.findViewById(android.R.id.content);

        Object tag = rootView.getTag(R.id.advert_helper_reward_video_helper_id);
        TTAdRewardVideoHelper rewardVideoHelper;
        if (tag instanceof TTAdRewardVideoHelper) {
            rewardVideoHelper = (TTAdRewardVideoHelper) tag;
            Log.d(TAG, "找到TTAdRewardVideoHelper, TTAdRewardVideoHelper:" + rewardVideoHelper.hashCode());
        } else {
            rewardVideoHelper = new TTAdRewardVideoHelper(activity);
            rootView.setTag(R.id.advert_helper_reward_video_helper_id, rewardVideoHelper);
            Log.d(TAG, "未找到TTAdRewardVideoHelper, 初始化新的TTAdRewardVideoHelper:" + rewardVideoHelper.hashCode());
        }
        if (TextUtils.isEmpty(message)) {
            /*不显示弹窗，直接加载广告*/
            rewardVideoHelper.showWaterModel(ttAdVideoListener);
            return;
        }
        rewardVideoHelper.showWaterModel(message, ttAdVideoListener);
    }

    /**
     * 加载XBanner轮播广告
     *
     * @param activity
     * @param mXBanner
     * @param placeholder 没有广告资源时的占位图
     */
    public static void loadLoopBanner(AppCompatActivity activity, XBanner mXBanner, @DrawableRes int placeholder) {
        List<AdvertModel> list = AdvertUtils.searchAdvertByLocation("banner");
        List<SimpleBannerInfo> advertList = new ArrayList<>();
        if (ObjectUtils.isEmpty(list)) {
            Log.e(TAG, "没有Banner轮播广告资源");
            advertList.add(new LocalImageInfo(placeholder));
        } else {
            for (AdvertModel model : list) {
                advertList.add(new BannerImageInfo(model));
                AdvertUtils.showAdvertRecord(activity, model);
            }
        }
        mXBanner.setBannerData(R.layout.fhad_item_banner, advertList);
        mXBanner.loadImage((banner, object, view, position) -> {
            RelativeLayout rl_content = (RelativeLayout) view;
            rl_content.removeAllViews();
            BannerAdvertHelper bannerAdvertHelper = new BannerAdvertHelper();
            if (object instanceof BannerImageInfo) {
                AdvertModel model = (AdvertModel) ((BannerImageInfo) object).getXBannerUrl();
                bannerAdvertHelper.showAdvertModelItem(activity, rl_content, model);
            } else if (object instanceof LocalImageInfo) {
                ImageView imageView = new ImageView(activity);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                rl_content.addView(imageView, 0, params);
                int resId = ((LocalImageInfo) object).getXBannerUrl();
                imageView.setImageResource(resId);
            }
        });
        mXBanner.setOnItemClickListener((banner1, model1, view1, position1) -> {
            SimpleBannerInfo bannerInfo = advertList.get(position1);
            if (bannerInfo instanceof BannerImageInfo) {
                AdvertModel advertModel = (AdvertModel) ((BannerImageInfo) bannerInfo).getXBannerUrl();
                AdvertUtils.clickAdvert(activity, advertModel);
            } else if (bannerInfo instanceof LocalImageInfo) {
                // TODO: 2021/1/7 点击本地图片banner
            }
        });
        mXBanner.setAutoPalyTime(1000 * 10);
        mXBanner.startAutoPlay();
    }

    /*显示插屏广告*/

    /**
     * 方法已过时
     * @see #showExpressAdv(FragmentActivity, String, TTExpressAdvHelper.TTExpressAdvListener)
     *
     * @param activity
     * @param listener
     */
    @Deprecated
    public static void showExpressAdv(FragmentActivity activity, TTExpressAdvHelper.TTExpressAdvListener listener) {
        TTExpressAdvHelper ttExpressAdvHelper = new TTExpressAdvHelper(activity);
        ttExpressAdvHelper.showAdvert(listener);
    }

    /*显示插屏广告*/
    public static void showExpressAdv(FragmentActivity activity, @TTExpressAdvHelper.AdvTypeConfig String advType, TTExpressAdvHelper.TTExpressAdvListener listener) {
        TTExpressAdvHelper ttExpressAdvHelper = new TTExpressAdvHelper(activity, advType);
        ttExpressAdvHelper.showAdvert(listener);
    }

    /*显示新插屏广告*/
    public static void shoNewExpressAdv(FragmentActivity activity,NewInterstitialAdvertHelper.OnAdvertCallback callback) {
        NewInterstitialAdvertHelper newInterstitialAdvertHelper = new NewInterstitialAdvertHelper(new WeakReference<>(activity), "newinsert");
        newInterstitialAdvertHelper.setCallback(callback);
        newInterstitialAdvertHelper.loadNewInterstitialAd();

    }
}
