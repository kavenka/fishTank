package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mibo.fishtank.R;
import com.mibo.fishtank.adapter.DeviceListAdapter;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.weight.TitleBar;

import java.util.ArrayList;

public class SearchNewDeviceActivity extends BaseActivity {

    private ArrayList<Device> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_new_device_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.search_device_title);
        titleBar.setCenterStr(R.string.device_list);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_device_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: 2017/5/26 0026 获取到的设备列表
        devices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Device device = new Device();
            device.setDeviceName("value" + i);
            devices.add(device);
        }
        DeviceListAdapter adapter = new DeviceListAdapter(context, devices,new OnClickDeviceItemListener());
        recyclerView.setAdapter(adapter);

    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickDeviceItemListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            Intent intent = new Intent(context, DeviceInNetActivity.class);
            intent.putExtra("sceneName", devices.get(pos).getDeviceName());
            startActivity(intent);
        }
    }
}
