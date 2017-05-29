package com.mibo.fishtank.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mibo.fishtank.entity.DeviceSwitch;
import com.mibo.fishtank.weight.DeviceSwitch.DeviceSwitchView;

import java.util.List;

/**
 * Created by Monty on 2017/5/29.
 */

public class DeviceSwitchAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<DeviceSwitch> mDeviceSwitches = null;

    public DeviceSwitchAdapter(Context context, List<DeviceSwitch> switchList) {
        this.mContext = context;
        this.mDeviceSwitches = switchList;

    }

    @Override
    public int getCount() {
        return mDeviceSwitches.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceSwitches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceSwitch deviceSwitch = mDeviceSwitches.get(position);
        DeviceSwitchView deviceSwitchView = new DeviceSwitchView(mContext);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.gravity = Gravity.CENTER;
//        deviceSwitchView.setLayoutParams(lp);
        deviceSwitchView.setSwitchTitle(deviceSwitch.name);
        deviceSwitchView.setSwitch(deviceSwitch.isOpen);
        return deviceSwitchView;
    }

    public void notifyDataSetChanged(List<DeviceSwitch> deviceSwich) {
        mDeviceSwitches = deviceSwich;
        super.notifyDataSetChanged();
    }
}
