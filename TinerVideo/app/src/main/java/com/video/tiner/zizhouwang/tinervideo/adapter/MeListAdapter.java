package com.video.tiner.zizhouwang.tinervideo.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.MeModel;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerShareView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zizhouwang on 2018/7/11.
 */

public class MeListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MeModel> mList;

    public MeListAdapter(Context context, List<MeModel> list, ListView listView) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    // 返回指定索引对应的数据项
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MeListAdapter.ViewHolder viewHolder;
        if (convertView == null) {// View未被实例化，即缓冲池中无缓存才创建View
            // 将控件id保存在viewHolder中
            viewHolder = new MeListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.me_list_item, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MeListAdapter.ViewHolder) convertView.getTag();
        }

        MeModel bean = mList.get(position);
        if (bean.itemIconId == -1) {
            viewHolder.itemTitleIV.setVisibility(View.GONE);
        } else {
            viewHolder.itemTitleIV.setVisibility(View.VISIBLE);
            viewHolder.itemTitleIV.setImageResource(bean.itemIconId);
        }
        viewHolder.itemTitleTV.setText(bean.itemTitle);
        if (bean.itemIconId == -1) {
            viewHolder.itemTitleIV.setImageResource(R.drawable.right_arrow);
        } else {
            viewHolder.itemTitleIV.setImageResource(bean.itemRightIconId);
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView itemTitleIV;
        public TextView itemTitleTV;
        public ImageView itemRightIV;
    }
}
