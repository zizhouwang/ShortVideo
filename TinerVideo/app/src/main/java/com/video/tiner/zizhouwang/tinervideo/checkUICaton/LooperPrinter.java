package com.video.tiner.zizhouwang.tinervideo.checkUICaton;

import android.os.Looper;
import android.util.Log;
import android.util.Printer;

/**
 * Created by zizhouwang on 2018/6/6.
 */

public class LooperPrinter {
    private static long lastTime;

    public static void start() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            private static final String START = ">>>>> Dispatching";
            private static final String END = "<<<<< Finished";

            @Override
            public void println(String content) {
                if (content.startsWith(START)) {
                    LogMonitor.getInstance().startMonitor();
                    lastTime = System.currentTimeMillis();
                }
                if (content.startsWith(END)) {
                    LogMonitor.getInstance().removeMonitor();
                    Log.e("TAG", "run time : " + (System.currentTimeMillis() - lastTime));
                }
            }
        });
    }
}
