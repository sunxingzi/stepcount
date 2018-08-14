package com.example.administrator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.stepapp.R;
import com.example.administrator.util.Utils;

/**
 * Created by Administrator on 2018/6/27.
 */

public class BodyWeightFragment extends BaseDialogFragment {
    public static final String BODY_WEIGHT = "bodyWeight";
    private WeightDataListener listener;

    public interface WeightDataListener {
        void setWeightData(float weightData);
    }

    private EditText inputWeight;

    @Override
    public int getLayout() {
        return R.layout.view_dialog_input;
    }

    public static BodyWeightFragment newInstance(float weight) {
        BodyWeightFragment fragment = new BodyWeightFragment();
        Bundle bundle = new Bundle();
        bundle.putFloat(BODY_WEIGHT, weight);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeightDataListener) {
            listener = (WeightDataListener) context;
        } else {
            throw new IllegalArgumentException("activity must implements WeightDataListener");
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
        inputWeight = (EditText) rootView.findViewById(R.id.input);

        msg.setText(R.string.setting_body_weiht);

        float bodyWeight = getArguments().getFloat(BODY_WEIGHT);
        Log.e("555", "findItemView: bodyWeight "+bodyWeight );
        inputWeight.setText(String.valueOf(bodyWeight));

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
                String val = inputWeight.getText().toString();
                if (!TextUtils.isEmpty(val) && val.length() > 0) {
                    float bodyWeight = Float.parseFloat(val);
                    //返回数据给settingactivity
                    listener.setWeightData(bodyWeight);
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
