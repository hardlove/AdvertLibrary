package com.thl.thl_advertlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.thl.thl_advertlibrary.activity.Fhad_WebPageActivity;
import com.thl.thl_advertlibrary.downloadhelper.DownloadHelper;
import com.thl.thl_advertlibrary.network.Fhad_HttpMethodUtils;
import com.thl.thl_advertlibrary.network.bean.AdvertModel;
import com.thl.thl_advertlibrary.permissions.PermissionHelper;
import com.thl.thl_advertlibrary.permissions.RequestPermissionListener;

import org.litepal.LitePal;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author ${dell}
 * @time 2019/9/4 16
 */
public class AdvertUtils {

    /**
     * 打开/关闭广告
     *
     * @param isOpen
     */
    public static void switchAdvert(boolean isOpen, String... advert_locations) {
        StringBuffer whereStr = new StringBuffer();
        whereStr.append("and ( ");
        if (null != advert_locations) {
            for (int i = 0; i < advert_locations.length; i++) {
                String advert_location = advert_locations[i];
                whereStr.append((i == 0 ? "" : "or ") + " advert_location = '" + advert_location + "' ");
            }
        }
        whereStr.append(")");
        List<AdvertModel> advertModels = LitePal.where((isOpen ? "is_open=0 "
                : "is_open=1  ") + whereStr.toString()).find(AdvertModel.class);
        if (null == advertModels) {
            return;
        }
        for (AdvertModel advertModel : advertModels) {
            advertModel.setIs_open(isOpen ? 1 : 0);
            advertModel.save();
        }

    }

    //根据广告位查询展示的广告列表
    public static List<AdvertModel> searchAdvertByLocation(String advert_location) {
        List<AdvertModel> advertModels = LitePal.where("advert_location =? and is_open =?",
                advert_location, "1").find(AdvertModel.class);
        return advertModels;
    }

    //根据广告位查询展示的广告第一条
    public static AdvertModel searchFirstAdvertByLocation(String advert_location) {
        AdvertModel advertModel = LitePal.where("advert_location =? and is_open =?",
                advert_location, "1").findFirst(AdvertModel.class);
        return advertModel;
    }

    //根据广告位查询展示的广告第一条
    public static AdvertModel searchFirstAdvertByType(int advert_type) {
        AdvertModel advertModel = LitePal.where("advert_type =? and is_open "
                , advert_type + "", "1").findFirst(AdvertModel.class);
        return advertModel;
    }

    /**
     * 显示广告，非sdk广告
     *
     * @param context
     * @param model
     * @param rl_content
     */
    public static void showAdvert(Activity context, AdvertModel model, RelativeLayout rl_content) {
        ImageView imageView = new ImageView(context);
        rl_content.addView(imageView, 0);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setLayoutParams(params);
        showAdvert(context, model, imageView);
    }

    /**
     * 显示广告，非sdk广告
     *
     * @param context
     * @param model
     * @param imageView
     */
    public static void showAdvert(Activity context, AdvertModel model, ImageView imageView) {
        imageView.setAdjustViewBounds(true);
        if (model.getAdvert_location().equals("open") || model.getAdvert_location().equals("active")) {//启动页广告要全屏
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (model.getAdvert_type() == 7) {//推呀链接拼上硬件id
            Glide.with(context).load(model.getAdvert_param_0() + "&device_id="
                    + Fhad_DeviceUtil.getDeviceId(context)).into(imageView);
        } else {
            Glide.with(context).load(model.getAdvert_param_0()).into(imageView);
        }

        if (!model.getAdvert_location().equals("banner")) {//避免banner每次展示都会调用
            showAdvertRecord(context, model);
        }
    }

    public static void showAdvertRecord(Activity context, AdvertModel model) {
        Fhad_HttpMethodUtils.clickRate(context, model.getAid(), "展示");
        if (model.getAdvert_type() == 5) {
            Fhad_HttpMethodUtils.doGetAsyn(model.getAdvert_param_1(), null);
        }
    }

    /**
     * 广告点击
     *
     * @param context
     * @param model
     */
    public static void clickAdvert(AppCompatActivity context, AdvertModel model) {
        switch (model.getAdvert_type()) {
            case 0:
                Log.d("=======", "html");
                if (model.getBrowser_open() == 0) {
                    Fhad_WebPageActivity.openActivity(context, model.getAdvert_param_1(), model.getAdvert_title(),model.getAdvert_param_3());
                } else {
                    Log.d("=======", "用外部浏览器打开");
                    Fhad_WebPageActivity.openUrlByBrowser(context, model.getAdvert_param_1());
                }
                break;
            case 1:
                Log.d("=======", "应用市场apk下载");
                if (Fhad_DeviceUtil.isAppInstalled(context, model.getAdvert_param_1())) {//需要下载的apk已安装，去打开
                    context.startActivity(context.getPackageManager().getLaunchIntentForPackage(model.getAdvert_param_1()));
                    break;
                } else {
                    String[] markets = model.getAdvert_param_2().split(",");
                    if (markets != null) {
                        for (String market : markets) {
                            // 判断是否安装过App，否则去市场下载
                            if (Fhad_DeviceUtil.isAppInstalled(context, market)) {
                                Fhad_DeviceUtil.openMarket(context, market, model.getAdvert_param_1());
                                break;
                            }
                        }
                    }
                }
                break;
            case 2:
                Log.d("=======", "apk（文件）下载");
                PermissionHelper.requestPermission(context.getSupportFragmentManager(), new RequestPermissionListener() {
                    @Override
                    public void onSuccess() {
                        new DownloadHelper(context).setUrl(model.getAdvert_param_1()).start();
                    }
                }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                break;
            case 3:
                Log.d("=======", "微信小程序");
                toSmallProgram(context, model.getAdvert_param_3(), model.getAdvert_param_2(),
                        model.getAdvert_param_1());
                break;
            case 4:
                Log.d("=======", "展示图");
                break;
            case 5:
                Log.d("=======", "广州图霸");
                if (model.getBrowser_open() == 0) {
                    Fhad_WebPageActivity.openActivity(context, model.getAdvert_param_2(), model.getAdvert_title());
                } else {
                    Fhad_WebPageActivity.openUrlByBrowser(context, model.getAdvert_param_2());
                }
                Fhad_HttpMethodUtils.doGetAsyn(model.getAdvert_param_3(), null);
                break;
            case 6:
                Log.d("=======", "广点通");
                break;
            case 7:
                Log.d("=======", "推啊");
                if (model.getBrowser_open() == 0) {
                    Fhad_WebPageActivity.openActivity(context, model.getAdvert_param_1() + "&device_id=" + Fhad_DeviceUtil.getDeviceId(context), model.getAdvert_title());
                } else {
                    Fhad_WebPageActivity.openUrlByBrowser(context, model.getAdvert_param_1() + "&device_id=" + Fhad_DeviceUtil.getDeviceId(context));
                }
                break;
            case 8:
                Log.d("=======", "ali百川");
                break;
            default:
                break;
        }
        Fhad_HttpMethodUtils.clickRate(context, model.getAid(), "点击");
    }

    public static void toSmallProgram(Context context, String wxAppId, String userName, String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, wxAppId);// 填应用AppId，APP在开放平台注册的id
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "请安装手机微信", Toast.LENGTH_LONG).show();
            return;
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName; // 填小程序原始id
        req.path = path; //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

}
