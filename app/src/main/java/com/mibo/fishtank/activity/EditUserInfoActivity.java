package com.mibo.fishtank.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.SelectHeadPicDialog;
import com.mibo.fishtank.weight.TitleBar;

public class EditUserInfoActivity extends BaseActivity {
    public static final int GET_IMAGE = 111;
    public static final int START_THUMBNAIL = 112;

    private ImageView headPicImg;
    private SelectHeadPicDialog selectHeadPicDialog;
    private EditText nameEdit;
    private EditText numEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_info_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.edit_user_info_title);
        titleBar.setCenterStr(R.string.edit_user_info);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        RelativeLayout headPicLayout = (RelativeLayout) findViewById(R.id.edit_user_head_linear);
        headPicImg = (ImageView) findViewById(R.id.edit_user_head_img);
        nameEdit = (EditText) findViewById(R.id.user_info_name_edit);
        numEdit = (EditText) findViewById(R.id.user_info_num_edit);
        PreferencesManager pm = PreferencesManager.getInstance(context);
        String nickNameStr = pm.getStringValue("nikeName");
        String telStr = pm.getStringValue("tel");
        nameEdit.setText(nickNameStr == null ? "" : nickNameStr);
        numEdit.setText(telStr == null ? "" : telStr);
        Button confirmBtn = (Button) findViewById(R.id.edit_confirm_btn);
        headPicLayout.setOnClickListener(new OnClickHeadPicListener());
        confirmBtn.setOnClickListener(new OnClickConfirmListener());

        selectHeadPicDialog = new SelectHeadPicDialog(context, new OnClickSelectBtn(), new OnClickMakeBtn());
        Window window = selectHeadPicDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == GET_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    //加载图片
    private void showImage(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        headPicImg.setImageBitmap(bm);
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickHeadPicListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectHeadPicDialog.show();
        }
    }

    private class OnClickSelectBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GET_IMAGE);
            selectHeadPicDialog.dismiss();
        }
    }

    private class OnClickMakeBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, START_THUMBNAIL);
            selectHeadPicDialog.dismiss();
        }
    }

    private class OnClickConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            PreferencesManager pm = PreferencesManager.getInstance(context);
            String nickName = nameEdit.getText().toString();
            pm.setStringValue("nikeName", nickName);
            String tel = numEdit.getText().toString();
            pm.setStringValue("tel", tel);
            Toast.makeText(context, R.string.reset_psw_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("nickName", nickName);
            intent.putExtra("tel", tel);
            setResult(10086, intent);
            finish();
        }
    }
}
