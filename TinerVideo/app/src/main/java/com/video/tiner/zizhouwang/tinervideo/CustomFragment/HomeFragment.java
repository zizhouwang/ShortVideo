package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerTabView;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zizhouwang on 2018/6/19.
 */

public class HomeFragment extends BaseFragment {

//    public List<VideoModel> oldVideoModelList = new LinkedList<>();
//    public Boolean isRefreshing = false;

    private FrameLayout homeFL;
    public ViewPager videoViewPager;
    public List<XListView> xListViews;
    private XListView verticalVideoListView;
    private XListView horizontalVideoListView;
    private View savedView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (savedView == null) {
            view = createNewView(inflater);
        } else {
            view = savedView;
        }
        view.setBackgroundResource(R.color.whiteColor);
        return view;
    }

    public View getSavedView() {
        if (savedView == null) {
            LayoutInflater inflater = LayoutInflater.from(FormatUtil.mainContext);
            savedView = createNewView(inflater);
        }
        return savedView;
    }

    @SuppressWarnings("unchecked")
    private View createNewView(final LayoutInflater inflater) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.home_layout, null);

        homeFL = view.findViewById(R.id.homeFL);
        videoViewPager = view.findViewById(R.id.videoViewPager);
        final TinerTabView tinerTabView = new TinerTabView(FormatUtil.mainContext);
        tinerTabView.setOnTabClickListener(new TinerTabView.OnTabClickListener() {
            @Override
            public void tabClicked(TextView tab, int index) {
                videoViewPager.setCurrentItem(index, true);
            }
        });
        tinerTabView.addTab("portrait");
        tinerTabView.addTab("landscape");
        videoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        Log.v("SCROLL_STATE_DRAGGING" ,"SCROLL_STATE_DRAGGING");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        FormatUtil.homeListView = xListViews.get(videoViewPager.getCurrentItem());
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        Log.v("SCROLL_STATE_IDLE" ,"getCurrentItem:" + videoViewPager.getCurrentItem());
                        XListView xListView = xListViews.get(videoViewPager.getCurrentItem());
                        if (videoViewPager.getCurrentItem() >= 1 && !xListView.isRefreshing && xListView.getAdapter() == null) {
                            SharedPreferences sp = view.getContext().getSharedPreferences("SP_VIDEO_LIST", Activity.MODE_PRIVATE);
                            String videoListJson = sp.getString(xListView.tagStr, "");
                            if (videoListJson != "") {
                                Gson gson = new Gson();
                                List<VideoModel> videoModelList = gson.fromJson(videoListJson, new TypeToken<LinkedList<VideoModel>>() {
                                }.getType());
                                if (videoModelList.size() == 0) {
                                    sendRequestWithHttpURLConnection(view.getContext(), xListView, false);
                                } else {
                                    VideoListAdapter videoListAdapter = new VideoListAdapter(view.getContext(), videoModelList, xListView, "home");
                                    xListView.setAdapter(videoListAdapter);
                                }
                            } else {
                                sendRequestWithHttpURLConnection(view.getContext(), xListView, false);
                            }
                        }
                        FrameLayout.LayoutParams yellowLineLayout = (FrameLayout.LayoutParams) tinerTabView.yellowLine.getLayoutParams();
                        yellowLineLayout.gravity = Gravity.BOTTOM;
                        yellowLineLayout.leftMargin = videoViewPager.getCurrentItem() * yellowLineLayout.width;
                        tinerTabView.yellowLine.setLayoutParams(yellowLineLayout);
                        break;
                }
            }
        });
        verticalVideoListView = new XListView(FormatUtil.mainContext);
        horizontalVideoListView = new XListView(FormatUtil.mainContext);
        xListViews = new ArrayList<>();
        xListViews.add(verticalVideoListView);
        xListViews.add(horizontalVideoListView);

        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), homeFL, videoViewPager, true);
        tinerNavView.bringToFront();
        tinerNavView.navTextView.setText("Pop Tiner");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));

        int tabHeight = 100 * FormatUtil.getScreenHeight(FormatUtil.mainContext) / 1280;
        FrameLayout.LayoutParams videoViewPagerLayout = (FrameLayout.LayoutParams) videoViewPager.getLayoutParams();
        FrameLayout.LayoutParams tinerTabViewLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tabHeight);
        tinerTabViewLayout.topMargin = videoViewPagerLayout.topMargin;
        homeFL.addView(tinerTabView, tinerTabViewLayout);
        videoViewPagerLayout.topMargin += tabHeight - 3;
        videoViewPager.setLayoutParams(videoViewPagerLayout);

        final View emptyView = inflater.inflate(R.layout.empty_view, null);
        homeFL.addView(emptyView);

        final Handler videoListLoadDataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj.getClass().equals(HashMap.class)) {
                    HashMap<String, Object> msgObjMap = (HashMap<String, Object>) msg.obj;
                    XListView xListView = (XListView) msgObjMap.get("listView");
                    VideoListAdapter videoListAdapter = (VideoListAdapter) msgObjMap.get("adapter");
                    xListView.setAdapter(videoListAdapter);
                }
            }
        };

        final Handler videoListRequestDataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj.getClass().equals(XListView.class)) {
                    XListView xListView = (XListView) msg.obj;
                    sendRequestWithHttpURLConnection(FormatUtil.mainContext, xListView, false);
                }
            }
        };

        final Handler videoViewPagerAdapterInitHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                homeFL.removeView(emptyView);
                videoViewPager.setAdapter(new PagerAdapter() {
                    @Override
                    public int getCount() {
                        return 2;
                    }

                    @Override
                    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                        return view == object;
                    }

                    @NonNull
                    @Override
                    public Object instantiateItem(@NonNull ViewGroup container, int position) {
                        FrameLayout frameLayout = new FrameLayout(container.getContext());
                        container.addView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        XListView xListView = xListViews.get(position);
                        frameLayout.addView((FrameLayout) xListView.loadVideoIV.getParent(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        frameLayout.addView(xListView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        return frameLayout;
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormatUtil.homeListView = xListViews.get(0);
                initXListView(inflater, verticalVideoListView);
                verticalVideoListView.tagStr = "VIDEO_LIST";
                initXListView(inflater, horizontalVideoListView);
                horizontalVideoListView.tagStr = "HORIZONTAL_VIDEO_LIST";

                SharedPreferences sp = view.getContext().getSharedPreferences("SP_VIDEO_LIST", Activity.MODE_PRIVATE);//创建sp对象,如果有key为"SP_PEOPLE"的sp就取出
                String videoListJson = sp.getString(FormatUtil.homeListView.tagStr, "");
                if (videoListJson != "") {
                    Gson gson = new Gson();
                    List<VideoModel> videoModelList = gson.fromJson(videoListJson, new TypeToken<LinkedList<VideoModel>>() {
                    }.getType());
                    if (videoModelList.size() == 0) {
                        Message msg = new Message();
                        msg.obj = xListViews.get(0);
                        videoListRequestDataHandler.sendMessage(msg);
//                        sendRequestWithHttpURLConnection(view.getContext(), FormatUtil.homeListView, false);
                    } else {
                        VideoListAdapter videoListAdapter = new VideoListAdapter(view.getContext(), videoModelList, FormatUtil.homeListView, "home");
//                        FormatUtil.homeListView.setAdapter(videoListAdapter);
                        Message msg = new Message();
                        HashMap<String, Object> hashObjMap = new HashMap<>();
                        hashObjMap.put("listView", xListViews.get(0));
                        hashObjMap.put("adapter", videoListAdapter);
                        msg.obj = hashObjMap;
                        videoListLoadDataHandler.sendMessage(msg);
                    }
                } else {
//                    sendRequestWithHttpURLConnection(view.getContext(), FormatUtil.homeListView, false);
                    Message msg = new Message();
                    msg.obj = xListViews.get(0);
                    videoListRequestDataHandler.sendMessage(msg);
                }

                videoListJson = sp.getString(xListViews.get(1).tagStr, "");
                if (videoListJson != "") {
                    Gson gson = new Gson();
                    List<VideoModel> videoModelList = gson.fromJson(videoListJson, new TypeToken<LinkedList<VideoModel>>() {
                    }.getType());
                    if (videoModelList.size() > 0) {
                        VideoListAdapter videoListAdapter = new VideoListAdapter(view.getContext(), videoModelList, xListViews.get(1), "home");
//                        xListViews.get(1).setAdapter(videoListAdapter);
                        Message msg = new Message();
                        HashMap<String, Object> hashObjMap = new HashMap<>();
                        hashObjMap.put("listView", xListViews.get(1));
                        hashObjMap.put("adapter", videoListAdapter);
                        msg.obj = hashObjMap;
                        videoListLoadDataHandler.sendMessage(msg);
                    }
                }
                videoViewPagerAdapterInitHandler.sendEmptyMessage(0);
            }
        }).start();

        savedView = view;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("homePage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("homePage");
    }

    public void initXListView(LayoutInflater inflater, final XListView xListView) {
        View emptyView = inflater.inflate(R.layout.empty_view, null);
        xListView.setEmptyView(emptyView);
        xListView.setPullLoadEnable(true);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (!xListView.isRefreshing) {
                    sendRequestWithHttpURLConnection(FormatUtil.mainContext, xListView, false);
                }
            }

            @Override
            public void onLoadMore() {
                if (!xListView.isRefreshing) {
                    sendRequestWithHttpURLConnection(FormatUtil.mainContext, xListView, true);
                }
            }
        });
        final ProgressBar loadVideoPB = emptyView.findViewById(R.id.emptyviewLoadVideoPB);
        final ImageView loadVideoIV = emptyView.findViewById(R.id.emptyviewLoadVideoIV);
        xListView.loadVideoPB = loadVideoPB;
        xListView.loadVideoIV = loadVideoIV;
        loadVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideoPB.setVisibility(View.VISIBLE);
                loadVideoIV.setVisibility(View.INVISIBLE);
                sendRequestWithHttpURLConnection(FormatUtil.mainContext, xListView, false);
            }
        });
    }

    public static void sendRequestWithHttpURLConnection(final Context context, final XListView xListView, final Boolean isLoadMore) {
        //开启线程来发起网络请求
        if (isLoadMore == false) {
            xListView.oldVideoModelList = new LinkedList<>();
        }
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                xListView.loadVideoPB.setVisibility(View.INVISIBLE);
                xListView.loadVideoIV.setVisibility(View.VISIBLE);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    xListView.isRefreshing = true;
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url;
                        if (xListView.tagStr == "VIDEO_LIST") {
                            url = new URL("http://172.96.240.118/?screenType=1");
                        } else {
                            url = new URL("http://172.96.240.118/?screenType=2");
                        }
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                        InputStream in = connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        String responseStr = response.toString();
                        JSONObject videoObjects = new JSONObject(responseStr);
                        int adWaitCount = videoObjects.getInt("adWaitCount");
                        FormatUtil.waitAdCount = adWaitCount;
                        if (FormatUtil.adCount > adWaitCount) {
                            FormatUtil.adCount = adWaitCount;
                        }
                        SharedPreferences adSp = context.getSharedPreferences("AD_WAIT_COUNT", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor adEditor = adSp.edit();
                        adEditor.putInt("adWaitCount", adWaitCount);
                        adEditor.apply();
                        JSONArray videoArray = videoObjects.getJSONArray("result");
                        if (videoArray.length() > 0 && !isLoadMore) {
                            SharedPreferences sp = context.getSharedPreferences("SP_VIDEO_LIST", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(xListView.tagStr, videoArray.toString());
                            editor.apply();
                        }
                        Gson gson = new Gson();
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject videoObject = videoArray.getJSONObject(i);
                            VideoModel videoModel = gson.fromJson(videoObject.toString(), VideoModel.class);
                            if (videoModel.getVideo_cdn_url().endsWith(".mp4")) {
                                xListView.oldVideoModelList.add(videoModel);
                            }
                        }
                        final List<VideoModel> videoModelList = new LinkedList<>();
                        videoModelList.addAll(xListView.oldVideoModelList);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isLoadMore) {
                                    HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) xListView.getAdapter();
                                    VideoListAdapter videoListAdapter = (VideoListAdapter) hAdapter.getWrappedAdapter();
                                    videoListAdapter.mList.addAll(videoModelList);
                                    videoListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.v("videoListSize", "" + videoModelList.size());
                                    if (videoModelList.size() == 0) {
                                        handler.sendEmptyMessage(0);
                                    }
                                    VideoListAdapter videoListAdapter = new VideoListAdapter(context, videoModelList, xListView, "home");
                                    xListView.setAdapter(videoListAdapter);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.v("网络超时", "网络超时");
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    } finally {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                xListView.isRefreshing = false;
                                xListView.stopRefresh();
                                xListView.stopLoadMore();
                                xListView.setRefreshTime("刚刚");
                            }
                        });
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        break;
                    }
                }
            }
        }).start();
    }

    private void wait3000(final Context context, final Boolean isLoadMore) {
//        //开启线程来发起网络请求
//        if (isLoadMore == false) {
//            oldVideoModelList = new LinkedList<>();
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    HttpURLConnection connection = null;
//                    BufferedReader reader = null;
//                    try {
//                        isRefreshing = true;
//                        Thread.sleep(30000);
//                    } catch (Exception e) {
//                        Log.v("网络超时", "网络超时");
//                        e.printStackTrace();
//                        try {
//                            Thread.sleep(3000);
//                        } catch (Exception ee) {
//                            ee.printStackTrace();
//                        }
//                    } finally {
//                        Handler mainHandler = new Handler(Looper.getMainLooper());
//                        mainHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                isRefreshing = false;
//                                verticalVideoListView.stopRefresh();
//                                verticalVideoListView.stopLoadMore();
//                                verticalVideoListView.setRefreshTime("刚刚");
//                            }
//                        });
//                        if (reader != null) {
//                            try {
//                                reader.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (connection != null) {
//                            connection.disconnect();
//                        }
//                        break;
//                    }
//                }
//            }
//        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
