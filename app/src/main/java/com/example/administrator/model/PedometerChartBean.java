package com.example.administrator.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/6/25.
 */

public class PedometerChartBean implements Parcelable{
    //记录当前的索引值
    private int index = 0;
    //记录全天的运动数据，用来生成曲线
    private int[] dataArray = new int[1440];
    public void reset(){
        for (int i:dataArray   ) {
            i = 0;
        }
        index = 0;
    }

    public PedometerChartBean() {
       index = 0;
    }

    protected PedometerChartBean(android.os.Parcel in){
        index = in.readInt();
        dataArray = in.createIntArray();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(int[] dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //写入数据和索引
        parcel.writeInt(index);
        parcel.writeIntArray(dataArray);

    }

    public static final android.os.Parcelable.Creator<PedometerChartBean> CREATOR
            = new android.os.Parcelable.Creator<PedometerChartBean>(){
        @Override
        public PedometerChartBean createFromParcel(Parcel in) {
            return new PedometerChartBean(in);
        }

        @Override
        public PedometerChartBean[] newArray(int size) {
            return new PedometerChartBean[size];
        }
    };
}
