package com.video.tiner.zizhouwang.tinervideo.checkUICaton;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

/**
 * Created by zizhouwang on 2018/6/6.
 */

public class LogMonitor {
    private static LogMonitor INSTANCE = new LogMonitor();
    private HandlerThread handlerThread = new HandlerThread("log");
    private Handler handler;
    private static final long TIME_BLOCK = 50L;

    private LogMonitor() {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private static Runnable printRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder builder = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                builder.append(element.toString() + "\n");
            }
            Log.e("low load", builder.toString());
        }
    };

    public static LogMonitor getInstance() {
        return INSTANCE;
    }

    public void startMonitor() {
        handler.postDelayed(printRunnable, TIME_BLOCK);
    }

    public void removeMonitor() {
        handler.removeCallbacks(printRunnable);
    }
}
