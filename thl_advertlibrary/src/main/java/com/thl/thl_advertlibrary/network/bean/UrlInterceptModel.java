package com.thl.thl_advertlibrary.network.bean;


import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 广告控制类
 */
public class UrlInterceptModel extends LitePalSupport implements Serializable {
    private String iid;//	String	广告id
    private String operation_name;
    private String application_name;
    private String url_fragment;
    private int fragment_type;
    private int after_operation;

    @Override
    public boolean save() {
        UrlInterceptModel model = LitePal.where("iid = ?", iid).findFirst(this.getClass());
        if (model != null) {
            model.delete();
        }
        return super.save();
    }


    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getOperation_name() {
        return operation_name;
    }

    public void setOperation_name(String operation_name) {
        this.operation_name = operation_name;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getUrl_fragment() {
        return url_fragment;
    }

    public void setUrl_fragment(String url_fragment) {
        this.url_fragment = url_fragment;
    }

    public int getFragment_type() {
        return fragment_type;
    }

    public void setFragment_type(int fragment_type) {
        this.fragment_type = fragment_type;
    }

    public int getAfter_operation() {
        return after_operation;
    }

    public void setAfter_operation(int after_operation) {
        this.after_operation = after_operation;
    }


}
