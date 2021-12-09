package com.sqm.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sqm.advert_helper.adv.CommonAdvertLoadHelper;
import com.thl.thl_advertlibrary.helper.NewTTExpressAdvHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonAdvertLoadHelper.shoNewExpressAdv(this, new NewTTExpressAdvHelper.TTExpressAdvListener() {
            @Override
            public void onSkip() {

            }

            @Override
            public void onNetworkError() {

            }
        },"mainadmulti");

    }
}