package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.TitleBar;

public class MainActivity extends BaseActivity {
    private Context context = this;
    private boolean isExit = false;
    private DrawerLayout drawerLayout;
    private CloudApi mCloudApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        initSDK();
    }

    private void initSDK() {
        mCloudApi = new CloudApi();
        mCloudApi.SetHandler(mHandler);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.main_title);
        titleBar.setCenterStr(R.string.main_title_txt);
        titleBar.setLeftImgRes(R.drawable.gengd);
        titleBar.setOnClickLeftListener(new OnClickLeftBtnListener());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        PreferencesManager pm = PreferencesManager.getInstance(context);
        String nikeName = pm.getStringValue("user_nikeName");
        TextView nickNameTv = (TextView) findViewById(R.id.id_draw_menu_item_login_tv);
        nickNameTv.setText(TextUtils.isEmpty(nikeName) ? "" : nikeName);
        LinearLayout sceneSettingLinear = (LinearLayout) findViewById(R.id.main_scene_setting);
        sceneSettingLinear.setOnClickListener(new OnClickSceneSettingListener());
        LinearLayout userCenterLinear = (LinearLayout) findViewById(R.id.main_user_center);
        userCenterLinear.setOnClickListener(new OnClickUserCenterListener());
        LinearLayout editPwdLinear = (LinearLayout) findViewById(R.id.main_edit_password);
        editPwdLinear.setOnClickListener(new OnClickSetPswListener());
        TextView loginOutTxt = (TextView) findViewById(R.id.main_login_out);
        loginOutTxt.setOnClickListener(new OnClickLoginOutListener());
        LinearLayout paiChaLinear = (LinearLayout) findViewById(R.id.pai_cha_layout);
        paiChaLinear.setOnClickListener(new OnClickPaiChaListener());
        LinearLayout addNewDeviceLayout = (LinearLayout) findViewById(R.id.add_new_device_layout);
        addNewDeviceLayout.setOnClickListener(new OnClickNewDeviceListener());

    }

    private class OnClickLeftBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private class OnClickSceneSettingListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SceneSettingActivity.class);
            startActivity(intent);
        }
    }

    private class OnClickUserCenterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, UserCenterActivity.class);
            startActivity(intent);
        }
    }

    private class OnClickSetPswListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SetUserPwdActivity.class);
            startActivity(intent);
        }
    }

    private class OnClickLoginOutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mCloudApi.SignOut();
        }
    }

    private class OnClickPaiChaListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DeviceDetailActivity.class);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), R.string.press_again_finish, Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudApi.MSG_WHAT_CLOUDAPI:
                    switch (msg.arg1) {
                        case CloudApi.LOGOUT: {
                            if (IFishTankError.SUCCESS == msg.arg2) {
                                String data = msg.obj.toString();
                                Log.i("TAG", "Rsp=" + data);
                                Toast.makeText(context, R.string.log_out_success, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LaunchActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        break;
                        case CloudApi.ERROR:
                            Toast.makeText(context, R.string.log_out_failed, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, LaunchActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                    }
                    break;
                case 0:
                    isExit = false;
                    break;
            }
        }
    };
}
