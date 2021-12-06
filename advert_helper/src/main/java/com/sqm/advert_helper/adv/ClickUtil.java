package com.sqm.advert_helper.adv;

/**
 * =====================================
 * Copyright (C)
 * 作   者: CL
 * 版   本：1.0.0
 * 创建日期：2019-12-23 16:22
 * 修改日期：
 * 描   述：
 * =====================================
 */
public class ClickUtil {
    private static long lastClickTime;
    /*
     * 防止连续点击
     */
    public static synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    /**
     * 防止连续点击
     *
     * @param millis 指定点击最小时间间隔,单位：毫秒
     * @return
     */
    public static synchronized boolean isFastClick(long millis) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < millis) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    /**
     * 防止连续点击: 支付专用，默认2500毫秒
     * @return
     */
    public static synchronized boolean isFastClickForPay() {
        return isFastClick(2500);

    }

}
