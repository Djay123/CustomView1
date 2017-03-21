package com.example.djay.customview.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.djay.customview.R;

/**
 * Created by DJAY on 2017/3/22.
 */

public class CustomProgressBar extends View {
    private int mFirstColor;
    private int mSecondColor;
    private float mCircleWidth;
    private int mSpeed;
    private int currentProgress;
    private Paint mPaint;

    /**
     * 是否应该开始下一个
     */
    private boolean isNext = false;
    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);

        // 绘图线程
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    currentProgress++;
                    if (currentProgress == 360)
                    {
                        currentProgress = 0;
                        if (!isNext)
                            isNext = true;
                        else
                            isNext = false;
                    }
                    postInvalidate();
                    try
                    {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    private void init(Context context,AttributeSet attrs){
        //把类和属性CustomProgressBar关联起来
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomProgressBar);
        mFirstColor = a.getColor(R.styleable.CustomProgressBar_firstColor,
                Color.GREEN);
        mSecondColor = a.getColor(R.styleable.CustomProgressBar_secondColor,
                Color.BLUE);
        mCircleWidth = a.getDimension(R.styleable.CustomProgressBar_circleWidth,
                5);
        mSpeed = a.getInt(R.styleable.CustomProgressBar_speed,20);
        a.recycle();

        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // super.onDraw(canvas);

        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = centre / 2 - (int) mCircleWidth / 2;// 半径
        mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心
        // 用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(
                centre - radius,
                centre - radius,
                centre + radius,
                centre + radius);
        if (!isNext)
        {// 第一颜色的圈完成，第二颜色跑
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            /**
             * 参数1：指定圆弧的外轮廓矩形区域。
             * 参数2：圆弧起始角度，单位为度。
             * 参数3：圆弧扫过的角度，顺时针方向，单位为度。
             * 参数4：如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
             * 参数5：绘制圆弧的画板属性，如颜色，是否填充等。
             */
            canvas.drawArc(oval, -90, currentProgress, true, mPaint); // 根据进度画圆弧
        } else
        {
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawArc(oval, -90, currentProgress, true, mPaint); // 根据进度画圆弧
        }
    }
}
