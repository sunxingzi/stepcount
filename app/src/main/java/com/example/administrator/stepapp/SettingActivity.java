package com.example.administrator.stepapp;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.fragment.BodyWeightFragment;
import com.example.administrator.fragment.StepLengthFragment;
import com.example.administrator.frame.AppBaseActivity;
import com.example.administrator.frame.BaseActivity;
import com.example.administrator.service.IPedometerService;
import com.example.administrator.service.PedometerService;
import com.example.administrator.util.LogWriter;
import com.example.administrator.util.Settings;
import com.example.administrator.util.Utils;

import butterknife.BindView;

public class SettingActivity extends AppBaseActivity implements StepLengthFragment.StepDataListener, BodyWeightFragment.WeightDataListener {
    private static final int REQUEST_SENSI = 5;
    private static final int REQUEST_TIME = 6;
    public static final String SEND_INDEX_KEY = "sen_index";
    public static final String SEND_TIME_KEY = "time_idex";
    public static final String STEP_LENGTH_DIALOG = "step_length_dialog";
    public static final String BODY_WEIGHT_DIALOG = "body_weight_dialog";
    private Settings setting = null;
    private IPedometerService mRemoteService;
    private Intent mServiceIntent = null;

    class ViewHolder {
        TextView title;
        TextView desc;
    }

    public class ListViewAdapter extends BaseAdapter {

        private String[] listTitle = {"设置步长", "设置体重", "传感器敏感度", "传感器采样时间"};

        @Override
        public int getCount() {
            return listTitle.length;
        }

        @Override
        public Object getItem(int position) {
            return listTitle[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(SettingActivity.this, R.layout.item_setting, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(listTitle[position]);
            switch (position) {
                case 0: {
                    final float stepLen = setting.getSetpLength();
                    holder.desc.setText(String.format(getResources().getString(R.string.stepLen), stepLen));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StepLengthFragment stepLengthDialog = StepLengthFragment.newInstance(stepLen);
                            stepLengthDialog.show(getSupportFragmentManager(), STEP_LENGTH_DIALOG);
                        }
                    });
                }
                break;
                case 1: {
                    final float bodyWeight = setting.getBodyWeight();
                    holder.desc.setText(String.format(getResources().getString(R.string.body_weight), bodyWeight));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BodyWeightFragment bodyWeightDialog = BodyWeightFragment.newInstance(bodyWeight);
                            bodyWeightDialog.show(getSupportFragmentManager(), BODY_WEIGHT_DIALOG);

                        }
                    });
                }
                break;
                case 2: {
                    double sensitivity = setting.getSensitivity();
                    String format = String.format(getResources().getString(R.string.sensitivity), Utils.getFormatVal(sensitivity, "#.00"));
                    holder.desc.setText(format);
                    String[] split = format.split("：");
                      /*1.97f, 2.96f, 4.44f, 6.66f, 10.0f, 15.0f, 22.50f, 33.75f, 50.62f*/
                    int senIndex = 0;
                    if (split.length > 1) {
                        switch (split[1]) {
                            case "1.97":
                                senIndex = 0;
                                break;
                            case "2.96":
                                senIndex = 1;
                                break;
                            case "4.44":
                                senIndex = 2;
                                break;
                            case "6.66":
                                senIndex = 3;
                                break;
                            case "10.0":
                                senIndex = 4;
                                break;
                            case "15.0":
                                senIndex = 5;
                                break;
                            case "22.50":
                                senIndex = 6;
                                break;
                            case "33.75":
                                senIndex = 7;
                                break;
                            case "50.62":
                                senIndex = 8;
                                break;
                            default:
                                break;
                        }
                    }
                    final int itemIndex = senIndex;
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SettingActivity.this, SenActivity.class);
                            intent.putExtra(SEND_INDEX_KEY, itemIndex);
                            startActivityForResult(intent, REQUEST_SENSI);
                        }
                    });
                }
                break;
                case 3: {
                    int interval = setting.getInterval();
                    Log.e("666", "getView: interval " + interval);
                    holder.desc.setText(String.format(getResources().getString(R.string.interval), Utils.getFormatVal(interval, "#.00")));
                    int itemIndex = 0;
                    switch (interval) {
                        case 100:
                            itemIndex = 0;
                            break;
                        case 200:
                            itemIndex = 1;
                            break;
                        case 300:
                            itemIndex = 2;
                            break;
                        case 400:
                            itemIndex = 3;
                            break;
                        case 500:
                            itemIndex = 4;
                            break;
                        case 600:
                            itemIndex = 5;
                            break;
                        case 700:
                            itemIndex = 6;
                            break;
                        case 800:
                            itemIndex = 7;
                            break;
                        default:
                            break;
                    }
                    final int timeIndex = itemIndex;
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SettingActivity.this, SampleTimeActivity.class);
                            intent.putExtra(SEND_TIME_KEY, timeIndex);
                            startActivityForResult(intent, REQUEST_TIME);
                        }
                    });
                }
            }
            return convertView;
        }
    }

    @Override
    public void setStepData(float stepLen) {
        Log.e("555", "setStepData: from StepLengthFragment " + stepLen);
        setting.setStepLength(stepLen);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setWeightData(float weightData) {
        Log.e("555", "setStepData: from StepLengthFragment " + weightData);
        setting.setBodyWeight(weightData);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IPedometerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SENSI:
                if (resultCode == RESULT_OK) {
                    int index = data.getIntExtra(SenActivity.SEN_INDEX_KEY, 0);
                    //
                    if(mRemoteService!= null){
                        try {
                            mRemoteService.setSensitivity(Settings.SENSITIVE_ARRAY[index]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    setting.setSensitivity(Settings.SENSITIVE_ARRAY[index]);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_TIME:
                if (resultCode == RESULT_OK) {
                    int index = data.getIntExtra(SampleTimeActivity.TIME_INDEX_KEY, 0);
                    //
                    if(mRemoteService != null){
                        try {
                            mRemoteService.setInterval(Settings.INTERVAL_ARRAY[index]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    setting.setInterval(Settings.INTERVAL_ARRAY[index]);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            default:
                break;
        }
    }

    @BindView(R.id.listView)
    protected ListView listView;
    private ListViewAdapter adapter;


    @Override
    protected void setTitle() {
        setTitle("设置");
        setTitleLeftImage(R.drawable.left_arrow);
    }

    @Override
    protected void initData() {
        setting = new Settings(SettingActivity.this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        adapter = new ListViewAdapter();
        listView.setAdapter(adapter);

//
        if (!Utils.isServiceRunning(this, "com.example.administrator.service.PedometerService")) {
            mServiceIntent = new Intent(this, PedometerService.class);
            startService(mServiceIntent);
        }
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(this.getApplicationContext(), PedometerService.class);
        }
        mServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //以bindService方法连接绑定服务
        bindService(mServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

 //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
