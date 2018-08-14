package com.example.administrator.frame;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.administrator.util.ACache;
import com.example.administrator.util.LogWriter;
import com.example.administrator.util.PrefsManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/25.
 */

public class FrameApplication extends Application {
    private static ErrorHandler crashHandler = null;
    private static FrameApplication mInstance;
    private static LinkedList<Activity> mList = new LinkedList<>();
    long exitTime = 0;

    public boolean closeAppByBackPressed(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - this.exitTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                this.exitTime = System.currentTimeMillis();
            } else {
                FrameApplication.exitApp();
            }
            return true;
        }
        return false;
    }

    public LinkedList<Activity> getList() {
        return mList;
    }

    public static ACache getFileCache() {
        return ACache.get(FrameApplication.mInstance);
    }

    public static void addToList(final Activity act) {
        FrameApplication.mList.add(act);
    }

    public static void removeFromList(final Activity act) {
        if (FrameApplication.mList.indexOf(act) != -1) {
            FrameApplication.mList.remove(act);
        }
    }

    public static void clearActivityStack() {
        for (int i = FrameApplication.mList.size() - 1; i >= 0; i--) {
            Activity activity = FrameApplication.mList.get(i);
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public static void exitApp() {
        try {
            for (int i = FrameApplication.mList.size() - 1; i >= 0; i--) {
                Activity activity = FrameApplication.mList.get(i);
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
        } finally {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    public static FrameApplication getInstance(){
        return FrameApplication.mInstance;
    }

    public static void setCrashHandler(Context context){
        FrameApplication.crashHandler.setToErrorHandler(context);
    }

    /**
     * 全局的prefs
     */
    private PrefsManager mPrefsManager;

    public PrefsManager getPrefsManager(){
        return this.mPrefsManager;
    }

    public boolean isAppOnForeground(){
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        String packageName = this.getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses == null){
            return false;
        }
        for (RunningAppProcessInfo appProcess:appProcesses     ) {
            if(appProcess.processName.equals(packageName) &&
                    (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FrameApplication.mInstance = this;
        this.mPrefsManager = new PrefsManager(this);
        FrameApplication.crashHandler = ErrorHandler.getInstance();
    }

    public void onLowMemory(){
        super.onLowMemory();
        LogWriter.e("Low memory");
    }
}
