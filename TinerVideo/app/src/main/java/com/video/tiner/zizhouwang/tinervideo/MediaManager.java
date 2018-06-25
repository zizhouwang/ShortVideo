package com.video.tiner.zizhouwang.tinervideo;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;

/**
 * Created by zizhouwang on 2018/6/13.
 */

public class MediaManager {
    public static MediaManager mediaManager;
    public static TextureView textureView;
    public static SurfaceTexture savedSurfaceTexture;
    public static Surface surface;

    public static MediaManager instance() {
        if (mediaManager == null) {
            mediaManager = new MediaManager();
        }
        return mediaManager;
    }
}
