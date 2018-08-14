package com.example.administrator.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.administrator.model.PedometerBean;
import com.example.administrator.util.LogWriter;

/**
 * Created by Administrator on 2018/6/25.
 */

public class PedometerListener implements SensorEventListener {
    public static int CURRENT_STEP = 0;
    //
    public  long mLimit = 300;//采样时间
    //
    public  float mSensitivity = 10;//sensitivity灵敏度
    private float mLastValues[] = new float[3 * 2];//最后保存的数据
    private float mScale[] = new float[2];

    private float mYOffSet;
    private static long end = 0;
    private static long start = 0;
//
    public long getLimit() {
        return mLimit;
    }

    public void setLimit(long limit) {
        mLimit = limit;
    }

    public float getSensitivity() {
        return mSensitivity;
    }

    public void setSensitivity(float sensitivity) {
        mSensitivity = sensitivity;
    }
//
    /**
     * 最后加速度方向
     */
    private float mLastDirections[] = new float[3 * 2];
    private float mLasrExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;
    private PedometerBean pedometerBean;

    public void resetCurrentStep() {
        CURRENT_STEP = 0;
    }

    public PedometerListener(Context context, PedometerBean pedometerBean) {
        super();
        int h = 480;
        mYOffSet = h * 0.5f;//240
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        this.pedometerBean = pedometerBean;
    }


    /**
     * 当传感器检测到的数值发生变化时会调用该方法
     *
     * @param sensorEvent
     */

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //加速传感器
                float vSum = 0;
                //加速传感器 X-axis, Y-axis, Z-axis分别对应value[0], value[1], value[2]
                for (int i = 0; i < 3; i++) {
                    float v = mYOffSet + sensorEvent.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;//记录三个轴向，传感器的平均值

                float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                if (direction == -mLastDirections[k]) {
                    //direction changed
                    int extType = (direction > 0 ? 0 : 1);
                    mLasrExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLasrExtremes[extType][k] - mLasrExtremes[1 - extType][k]);
                    if(diff > mSensitivity){
                        boolean isAlmostAsLargetAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargetEnought = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1-extType);

                        if(isAlmostAsLargetAsPrevious && isPreviousLargetEnought && isNotContra){
                            end = System.currentTimeMillis();
                            if(end - start > mLimit){
                                //此时判断为走了一步
                                CURRENT_STEP++;
                                pedometerBean.setStepCount(CURRENT_STEP);
                                pedometerBean.setLastStepTime(System.currentTimeMillis());
                                mLastMatch = extType;
                                start = end;
                                LogWriter.e("Current count = "+CURRENT_STEP);
                            }
                        }else{
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
