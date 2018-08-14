package com.example.administrator.frame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

/**
 * Created by Administrator on 2018/6/25.
 */

public abstract class BaseFragment extends Fragment{

    protected BaseActivity context;
    protected Bundle bundle;
    public void removeContext(){
        context = null;
    }

    public boolean onKeyDown(final int keyCode, final KeyEvent event){
        return true;
    }

    public void setBundle(final Bundle b){
        this.bundle = b;
    }

    public void setContext(final BaseActivity c){
        this.context = c;
    }
    public BaseActivity getContext(){
        return this.context;
    }

    @Override
    public void onResume() {
        this.onLoadData();
        super.onResume();
    }

    @Override
    public void onPause() {
        this.onUnLoadData();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        this.onRelease();
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        super.onCreate(savedInstanceState);
    }

    protected abstract void onInitVariable();
    protected  abstract View onInitView(LayoutInflater inflater, final Bundle savedInstanceState);

    protected void onViewCreate(final View v){

    }

    protected abstract void onRequestData();
    protected abstract void onLoadData();

    protected abstract void onUnLoadData();
    protected abstract void onRelease();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = this.onInitView(inflater,savedInstanceState);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if(v == null){
            v = new View(this.getActivity());
        }
        v.setLayoutParams(params);
        this.onViewCreate(v);
        this.onRequestData();
        return v;
    }
}
