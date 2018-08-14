package com.example.administrator.stepapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.frame.AppBaseActivity;
import com.example.administrator.frame.BaseActivity;

import butterknife.BindView;

public class SampleTimeActivity extends AppBaseActivity implements RadioGroup.OnCheckedChangeListener{
    public static final String TIME_INDEX_KEY = "time_index_key";
    @BindView(R.id.radio_group)
    protected RadioGroup radioGroup;
    @BindView(R.id.radio1)
    protected RadioButton radioButton1;
    @BindView(R.id.radio2)
    protected RadioButton radioButton2;
    @BindView(R.id.radio3)
    protected RadioButton radioButton3;
    @BindView(R.id.radio4)
    protected RadioButton radioButton4;
    @BindView(R.id.radio5)
    protected RadioButton radioButton5;
    @BindView(R.id.radio6)
    protected RadioButton radioButton6;
    @BindView(R.id.radio7)
    protected RadioButton radioButton7;
    @BindView(R.id.radio8)
    protected RadioButton radioButton8;

    private int index;



    @Override
    protected void setTitle() {
        setTitle("设置传感器采样时间");
        setTitleRightText("完成");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sample_time;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            int intExtra = intent.getIntExtra(SettingActivity.SEND_TIME_KEY, 0);
            Log.e("555", "onInitView: intExtra "+intExtra);
            switch (intExtra){
                case 0:
                    index = 0;
                    radioButton1.setChecked(true);
                    break;
                case 1:
                    index = 1;
                    radioButton2.setChecked(true);
                    break;
                case 2:
                    index = 2;
                    radioButton3.setChecked(true);
                    break;
                case 3:
                    index = 3;
                    radioButton4.setChecked(true);
                    break;
                case 4:
                    index = 4;
                    radioButton5.setChecked(true);
                    break;
                case 5:
                    index = 5;
                    radioButton6.setChecked(true);
                    break;
                case 6:
                    index = 6;
                    radioButton7.setChecked(true);
                    break;
                case 7:
                    index = 7;
                    radioButton8.setChecked(true);
                    break;
            }
        }
        radioGroup.setOnCheckedChangeListener(this);
    }


    @Override
    public void finish(View view) {
        super.finish(view);
        //设置完成返回
        Intent intent = new Intent();
        Log.e("555",String.valueOf(index));
        intent.putExtra(TIME_INDEX_KEY, index);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radio1:
                index = 0;
                break;
            case R.id.radio2:
                index = 1;
                break;
            case R.id.radio3:
                index = 2;
                break;
            case R.id.radio4:
                index = 3;
                break;
            case R.id.radio5:
                index = 4;
                break;
            case R.id.radio6:
                index = 5;
                break;
            case R.id.radio7:
                index = 6;
                break;
            case R.id.radio8:
                index = 7;
                break;
            default:
                break;
        }
    }
}
