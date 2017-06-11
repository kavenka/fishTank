package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mibo.fishtank.R;

/**
 * ph校准页面
 */
public class PhCorrectActivity extends BaseActivity {

    private TextView tvPHValue;
    private TextView tvTips;
    private EditText etStandard;
    private Button btnNext;

    private String uid;

    public static Intent BuildIntent(Context context,String uid){
        Intent intent = new Intent(context,PhCorrectActivity.class);
        intent.putExtra("uid",uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_correct_layout);

        getIntentData();

        initView();
    }

    private void initView() {
        tvPHValue = (TextView) findViewById(R.id.tv_Ph);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        etStandard = (EditText)findViewById(R.id.et_standard_value);
        btnNext = (Button) findViewById(R.id.btn_next);
    }

    private void getIntentData() {
        String uid = getIntent().getStringExtra("uid");
        if(TextUtils.isEmpty(uid)){
            Toast.makeText(this,"设备异常",Toast.LENGTH_LONG).show();
        }
        this.uid = uid;
    }
}
