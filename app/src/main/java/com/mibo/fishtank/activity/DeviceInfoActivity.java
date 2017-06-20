package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceInfoActivity extends BaseActivity {

    private String uid;
    private EditText pwdEdit;
    private Button delectDeviceBtn;
    private String sceneId;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.activity_devide_info);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        sceneId = intent.getStringExtra("sceneId");
        initView();
    }

    private void initView() {
        Device device = DataBaseManager.queryDevice(uid);
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_info_title);
        titleBar.setCenterStr(R.string.device_in_net);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickRightListener());

        loadingDialog = new LoadingDialog(context, "正在删除...");

        TextView typeTxt = (TextView) findViewById(R.id.type_txt);
        TextView uidTxt = (TextView) findViewById(R.id.uid_txt);
        TextView changShanTxt = (TextView) findViewById(R.id.changshan_txt);
        TextView xingHaoTxt = (TextView) findViewById(R.id.xinghao_txt);
        TextView timeTxt = (TextView) findViewById(R.id.time_txt);
        pwdEdit = (EditText) findViewById(R.id.pwd_txt);
        delectDeviceBtn = (Button) findViewById(R.id.delete_device_btn);
        delectDeviceBtn.setOnClickListener(new OnClickDelectListener());

        typeTxt.setText("智能排插");
        uidTxt.setText(device.getUid());
        changShanTxt.setText("1");
        xingHaoTxt.setText(device.getModel());
        timeTxt.setText(device.getTime());

        pwdEdit.setText(device.getDevPwd());


    }

    /**
     * 添加设备回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSceneEvent(AddOrUpSceneEvent event) {
        loadingDialog.close();
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }
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

    private class OnClickDelectListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FishTankUserApiManager.getInstance().toDelectDevice(uid, sceneId, pwdEdit.getText().toString());
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
