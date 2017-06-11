package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * ph校准页面
 */
public class PhCorrectActivity extends BaseActivity {

    private TextView tvPHValue;
    private TextView tvTips;
    private EditText etStandard;
    private Button btnNext;
    private TextView tvTip;
    private LoadingDialog loadingDialog;

    private String uid;

    private boolean isStep1; // 是否已经到了第二步

    private float phStandardValue1;
    private float phStandardValue2;

    private float phRealValue1;
    private float phRealValue2;

    private float phRealValue;

    public static Intent BuildIntent(Context context, String uid) {
        Intent intent = new Intent(context, PhCorrectActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacks(getPhRunable);
        getPhRunable = null;
        myHandler = null;
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetParameterLstener(GetParameterEvent event) {
        IFishTankApi.MsgGetParamRsp msgGetParamRsp = event.msgGetParamRsp;
        String uid = event.uid;
        int result = event.result;
        if (result == 0) {
            Log.d("monty", "ph实测值：" + msgGetParamRsp.Ph);
            phRealValue = msgGetParamRsp.Ph;
            tvPHValue.setText(msgGetParamRsp.Ph + "");
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
            tvPHValue.setText("- -");
            myHandler.removeCallbacks(getPhRunable);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetPhCal(SetParamsEvent event) {
        if (event.result == 0) {
            Toast.makeText(this, "校准成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "校准失败", Toast.LENGTH_SHORT).show();
            myHandler.postDelayed(getPhRunable, 1000);
        }
        loadingDialog.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_correct_layout);
        getIntentData();
        initView();

        myHandler.postDelayed(getPhRunable, 1000);

    }

    private Runnable getPhRunable = new Runnable() {
        @Override
        public void run() {
            FishTankApiManager.getInstance().getPhParam(uid);
            myHandler.postDelayed(getPhRunable, 1000);
        }
    };

    private Handler myHandler = new Handler();

    private void initView() {
        loadingDialog = new LoadingDialog(this, "正在校准...");
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_in_net_title);
        titleBar.setCenterStr("PH探头校准");
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvPHValue = (TextView) findViewById(R.id.tv_Ph);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        etStandard = (EditText) findViewById(R.id.et_standard_value);
        btnNext = (Button) findViewById(R.id.btn_next);
        tvTip = (TextView) findViewById(R.id.tips) ;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStep1) { // 第一次点击
                    if (TextUtils.isEmpty(etStandard.getText())) {
                        Toast.makeText(PhCorrectActivity.this, "请输入标准值", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isStep1 = true;
                    tvTips.setText(R.string.ph_correct_step2);
                    btnNext.setText("完成");
                    phStandardValue1 = Float.valueOf(etStandard.getText().toString());
                    phRealValue1 = phRealValue;
                    etStandard.getText().clear();
                    tvTip.setText("第一组数据：标准值："+phStandardValue1+"，实测值："+phRealValue1 );
                } else { // 第二次点击
                    phStandardValue2 = Float.valueOf(etStandard.getText().toString());
                    phRealValue2 = phRealValue;
                    loadingDialog.show();
                    myHandler.removeCallbacks(getPhRunable);
                    Float[] phCal = new Float[]{phRealValue1, phStandardValue1, phRealValue2, phStandardValue2};
                    FishTankApiManager.getInstance().setPhCal(uid, phCal);

                }
            }
        });
    }

    private void getIntentData() {
        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_LONG).show();
        }
        this.uid = uid;
    }
}
