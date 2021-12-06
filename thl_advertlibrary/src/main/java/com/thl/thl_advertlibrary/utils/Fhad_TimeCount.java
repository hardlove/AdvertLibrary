package com.thl.thl_advertlibrary.utils;

import android.os.CountDownTimer;


/**
 * 计数器
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_TimeCount extends CountDownTimer {

    private TimeOutCallback callback;

    public Fhad_TimeCount(long millisInFuture, long countDownInterval, TimeOutCallback l) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        this.callback = l;
    }

    @Override
    public void onFinish() {//计时完毕时触发
        this.callback.onFinish();
    }

    @Override
    public void onTick(long millisUntilFinished) {//计时过程显示
        this.callback.onTick(millisUntilFinished);
    }

    public interface TimeOutCallback {
        void onFinish();

        void onTick(long st);
    }
}
