package com.sqm.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sqm.advert_helper.adv.CommonAdvertLoadHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonAdvertLoadHelper.shoNewExpressAdv(this,null);

    }
}