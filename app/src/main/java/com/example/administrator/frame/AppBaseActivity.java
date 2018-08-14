package com.example.administrator.frame;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.stepapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/29.
 */

public abstract class AppBaseActivity extends BaseActivity {
    @BindView(R.id.leftText)
    protected TextView tv_left;
    @BindView(R.id.leftImg)
    protected ImageView iv_left;
    @BindView(R.id.titleImage)
    protected ImageView iv_title_image;
    @BindView(R.id.title)
    protected TextView tv_title_name;
    @BindView(R.id.rightText)
    protected TextView tv_right;
    @BindView(R.id.rightImg)
    protected ImageView iv_right_image;


    protected void initView() {
        LinearLayout linearLayout = findViewById(R.id.layout_title);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.addView(View.inflate(this, getLayoutId(), null), layoutParams);
        ButterKnife.bind(this);
        setTitle();
    }

    @Override
    protected void onInitVariable() {
        initData();
    }

    protected int getContentViewId() {
        return R.layout.activity_app_base;
    }


    /**
     * 设置title名称
     */
    protected abstract void setTitle();

    protected abstract void initData();

    protected abstract int getLayoutId();

    public void setTitle(String titleName) {
        tv_title_name.setText(titleName);
    }

    public void setTitleLeftText(String text) {
        tv_left.setText(text);
        tv_left.setVisibility(View.VISIBLE);
    }

    public void setTitleLeftImage(int resource) {
        iv_left.setImageResource(resource);
        iv_left.setVisibility(View.VISIBLE);
    }

    public void setTitleImage(int resource) {
        iv_title_image.setImageResource(resource);
        iv_title_image.setVisibility(View.VISIBLE);
    }

    public void setTitleRightText(String text) {
        tv_right.setText(text);
        tv_right.setVisibility(View.VISIBLE);
    }

    public void setTitleLeftText(int id) {
        iv_left.setImageResource(id);
    }

    public void setTitleRightImg(int resource) {
        iv_right_image.setImageResource(resource);
        iv_right_image.setVisibility(View.VISIBLE);
    }


    public void back(View v) {
        this.finish();
    }

    public void finish(View view) {
    }
}
