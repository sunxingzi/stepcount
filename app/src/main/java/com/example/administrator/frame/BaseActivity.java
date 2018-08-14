package com.example.administrator.frame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2018/6/25.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private BaseFragment currentFragment;

    //是否显示应用程序标题栏
    protected boolean isHideAppTitle = true;
    //是否显示系统标题栏
    protected boolean isHideSystemTitle = false;

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    public boolean onKeyDown(final int keyCode, final KeyEvent event){
        if(currentFragment != null){
            return currentFragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        if(this.isHideAppTitle){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        if(this.isHideSystemTitle){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(getContentViewId());
        initView();
        initData(savedInstanceState);
        //将当前activity加入列表
        FrameApplication.addToList(this);
    }
    //-------------------------------------------------------------------------------------------------
    public void addFragment(final int viewID, final BaseFragment f){
        addFragment(viewID, f, false);
    }

    public void addFragment(final int viewID, final BaseFragment f, final boolean addToBackstack){
        final FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        f.setContext(this);
        transaction.add(viewID,f);
        if(addToBackstack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
        currentFragment  =f;
    }

    //---------------------------------------------------------------------------------------------------
    public void replaceFragment(final int viewID, final BaseFragment f, final boolean addToBackstack){
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID,f);
        f.setContext(this);
        if(addToBackstack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
        currentFragment = f;
    }

    public void replaceFragment(final int viewID, final BaseFragment f){
        this.replaceFragment(viewID, f,true);
    }
    //-----------------------------------------------------------------------------------------------
    public void deleteFragment(final int viewID, final BaseFragment f, final boolean addToBackstack){
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.remove(f);
        f.removeContext();
        if(addToBackstack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
        currentFragment = null;
    }
    //--------------------------------------------------------------------------------------------------
    public void popStackFragment(){
        this.getSupportFragmentManager().popBackStack();
    }
    //---------------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        //清理一些无用的数据
        FrameApplication.removeFromList(this);
        super.onDestroy();
    }

    /**
     * 初始化变量
     */
    protected abstract void onInitVariable();

    /**
     * 获取布局ID
     * @return
     */
    protected abstract int getContentViewId();
    protected abstract void initView();


    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void initData(Bundle savedInstanceState){

    }
}
