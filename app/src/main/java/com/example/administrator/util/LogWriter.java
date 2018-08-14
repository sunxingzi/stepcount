package com.example.administrator.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2018/6/25.
 */

public class LogWriter {

    private static final String DEBUG_TAG = "sun_step";
    public static boolean isDebug = true;
    public static boolean isWriteToLog = false;

    //----------------------------------------------------------------------------------------------
    public static void LogToFile(final String newLog) {
        LogWriter.LogToFile(LogWriter.DEBUG_TAG, newLog);
    }

    public static void LogToFile(String tag, String text) {
        if (!LogWriter.isWriteToLog) {
            return;
        }
        String needWriteMessage = tag + ":" + text;
        String fileName = Environment.getDataDirectory().getPath() + "/logFile.txt";
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(needWriteMessage);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logToFile(String msg) {
        String fileName = "/mnt/sdcard/logFile.txt";
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------------
    public static void d(final Object obj, final String msg) {
        if (LogWriter.isDebug) {
            Log.d(LogWriter.DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }

        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void d(String msg) {
        if (LogWriter.isDebug) {
            Log.d(LogWriter.DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void d(String DEBUG_TAG, Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.d(DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    public static void d(String DEBUG_TAG, String msg) {
        if (LogWriter.isDebug) {
            Log.d(DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    public static void debugError(String msg) {
        if (LogWriter.isDebug) {
            Log.e(LogWriter.DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    public static void debugError(String tag, String msg) {
        if (LogWriter.isDebug) {
            Log.e(tag, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    public static void debugInfo(String msg) {
        if (LogWriter.isDebug) {
            Log.i(LogWriter.DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    //----------------------------------------------------------------------------------------------
    public static void e(Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.e(LogWriter.DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (LogWriter.isDebug) {
            if (msg != null) {
                Log.e(LogWriter.DEBUG_TAG, msg);
            }
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void e(String DEBUG_TAG, Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.e(DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
    public static void e(String DEBUG_TAG, String msg) {
        if (LogWriter.isDebug) {
            Log.e(DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
    //----------------------------------------------------------------------------------------------
    public static void i(Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.i(LogWriter.DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void i(String msg) {
        if (LogWriter.isDebug) {
            if (msg != null) {
                Log.i(LogWriter.DEBUG_TAG, msg);
            }
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void i(String DEBUG_TAG, Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.i(DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
    public static void i(String DEBUG_TAG, String msg) {
        if (LogWriter.isDebug) {
            Log.e(DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    //----------------------------------------------------------------------------------------------
    public static void w(Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.w(LogWriter.DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void w(String msg) {
        if (LogWriter.isDebug) {
            if (msg != null) {
                Log.w(LogWriter.DEBUG_TAG, msg);
            }
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void w(String DEBUG_TAG, Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.w(DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
    public static void w(String DEBUG_TAG, String msg) {
        if (LogWriter.isDebug) {
            Log.w(DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }

    public static  void warnInfo(String msg){
        if(LogWriter.isDebug){
            Log.w(LogWriter.DEBUG_TAG, msg);
        }
        if(LogWriter.isWriteToLog){
            LogWriter.LogToFile(LogWriter.DEBUG_TAG,msg);
        }
    }
//--------------------------------------------------------------------------------------------------
public static void v(Object obj, String msg) {
    if (LogWriter.isDebug) {
        Log.v(LogWriter.DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
    }
    if (LogWriter.isWriteToLog) {
        LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
    }
}

    public static void v(String msg) {
        if (LogWriter.isDebug) {
            if (msg != null) {
                Log.v(LogWriter.DEBUG_TAG, msg);
            }
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(LogWriter.DEBUG_TAG, msg);
        }
    }

    public static void v(String DEBUG_TAG, Object obj, String msg) {
        if (LogWriter.isDebug) {
            Log.v(DEBUG_TAG, obj.getClass().getSimpleName() + ": " + msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
    public static void v(String DEBUG_TAG, String msg) {
        if (LogWriter.isDebug) {
            Log.v(DEBUG_TAG, msg);
        }
        if (LogWriter.isWriteToLog) {
            LogWriter.LogToFile(DEBUG_TAG, msg);
        }
    }
}
