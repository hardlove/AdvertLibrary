package com.thl.thl_advertlibrary.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: klaus
 * created on: 2021/8/23 13:45
 * description:
 */
public class WebUrlFilter {
    private static final String TAG = WebUrlFilter.class.getSimpleName();
    private List<String> filters;

    public static WebUrlFilter get(){
       return Holder.instance;
    }

    private  static class Holder {
        public  final static WebUrlFilter instance = new WebUrlFilter();
    }

    public WebUrlFilter() {
        filters = new ArrayList<>();
//        filters.add("https://resource.sqcat.cn/product/gaokao/h5/gaokao1/peking.html");
//        filters.add("https://resource.sqcat.cn/product/gaokao/h5/gaokao1/lecture.html");
//        filters.add("https://resource.sqcat.cn/test/gaokao/h5/gaokao1/peking.html");
//        filters.add("https://resource.sqcat.cn/test/gaokao/h5/gaokao1/lecture.html");
    }

    /**
     * 添加需要js注入的url地址，
     * @param url
     */
    public void addFilters(String url) {
        if (!filters.contains(url)) {
            filters.add(url);
        }
    }

    /**
     * 根据添加的url，过滤是否需要添加注入js代码
     * @param url
     * @return 默认返回true
     */
    public boolean isShouldPayUrl(String url) {
        return true;
//        return filters.contains(url);
    }
}
