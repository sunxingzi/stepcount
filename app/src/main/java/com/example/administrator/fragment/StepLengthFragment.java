package com.example.administrator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.stepapp.R;
import com.example.administrator.util.Utils;

/**
 * Created by Administrator on 2018/6/27.
 */

public class StepLengthFragment extends BaseDialogFragment {
    public static final String STEP_LENGTH = "stepLength";
    private StepDataListener listener;

    public interface StepDataListener {
        void setStepData(float stepLen);
    }
    private EditText inputStepLen;
    @Override
    public int getLayout() {
        return R.layout.view_dialog_input;
    }

    public static StepLengthFragment newInstance(float stepLen){
        StepLengthFragment fragment = new StepLengthFragment();
        Bundle bundle = new Bundle();
        bundle.putFloat(STEP_LENGTH,stepLen);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StepDataListener){
            listener = (StepDataListener) context;
        }else{
            throw new IllegalArgumentException("activity must implements StepDataListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void findItemView(View rootView) {
        TextView msg = (TextView) rootView.findViewById(R.id.tv_msg);
        Button cancel = (Button) rootView.findViewById(R.id.btn_cancle);
        Button sure = (Button) rootView.findViewById(R.id.btn_sure);
        inputStepLen = (EditText) rootView.findViewById(R.id.input);
        msg.setText(R.string.setting_step_length);

        float stepLength = getArguments().getFloat(STEP_LENGTH);
        inputStepLen.setText(String.valueOf(stepLength));

        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);
    }

    @Override
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancle:
                getDialog().dismiss();
                break;
            case R.id.btn_sure:
                String val = inputStepLen.getText().toString();
                if(!TextUtils.isEmpty(val) && val.length() > 0){
                    float stepLen = Float.parseFloat(val);
                    //返回数据给settingactivity
                    listener.setStepData(stepLen);
                } else {
                    Utils.makeToast(getActivity(), getString(R.string.please_input_exact_params));
                }
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
}
