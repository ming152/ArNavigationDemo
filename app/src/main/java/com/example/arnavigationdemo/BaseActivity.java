package com.example.arnavigationdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.arnavigationdemo.util.DisplayUtil;

/**
 * Created by ming on 2018/7/25.
 */

public class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            DisplayUtil.MIUISetStatusBarLightMode(this.getWindow(), true);
            DisplayUtil.FlymeSetStatusBarLightMode(this.getWindow(), true);
        }
    }
}
