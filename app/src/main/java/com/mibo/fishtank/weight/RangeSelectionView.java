package com.mibo.fishtank.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mibo.fishtank.R;

import java.math.BigDecimal;

/**
 * Created by Monty on 2017/5/27.
 */
public class RangeSelectionView extends View {

    private static final String TAG = "RangeSelectionView";

    private Paint paintBackground;//背景线的画笔
    private Paint paintCircle;//起始点圆环的画笔
    private Paint paintWhileCircle;//起始点内圈白色区域的画笔
    private Paint paintText;//起始点数值的画笔
    private Paint paintConnectLine;//起始点连接线的画笔

    private int height = 0;//控件的高度
    private int width = 0;//控件的宽度

    private float lineY = 0;//y轴的中间位置

    private float backlineWidth = 5;//底线的宽度

    private float marginhorizontal = 80;//横向边距

    private float textY = 0;//文字距顶部的距离

    private float textHeight = 0;//文字高度
    private float textWidth = 0;//文字高度

    private float pointStart = 0;//起点的X轴位置

    private float pointEnd = 0;//始点的Y轴位置

    private float circleRadius = 25;//起始点圆环的半径

    private float numStart = 0;//数值的开始值

    private float numEnd = 0;//数值的结束值

    private int textSize = 30;//文字的大小

    private String strUnit = "";//刻度的单位

    private boolean isRunning = false;//是否可以滑动

    private boolean isStart = true;//起点还是终点 true：起点；false：终点。


    public RangeSelectionView(Context context) {
        super(context);
        init();
    }

    public RangeSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RangeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("TAG", "onMeasure**************************************");

        //获取控件的宽高、中线位置、起始点、起始数值
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        // 计算文字高度
        Paint.FontMetrics fontMetrics = paintText.getFontMetrics();
        textHeight = (int) (Math.ceil(fontMetrics.descent - fontMetrics.ascent) + 2);


        // 计算整个绘画区域离控件上下的距离
        float margin = (height - (textHeight + circleRadius + 10)) / 2;
        // 计算线的y坐标
        lineY = margin + textHeight + 10 + (circleRadius / 2);
        // 文本的Y坐标
        textY = margin + 10;

        pointStart = marginhorizontal;
        pointEnd = width - marginhorizontal;

        numStart = getProgressNum(pointStart / 2);

        numEnd = getProgressNum(pointEnd / 2);

        showLog("height:" + height + "textHeight:" + textHeight + ",textY:" + textY + ",lineY:" + lineY + ",circleRadius:" + circleRadius);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果点击的点在第一个圆内就是起点,并且可以滑动
                if (event.getX() >= (pointStart - circleRadius) && event.getX() <= (pointStart + circleRadius)) {
                    isRunning = true;
                    isStart = true;

                    pointStart = event.getX();
                    //如果点击的点在第二个圆内就是终点,并且可以滑动
                } else if (event.getX() <= (pointEnd + circleRadius) && event.getX() >= (pointEnd - circleRadius)) {
                    isRunning = true;
                    isStart = false;

                    pointEnd = event.getX();
                } else {
                    //如果触控点不在圆环内，则不能滑动
                    isRunning = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRunning) {
                    if (isStart) {
                        //起点滑动时，重置起点的位置和进度值
                        pointStart = event.getX();
                        numStart = getProgressNum(pointStart);
                    } else {
                        //始点滑动时，重置始点的位置和进度值
                        pointEnd = event.getX();
                        numEnd = getProgressNum(pointEnd);
                    }

                    flushState();//刷新状态
                }

                break;
            case MotionEvent.ACTION_UP:

                flushState();
                break;
        }

        return true;
    }

    /**
     * 刷新状态和屏蔽非法值
     */
    private void flushState() {

        //起点非法值
        if (pointStart < marginhorizontal) {
            pointStart = marginhorizontal;
        }
        //终点非法值
        if (pointEnd > width - marginhorizontal) {
            pointEnd = width - marginhorizontal;
        }

        //防止起点位置大于终点位置（规定：如果起点位置大于终点位置，则将起点位置放在终点位置前面,即：终点可以推着起点走，而起点不能推着终点走）
        if (pointStart + circleRadius > pointEnd - circleRadius) {

            pointStart = pointEnd - 2 * circleRadius;

        }

        //防止终点把起点推到线性范围之外
        if (pointEnd < marginhorizontal + 2 * circleRadius) {
            pointEnd = marginhorizontal + 2 * circleRadius;
            pointStart = marginhorizontal;
        }

        invalidate();//这个方法会导致onDraw方法重新绘制

    }

    //进度范围
    float beginNum = 0;
    float endNum = 1000;

    //计算进度数值
    private float getProgressNum(float progress) {

        return (int) progress / (width - 2 * marginhorizontal) * (endNum - beginNum);

    }

    //初始化画笔
    private void init() {

        paintBackground = new Paint();
        paintBackground.setColor(getResources().getColor(R.color.colorBackline));
        paintBackground.setStrokeWidth(backlineWidth);
        paintBackground.setAntiAlias(true);

        paintCircle = new Paint();
        paintCircle.setColor(getResources().getColor(R.color.colorTipCircle));
        paintCircle.setStrokeWidth(backlineWidth);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);

        paintWhileCircle = new Paint();
        paintWhileCircle.setColor(getResources().getColor(R.color.white));
        paintCircle.setStyle(Paint.Style.FILL);
        paintWhileCircle.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(getResources().getColor(R.color.white));
        paintText.setTextSize(textSize);
        paintText.setAntiAlias(true);

        paintConnectLine = new Paint();
        paintConnectLine.setColor(getResources().getColor(R.color.colorTipCircle));
        paintConnectLine.setStrokeWidth(backlineWidth + 5);
        paintConnectLine.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        showLog("textHeight:" + textHeight + ",textY:" + textY + ",lineY:" + lineY + ",circleRadius:" + circleRadius);
        //背景线
        canvas.drawLine(marginhorizontal, lineY, width - marginhorizontal, lineY, paintBackground);
        //起点位置的外圈圆
        canvas.drawCircle(pointStart, lineY, circleRadius, paintCircle);
        //起点位置的内圈圆
        canvas.drawCircle(pointStart, lineY, circleRadius - backlineWidth, paintWhileCircle);
        //终点位置的外圈圆
        canvas.drawCircle(pointEnd, lineY, circleRadius, paintCircle);
        //终点位置的内圈圆
        canvas.drawCircle(pointEnd, lineY, circleRadius - backlineWidth, paintWhileCircle);
        //起始点连接线
        canvas.drawLine(pointStart + circleRadius, lineY, pointEnd - circleRadius, lineY, paintConnectLine);
        Rect rect = new Rect();
        paintText.getTextBounds(numStart + "", 0, (numStart + "").length(), rect);
        textWidth = rect.width();
        float textX = pointStart - textWidth / 2;
        //起点数值
        canvas.drawText(format(numStart), textX, textY, paintText);
        paintText.getTextBounds(numEnd + "", 0, (numEnd + "").length(), rect);
        textWidth = rect.width();
        textX = pointEnd - textWidth / 2;
        //终点数值
        canvas.drawText(format(numEnd) + strUnit, textX, textY, paintText);

    }

    String format(float f){
        BigDecimal b = new BigDecimal(f);
        return  b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue()+"";

    }

    void showLog(String msg) {
        Log.d(TAG, msg);
    }
}
