package com.example.administrator.stepapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.administrator.frame.AppBaseActivity;
import com.example.administrator.frame.BaseActivity;

/**
 * Created by Administrator on 2018/6/25.
 */

public class WelcomeActivity extends AppBaseActivity{
    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }
}
