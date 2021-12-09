package com.thl.thl_advertlibrary.network;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thl.thl_advertlibrary.config.AdvertConfig;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.network.bean.Fhad_BaseCallBack;
import com.thl.thl_advertlibrary.network.bean.Fhad_BaseModel;
import com.thl.thl_advertlibrary.network.bean.Fhad_ClickRateModel;
import com.thl.thl_advertlibrary.network.bean.FreeTimeModel;
import com.thl.thl_advertlibrary.network.bean.UrlInterceptModel;
import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;
import com.thl.thl_advertlibrary.utils.Fhad_PackageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

import okhttp3.Call;


/**
 * @author ${dell}
 * @time 2019/9/4 09
 */
public class Fhad_HttpMethodUtils {
    public static final String url_updateAdvert = "/LR_WaterMark/AppData/GetAllAdList";
    public static final String url_clickRate = "/LR_WaterMark/AppData/InfoView";
    public static final String url_intercept = "/Browser/AppApi/GetWebViewH5";
    public static final String url_freeTime = "/Browser/AdApi/GetAppSet";
    private static final long delayTime = 1000 * 5;

    /**
     * 更新广告控制
     */
    public static void updateAdvert(Context context) {

        String url = AdvertConfig.GENERAL_HOST_BUSS + url_updateAdvert; //url设置成自己的json-server地址

        OkHttpUtils
                .get()
                .url(url)
                .addParams("versionCode", Fhad_PackageUtil.getVersionCode(context) + "")
                .addParams("channel", Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"))
                .addParams("appName", Fhad_PackageUtil.getPackageName(context))
                .build()
                .execute(new Fhad_BaseCallBack<Fhad_BaseModel<List<AdvertModel>>>() {

                    @Override
                    public void success(Fhad_BaseModel<List<AdvertModel>> result) {
//                        Log.d("Fhad_HttpMethodUtils", "buttonAdvertList:" + result.getData().toString());
                        LitePal.deleteAll(AdvertModel.class);
                        if (!(result.getData() == null || result.getData().isEmpty())) {
                            LitePal.saveAll(result.getData());
                        }
                    }
                });
        //延时执行，避免开屏时界面卡死
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setAdvertFreeTime(context);
            interceptAdvert();
        }, delayTime);
    }

    /**
     * 查询单个广告位广告
     */
    public static void searchAdvert(Context context,String advert_location,Fhad_BaseCallBack callback) {

        String url = AdvertConfig.GENERAL_HOST_BUSS + url_updateAdvert; //url设置成自己的json-server地址

        OkHttpUtils
                .get()
                .url(url)
                .addParams("itemCode", advert_location)
                .addParams("versionCode", Fhad_PackageUtil.getVersionCode(context) + "")
                .addParams("channel", Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"))
                .addParams("appName", Fhad_PackageUtil.getPackageName(context))
                .build()
                .execute(callback);
        setAdvertFreeTime(context);
    }

    /**
     * 更新广告控制
     */
    public static void updateAdvert(Context context, StringCallback callBack) {
        String url = AdvertConfig.GENERAL_HOST_BUSS + url_updateAdvert; //url设置成自己的json-server地址
        OkHttpUtils
                .get()
                .url(url)
                .addParams("versionCode", Fhad_PackageUtil.getVersionCode(context) + "")
                .addParams("channel", Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"))
                .addParams("appName", Fhad_PackageUtil.getPackageName(context))
                .build()
                .execute(callBack);

        //延时执行，避免开屏时界面卡死
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setAdvertFreeTime(context);
            interceptAdvert();
        }, delayTime);

    }

    /**
     * 更新广告控制
     */
    public static void updateAdvert(Context context, Fhad_BaseCallBack callBack) {
        String url = AdvertConfig.GENERAL_HOST_BUSS + url_updateAdvert; //url设置成自己的json-server地址
        OkHttpUtils
                .get()
                .url(url)
                .addParams("versionCode", Fhad_PackageUtil.getVersionCode(context) + "")
                .addParams("channel", Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"))
                .addParams("appName", Fhad_PackageUtil.getPackageName(context))
                .build()
                .execute(callBack);

        //延时执行，避免开屏时界面卡死
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setAdvertFreeTime(context);
            interceptAdvert();
        }, delayTime);
    }

    /**
     * 更新广告控制
     */
    private static void interceptAdvert() {
        String url = AdvertConfig.GENERAL_HOST_BUSS + url_intercept; //url设置成自己的json-server地址
//        doGetAsyn(url, new Fhad_BaseCallBack<Fhad_BaseModel<List<UrlInterceptModel>>>() {
//
//
//            @Override
//            public void success(Fhad_BaseModel<List<UrlInterceptModel>> result) {
//                LitePal.deleteAll(UrlInterceptModel.class);
//                if (!(result.getData() == null || result.getData().isEmpty())) {
//                    LitePal.saveAll(result.getData());
//                }
//            }
//
//        });

        doGetAsyn(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Fhad_BaseModel<List<UrlInterceptModel>> result = new Gson().fromJson(response, new TypeToken<Fhad_BaseModel<List<UrlInterceptModel>>>() {
                }.getType());
                LitePal.deleteAll(UrlInterceptModel.class);
                if (!(result.getData() == null || result.getData().isEmpty())) {
                    LitePal.saveAll(result.getData());
                }
            }
        });
    }

    /**
     * 设置激活广告时间
     */
    private static void setAdvertFreeTime(Context context) {
        String url = AdvertConfig.GENERAL_HOST_BUSS + url_freeTime;
        JSONObject object = new JSONObject();
        try {
            object.put("F_VersionCode", Fhad_PackageUtil.getVersionCode(context) + "");
            object.put("F_Channel", Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"));
            object.put("F_Package", Fhad_PackageUtil.getPackageName(context));
            object.put("IsVip", "1");
//            object.put("RegTime", "")
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .post()
                .url(url)
                .addParams("stringEntity", object.toString())
                .addParams("code", "0")
                .build()
                .execute(new Fhad_BaseCallBack<Fhad_BaseModel<FreeTimeModel>>() {


                    @Override
                    public void success(Fhad_BaseModel<FreeTimeModel> result) {
                        if (result.getData() != null) {
                            AdvertConfig.freeTimeModel = result.getData();
                            AdvertConfig.advertFreeTime = AdvertConfig.freeTimeModel.getF_AdActiveSecond() * 1000;
                            CopyStringToClipboard(context);
                        }
                    }
                });
    }


    public static void CopyStringToClipboard(Context context) {
        if (AdvertConfig.freeTimeModel != null && (!TextUtils.isEmpty(AdvertConfig.freeTimeModel.getF_CopyText1())) &&
                AdvertConfig.freeTimeModel.lastCopyTime < System.currentTimeMillis() - AdvertConfig.freeTimeModel.getF_CopyDelaySecond() * 1000) {
            ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setText(AdvertConfig.freeTimeModel.getF_CopyText1()); // 复制
        }
    }

    /**
     * 点击率统计
     *
     * @param contentId
     * @param param
     */
    public static void clickRate(Context context, String contentId, String param) {
        if (context == null) {
            return;
        }
        Fhad_ClickRateModel info = new Fhad_ClickRateModel();
        info.setF_CreateUserId(Fhad_DeviceUtil.getDeviceId(context));
        info.setF_AppName(Fhad_PackageUtil.getPackageName(context));
        info.setF_VersionCode(Fhad_PackageUtil.getVersionCode(context));
        info.setF_InfoId(contentId);
        info.setF_Info1(param);
        info.setF_Channel(Fhad_PackageUtil.getChannelName(context, "UMENG_CHANNEL"));
        OkHttpUtils
                .post()
                .url(AdvertConfig.GENERAL_HOST_BUSS + url_clickRate)
                .addParams("strEntity", new Gson().toJson(info))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }
                });
    }

    public static void doGetAsyn(String url, Callback callback) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    public static void doPostAsyn(String url, Callback callback) {
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(callback);
    }

}

