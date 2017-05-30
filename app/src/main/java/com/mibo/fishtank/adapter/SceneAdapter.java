package com.mibo.fishtank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Scene;

import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/31 0031.
 */

public class SceneAdapter extends BaseAdapter {
    private Context context;
    private List<Scene> scenes;

    public SceneAdapter(Context context, List<Scene> scenes) {
        this.context = context;
        this.scenes = scenes;
    }

    @Override
    public int getCount() {
        return scenes.size();
    }

    @Override
    public Object getItem(int position) {
        return scenes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        SceneViewHolder sceneViewHolder = null;
        AddBtnViewHolder addBtnViewHolder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            switch (type) {
                case 0:
                    convertView = layoutInflater.inflate(R.layout.scene_item_layout, null);
                    sceneViewHolder = new SceneViewHolder();
                    sceneViewHolder.sceneBtn = (TextView) convertView.findViewById(R.id.scene_name);
                    sceneViewHolder.sceneBtn.setText(scenes.get(position).getName());
                    convertView.setTag(sceneViewHolder);
                    break;
                case 1:
                    convertView = layoutInflater.inflate(R.layout.scene_add_layout, null);
                    addBtnViewHolder = new AddBtnViewHolder();
                    addBtnViewHolder.addBtn = (LinearLayout) convertView.findViewById(R.id.add_new_scene_layout);
                    convertView.setTag(addBtnViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    sceneViewHolder = (SceneViewHolder) convertView.getTag();
                    sceneViewHolder.sceneBtn.setText(scenes.get(position).getName());
                    break;
            }

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == scenes.size() - 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class SceneViewHolder {
        TextView sceneBtn;
    }

    private class AddBtnViewHolder {
        LinearLayout addBtn;
    }
}
