package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

public class DeviceDetailActivity extends BaseActivity {

    private CloudApi mCloudApi;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        initSDK();
        initView();
        loadingDialog.show();
        mCloudApi.GetDevCfg();
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
                    switch (msg.arg1) {
                        case CloudApi.GETDEVCFG:
                            if (IFishTankError.SUCCESS == msg.arg2) {
                                Toast.makeText(context, "huoqu chenggong", Toast.LENGTH_SHORT).show();  

                            } else {
                                loadingDialog.close();
                                Toast.makeText(context, R.string.login_failure, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case CloudApi.ERROR:
                            loadingDialog.close();
                            Toast.makeText(context, R.string.login_failure, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };
}
