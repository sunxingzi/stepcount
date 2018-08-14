package com.example.administrator.frame;

import android.content.Context;

import com.example.administrator.util.LogWriter;

/**
 * Created by Administrator on 2018/6/25.
 */

public class ErrorHandler implements Thread.UncaughtExceptionHandler {

    private static ErrorHandler instance;
    private static volatile boolean onError = false;


    private ErrorHandler() {

    }

    public static ErrorHandler getInstance() {
        if (ErrorHandler.instance == null) {
            ErrorHandler.instance = new ErrorHandler();
        }
        return ErrorHandler.instance;
    }

    /**
     * 初始化，注册context对象，获取系统默认的UncaughtException处理器，设置该CrashHandler为程序的默认处理器
     *
     * @param context
     */
    public void setToErrorHandler(final Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数处理
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //处理异常
        LogWriter.logToFile("崩溃简短信息："+ex.getMessage());
        LogWriter.logToFile("崩溃简短信息："+ex.toString());
        LogWriter.logToFile("崩溃线程名称："+thread.getName() + "崩溃线程ID: "+thread.getId());
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement element : trace) {
            LogWriter.debugError("Line " + element.getLineNumber() +" : "+element.toString());
        }
        ex.printStackTrace();
        FrameApplication.exitApp();
    }
}
