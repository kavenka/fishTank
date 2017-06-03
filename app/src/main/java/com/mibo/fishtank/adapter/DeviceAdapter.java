package com.mibo.fishtank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;

import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/31 0031.
 */

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<Device> devices;

    public DeviceAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        DeviceViewHolder deviceViewHolder= null;
        AddBtnViewHolder addBtnViewHolder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            switch (type) {
                case 0:
                    convertView = layoutInflater.inflate(R.layout.devices_main_item_layout, null);
                    deviceViewHolder = new DeviceViewHolder();
                    deviceViewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
//                    deviceViewHolder.deviceBtn = (LinearLayout) convertView.findViewById(R.id.device_layout);
                    deviceViewHolder.deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
                    deviceViewHolder.deviceName.setText(devices.get(position).getUid());
                    convertView.setTag(deviceViewHolder);
                    break;
                case 1:
                    convertView = layoutInflater.inflate(R.layout.device_add_layout, null);
                    addBtnViewHolder = new AddBtnViewHolder();
                    addBtnViewHolder.addBtn = (LinearLayout) convertView.findViewById(R.id.add_new_device_layout);
                    convertView.setTag(addBtnViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    deviceViewHolder = (DeviceViewHolder) convertView.getTag();
                    deviceViewHolder.deviceName.setText(devices.get(position).getUid());
                    break;
            }

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == devices.size() - 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class DeviceViewHolder {
        LinearLayout deviceBtn;
        TextView deviceName;
        ImageView deviceImg;
    }

    private class AddBtnViewHolder {
        LinearLayout addBtn;
    }
}
