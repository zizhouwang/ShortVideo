package com.video.tiner.zizhouwang.tinervideo.subview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.video.tiner.zizhouwang.tinervideo.CustomUI.BaseFrameLayout;
import com.video.tiner.zizhouwang.tinervideo.CustomUI.BaseImageView;
import com.video.tiner.zizhouwang.tinervideo.ImageLoader.ImageLoader;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.videoProxy.HttpGetProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/6/2.
 */

public class TinerVideoView extends LinearLayout implements TextureView.SurfaceTextureListener {

    private float mPosX, mPosY, mCurPosX, mCurPosY, oldCurPosX, oldCurPoxY;
    private List<Float> curPoss = new ArrayList<>();
    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度
    private FrameLayout.LayoutParams videoFLLayout = null;
    private int videoFLTopMargin = -1;
    private int videoFLBottomMargin = -1;

    public int height;
    public int videoWidth;
    private int fullScreenTopMargin;
    private int fullScreenBottomMargin;
    public String videoPath;
    public SurfaceTexture savedSurfaceTexture;
    public Surface savedSurface;

    public Boolean isFullScreen = false;
    public Boolean isLoading;
    public Boolean isNeedPlay;
    public Boolean isSetVideoPath;
    public Boolean isPrepare;
    private Boolean isNeedStart;
    private Boolean isLoadedAfterAvai;
    private Boolean isLoadedAfterPrepare;

    public TextureView tinerTextureView;
    public MediaPlayer tinerMediaPlayer;
    public BaseImageView videoThumbnailIV;
    private ImageView cropThumbnailIV;
    public ImageView playVideoIV;
    public TextView videoTitleTV;
    public BaseFrameLayout videoFL;
    public FrameLayout videoParentFL;
    public LinearLayout videoLL;
    public FrameLayout videoControlFL;

    public FrameLayout videoProgressFL;
    public TextView currentTimeTV;
    public SeekBar currentTimeSB;
    public TextView totalTimeTV;
    public ImageView fullScreenIV;
    public View fullScreenView;
    public ProgressBar loadVideoPB;

    private VelocityTracker mVelocityTracker;
    private int mPointerId;

    Scroller mScroller = new Scroller(FormatUtil.mainContext);

    private HashMap<String, HttpGetProxy> httpGetProxyHashMap;
    private String proxyURL = null;

    public TinerVideoView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        final TinerVideoView thiss = this;
        FormatUtil.mScroller = mScroller;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.customView);
        ta.recycle();
        LayoutInflater mInflatet = LayoutInflater.from(context);
        LinearLayout convertView = null;
        try {
            convertView = (LinearLayout) mInflatet.inflate(R.layout.video_view, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addView(convertView);
        init(context, convertView);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void init(final Context context, LinearLayout convertView) {
        final TinerVideoView thiss = this;
        videoThumbnailIV = convertView.findViewById(R.id.videoThumbnailIV);
        cropThumbnailIV = convertView.findViewById(R.id.cropThumbnailIV);
        playVideoIV = convertView.findViewById(R.id.playVideoIV);
        videoTitleTV = convertView.findViewById(R.id.videoTitleTV);
        videoFL = convertView.findViewById(R.id.videoFL);
        videoParentFL = convertView.findViewById(R.id.videoParentFL);
        videoLL = convertView.findViewById(R.id.videoLL);
        videoControlFL = convertView.findViewById(R.id.videoControlFL);

        videoThumbnailIV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                acquireVelocityTracker(event);
//                final VelocityTracker verTracker = mVelocityTracker;
                if (isFullScreen == false) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    return true;
                }
                int oldCurrentFullScreenTag = FormatUtil.homeListView.currentFullScreenTag;
                boolean moveResult = moveVideoFLWithGlide(v, event, true);
                for (int i = 0; i < FormatUtil.homeListView.mTotalItemViews.size(); i++) {
                    if ((int) FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.videoThumbnailIV.getTag() == oldCurrentFullScreenTag) {
                        continue;
                    }
                    FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.moveVideoFLWithGlide(FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.videoThumbnailIV, event, false);
                }
                return moveResult;
            }
        });

        playVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNeedPlay == false) {
                    thiss.videoStart();
                } else {
                    thiss.videoPause();
                }
            }
        });
    }

    public boolean moveVideoFLWithGlide(View v, MotionEvent event, boolean isClickedView) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isClickedView == true && FormatUtil.homeListView.currentFullScreenTag == (int) FormatUtil.homeListView.mTotalItemViews.get(FormatUtil.homeListView.mTotalItemViews.size() - 1).tinerInteView.videoThumbnailIV.getTag()) {
                    FormatUtil.homeListView.smoothScrollToPositionFromTop((int) videoThumbnailIV.getTag() + 1, -100, 0);
                }
                for (int i = 0; i < FormatUtil.homeListView.mTotalItemViews.size(); i++) {
                    if (FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.isFullScreen == false) {
                        FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.changeFullScreenViewLevel();
                    }
                }
                videoFLLayout = (FrameLayout.LayoutParams) videoFL.getLayoutParams();
                videoFLTopMargin = videoFLLayout.topMargin;
                videoFLBottomMargin = videoFLLayout.bottomMargin;
                mCurPosX = -1;
                mCurPosY = -1;
                oldCurPosX = 0;
                oldCurPoxY = 0;
                mPosX = event.getRawX();
                mPosY = event.getRawY();
                curPoss = new ArrayList<>();
                mPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFullScreen == false) {
                    break;
                }
                oldCurPoxY = mCurPosY;
                mCurPosX = event.getRawX();
                mCurPosY = event.getRawY();
                videoFLLayout.topMargin = (int)(videoFLTopMargin - (mPosY - mCurPosY));
                videoFLLayout.bottomMargin = (int)(videoFLBottomMargin + (mPosY - mCurPosY));
//                updateFullScreenLayout(videoFL, videoFLLayout.topMargin, videoFLLayout.bottomMargin);
                videoFL.setLayoutParams(videoFLLayout);

                if (oldCurPoxY != -1) {
                    curPoss.add(mCurPosY - oldCurPoxY);
                }
                if (curPoss.size() > 5) {
                    curPoss.remove(0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFullScreen == false || (mCurPosX == -1 && mCurPosY == -1) || (mPosY == mCurPosY)) {
                    if (isClickedView == true) {
                        v.performClick();
                    }
                    return true;
                }
                float velocityY = 0.0f;
                for (int i = 0; i < curPoss.size(); i++) {
                    velocityY += curPoss.get(i);
                }
                int changeCurrentFullScreenTagNumber = 100;
                if (isClickedView == true) {
                    if (velocityY > changeCurrentFullScreenTagNumber) {
                        FormatUtil.homeListView.currentFullScreenTag--;
                    }
                    if (velocityY < -changeCurrentFullScreenTagNumber) {
                        FormatUtil.homeListView.currentFullScreenTag++;
                    }
                }
                List<Integer> layoutDataList = calculateFullScreenLayout(0, 0);
                FormatUtil.moveViewByMargin(videoFL, 0, layoutDataList.get(0), 0, layoutDataList.get(1), 200);
                videoFLLayout = null;
                break;
        }
        return true;
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void initLoadVideoPB() {
        loadVideoPB = FormatUtil.getLoadVideoPB();
        FrameLayout.LayoutParams loadVideoLayoutParams = new FrameLayout.LayoutParams(FormatUtil.dpToPx(32), FormatUtil.dpToPx(32));
        loadVideoLayoutParams.gravity = Gravity.CENTER;
        loadVideoPB.setLayoutParams(loadVideoLayoutParams);
        loadVideoPB.setVisibility(INVISIBLE);
        videoFL.addView(loadVideoPB, 0);
        loadVideoPB.bringToFront();
    }

    private void initControlFrameLayout(final Handler videoStartHandlerr) {
        final TinerVideoView thiss = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater mInflatet = LayoutInflater.from(FormatUtil.mainContext);
                FrameLayout convertView = (FrameLayout) mInflatet.inflate(R.layout.video_control_view, null);
                FrameLayout.LayoutParams convertViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, FormatUtil.dpToPx(80));
                convertViewLayoutParams.gravity = Gravity.BOTTOM;
                videoProgressFL = convertView.findViewById(R.id.videoProgressFL);
                videoProgressFL.setLayoutParams(convertViewLayoutParams);
                initControlFrameLayout.sendEmptyMessage(0);
                videoStartHandlerr.sendEmptyMessage(0);
            }
        }).start();
    }

    private Handler initControlFrameLayout = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            videoControlFL.addView(videoProgressFL, 1);
            currentTimeTV = videoProgressFL.findViewById(R.id.currentTimeTV);
            currentTimeSB = videoProgressFL.findViewById(R.id.currentTimeSB);
            totalTimeTV = videoProgressFL.findViewById(R.id.totalTimeTV);
            fullScreenIV = videoProgressFL.findViewById(R.id.fullScreenIV);
            fullScreenView = videoProgressFL.findViewById(R.id.fullScreenView);
            if (isFullScreen == false) {
                fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.full_screen));
            } else {
                fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.shrink_screen));
            }
            videoProgressFL.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    FrameLayout.LayoutParams currentTimeSBLayoutParams = (FrameLayout.LayoutParams) currentTimeSB.getLayoutParams();
                    if (currentTimeSBLayoutParams.leftMargin == 0) {
                        float currentTimeTVX = currentTimeTV.getX();
                        float currentTimeTVWidth = currentTimeTV.getWidth();
                        float totalTimeTVX = totalTimeTV.getX();
                        float screenWidth = totalTimeTV.getContext().getResources().getDisplayMetrics().widthPixels;
                        currentTimeSBLayoutParams.setMargins((int) (currentTimeTVX + currentTimeTVWidth + 0.0f), 0, (int) (screenWidth - totalTimeTVX + 0.0f), 0);
                        currentTimeSB.setLayoutParams(currentTimeSBLayoutParams);
                    }
                }
            });
            currentTimeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == true) {
                        currentTimeTV.setText(FormatUtil.integerToTimeStr(seekBar.getProgress()));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (FormatUtil.isPlayingVideoView != null) {
                        if (seekBar == FormatUtil.isPlayingVideoView.currentTimeSB) {
                            FormatUtil.isUserSliding = true;
                        }
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    FormatUtil.isUserSliding = false;
                    int msec = seekBar.getProgress() * 1000;
                    Log.v("seekBarProgress", "" + msec);
                    tinerMediaPlayer.seekTo(msec);
                }
            });
            fullScreenView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FormatUtil.homeListView.currentFullScreenTag = (int) videoThumbnailIV.getTag();
//                    FormatUtil.homeListView.smoothScrollToPositionFromTop((int) videoThumbnailIV.getTag() + 1, -100, 0);
                    for (int i = 0; i < FormatUtil.homeListView.mTotalItemViews.size(); i++) {
                        FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.changeFullScreenViewLevel();
                        if ((int) FormatUtil.homeListView.mTotalItemViews.get(i).tinerInteView.videoThumbnailIV.getTag() == FormatUtil.homeListView.currentFullScreenTag) {
                        }
                    }
                }
            });
            updateControlFrameLayout();
        }
    };

    public void changeFullScreenViewLevel() {
        int screenHeight = FormatUtil.getScreenHeight(FormatUtil.mainContext);
        int screenWidth = FormatUtil.getScreenWidth(FormatUtil.mainContext);
        Window window = ((AppCompatActivity) FormatUtil.mainContext).getWindow();
        ((ViewGroup) videoFL.getParent()).removeView(videoFL);
        if (isFullScreen == false) {
            ViewGroup vg = FormatUtil.getWindow(((AppCompatActivity) FormatUtil.mainContext));
            if (Build.VERSION.SDK_INT >= 21) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                int systemUiVIsi = vg.getSystemUiVisibility();
                int fullScreenFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                systemUiVIsi |= fullScreenFlag;
                vg.setSystemUiVisibility(systemUiVIsi);
            }
            vg.addView(videoFL);
        } else {
            ViewGroup vg = FormatUtil.getWindow(((AppCompatActivity) FormatUtil.mainContext));
            videoParentFL.addView(videoFL);
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                int systemUiVIsi = vg.getSystemUiVisibility();
                int fullScreenFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                systemUiVIsi &= ~fullScreenFlag;
                vg.setSystemUiVisibility(systemUiVIsi);
            }
        }
        changeFullScreenLayout(screenWidth, screenHeight);
    }

    private void changeFullScreenLayout(int screenWidth, int screenHeight) {
        if (isFullScreen == false) {
            isFullScreen = true;
            if (fullScreenIV != null) {
                fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.shrink_screen));
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoFL.getLayoutParams();
            ViewGroup.LayoutParams videoTextureLayoutParams = tinerTextureView.getLayoutParams();
            float screenXY = screenWidth * 1.0f / screenHeight;
            float videoXY = videoWidth * 1.0f / height;
            float screenYX = screenHeight * 1.0f / screenWidth;
            if (videoXY > screenYX) {
                layoutParams.width = screenHeight;
                layoutParams.height = screenWidth;
                layoutParams.topMargin = (screenHeight - screenWidth) / 2;
                layoutParams.leftMargin = -(screenHeight - screenWidth) / 2;
                videoFL.setRotation(90);
                videoTextureLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoTextureLayoutParams.height = height * screenHeight / videoWidth;
            } else if (videoXY > 1) {
                layoutParams.width = screenHeight;
                layoutParams.height = screenWidth;
                layoutParams.topMargin = (screenHeight - screenWidth) / 2;
                layoutParams.leftMargin = -(screenHeight - screenWidth) / 2;
                videoFL.setRotation(90);
                videoTextureLayoutParams.width = videoWidth * screenWidth / height;
                videoTextureLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoTextureLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (videoXY > screenXY) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

                videoTextureLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoTextureLayoutParams.height = height * screenWidth / videoWidth;
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

                videoTextureLayoutParams.width = videoWidth * screenHeight / height;
                videoTextureLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            videoFL.setLayoutParams(layoutParams);
            tinerTextureView.setLayoutParams(videoTextureLayoutParams);
            fullScreenTopMargin = layoutParams.topMargin;
            fullScreenBottomMargin = layoutParams.bottomMargin;
            updateFullScreenLayout(videoFL, layoutParams.topMargin, layoutParams.bottomMargin);
        } else {
            isFullScreen = false;
            if (fullScreenIV != null) {
                fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.full_screen));
            }
            videoLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoFL.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = height;
            layoutParams.setMargins(0, 0, 0, 0);
            videoFL.setLayoutParams(layoutParams);
            videoFL.setRotation(0);
            FrameLayout.LayoutParams videoTextureFL = (FrameLayout.LayoutParams) tinerTextureView.getLayoutParams();
            videoTextureFL.width = videoWidth;
            videoTextureFL.height = height;
            tinerTextureView.setLayoutParams(videoTextureFL);
        }
    }

    private void updateFullScreenLayout(FrameLayout frameLayout, int startTopMargin, int startBottomMargin) {
        FrameLayout.LayoutParams videoFLLayout = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        List<Integer> layoutDataList = calculateFullScreenLayout(startTopMargin, startBottomMargin);
        videoFLLayout.topMargin = layoutDataList.get(0);
        videoFLLayout.bottomMargin = layoutDataList.get(1);
        frameLayout.setLayoutParams(videoFLLayout);
    }

    private List<Integer> calculateFullScreenLayout(int startTopMargin, int startBottomMargin) {
        int diffItemCount = (int) videoThumbnailIV.getTag() - FormatUtil.homeListView.currentFullScreenTag;
//        videoFLLayout.topMargin = startTopMargin + diffItemCount * FormatUtil.getScreenHeight(FormatUtil.mainContext);
//        videoFLLayout.bottomMargin = startBottomMargin - diffItemCount * FormatUtil.getScreenHeight(FormatUtil.mainContext);
        List<Integer> layoutDataList = new ArrayList<>();
        layoutDataList.add(startTopMargin + diffItemCount * FormatUtil.getScreenHeight(FormatUtil.mainContext));
        layoutDataList.add(startBottomMargin - diffItemCount * FormatUtil.getScreenHeight(FormatUtil.mainContext));
        return layoutDataList;
    }

    private void loadControlFrameLayoutAfterAvai() {
        if (isNeedStart == true && isPrepare == false && isLoadedAfterAvai == false) {
            isLoadedAfterAvai = true;
            float currentTimeTVX = currentTimeTV.getX();
            float currentTimeTVWidth = currentTimeTV.getWidth();
            float totalTimeTVX = totalTimeTV.getX();
            float screenWidth = totalTimeTV.getContext().getResources().getDisplayMetrics().widthPixels;
            FrameLayout.LayoutParams currentTimeSBLayoutParams = (FrameLayout.LayoutParams) currentTimeSB.getLayoutParams();
            currentTimeSBLayoutParams.setMargins((int) (currentTimeTVX + currentTimeTVWidth + 0.0f), 0, (int) (screenWidth - totalTimeTVX + 0.0f), 0);
            currentTimeSB.setLayoutParams(currentTimeSBLayoutParams);
        }
    }

    private void loadControlFrameLayoutAfterPrepare(MediaPlayer mp) {
        if (isNeedStart == true && isPrepare == true && isLoadedAfterPrepare == false) {
            try {
                isLoadedAfterPrepare = true;
                currentTimeSB.setEnabled(true);
                int time = Math.round(tinerMediaPlayer.getDuration() / 1000.0f);
                String timeStr = FormatUtil.integerToTimeStr(time);
                totalTimeTV.setText(timeStr);
                currentTimeSB.setMax(time);
                currentTimeSB.setProgress(0);
                float currentTimeTVX = currentTimeTV.getX();//32px
                float currentTimeTVWidth = currentTimeTV.getWidth();//80px
                float totalTimeTVX = totalTimeTV.getX();//528px
                float screenWidth = totalTimeTV.getContext().getResources().getDisplayMetrics().widthPixels;
                float screenHeight = FormatUtil.getScreenHeight(getContext());
                FrameLayout.LayoutParams currentTimeSBLayoutParams = (FrameLayout.LayoutParams) currentTimeSB.getLayoutParams();
                if (isFullScreen == true) {
                    currentTimeSBLayoutParams.setMargins((int) (currentTimeTVX + currentTimeTVWidth + 0.0f), 0, (int) (screenHeight - totalTimeTVX + 0.0f), 0);
                } else {
                    currentTimeSBLayoutParams.setMargins((int) (currentTimeTVX + currentTimeTVWidth + 0.0f), 0, (int) (screenWidth - totalTimeTVX + 0.0f), 0);
                }
                currentTimeSB.setLayoutParams(currentTimeSBLayoutParams);
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        currentTimeTV.setText(FormatUtil.integerToTimeStr(currentTimeSB.getProgress()));
                    }
                });
                loadVideoPB.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (isPrepare == false) {
            loadControlFrameLayoutAfterAvai();
            tinerTextureView.setVisibility(View.INVISIBLE);
        }
        if (savedSurfaceTexture == null) {
            savedSurfaceTexture = surface;
            savedSurface = new Surface(surface);
        } else {
            tinerTextureView.setSurfaceTexture(savedSurfaceTexture);
        }
        try {
            if (tinerMediaPlayer == null) {
                final TinerVideoView thiss = this;
                tinerMediaPlayer = new MediaPlayer();
                tinerMediaPlayer.setSurface(savedSurface);
                tinerMediaPlayer.setScreenOnWhilePlaying(true);
                tinerMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                tinerMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isLoading = false;
                        isPrepare = true;
                        loadControlFrameLayoutAfterPrepare(mp);
                        if (FormatUtil.isPlayingVideoView != thiss) {
                            return;
                        }
                        if (isNeedPlay == true) {
                            videoPlay();
                        }
                    }
                });
                tinerMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                Log.v("BUFFERING_PAUSE", "暂停了");
                                isLoading = true;
                                loadVideoPB.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                Log.v("BUFFERING_CONTINUE", "继续");
                                isLoading = false;
                                loadVideoPB.setVisibility(View.INVISIBLE);
                                break;
                        }
                        return false;
                    }
                });
                tinerMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if (extra == -2147483648 || extra == -107) {
                            return true;
                        }
                        if (playVideoIV.isEnabled() == false) {
                            return true;
                        }
                        videoPause();
                        tinerTextureView.setVisibility(View.INVISIBLE);
                        Log.v("playError", "what:" + what + " extra:" + extra);
                        playVideoIV.setEnabled(false);
                        playVideoIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.load_video_error));
                        int pading = playVideoIV.getPaddingBottom();
                        playVideoIV.setPadding(pading + 0, pading + 0, pading + 0, pading + 0);
                        return true;
                    }
                });
                tinerMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        thiss.videoPause();
                        thiss.currentTimeTV.setText(thiss.totalTimeTV.getText());
                        thiss.currentTimeSB.setProgress(thiss.currentTimeSB.getMax());
                    }
                });
                if (isSetVideoPath == false && proxyURL != null) {
                    tinerMediaPlayer.setDataSource(proxyURL);
                    tinerMediaPlayer.prepareAsync();
                    isSetVideoPath = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return savedSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void updateUIAndData(int position, int height, int videoWidth, VideoModel bean, HashMap<String, HttpGetProxy> httpGetProxyHashMap, int port) {
        if (tinerMediaPlayer != null) {
//            tinerMediaPlayer.release();
//            tinerMediaPlayer = null;
        }
        if (tinerTextureView != null) {
//            videoFL.removeView(tinerTextureView);
//            tinerTextureView = null;
        }
        if (savedSurfaceTexture != null) {
//            savedSurfaceTexture.release();
//            savedSurfaceTexture = null;
        }
        if (savedSurface != null) {
//            savedSurface.release();
//            savedSurface = null;
        }
        if (videoProgressFL != null) {
            videoProgressFL.setVisibility(View.INVISIBLE);
        }
        this.height = height;
        this.videoWidth = videoWidth;
        proxyURL = null;
        videoPath = bean.getVideo_cdn_url();
        videoTitleTV.setText(bean.getShare_text());
        if (tinerTextureView == null) {
            tinerTextureView = new TextureView(getContext());
            tinerTextureView.setSurfaceTextureListener(this);
            videoFL.addView(tinerTextureView, 2, new FrameLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (isFullScreen == false) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tinerTextureView.getLayoutParams();
            layoutParams.width = videoWidth;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.CENTER;
            tinerTextureView.setLayoutParams(layoutParams);
//            fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.full_screen));
        } else {
//            fullScreenIV.setImageBitmap(FormatUtil.readBitMap(getContext(), R.drawable.shrink_screen));
        }
        tinerTextureView.setVisibility(View.VISIBLE);
        final TinerVideoView thiss = this;
        isSetVideoPath = false;
        isPrepare = false;
        isNeedStart = false;
        isLoadedAfterAvai = false;
        isLoadedAfterPrepare = false;
        Log.v("updateUIAndData", "updateUIAndData " + bean.getShare_text());
        videoThumbnailIV.setTag(position);
        videoThumbnailIV.setAlpha(1.0f);
        videoThumbnailIV.setVisibility(View.VISIBLE);
        videoThumbnailIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoStart();
            }
        });
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImageViewWithTagAndVideoPath(videoThumbnailIV, bean.getVideo_cdn_url(), position, videoWidth, height, cropThumbnailIV);
        playVideoIV.setVisibility(View.VISIBLE);
        playVideoIV.setEnabled(true);
        playVideoIV.setImageBitmap(FormatUtil.readBitMap(this.getContext(), R.drawable.play50_50));
        int padding = 30;
        playVideoIV.setPadding(padding, padding, padding, padding);
        updateControlFrameLayout();
        videoControlFL.setBackgroundResource(R.color.clearColor);
        videoControlFL.setVisibility(View.VISIBLE);
        isNeedPlay = false;
        isLoading = false;
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        if (isFullScreen != true) {
            videoFL.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, height));
        }
        this.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, height));
        if (FormatUtil.isPlayingVideoView == this) {
            FormatUtil.isPlayingVideoView = null;
        }
        if (bean.getChannel().equals("Break")) {
            proxyURL = videoPath;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpProxyCacheServer proxy = FormatUtil.getProxy();
                    String proxyUrl = proxy.getProxyUrl(videoPath);
                    proxyURL = proxyUrl;
                    setDataSourceHandler.sendEmptyMessage(0);
                }
            }).start();
        }
        if (loadVideoPB == null) {
            initLoadVideoPB();
        }
    }

    private void updateControlFrameLayout() {
        if (currentTimeSB != null) {
            FrameLayout.LayoutParams currentTimeSBLayoutParams = (FrameLayout.LayoutParams) currentTimeSB.getLayoutParams();
            currentTimeSBLayoutParams.setMargins(154, 0, 259, 0);
            currentTimeSB.setLayoutParams(currentTimeSBLayoutParams);
            currentTimeSB.setProgress(0);
            currentTimeTV.setText("00:00");
            totalTimeTV.setText("00:00");
            currentTimeSB.setEnabled(false);
            loadVideoPB.setVisibility(View.INVISIBLE);
        }
    }

    private Handler setDataSourceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (isSetVideoPath == false) {
                    try {
                        tinerMediaPlayer.reset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tinerMediaPlayer.setDataSource(proxyURL);
                    tinerMediaPlayer.prepareAsync();
                    isSetVideoPath = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void videoStart() {
        if (tinerMediaPlayer == null) {
            return;
        }
        FormatUtil.waitPlayingVideoView = this;
        loadVideoPB.setVisibility(View.VISIBLE);
        playVideoIV.setVisibility(View.INVISIBLE);
        isNeedStart = true;
        if (videoProgressFL == null) {
            initControlFrameLayout(videoStartHandler);
        } else {
            videoProgressFL.setVisibility(View.VISIBLE);
            videoStartHandler.sendEmptyMessage(0);
        }
        videoThumbnailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoControlShowAndHide();
            }
        });
    }

    private Handler videoStartHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadControlFrameLayoutAfterAvai();
            loadControlFrameLayoutAfterPrepare(tinerMediaPlayer);
            isNeedPlay = true;
            playVideoIV.setImageBitmap(FormatUtil.readBitMap(FormatUtil.mainContext, R.drawable.pause));
            if (currentTimeSB.getProgress() == currentTimeSB.getMax()) {
                currentTimeSB.setProgress(0);
            }
            if (FormatUtil.isPlayingVideoView != null) {
                FormatUtil.isPlayingVideoView.detachFocus();
            }
            if (isPrepare.equals(false)) {
                isLoading = true;
            } else {
                isLoading = false;
            }
            FormatUtil.isPlayingVideoView = FormatUtil.waitPlayingVideoView;
            if (isSetVideoPath == false) {
                try {
                    try {
                        tinerMediaPlayer.reset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tinerMediaPlayer.setDataSource(proxyURL);
                    tinerMediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isSetVideoPath = true;
            } else {
                if (isPrepare == true) {
                    videoPlay();
                }
            }
            videoControlHide(false);
        }
    };

    private void videoPlay() {
        cropThumbnailIV.setImageBitmap(null);
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoThumbnailIV.setAlpha(0.0f);
            }
        }, 270);
        tinerTextureView.setVisibility(View.VISIBLE);
        playVideoIV.setVisibility(View.VISIBLE);
        tinerMediaPlayer.start();
        videoControlHide(true);
    }

    public void videoPause() {
        isNeedPlay = false;
        videoControlShow();
        loadVideoPB.setVisibility(View.INVISIBLE);
        playVideoIV.setVisibility(View.VISIBLE);
        playVideoIV.setImageBitmap(FormatUtil.readBitMap(this.getContext(), R.drawable.play50_50));
        if (isPrepare == true) {
            tinerMediaPlayer.pause();
        }
        FormatUtil.isPlayingVideoView = null;
    }

    public void detachFocus() {
        isNeedPlay = false;
        videoControlShow();
        loadVideoPB.setVisibility(View.INVISIBLE);
        playVideoIV.setImageBitmap(FormatUtil.readBitMap(this.getContext(), R.drawable.play50_50));
        if (tinerMediaPlayer != null && isPrepare == true) {
            tinerMediaPlayer.pause();
        }
    }

    public void videoControlShowAndHide() {
        if (videoControlFL.getVisibility() == View.VISIBLE) {
            videoControlHide(true);
        } else {
            videoControlShow();
        }
    }

    public void videoControlShow() {
        loadVideoPB.setVisibility(View.INVISIBLE);
        videoControlFL.setVisibility(View.VISIBLE);
        videoControlFL.setBackgroundResource(R.color.clearColor);
        videoTitleTV.setVisibility(View.VISIBLE);
    }

    public void videoControlHide(boolean isHideTitle) {
        if (isLoading) {
            loadVideoPB.setVisibility(View.VISIBLE);
        } else {
            loadVideoPB.setVisibility(View.INVISIBLE);
        }
        videoControlFL.setVisibility(View.INVISIBLE);
        videoControlFL.setBackgroundResource(R.color.clearColor);
        if (isHideTitle == true) {
            videoTitleTV.setVisibility(View.INVISIBLE);
        }
    }
}
