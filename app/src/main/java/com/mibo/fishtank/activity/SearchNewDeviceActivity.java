package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.ScanDevEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.adapter.DeviceListAdapter;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 搜索设备列表
 */
public class SearchNewDeviceActivity extends BaseActivity {

    private ArrayList<Device> devices;
    private LoadingDialog loadingDialog;
    private DeviceListAdapter adapter;
    private String sceneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.search_new_device_activity);
        sceneId = getIntent().getStringExtra("sceneId");
        initView();
        FishTankApiManager.getInstance().toScanDev();
    }


    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.search_device_title);
        titleBar.setCenterStr(R.string.device_list);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        loadingDialog = new LoadingDialog(context, "正在搜索设备...");
        loadingDialog.show();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_device_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        devices = new ArrayList<>();
        adapter = new DeviceListAdapter(context, devices, new OnClickDeviceItemListener());
        recyclerView.setAdapter(adapter);

    }

    /**
     * 扫描设备回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanDevEvent(ScanDevEvent event) {
        IFishTankApi.MsgScanDevRsp msgScanDevRsp = event.msgScanDevRsp;
        loadingDialog.close();
        Device device = new Device();
        device.setHWVer(msgScanDevRsp.HWVer);
        device.setIp(msgScanDevRsp.Ip);
        device.setModel(msgScanDevRsp.Model);
        device.setPort(msgScanDevRsp.Port);
        device.setSWVer(msgScanDevRsp.SWVer);
        device.setUid(msgScanDevRsp.Uid);
        devices.add(device);
        adapter.notifyDataSetChanged();
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
            Intent intent = new Intent(context, AddDeviceActivity.class);
            intent.putExtra("Uid", devices.get(pos).getUid());
            intent.putExtra("Model", devices.get(pos).getModel());
            intent.putExtra("sceneId", sceneId);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
