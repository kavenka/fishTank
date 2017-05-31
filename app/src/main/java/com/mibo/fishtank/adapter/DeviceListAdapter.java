package com.mibo.fishtank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;

import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/26 0026.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {
    private List<Device> devices;
    private Context context;
    private View.OnClickListener onClickListener;

    public DeviceListAdapter(Context context, List<Device> devices, View.OnClickListener onClickListener) {
        this.context = context;
        this.devices = devices;
        this.onClickListener = onClickListener;
    }

    @Override
    public DeviceListAdapter.DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_item_layout, parent, false);
        return new DeviceListAdapter.DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.DeviceHolder holder, int position) {
        holder.tv.setText(devices.get(position).getUid());
        holder.layout.setTag(position);
        holder.layout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    class DeviceHolder extends RecyclerView.ViewHolder {
        TextView tv;
        RelativeLayout layout;

        public DeviceHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.device_name);
            layout = (RelativeLayout) view.findViewById(R.id.device_name_layout);
        }
    }
}
