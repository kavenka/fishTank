package com.mibo.fishtank.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mibo.fishtank.R;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class LoadingAddingDeviceView extends LinearLayout {

    private ImageView addingDeviceView;
    private float startAngle = 0f;

    public LoadingAddingDeviceView(Context context) {
        this(context, null);
    }

    public LoadingAddingDeviceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingAddingDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.adding_device_loading_layout, null);
        addingDeviceView = (ImageView) view.findViewById(R.id.adding_device_view);
        addView(view);
    }


    public void startAnim() {
        stopAnim();
        startViewAnim(0f, 1f, 1000);
    }

    public void stopAnim() {
        if (valueAnimator != null) {
            clearAnimation();
            valueAnimator.setRepeatCount(1);
            valueAnimator.cancel();
            valueAnimator.end();
        }
    }

    ValueAnimator valueAnimator;

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);

        valueAnimator.setDuration(time);
        valueAnimator.setTarget(addingDeviceView);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);//

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float) valueAnimator.getAnimatedValue();
                startAngle = 360 * value;

                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }

        return valueAnimator;
    }
}
