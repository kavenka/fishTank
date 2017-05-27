package com.mibo.fishtank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Scene;

import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/26 0026.
 */

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneHolder> {
    private List<Scene> scenes;
    private Context context;
    private View.OnClickListener onClickListener;

    public SceneAdapter(Context context, List<Scene> scenes, View.OnClickListener onClickListener) {
        this.context = context;
        this.scenes = scenes;
        this.onClickListener = onClickListener;
    }

    @Override
    public SceneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scene_item_layout, parent, false);
        return new SceneHolder(view);
    }

    @Override
    public void onBindViewHolder(SceneHolder holder, int position) {
        holder.tv.setText(scenes.get(position).getSceneName());
        holder.tv.setTag(position);
        holder.tv.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return scenes.size();
    }


    class SceneHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public SceneHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.scene_name);
        }
    }
}


