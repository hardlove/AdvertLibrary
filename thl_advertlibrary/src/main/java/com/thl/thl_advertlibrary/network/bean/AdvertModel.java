package com.thl.thl_advertlibrary.network.bean;


import com.google.gson.Gson;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 广告控制类
 */
public class AdvertModel extends LitePalSupport implements Serializable {

    private int id;
    private String aid;//	String	广告id
    private String advert_location;//	open开屏、banner主页横幅、finish完成页、cancel取消付款、quit退出、float浮标、button小图标、Lock视频解锁广告、active激活广告、chapin1普通插屏、neirong1内容横幅、apptab（tab页）、flow（信息流）
    private String advert_title;//	广告标题
    private long update_time;//	long	修改时间
    private int is_open;//	int	是否开启
    private int browser_open;//	int	打开方式 0-应用内，1-外部浏览器

    private int width;//	广告宽
    private int height;//	广告高度

    private int advert_type;//	int	广告类型	0-html；1:应用市场apk下载;2-apk（文件）下载；3-微信小程序；4-展示图； 5-广州图霸；6-广点通sdk,7-推啊；8-淘宝商品推广,9-穿山甲
    private String advert_param_0;//	String	参数1	0/1/2/3/4/8：为图片路径 6：广点通id 7：图片路径 5：图片路径，9：应用id
    private String advert_param_1;//	String	参数2	0-页面url;2-apk的url；3：path；1-apk包名;5-图片展示回调；6-channel 7-html的url 8：appKey,9-广告位id
    private String advert_param_2;//	String	参数3	2-文件md5；3-userName（小程序id）；1-应用市场包名列表；8：secret 5：html的url；
    private String advert_param_3;//	String	参数4	8:pid   5：html打开的回调,;3-应用id
    private String advert_param_4;//	String	参数5	8:AdzoneId
    private String advert_param_5;//	String	参数6	8:FavoritesId
    private String advert_param_6;//	String	参数7	8:unionId
    private String advert_param_7;//	String	参数8	8:subPid

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdvert_param_7() {
        return advert_param_7;
    }

    public void setAdvert_param_7(String advert_param_7) {
        this.advert_param_7 = advert_param_7;
    }


    public String getAdvert_param_6() {
        return advert_param_6;
    }

    public void setAdvert_param_6(String advert_param_6) {
        this.advert_param_6 = advert_param_6;
    }

    public int getBrowser_open() {
        return browser_open;
    }

    public void setBrowser_open(int browser_open) {
        this.browser_open = browser_open;
    }

    public String getAdvert_param_5() {
        return advert_param_5;
    }

    public void setAdvert_param_5(String advert_param_5) {
        this.advert_param_5 = advert_param_5;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getAdvert_location() {
        return advert_location;
    }

    public void setAdvert_location(String advert_location) {
        this.advert_location = advert_location;
    }

    public String getAdvert_title() {
        return advert_title;
    }

    public void setAdvert_title(String advert_title) {
        this.advert_title = advert_title;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public int getAdvert_type() {
        return advert_type;
    }

    public void setAdvert_type(int advert_type) {
        this.advert_type = advert_type;
    }

    public String getAdvert_param_0() {
        return advert_param_0;
    }

    public void setAdvert_param_0(String advert_param_0) {
        this.advert_param_0 = advert_param_0;
    }

    public String getAdvert_param_1() {
        return advert_param_1;
    }

    public void setAdvert_param_1(String advert_param_1) {
        this.advert_param_1 = advert_param_1;
    }

    public String getAdvert_param_2() {
        return advert_param_2;
    }

    public void setAdvert_param_2(String advert_param_2) {
        this.advert_param_2 = advert_param_2;
    }

    public String getAdvert_param_3() {
        return advert_param_3;
    }

    public void setAdvert_param_3(String advert_param_3) {
        this.advert_param_3 = advert_param_3;
    }

    public String getAdvert_param_4() {
        return advert_param_4;
    }

    public void setAdvert_param_4(String advert_param_4) {
        this.advert_param_4 = advert_param_4;
    }

    @Override
    public boolean save() {
        AdvertModel model = LitePal.where("aid = ?", aid).findFirst(this.getClass());
        if (model != null) {
            model.delete();
        }
        return super.save();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
