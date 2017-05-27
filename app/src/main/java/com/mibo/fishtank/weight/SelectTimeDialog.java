package com.mibo.fishtank.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.mibo.fishtank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/23 0023.
 */

public class SelectTimeDialog extends Dialog {

    public SelectTimeDialog(Context context) {
        super(context);
    }

//    public SelectTimeDialog(Context context) {
//        this(context);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time_dialog_layout);

        initView();
    }

    private void initView() {
        PickerView minute_pv = (PickerView) findViewById(R.id.minute_pv);
        PickerView second_pv = (PickerView) findViewById(R.id.second_pv);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            data.add("0" + i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        minute_pv.setData(data);
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                Toast.makeText(getContext(), "选择了 " + text + " 分",
                        Toast.LENGTH_SHORT).show();
            }
        });
        second_pv.setData(seconds);
        second_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                Toast.makeText(getContext(), "选择了 " + text + " 秒",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
