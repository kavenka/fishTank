package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.TitleBar;

public class DeviceInfoActivity extends BaseActivity {

    private String uid;
    private EditText pwdEdit;
    private Button btnDeleteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_info);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        initView();
    }

    private void initView() {
        Device device = DataBaseManager.queryDevice(uid);
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_info_title);
        titleBar.setCenterStr(R.string.device_in_net);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickRightListener());

        TextView typeTxt = (TextView) findViewById(R.id.type_txt);
        TextView uidTxt = (TextView) findViewById(R.id.uid_txt);
        TextView changShanTxt = (TextView) findViewById(R.id.changshan_txt);
        TextView xingHaoTxt = (TextView) findViewById(R.id.xinghao_txt);
        TextView timeTxt = (TextView) findViewById(R.id.time_txt);
        pwdEdit = (EditText) findViewById(R.id.pwd_txt);

        typeTxt.setText("智能排插");
        uidTxt.setText(device.getUid());
        changShanTxt.setText("1");
        xingHaoTxt.setText(device.getModel());
        timeTxt.setText(device.getTime());

        pwdEdit.setText(device.getDevPwd());

        btnDeleteDevice = (Button) findViewById(R.id.btn_deleteDevice);
        btnDeleteDevice.setOnClickListener(new OnDeleteBtnClickListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickRightListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FishTankUserApiManager.getInstance().toUpdateDevice(uid, pwdEdit.getText().toString());
            finish();
        }
    }

    private class OnDeleteBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO: 2017/6/20 删除设备
        }
    }
}
