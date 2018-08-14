package com.example.administrator.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.frame.FrameApplication;
import com.example.administrator.model.PedometerBean;
import com.example.administrator.model.PedometerChartBean;
import com.example.administrator.util.ACache;
import com.example.administrator.util.DBHelper;
import com.example.administrator.util.LogWriter;
import com.example.administrator.util.Settings;
import com.example.administrator.util.Utils;

/**
 * Created by Administrator on 2018/6/25.
 */

public class PedometerService extends Service {
    private static final long SAVE_TIME = 60000L;//一分钟
    //传感器
    private SensorManager mSensorMgr;
    private static final int STATUS_NOT_RUNNING = 0;//非运动中
    private static final int STATUS_RUNNING = 1;//运动中
    private PedometerListener pedometerListener;//监听运动状态
    private PedometerBean pedometerBean;//记录步数等信息
    private PedometerChartBean pedometerChartBean;//记录显示数据
    private int runStatus = 0;//当前运动状态
    private Settings settings;

    private static Handler handler = new Handler();

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runStatus == STATUS_RUNNING) {
                if (handler != null && pedometerChartBean != null) {
                    handler.removeCallbacks(timeRunnable);
                    updateCharData();
                    handler.postDelayed(timeRunnable, SAVE_TIME);//一分钟刷新一次记录数值，然后保存
                }
            }
        }
    };

    private void updateCharData() {
        if (pedometerChartBean.getIndex() < 1440) {
            pedometerChartBean.setIndex(pedometerChartBean.getIndex() + 1);
            pedometerChartBean.getDataArray()[pedometerChartBean.getIndex()] = pedometerBean.getStepCount();
        }
    }


    private void saveChartData() {
        String json = Utils.objToJson(pedometerChartBean);
        ACache cache = FrameApplication.getFileCache();
        cache.put("JsonData", json);
    }

    public void onCreate() {
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pedometerBean = new PedometerBean();
        pedometerBean.setCreateTime(Utils.getTimestempByDay());
        pedometerListener = new PedometerListener(this, pedometerBean);
        pedometerChartBean = new PedometerChartBean();
        settings = new Settings(this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public int getStepsCount() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStepCount();
            }
            return 0;
        }

        @Override
        public void resetCount() throws RemoteException {
            if (pedometerBean != null) {
                pedometerBean.reset();
                saveData();
            }
            if (pedometerChartBean != null) {
                pedometerChartBean.reset();
                saveChartData();
            }
            if (pedometerListener != null) {
                pedometerListener.resetCurrentStep();
            }

        }

        @Override
        public void startSetpsCount() throws RemoteException {

            startStep();
        }

        @Override
        public void stopSetpsCount() throws RemoteException {

            stopStep();
        }

        @Override
        public double getCalorie() throws RemoteException {
            if (pedometerBean != null) {
                return getCalorieByStep(pedometerBean.getStepCount());
            }
            return 0;
        }

        @Override
        public double getDistance() throws RemoteException {
            return getDistanceVal();
        }

        @Override
        public void saveData() throws RemoteException {
            saveDataToDb();

        }

        @Override
        public void setSensitivity(float sensitivity) throws RemoteException {

            if (settings != null) {
                settings.setSensitivity(sensitivity);
            }
            //
            if(pedometerListener != null){
                pedometerListener.setSensitivity(sensitivity);
            }
            Log.e("666", "setSensitivity: "+sensitivity );
        }

        @Override
        public double getSensitivity() throws RemoteException {
            return settings.getSensitivity();
        }

        @Override
        public int getInterval() throws RemoteException {
            return settings.getInterval();
        }

        @Override
        public void setInterval(int interval) throws RemoteException {
            if (settings != null) {
                settings.setInterval(interval);
            }
            //
            if(pedometerListener != null){
                pedometerListener.setLimit(interval);
            }

            Log.e("666", "setInterval: "+interval );
        }

        @Override
        public long getStartTimestmp() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStartTime();
            }
            return 0;
        }

        @Override
        public int getServiceRunningStatus() throws RemoteException {
            return runStatus;
        }

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return pedometerChartBean;
        }
    };

    /**
     * 距离 单位千米
     *
     * @return
     */
    public double getDistanceVal() {
        if (pedometerBean != null) {
            Settings settings = new Settings(PedometerService.this);
            double distance = (pedometerBean.getStepCount() * (long) settings.getSetpLength()) / 100000.0f;
            return distance;
        }
        return 0;
    }

    public void saveDataToDb() {
        if (pedometerBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(PedometerService.this, "PedometerDB");
                    pedometerBean.setDistance(getDistanceVal());
                    pedometerBean.setCalorie(getCalorieByStep(pedometerBean.getStepCount()));
                    long time = pedometerBean.getLastStepTime() - pedometerBean.getStartTime() / 1000;
                    if (time == 0) {
                        pedometerBean.setPace(0);
                        pedometerBean.setSpeed(0);
                    } else {
                        int pace = Math.round(60 * pedometerBean.getStepCount() / time);
                        pedometerBean.setPace(pace);
                        long speed = Math.round((pedometerBean.getDistance() / 1000) / (time / 60 * 60));
                        pedometerBean.setSpeed(speed);
                    }
                    dbHelper.writeToDatabase(pedometerBean);
                    saveChartData();
                }
            }).start();
        }
    }

    public void stopStep(){
        if(mSensorMgr != null && pedometerListener != null){
            Sensor sensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(sensor == null){
                Utils.makeToast(this,"手机中没有可用的传感器!");
                return;
            }
            mSensorMgr.unregisterListener(pedometerListener,sensor);
            runStatus = STATUS_NOT_RUNNING;
            handler.removeCallbacks(timeRunnable);
            LogWriter.e("Stop Step COunt");
        }
    }

    public void startStep(){
        if(mSensorMgr!= null && pedometerListener != null){
            Sensor sensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(sensor == null){
               Utils.makeToast(this,"手机中没有可用的传感器!");
                return;
            }
            mSensorMgr.registerListener(pedometerListener,sensor,SensorManager.SENSOR_DELAY_GAME);
            pedometerBean.setStartTime(System.currentTimeMillis());
            runStatus = STATUS_RUNNING;
            handler.postDelayed(timeRunnable,SAVE_TIME);//开始记录
            LogWriter.e("Start Step Count");
        }
    }

    private double getCalorieByStep(int frequency){
        Settings settings = new Settings(this);
        double METRIC_RUNNING_FACTOR = 1.02784823;//跑步
        double METRIC_WALKING_FACTOR = 0.708;//走路
        double mCalories = 0;
        //跑步热量 （kcal） = 体重(kg) * 距离(公里) * 1.02784823;
        //走路热量 （kcal） = 体重(kg) * 距离(公里) * 0.708;
        mCalories = (settings.getBodyWeight() * METRIC_WALKING_FACTOR) * settings.getSetpLength() * frequency / 100000.0;
        return mCalories;
    }

}
