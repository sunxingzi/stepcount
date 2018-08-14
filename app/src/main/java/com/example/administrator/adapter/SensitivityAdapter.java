package com.example.administrator.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.stepapp.R;

import java.util.List;

import javax.security.auth.login.LoginException;

/**
 * Created by Administrator on 2018/6/26.
 */

public class SensitivityAdapter  extends BaseAdapter{
    public static boolean isSelect = false;
    private Context context;
    private List<String> datas;

    public SensitivityAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = datas.get(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_sensitivity,null);
            viewHolder.itemTextView = (TextView) convertView.findViewById(R.id.item_text);
            viewHolder.itemIVSelect = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemTextView.setText(data);
        return convertView;
    }


    class ViewHolder{
        TextView itemTextView;
        ImageView itemIVSelect;
    }
}
