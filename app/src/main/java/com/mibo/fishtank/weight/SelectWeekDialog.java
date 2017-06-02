package com.mibo.fishtank.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mibo.fishtank.R;

import java.util.Calendar;

import static com.mibo.fishtank.R.style.dialog;

/**
 * Created by Monty on 2017/5/30.
 */

public class SelectWeekDialog extends Dialog {

    private TextView btnConfirm;
    private ListView mListView;

    private boolean[] checkBoxValue = new boolean[7];
    private String[] itemName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    private OnWeekSelectListener mOnWeekSelectListener;

    public SelectWeekDialog(Context context, boolean[] defaultValues, OnWeekSelectListener onWeekSelectListener) {
        super(context, dialog);
        this.checkBoxValue = defaultValues;
        this.mOnWeekSelectListener = onWeekSelectListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_week_dialog_layout);

        initView();

    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_week);

        mListView.setAdapter(new WeekAdapter());

        btnConfirm = (TextView) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnWeekSelectListener.onCheck(SelectWeekDialog.this, checkBoxValue);
            }
        });
    }

    public interface OnWeekSelectListener {
        void onCheck(SelectWeekDialog dialog, boolean[] value);
    }

    public void show(@NonNull int hour, @NonNull int minute) {
        super.show();
        if (hour > 24 || hour < 0 || minute > 60 || minute < 0) {
            new IllegalArgumentException("参数错误");
        }

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        int width = window.getWindowManager().getDefaultDisplay().getWidth();
        lp.width = width - (int) (width * 0.3);  // 宽度等于屏幕宽度减去屏幕宽度的30%
        window.setAttributes(lp);
    }

    @Override
    public void show() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        show(hour, minute);
    }

    class WeekAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return itemName.length;
        }

        @Override
        public Object getItem(int position) {
            return itemName[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.select_week_item_layout, null);

            TextView tvWeekName = (TextView) itemView.findViewById(R.id.tv_weekName);
            final CheckBox cBox = (CheckBox) itemView.findViewById(R.id.cbox);

            tvWeekName.setText(itemName[position]);
            tvWeekName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cBox.setChecked(!cBox.isChecked());
                }
            });
            cBox.setChecked(checkBoxValue[position]);
            cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkBoxValue[position] = isChecked;
                }
            });
            return itemView;
        }
    }

}
