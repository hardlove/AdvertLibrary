package com.thl.thl_advertlibrary.network.bean;



/**
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_BaseModel<T> {
    private int code;

    private String info;

    private T data;

    public boolean isSuccess() {
        //成功
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
