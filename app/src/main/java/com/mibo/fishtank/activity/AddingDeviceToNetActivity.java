package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.view.View;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.AddingLoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

public class AddingDeviceToNetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_device_to_net_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.adding_device_title);
        titleBar.setCenterStr(R.string.device_in_net);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        AddingLoadingDialog dialog = new AddingLoadingDialog(this);
        dialog.show();

    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
