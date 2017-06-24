package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private boolean isStep2; // 是否已经到了第二步

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
            myHandler.postDelayed(getPhRunable, 1000);
            if (TextUtils.isEmpty(event.msg)) {
                Toast.makeText(this, "校准失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, event.msg, Toast.LENGTH_SHORT).show();
            }
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
        etStandard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("montyp", "beforeTextChanged,s:" + s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("montyp", "onTextChanged,s:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("montyp", "afterTextChanged,s:" + s.toString());
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") >1) {

                        etStandard.setText(s.toString().subSequence(0,
                                s.toString().indexOf(".") +2));

                        etStandard.setSelection(s.toString().trim().length()-1
                        );
                    }
                }
                //这部分是处理如果用户输入以.开头，在前面加上0
                if (s.toString().trim().substring(0).equals(".")) {

                    etStandard.setText("0"+s);
                    etStandard.setSelection(2);
                }
                //这里处理用户 多次输入.的处理 比如输入 1..6的形式，是不可以的
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etStandard.setText(s.subSequence(0, 1));
                        etStandard.setSelection(1);
                        return;
                    }
                }

                if(!TextUtils.isEmpty(etStandard.getText().toString())){
                    double value = Double.parseDouble(etStandard.getText().toString());
                    if(value>14.0){
                        etStandard.setText(14.0+"");
                        etStandard.setSelection(etStandard.length());
                    }
                }
            }
        });
        btnNext = (Button) findViewById(R.id.btn_next);
        tvTip = (TextView) findViewById(R.id.tips);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStep2) { // 第一次点击
                    if (TextUtils.isEmpty(etStandard.getText())) {
                        Toast.makeText(PhCorrectActivity.this, "请输入标准值", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    phStandardValue1 = Float.valueOf(etStandard.getText().toString());
                    if (!checkPhValue(phStandardValue1)) {
                        return;
                    }

                    isStep2 = true;
                    tvTips.setText(R.string.ph_correct_step2);
                    btnNext.setText("完成");
                    phRealValue1 = phRealValue;
                    etStandard.getText().clear();
                    tvTip.setText("第一组数据：标准值：" + phStandardValue1 + "，实测值：" + phRealValue1);
                } else { // 第二次点击
                    phStandardValue2 = Float.valueOf(etStandard.getText().toString());
                    if (!checkPhValue(phStandardValue2)) {
                        return;
                    }
                    phRealValue2 = phRealValue;
                    loadingDialog.show();
                    myHandler.removeCallbacks(getPhRunable);
                    Float[] phCal = new Float[]{phStandardValue1, phRealValue1, phStandardValue2, phRealValue2};
                    FishTankApiManager.getInstance().setPhCal(uid, phCal);

                }
            }
        });
    }

    /**
     * 检查输入的标准值是否合法
     *
     * @param value
     */
    private boolean checkPhValue(float value) {
        if (value < 0 || value > 14) {
            Toast.makeText(PhCorrectActivity.this, "您输入的标准值不合法，合法范围0～14", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void getIntentData() {
        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_LONG).show();
        }
        this.uid = uid;
    }
}
