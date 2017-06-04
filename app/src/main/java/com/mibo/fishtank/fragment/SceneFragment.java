package com.mibo.fishtank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.activity.AddNewDeviceActivity;
import com.mibo.fishtank.activity.DeviceDetailActivity;
import com.mibo.fishtank.adapter.DeviceAdapter;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.DataBaseManager;

import java.util.ArrayList;


public class SceneFragment extends BaseFragment {

    private String sceneId;
    private ArrayList<String> devices;
    private DeviceAdapter adapter;
    private ArrayList<Device> deviceList;

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
        ListView deviceListView = (ListView) view.findViewById(R.id.device_list_view);
        deviceListView.setOnItemClickListener(new OnClickSceneItemListener());
        deviceList = DataBaseManager.queryAllDevice(this.devices);
        deviceList.add(new Device());
        adapter = new DeviceAdapter(context, deviceList);
        deviceListView.setAdapter(adapter);

        return view;
    }

    private class OnClickSceneItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == deviceList.size() - 1) {
                Intent intent = new Intent(context, AddNewDeviceActivity.class);
                intent.putExtra("sceneId", sceneId);
                startActivity(intent);
            } else {
                Intent intent = DeviceDetailActivity.BuildIntent(context, deviceList.get(position).getUid());
                startActivity(intent);
            }
        }
    }
}
