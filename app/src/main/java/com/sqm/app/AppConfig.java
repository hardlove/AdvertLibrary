package com.sqm.app;

public class AppConfig {
    /*Debug版本不加密，Release版本加密*/
    public static boolean API_ENCRYPTION = !BuildConfig.DEBUG;
    public static final String AES_KEY = "commonconfig@666";
    /*广告配置（正式环境）*/
    public static final String GENERAL_HOST_BUSS = "http://browser.51star.top:8080";
    /* 广告配置（测试环境）*/
    public static final String GENERAL_HOST_BUSS_TEST = "http://browser.fanghenet.com";

    /*用户协议*/
    public static final String USER_AGREEMENT_URL = "http://www.junke.online/9192eed3-9023-41a6-91c6-ec13fee0b0aa.html";
    /*隐私政策*/
    public static final String PRIVACY_POLICY_URL = "http://www.junke.online/f616c222-471b-4632-ab73-6e37b0fc632d.html";
    public static String BASE_URL;
    /*APP信息采集别名*/
    public static final String APP_GATHER_NAME = "kongkeApp_01";

    /*修改服务器环境*/
    static {
//        if (BuildConfig.isRelease) {
//            BASE_URL = "http://tool.sqcat.cn";// 正式
//        } else {
//            BASE_URL = "http://dev1.sqcat.cn";// 测试
//        }
        BASE_URL = "http://dev1.sqcat.cn";// 测试
    }

}
