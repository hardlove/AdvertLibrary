package com.thl.thl_advertlibrary.network.bean;



/**
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_ClickRateModel {
    
    private String F_InfoId;//信息Id",
    private String F_AppName;//App名称",
    private int F_VersionCode;//App名称",
    private String F_IP;//用户Ip",
    private String F_Info1;//信息1",
    private String F_CreateUserId;//System",
    private String F_CreateUserName;//超级管理员"
    private String F_Channel ;

    public String getF_Channel() {
        return F_Channel;
    }

    public void setF_Channel(String f_Channel) {
        F_Channel = f_Channel;
    }

    public int getF_VersionCode() {
        return F_VersionCode;
    }

    public void setF_VersionCode(int f_VersionCode) {
        F_VersionCode = f_VersionCode;
    }

    public String getF_InfoId() {
        return F_InfoId;
    }

    public void setF_InfoId(String f_InfoId) {
        F_InfoId = f_InfoId;
    }

    public String getF_AppName() {
        return F_AppName;
    }

    public void setF_AppName(String f_AppName) {
        F_AppName = f_AppName;
    }

    public String getF_IP() {
        return F_IP;
    }

    public void setF_IP(String f_IP) {
        F_IP = f_IP;
    }

    public String getF_Info1() {
        return F_Info1;
    }

    public void setF_Info1(String f_Info1) {
        F_Info1 = f_Info1;
    }

    public String getF_CreateUserId() {
        return F_CreateUserId;
    }

    public void setF_CreateUserId(String f_CreateUserId) {
        F_CreateUserId = f_CreateUserId;
    }

    public String getF_CreateUserName() {
        return F_CreateUserName;
    }

    public void setF_CreateUserName(String f_CreateUserName) {
        F_CreateUserName = f_CreateUserName;
    }


}
