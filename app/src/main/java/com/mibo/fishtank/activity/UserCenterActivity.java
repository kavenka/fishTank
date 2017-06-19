package com.mibo.fishtank.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.GlideUtils;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.TitleBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class UserCenterActivity extends BaseActivity {
    private Context context = this;
    public static final int TO_EDIT_INFO_CODE = 123;
    private TextView nickName;
    private TextView tel;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_activity);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlideUtils.showUserIcon(this, imageView);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.user_center_title);
        titleBar.setCenterStr(R.string.main_user_center);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        titleBar.setRightStr(getString(R.string.user_edit_txt));
        titleBar.setOnClickRightListener(new OnClickRightListener());

        nickName = (TextView) findViewById(R.id.user_name_txt);
        tel = (TextView) findViewById(R.id.user_num_txt);
        imageView = (ImageView) findViewById(R.id.user_head_img);
//        GlideUtils.showUserIcon(this,imageView);
//        User user = DataBaseManager.queryUser(Constans.CURRENT_TEL);

        PreferencesManager pm = PreferencesManager.getInstance(context);
        String nickNameStr = pm.getStringValue("nikeName");
        String telStr = pm.getStringValue("tel");
        this.nickName.setText(TextUtils.isEmpty(nickNameStr) ? "尚未设置" : nickNameStr);
        this.tel.setText(TextUtils.isEmpty(telStr) ? "尚未设置" : telStr);

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

            AndPermission.with(UserCenterActivity.this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            AndPermission.rationaleDialog(context, rationale).show();
                        }
                    })
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            Intent intent = new Intent(context, EditUserInfoActivity.class);
                            startActivityForResult(intent, TO_EDIT_INFO_CODE);
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
//                    Toast.makeText(UserCenterActivity.this,"请先授权，否则无法修改个人信息",Toast.LENGTH_SHORT).show();
                            AndPermission.defaultSettingDialog(UserCenterActivity.this, 400)
                                    .setTitle("权限申请失败")
                                    .setMessage("您拒绝了我们必要的一些权限，已经没法愉快的玩耍了，请在设置中授权！")
                                    .setPositiveButton("好，去设置")
                                    .show();
                        }
                    }).start();
        }
    }


}
