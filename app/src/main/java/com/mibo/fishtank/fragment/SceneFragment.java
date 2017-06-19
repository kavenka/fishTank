package com.mibo.fishtank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.activity.AddNewDeviceActivity;
import com.mibo.fishtank.activity.DeviceDetailActivity;
import com.mibo.fishtank.activity.DeviceInfoActivity;
import com.mibo.fishtank.adapter.DeviceAdapter;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.utils.DataBaseManager;

import java.util.ArrayList;


public class SceneFragment extends BaseFragment {

    private String sceneId;
    private ArrayList<String> devices;
    private DeviceAdapter adapter;
    private ArrayList<Device> deviceList;
    private int size;

    public SceneFragment() {

    }

    public static SceneFragment newInstance() {
        return new SceneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        sceneId = bundle.getString("sceneId");
        devices = bundle.getStringArrayList("devices");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
//        LinearLayout addDevice = (LinearLayout) view.findViewById(R.id.add_new_device_layout);
//        addDevice.setOnClickListener(new OnClickNewDeviceListener());
//        LinearLayout paiChaLayout = (LinearLayout) view.findViewById(R.id.pai_cha_layout);
//        paiChaLayout.setOnClickListener(new OnClickPaiChaListener());
        deviceList = DataBaseManager.queryAllDevice(this.devices);
        size = deviceList.size();
        ListView deviceListView = (ListView) view.findViewById(R.id.device_list_view);
        deviceListView.setOnItemClickListener(new OnClickSceneItemListener());
        deviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (size != 0) {
                    Intent intent = new Intent(context, DeviceInfoActivity.class);
                    intent.putExtra("uid", devices.get(position));
                    startActivity(intent);
                }
                return true;
            }
        });
        Log.d("monty", "deviceList:" + deviceList.toString());
        if (deviceList != null && deviceList.size() == 0) {
            deviceList.add(new Device());
        }
        adapter = new DeviceAdapter(context, deviceList);
        deviceListView.setAdapter(adapter);

        return view;
    }

    private class OnClickSceneItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (size == 0) {
                Intent intent = new Intent(context, AddNewDeviceActivity.class);
                intent.putExtra("sceneId", sceneId);
                startActivity(intent);
            } else {
                Scene scene = DataBaseManager.querySceneBySceneId(sceneId);
                Intent intent = DeviceDetailActivity.BuildIntent(context, deviceList.get(position).getUid(), scene.getName()+"_智能排插");
                startActivity(intent);
            }
        }
    }
}
