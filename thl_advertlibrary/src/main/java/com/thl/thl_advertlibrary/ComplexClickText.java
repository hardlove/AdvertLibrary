package com.thl.thl_advertlibrary;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.thl.thl_advertlibrary.activity.Fhad_WebPageActivity;

public class ComplexClickText extends ClickableSpan {
    private Context context;
    private String url;
    private String title;
    private int color;

    public ComplexClickText(Context context, String url, String title) {
        this.context = context;
        this.url = url;
        this.title = title;
        this.color = Color.RED;
    }
    public ComplexClickText(Context context, String url, String title,int color) {
        this.context = context;
        this.url = url;
        this.title = title;
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        //设置文本的颜色
        ds.setColor(color);
        //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {

        Fhad_WebPageActivity.openActivity(context, url, title);
    }
}