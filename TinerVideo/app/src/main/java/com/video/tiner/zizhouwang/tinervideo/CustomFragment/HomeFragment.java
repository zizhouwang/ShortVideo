package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zizhouwang on 2018/6/19.
 */

public class HomeFragment extends Fragment {

    private List<VideoModel> oldVideoModelList = new LinkedList<>();
    private Boolean isRefreshing = false;

    private XListView pullToRefreshLayout;
    private ImageView loadVideoIV;
    private ProgressBar loadVideoPB;
    private View savedView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (savedView == null) {
            return createNewView(inflater);
        } else {
            return savedView;
        }
    }

    public View getSavedView() {
        if (savedView == null) {
            LayoutInflater inflater = LayoutInflater.from(FormatUtil.mainContext);
            savedView = createNewView(inflater);
        }
        return savedView;
    }

    private View createNewView(LayoutInflater inflater) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.home_layout, null);

        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), (ViewGroup) view.findViewById(R.id.homeFL), view.findViewById(R.id.videoListView), true);
        tinerNavView.bringToFront();
        tinerNavView.navTextView.setText("Funny Video");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        pullToRefreshLayout = view.findViewById(R.id.videoListView);

        pullToRefreshLayout.setPullLoadEnable(true);
        View emptyView = view.findViewById(R.id.emptyview);
        pullToRefreshLayout.setEmptyView(emptyView);
        pullToRefreshLayout.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing == false) {
                    sendRequestWithHttpURLConnection(view.getContext(), false);
                }
            }

            @Override
            public void onLoadMore() {
                if (isRefreshing == false) {
                    sendRequestWithHttpURLConnection(view.getContext(), true);
                }
            }
        });
        loadVideoPB = view.findViewById(R.id.emptyviewLoadVideoPB);
        loadVideoIV = view.findViewById(R.id.emptyviewLoadVideoIV);
        loadVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideoPB.setVisibility(View.VISIBLE);
                loadVideoIV.setVisibility(View.INVISIBLE);
                sendRequestWithHttpURLConnection(view.getContext(), false);
            }
        });

        SharedPreferences sp = view.getContext().getSharedPreferences("SP_VIDEO_LIST", Activity.MODE_PRIVATE);//创建sp对象,如果有key为"SP_PEOPLE"的sp就取出
        String videoListJson = sp.getString("VIDEO_LIST", "");
        if (videoListJson != "") {
            Gson gson = new Gson();
            List<VideoModel> videoModelList = gson.fromJson(videoListJson, new TypeToken<LinkedList<VideoModel>>() {
            }.getType());
            if (videoModelList.size() == 0) {
                sendRequestWithHttpURLConnection(view.getContext(), false);
            } else {
                VideoListAdapter videoListAdapter = new VideoListAdapter(view.getContext(), videoModelList);
                pullToRefreshLayout.setAdapter(videoListAdapter);
            }
        } else {
            sendRequestWithHttpURLConnection(view.getContext(), false);
        }

        savedView = view;

        return view;
    }

    private void sendRequestWithHttpURLConnection(final Context context, final Boolean isLoadMore) {
        //开启线程来发起网络请求
        if (isLoadMore == false) {
            oldVideoModelList = new LinkedList<>();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    isRefreshing = true;
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL("http://101.200.77.107:6566/short-video/get-short-videos?index=0");
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
                        JSONArray videoArray = videoObjects.getJSONArray("result");
                        if (videoArray.length() > 0 && isLoadMore == false) {
                            SharedPreferences sp = context.getSharedPreferences("SP_VIDEO_LIST", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("VIDEO_LIST", videoArray.toString());
                            editor.commit();
                        }
                        Gson gson = new Gson();
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject videoObject = videoArray.getJSONObject(i);
                            VideoModel videoModel = gson.fromJson(videoObject.toString(), VideoModel.class);
                            oldVideoModelList.add(videoModel);
                        }
                        final List<VideoModel> videoModelList = new LinkedList<>();
                        videoModelList.addAll(oldVideoModelList);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isLoadMore) {
                                    HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) pullToRefreshLayout.getAdapter();
                                    VideoListAdapter videoListAdapter = (VideoListAdapter) hAdapter.getWrappedAdapter();
                                    videoListAdapter.mList.addAll(videoModelList);
                                    videoListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.v("videoListSize", "" + videoModelList.size());
                                    VideoListAdapter videoListAdapter = new VideoListAdapter(context, videoModelList);
                                    pullToRefreshLayout.setAdapter(videoListAdapter);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.v("网络超时", "网络超时");
                        e.printStackTrace();
                        try {
                            Thread.sleep(3000);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        loadVideoPB.setVisibility(View.INVISIBLE);
                        loadVideoIV.setVisibility(View.VISIBLE);
                    } finally {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                isRefreshing = false;
                                pullToRefreshLayout.stopRefresh();
                                pullToRefreshLayout.stopLoadMore();
                                pullToRefreshLayout.setRefreshTime("刚刚");
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
        //开启线程来发起网络请求
        if (isLoadMore == false) {
            oldVideoModelList = new LinkedList<>();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        isRefreshing = true;
                        Thread.sleep(30000);
                    } catch (Exception e) {
                        Log.v("网络超时", "网络超时");
                        e.printStackTrace();
                        try {
                            Thread.sleep(3000);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    } finally {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                isRefreshing = false;
                                pullToRefreshLayout.stopRefresh();
                                pullToRefreshLayout.stopLoadMore();
                                pullToRefreshLayout.setRefreshTime("刚刚");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
