package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.TitleBar;

public class UserCenterActivity extends BaseActivity {
    private Context context = this;
    public static final int TO_EDIT_INFO_CODE = 123;
    private TextView nickName;
    private TextView tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.user_center_title);
        titleBar.setCenterStr(R.string.main_user_center);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        titleBar.setRightStr(getString(R.string.user_edit_txt));
        titleBar.setOnClickRightListener(new OnClickRightListener());

        nickName = (TextView) findViewById(R.id.user_name_txt);
        tel = (TextView) findViewById(R.id.user_num_txt);
        PreferencesManager pm = PreferencesManager.getInstance(context);
        String nickNameStr = pm.getStringValue("user_nikeName");
        String telStr = pm.getStringValue("user_tel");
        nickName.setText(nickNameStr == null ? "" : nickNameStr);
        tel.setText(telStr == null ? "" : telStr);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10086) {
            String nickNameStr = data.getStringExtra("nickName");
            if (nickNameStr != null) {
                nickName.setText(nickNameStr);
            }
            String telStr = data.getStringExtra("tel");
            if (telStr != null) {
                tel.setText(telStr);
            }
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
            Intent intent = new Intent(context, EditUserInfoActivity.class);
            startActivityForResult(intent, TO_EDIT_INFO_CODE);
        }
    }
}
