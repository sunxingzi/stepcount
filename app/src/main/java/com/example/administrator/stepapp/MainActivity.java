package com.example.administrator.stepapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.administrator.frame.AppBaseActivity;
import com.example.administrator.frame.BaseActivity;
import com.example.administrator.model.PedometerChartBean;
import com.example.administrator.service.IPedometerService;
import com.example.administrator.service.PedometerService;
import com.example.administrator.util.LogWriter;
import com.example.administrator.util.Settings;
import com.example.administrator.util.Utils;
import com.example.administrator.widgets.CircleProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.administrator.util.DateUtils.*;

public class MainActivity extends AppBaseActivity {
    @BindView(R.id.btnStart)
    protected Button startButton;
    @BindView(R.id.reset)
    protected Button resetButton;
    @BindView(R.id.circleProgressBar)
    protected CircleProgressBar circleProgressBar;
    @BindView(R.id.stepCountView)
    protected View stepCountView;
    @BindView(R.id.stepCount)
    protected TextView stepCount;
    @BindView(R.id.textCalorie)
    protected TextView calorieCount;
    @BindView(R.id.time)
    protected TextView time;
    @BindView(R.id.distance)
    protected TextView distance;
    @BindView(R.id.tv_target_distance)
    protected TextView tvTargetDistance;
    @BindView(R.id.chart1)

    protected BarChart mChart;
    private static final int STATUS_NOT_RUNNING = 0;
    private static final int STATUS_RUNNING = 1;
    private static final long SLEEP_TIME = 60000L;
    private static final int MESSAGE_UPDATE_STEP_COUNT = 11;
    private static final int MESSAGE_UPDATE_CHART = 12;

    private volatile boolean isRunning = false;
    private boolean bindService = false;
    private PedometerChartBean chartBean;
    private Intent mServiceIntent = null;
    private IPedometerService mRemoteService;
    private OptionsPickerView stepPvOptions;
    private List<String> stepOptionItems = new ArrayList<>();

    private Settings setting = null;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_STEP_COUNT: {
                  //  handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                    updateStepCount();
                }
                break;
                case MESSAGE_UPDATE_CHART: {
                  //  handler.removeMessages(MESSAGE_UPDATE_CHART);
                    if (chartBean != null) {
                        setData(chartBean);
                    }
                }
                break;
                default:
            }
            super.handleMessage(msg);
        }
    };


    public void setData(PedometerChartBean pedometerChartBean) {
        ArrayList<BarEntry> yVals = new ArrayList<>();
        if (pedometerChartBean != null) {
            for (int i = 0; i < pedometerChartBean.getIndex(); i++) {
                int valY = pedometerChartBean.getDataArray()[i];
                yVals.add(new BarEntry(i, valY));
            }
            time.setText(String.valueOf(pedometerChartBean.getIndex()) + "分");
            BarDataSet set = new BarDataSet(yVals, "所走步数");
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            BarData data = new BarData(set);
            data.setValueTextSize(10f);
            mChart.setData(data);
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    private volatile boolean isChartUpdate = false;

    private class ChartRunnable implements Runnable {
        @Override
        public void run() {
            while (isChartUpdate) {
                try {
                    chartBean = mRemoteService.getChartData();
                    handler.removeMessages(MESSAGE_UPDATE_CHART);
                    handler.sendEmptyMessage(MESSAGE_UPDATE_CHART);
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    LogWriter.e(e.toString());
                } catch (RemoteException e) {
                    LogWriter.e(e.toString());
                }
            }
        }
    }

    private int status = 1;

    private class StepRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    status = mRemoteService.getServiceRunningStatus();
                    handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                    handler.sendEmptyMessage(MESSAGE_UPDATE_STEP_COUNT);
                    Thread.sleep(200);
                } catch (RemoteException e) {
                    LogWriter.e(e.toString());
                } catch (InterruptedException e) {
                    LogWriter.e(e.toString());
                }
            }
        }
    }

    public void updateStepCount() {
        if (mRemoteService != null) {
            //服务正在运行
            int stepCountVal = 0;
            double calorieVal = 0;
            double distanceVal = 0;
            try {
                stepCountVal = mRemoteService.getStepsCount();
                calorieVal = mRemoteService.getCalorie();
                distanceVal = mRemoteService.getDistance();
                LogWriter.e("distance = " + distanceVal);
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
            stepCount.setText(String.valueOf(stepCountVal) + "步");
            calorieCount.setText(Utils.getFormatVal(calorieVal, "0.00") + "卡");
            distance.setText(Utils.getFormatVal(distanceVal, "0.00"));
            circleProgressBar.setProgress(stepCountVal);
        }
    }

    @Override
    protected void onInitVariable() {
        //init step option items
        for (int i = 0; i < 14; i++) {
            stepOptionItems.add(String.valueOf(2000 + i * 1000));
        }

    }

    @Override
    protected void setTitle() {
        setTitle("计步器");
        setTitleRightImg(R.drawable.setting_icon);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initStepOptionPicker() {
        stepPvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String val = stepOptionItems.get(options1);
                Log.e("888", "onOptionsSelect: val " + val);
                tvTargetDistance.setText(String.format(getResources().getString(R.string.my_target_distance), val));
                setting.setTargetDistance(Integer.valueOf(val));
                circleProgressBar.setMax(Integer.valueOf(val));
                Log.e("888", "onOptionsSelect: circleProgressBar.getMax() " + circleProgressBar.getMax());

            }
        }).setTitleText("选择目标步数")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(getResources().getColor(R.color.fontBlue))//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(getResources().getColor(R.color.pressed))
                .setTitleColor(Color.BLACK)
                .setCancelColor(getResources().getColor(R.color.fontBlue))
                .setSubmitColor(getResources().getColor(R.color.fontBlue))
                .setTextColorCenter(getResources().getColor(R.color.fontBlue))
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项
                .isCenterLabel(false)
                .setLabels("", "", "")
                .setBackgroundId(0x00000000)
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();

        stepPvOptions.setPicker(stepOptionItems);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = mRemoteService.getServiceRunningStatus();
                if (status == STATUS_RUNNING) {
                    startButton.setText("停止");
                    isRunning = true;
                    isChartUpdate = true;
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                    chartBean = mRemoteService.getChartData();
                    int[] dataArray = chartBean.getDataArray();
                    int index = chartBean.getIndex();
                    setData(chartBean);
                } else {
                    startButton.setText("启动");
                }
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
        }
    };

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initStepOptionPicker();
        setting = new Settings(MainActivity.this);
        circleProgressBar.setMax( setting.getTargetDiatance());
        Log.e("888", "onInitView: circleProgressBar.getMax() " + circleProgressBar.getMax());
        circleProgressBar.setProgress(0);
        setBarChatStyle();
        time.setText("0");
        stepCount.setText("0步");
        calorieCount.setText("0.00卡");
        distance.setText("0.00");
        tvTargetDistance.setText(String.format(getResources().getString(R.string.my_target_distance), String.valueOf(setting.getTargetDiatance())));
        requestData();
    }

    @Override
    public void finish(View view) {
        super.finish(view);
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.reset)
    public void reset() {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.my_dialog).create();
        alertDialog.show();
        if (alertDialog.getWindow() == null) {
            return;
        }
        alertDialog.getWindow().setContentView(R.layout.pop_user);
        TextView msg = (TextView) alertDialog.findViewById(R.id.tv_msg);
        TextView title = (TextView) alertDialog.findViewById(R.id.tv_title);
        Button cancel = (Button) alertDialog.findViewById(R.id.btn_cancle);
        Button sure = (Button) alertDialog.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) {
            return;
        }
        title.setText("确认重置");
        msg.setText("您的记录将会被清零，确定吗？");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRemoteService != null) {
                    try {
                        mRemoteService.stopSetpsCount();
                        mRemoteService.resetCount();
                        chartBean = mRemoteService.getChartData();
                        setData(chartBean);
                        status = mRemoteService.getServiceRunningStatus();
                        if (status == STATUS_RUNNING) {
                            startButton.setText("停止");
                        } else if (status == STATUS_NOT_RUNNING) {
                            startButton.setText("启动");
                        }
                    } catch (RemoteException e) {
                        LogWriter.e(e.toString());
                    }
                }
                alertDialog.dismiss();
            }
        });
    }


    @OnClick(R.id.btnStart)
    public void startClick() {
        try {
            status = mRemoteService.getServiceRunningStatus();
        } catch (RemoteException e) {
            LogWriter.e(e.toString());
        }
        if (status == STATUS_NOT_RUNNING) {
            //没有运行，应该启动开始计算步数，保存数据
            if (mRemoteService != null) {
                try {
                    mRemoteService.startSetpsCount();
                    startButton.setText("停止");
                    isRunning = true;
                    isChartUpdate = true;
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                    chartBean = mRemoteService.getChartData();
                    setData(chartBean);
                } catch (RemoteException e) {
                    LogWriter.e(e.toString());
                }
            }
        } else if (status == STATUS_RUNNING) {
            //应该在运行了，那就应该停止计算步数
            if (mRemoteService != null) {
                try {
                    mRemoteService.stopSetpsCount();
                    startButton.setText("启动");
                    isRunning = false;
                    isChartUpdate = false;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.tv_target_distance)
    public void setTargetDistance() {
        if (stepPvOptions != null) {
            stepPvOptions.show();
        }
    }

    private void setBarChatStyle() {
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawBarShadow(false);//取消阴影
        mChart.setDrawValueAboveBar(true);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setGranularity(2f);
        mChart.getXAxis().setDrawGridLines(false);
        YAxis axisLeft = mChart.getAxisLeft();
        YAxis axisRight = mChart.getAxisRight();
        axisLeft.setAxisMinimum(0f);
        axisLeft.setDrawGridLines(false);
        axisRight.setEnabled(false);
        Description description = new Description();
        description.setEnabled(false);
        mChart.setDescription(description);
        mChart.setMaxVisibleValueCount(288);
        mChart.setPinchZoom(true);//取消缩放
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDrawGridBackground(false);
    }


    private void requestData() {
        if (!Utils.isServiceRunning(this, "com.example.administrator.service.PedometerService")) {
            mServiceIntent = new Intent(this, PedometerService.class);
            startService(mServiceIntent);
        }
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(this.getApplicationContext(), PedometerService.class);
        }
        mServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //以bindService方法连接绑定服务
        bindService = bindService(mServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        if (bindService && mRemoteService != null) {
            try {
                status = mRemoteService.getServiceRunningStatus();
                Log.e("555", "onRequestData: status " + status);
                if (status == 0) {
                    startButton.setText("启动");
                } else {
                    startButton.setText("停止");
                    mRemoteService.startSetpsCount();
                    isRunning = true;
                    isChartUpdate = true;
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                }
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
        } else {
            startButton.setText("启动");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveData();
    }

    private void saveData() {
        if (mRemoteService != null) {
            try {
                mRemoteService.saveData();
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (bindService) {
            bindService = false;
            isRunning = false;
            isChartUpdate = false;
            unbindService(serviceConnection);
        }
    }
}
