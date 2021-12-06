package com.thl.thl_advertlibrary.network.bean;

/**
 * @author
 * @time 2020/3/27 17
 */
public class FreeTimeModel {


    /**
     * F_SetId : e22be38c-e8a8-4cbc-8be6-9282cf706b5d
     * F_Package : com.lilaitech.signin
     * F_Channel : vivo
     * F_VersionCode : 1
     * F_AdActiveSecond : 10
     * F_CopyDelaySecond : 3600
     * F_EnabledMark : 1
     * F_CopyText1 : ￥tqXo1RhQgXB￥
     * F_CopyText2 : bbb
     * F_CopyTimes : 1
     * F_CreateDate : null
     * F_ModifyDate : null
     */

    private String F_SetId;
    private String F_Package;
    private String F_Channel;
    private int F_VersionCode;
    private int F_AdActiveSecond;
    private int F_CopyDelaySecond;
    private int F_EnabledMark;
    private String F_CopyText1;
    private String F_CopyText2;
    private int F_CopyTimes;
    private Object F_CreateDate;
    private Object F_ModifyDate;

    private int F_NoAdTimes;//初始免广告次数：
    private int F_ViewAdTimes; //看广告免费次数：

    public int lastCopyTime;

    public int getF_NoAdTimes() {
        return F_NoAdTimes;
    }

    public void setF_NoAdTimes(int f_NoAdTimes) {
        F_NoAdTimes = f_NoAdTimes;
    }

    public int getF_ViewAdTimes() {
        return F_ViewAdTimes;
    }

    public void setF_ViewAdTimes(int f_ViewAdTimes) {
        F_ViewAdTimes = f_ViewAdTimes;
    }
    public String getF_SetId() {
        return F_SetId;
    }

    public void setF_SetId(String F_SetId) {
        this.F_SetId = F_SetId;
    }

    public String getF_Package() {
        return F_Package;
    }

    public void setF_Package(String F_Package) {
        this.F_Package = F_Package;
    }

    public String getF_Channel() {
        return F_Channel;
    }

    public void setF_Channel(String F_Channel) {
        this.F_Channel = F_Channel;
    }

    public int getF_VersionCode() {
        return F_VersionCode;
    }

    public void setF_VersionCode(int F_VersionCode) {
        this.F_VersionCode = F_VersionCode;
    }

    public int getF_AdActiveSecond() {
        return F_AdActiveSecond;
    }

    public void setF_AdActiveSecond(int F_AdActiveSecond) {
        this.F_AdActiveSecond = F_AdActiveSecond;
    }

    public int getF_CopyDelaySecond() {
        return F_CopyDelaySecond;
    }

    public void setF_CopyDelaySecond(int F_CopyDelaySecond) {
        this.F_CopyDelaySecond = F_CopyDelaySecond;
    }

    public int getF_EnabledMark() {
        return F_EnabledMark;
    }

    public void setF_EnabledMark(int F_EnabledMark) {
        this.F_EnabledMark = F_EnabledMark;
    }

    public String getF_CopyText1() {
        return F_CopyText1;
    }

    public void setF_CopyText1(String F_CopyText1) {
        this.F_CopyText1 = F_CopyText1;
    }

    public String getF_CopyText2() {
        return F_CopyText2;
    }

    public void setF_CopyText2(String F_CopyText2) {
        this.F_CopyText2 = F_CopyText2;
    }

    public int getF_CopyTimes() {
        return F_CopyTimes;
    }

    public void setF_CopyTimes(int F_CopyTimes) {
        this.F_CopyTimes = F_CopyTimes;
    }

    public Object getF_CreateDate() {
        return F_CreateDate;
    }

    public void setF_CreateDate(Object F_CreateDate) {
        this.F_CreateDate = F_CreateDate;
    }

    public Object getF_ModifyDate() {
        return F_ModifyDate;
    }

    public void setF_ModifyDate(Object F_ModifyDate) {
        this.F_ModifyDate = F_ModifyDate;
    }
}
