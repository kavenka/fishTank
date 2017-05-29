package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceDetailActivity extends BaseActivity {

    private CloudApi mCloudApi;
    private LoadingDialog loadingDialog;
    private String mDstDevUid = "5CCF7F07B170";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        initSDK();
        initView();
        loadingDialog.show();
        mCloudApi.GetDevCfg();

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        loadingDialog.show();
        FishTankApiManager.getInstance().loginDevice(mDstDevUid);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event){
        if(event.loginSuccess){
            FishTankApiManager.getInstance().getDeviceParam(mDstDevUid);
        }else{
            Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
            loadingDialog.close();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetParameterLstener(GetParameterEvent event){
        IFishTankApi.MsgGetParamRsp msgGetParamRsp = event.msgGetParamRsp;
        String uid = event.uid;
        int result = event.result;
        if(result==0){
            FishTankApiManager.getInstance().getDeviceParam(mDstDevUid);
        }else{
            Toast.makeText(this,"获取失败",Toast.LENGTH_SHORT).show();
        }
        loadingDialog.close();
    }


    /**
     * 初始化SDK
     */
    private void initSDK() {
        mCloudApi = new CloudApi();

        mCloudApi.SetHandler(mHandler);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_detail_title);
        titleBar.setCenterStr(R.string.set_params_title);
        titleBar.setRightStr("设置");

        loadingDialog = new LoadingDialog(context, "加载中");

        titleBar.setOnClickRightListener(new OnClickEditListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickEditListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SetParamsActivity.class);
            startActivity(intent);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudApi.MSG_WHAT_CLOUDAPI:
                    Log.d("monty", "arg2:" + msg.arg2);
                    switch (msg.arg1) { // 判断操作类型
                        case CloudApi.GETDEVCFG:
                            if (IFishTankError.SUCCESS == msg.arg2) { // 判断操作结果
                                Toast.makeText(context, "huoqu chenggong", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case CloudApi.ERROR:
                            Toast.makeText(context, R.string.login_failure, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
            loadingDialog.close();
        }
    };
}
