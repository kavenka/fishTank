package com.mibo.fishtank.weight;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mibo.fishtank.R;

/**
 * Created by Administrator
 * on 2017/5/22 0022.
 */

public class SelectHeadPicDialog extends AlertDialog {

    private View.OnClickListener onClickMakeBtn;
    private View.OnClickListener onClickSelectBtn;

    protected SelectHeadPicDialog(Context context) {
        super(context);
    }

    public SelectHeadPicDialog(Context context, View.OnClickListener onClickSelectBtn, View.OnClickListener onClickMakeBtn) {
        this(context);
        this.onClickSelectBtn = onClickSelectBtn;
        this.onClickMakeBtn = onClickMakeBtn;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pic_dialog_layout);

        initView();
    }

    private void initView() {
        TextView selectBtn = (TextView) findViewById(R.id.select_pic_btn);
        TextView makeBtn = (TextView) findViewById(R.id.select_make_btn);
        TextView cancelBtn = (TextView) findViewById(R.id.select_pic_cancel_btn);

        selectBtn.setOnClickListener(onClickSelectBtn);
        makeBtn.setOnClickListener(onClickMakeBtn);
        cancelBtn.setOnClickListener(new OnClickCancelListener());
    }

    private class OnClickCancelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }
}
