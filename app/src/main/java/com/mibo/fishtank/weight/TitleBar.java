package com.mibo.fishtank.weight;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;

/**
 * Created by Administrator
 * on 2017/5/8 0008.
 */

public class TitleBar extends RelativeLayout {

    private ImageView leftImg;
    private TextView centerStr;
    private TextView rightTxt;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        RelativeLayout titleBar = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.title_bar_layout, this, false);
        leftImg = (ImageView) titleBar.findViewById(R.id.title_left_img);
        centerStr = (TextView) titleBar.findViewById(R.id.title_center_txt);
        rightTxt = (TextView) titleBar.findViewById(R.id.title_right_txt);
        addView(titleBar);
    }

    public void setLeftImgRes(@DrawableRes int resId) {
        leftImg.setImageResource(resId);
    }

    public void setOnClickLeftListener(View.OnClickListener onClickListener) {
        leftImg.setOnClickListener(onClickListener);
    }

    public void setCenterStr(@StringRes int str) {
        centerStr.setText(str);
    }

    public void setCenterStr(String str) {
        centerStr.setText(str);
    }

    public void setRightStr(String str) {
        rightTxt.setText(str);
    }

    public void setOnClickRightListener(View.OnClickListener onClickListener) {
        rightTxt.setOnClickListener(onClickListener);
    }
}
