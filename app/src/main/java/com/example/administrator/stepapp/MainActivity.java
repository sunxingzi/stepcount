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
import com.example.administrator.util.DateUtils;
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
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.example.administrator.util.DateUtils.*;

public class MainActivity extends AppBaseActivity {
    private static final String TAG = "MainActivity";
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
   /* @BindView(R.id.chart1)
    protected BarChart mChart;*/

    @BindView(R.id.step_chart)
    protected LineChartView lineChart;
    private static final long MINUTE_MILLISECONDS = 1000 * 60;
    private long startTime;
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

    private List<PointValue> mPointValues;
    private List<PointValue> mStartPointValues;
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    //String[] date = {"01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};//X轴的标注
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

    private float x;

    public void setData(PedometerChartBean pedometerChartBean) {
        if (pedometerChartBean != null) {
            for (int i = 0; i < pedometerChartBean.getIndex(); i++) {
                int valY = pedometerChartBean.getDataArray()[i];
                Log.e(TAG, "setData: valY = " + valY);
                mPointValues.add(new PointValue(i, valY));
                x = mPointValues.get(i).getX();
            }
            //根据新的点集合画出新的线
            ////////////////////////////////////////
            Line line = new Line(mPointValues).setColor(Color.RED);
            line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line.setFilled(false);//是否填充曲线的面积
            //  line.setFormatter(new SimpleLineChartValueFormatter(1));
            line.setHasLabels(true);//曲线的数据坐标是否加上备注

            line.setPointRadius(3);//坐标点的大小
            line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）

            line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
            ///////////////////////////////////////////////////////////////////
            lines.add(line);
            mLineChartData.setLines(lines);
            lineChart.setLineChartData(mLineChartData);
            Viewport port;
            if(x > 5){
                port = initViewPort(x-5,x);
            }
            else {
                port = initViewPort(0,5);
            }
            lineChart.setMaximumViewport(port);
            lineChart.setCurrentViewport(port);
        }
        Log.e(TAG, "getAxisPoints");
      /*  ArrayList<BarEntry> yVals = new ArrayList<>();
        if (pedometerChartBean != null) {
            for (int i = 0; i < pedometerChartBean.getIndex(); i++) {
                int valY = pedometerChartBean.getDataArray()[i];
                ///////////////////////////////////////////////////////////图表点
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
        }*/
    }

    /**
     * 设置X 轴的显示
     */
 /*   private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
        Log.e(TAG, "getAxisXLables");
    }*/

    /**
     * 图表的每个点的显示
     */
  /*  private void getAxisPoints() {

        for (int i = 0; i <  dataList.size(); i++) {
            mPointValues.add(new PointValue(i, Float.parseFloat(dataList.get(i))));
            // mPointValues.setLabel(date.getValue()+"℃");
        }
        Log.e(TAG,"getAxisPoints");
    }*/

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
            long nowTime = System.currentTimeMillis();
            long elapseTime = nowTime - startTime;
            time.setText(String.valueOf(elapseTime / MINUTE_MILLISECONDS));

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
        circleProgressBar.setMax(setting.getTargetDiatance());
        Log.e("888", "onInitView: circleProgressBar.getMax() " + circleProgressBar.getMax());
        circleProgressBar.setProgress(0);
        /////////////////////////////////////////
        //setBarChatStyle();
    //    getAxisXLables();
        initLineChart();
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
        //点击开始按钮的时间
        startTime = System.currentTimeMillis();
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

    private List<Line> lines;
    private LineChartData mLineChartData;

    private void initLineChart() {
        mPointValues = new ArrayList<PointValue>();
     //   mPointValues.add(new PointValue(0, 0));
        lines = new ArrayList<Line>();
        mLineChartData = new LineChartData();
        mLineChartData.setLines(null);//////////////////////////////////////////////

        //设置图表上点的背景色，如果不设置，图表上点的背景色就与线的颜色一样
        mLineChartData.setValueLabelBackgroundEnabled(true);
        mLineChartData.setValueLabelBackgroundAuto(false);
        mLineChartData.setValueLabelBackgroundColor(Color.parseColor("#FF0099CC"));

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.parseColor("#FF0099CC"));  //设置字体颜色
        axisX.setName("分钟");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        mLineChartData.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线

        //  Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("步数/步");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        axisY.setMaxLabelChars(7);//默认是3，只能看到最后三个数字
        axisY.setHasLines(true);
        axisY.setTextColor(Color.parseColor("#FF0099CC"));
        mLineChartData.setAxisYLeft(axisY);  //Y轴设置在左边
//////////////////////////////////////////////////////////////////////

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);//设置图表是否可以与用户交互
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 4);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(mLineChartData);//为图表设置数据，数据类型为LineChartData
        lineChart.setVisibility(View.VISIBLE);

        Viewport viewport = initViewPort(0, 5);
        lineChart.setMaximumViewport(viewport);
   /*     float minY = 0f;
        float maxY = 100;
        Viewport viewport = new Viewport(lineChart.getMaximumViewport());
        viewport.bottom = minY;
        viewport.top = maxY;
        lineChart.setMaximumViewport(viewport);
        viewport.left = 0;
        lineChart.setCurrentViewport(viewport);*/
      //  lineChart.startDataAnimation();
    }
    private Viewport initViewPort(float left,float right) {
        Viewport port = new Viewport();
        port.top = 1000;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }


  /*  private void setBarChatStyle() {
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
    }*/


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
