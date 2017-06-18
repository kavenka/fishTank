package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.DevCfgEvent;
import com.mibo.fishtank.FishTankmManage.event.UserLoginOutEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.adapter.SceneFragmentAdapter;
import com.mibo.fishtank.entity.DevCfgEntity;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.entity.User;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.utils.GlideUtils;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private Context context = this;
    private boolean isExit = false;
    private DrawerLayout drawerLayout;
    private LoadingDialog loadingDialog;
    private SceneFragmentAdapter adapter;
    private ArrayList<Scene> scenes;
    private TitleBar titleBar;
    private ViewPager viewPager;
    private TextView nickNameTv;
    private ImageView ivUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        getSceneAndDeviceData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlideUtils.showUserIcon(this,ivUserIcon);
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.main_title);
        titleBar.setLeftImgRes(R.drawable.gengd);
        titleBar.setOnClickLeftListener(new OnClickLeftBtnListener());

        loadingDialog = new LoadingDialog(context, "正在获取设备信息...");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        scenes = new ArrayList<>();
        adapter = new SceneFragmentAdapter(getSupportFragmentManager(), scenes);
        viewPager.addOnPageChangeListener(new OnPagerChange());
        viewPager.setAdapter(adapter);

        User user = DataBaseManager.queryUser(Constans.CURRENT_TEL);
        nickNameTv = (TextView) findViewById(R.id.id_draw_menu_item_login_tv);
        nickNameTv.setText(TextUtils.isEmpty(user.getUserName()) ? Constans.CURRENT_TEL : user.getUserName());

        ivUserIcon = (ImageView) findViewById(R.id.imageView);
//        GlideUtils.showUserIcon(this,ivUserIcon);
        LinearLayout sceneSettingLinear = (LinearLayout) findViewById(R.id.main_scene_setting);
        sceneSettingLinear.setOnClickListener(new OnClickSceneSettingListener());
        LinearLayout userCenterLinear = (LinearLayout) findViewById(R.id.main_user_center);
        userCenterLinear.setOnClickListener(new OnClickUserCenterListener());
        LinearLayout editPwdLinear = (LinearLayout) findViewById(R.id.main_edit_password);
        editPwdLinear.setOnClickListener(new OnClickSetPswListener());
        TextView loginOutTxt = (TextView) findViewById(R.id.main_login_out);
        loginOutTxt.setOnClickListener(new OnClickLoginOutListener());

    }

    private void getSceneAndDeviceData() {
        loadingDialog.show();
        FishTankUserApiManager.getInstance().toGetDevCfg();//获取设备信息和场景信息
    }

    /**
     * 当前用户下的设备信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDevCfgEvent(DevCfgEvent event) {
        loadingDialog.close();
        Message msg = event.msg;
        if (IFishTankError.SUCCESS == msg.arg2) {
            String resultJson = msg.obj.toString();
            Log.d("monty", "MainActivity -> onGetDevCfgEvent -> resultJson:" + resultJson);
            DevCfgEntity devCfgEntity = new DevCfgEntity();
            devCfgEntity.parserEntity(resultJson);
            scenes.clear();
            List<Scene> dataBaseScene = DataBaseManager.queryAllScene();
            if (dataBaseScene.size() == 0) {//如果没有场景，默认添加一个客厅的场景
                FishTankUserApiManager.getInstance().toAddScene("客厅");
                return;
            }
            scenes.addAll(dataBaseScene);//解析好的场景实体集合添加到viewPager中
            adapter.notifyDataSetChanged();
            titleBar.setCenterStr(scenes.get(0).getName());
            if (scenes.size() == 1) {

            }
        } else {
            Toast.makeText(context, "获取场景和设备信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当用户登录的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginEvent(UserLoginOutEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            PreferencesManager pm = PreferencesManager.getInstance(context);
            pm.setStringValue("pwd", "");
            pm.setBooleanValue("isLoginSuccess", false);
            Toast.makeText(context, R.string.log_out_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LaunchActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 当用户信息修改的监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserChangeEvent(User user) {
        nickNameTv.setText(TextUtils.isEmpty(user.getUserName()) ? Constans.CURRENT_TEL : user.getUserName());
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
            FishTankUserApiManager.getInstance().toSignOut();
        }
    }


    private class OnPagerChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            titleBar.setCenterStr(scenes.get(position).getName());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
