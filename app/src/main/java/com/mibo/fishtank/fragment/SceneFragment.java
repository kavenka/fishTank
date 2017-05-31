package com.mibo.fishtank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mibo.fishtank.R;
import com.mibo.fishtank.activity.AddNewDeviceActivity;
import com.mibo.fishtank.activity.DeviceDetailActivity;


public class SceneFragment extends BaseFragment {

    public SceneFragment() {

    }

    public static SceneFragment newInstance() {
        return new SceneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        LinearLayout addDevice = (LinearLayout) view.findViewById(R.id.add_new_device_layout);
        addDevice.setOnClickListener(new OnClickNewDeviceListener());
        LinearLayout paiChaLayout = (LinearLayout) view.findViewById(R.id.pai_cha_layout);
        paiChaLayout.setOnClickListener(new OnClickPaiChaListener());

        return view;
    }

    private class OnClickPaiChaListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = DeviceDetailActivity.BuildIntent(context, "");
            startActivity(intent);
        }
    }

    private class OnClickNewDeviceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddNewDeviceActivity.class);
            startActivity(intent);
        }
    }

}
