package com.example.djay.customview.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.djay.customview.R;

/**
 *
 * Created by DJAY on 2017/3/22.
 */

public class CustomControlBar extends View {
    private int mFirstColor;
    private int mSencondColor;
    private float mCircleWidth;
    private int mCount;
    private int mSplitSize;
    private Bitmap mBackground;

    private Paint mPaint;
    private int progress = 3;
    private Rect mRect;
    public CustomControlBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomControlBar);
        mFirstColor = a.getColor(R.styleable.CustomControlBar_firstColor,
                Color.GRAY);
        mSencondColor = a.getColor(R.styleable.CustomControlBar_secondColor,
                Color.GREEN);
        mCircleWidth = a.getDimension(R.styleable.CustomControlBar_circleWidth,
                5);
        mCount = a.getInt(R.styleable.CustomControlBar_count,36);
        mSplitSize = a.getInt(R.styleable.CustomControlBar_splitSize,20);
        mBackground = BitmapFactory.decodeResource(getResources(),
                a.getResourceId(R.styleable.CustomControlBar_image,0));
        a.recycle();

        mPaint = new Paint();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        mRect = new Rect();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStrokeWidth(mCircleWidth);//设置笔画宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND);//定义线段断电形状为圆头
        mPaint.setStyle(Paint.Style.STROKE);//设置为空心
        int center = getWidth() / 2;//获得圆心得X坐标
        int r = (int)(center - mCircleWidth / 2);//半径
        //画小块块
        drawOval(canvas,center,r);

        //内层圆的半径
        int rIn = (int)(center - mCircleWidth);
        //内接正方形的左边坐标
        mRect.left = (int)(rIn - (Math.sqrt(2) * 1.0f / 2) * rIn + mCircleWidth);
        //内接正方形的上边坐标
        mRect.bottom = (int)(rIn - (Math.sqrt(2) * 1.0f / 2) * rIn + mCircleWidth);
        //内接正方形的右边坐标
        mRect.right = (int)(mRect.left + Math.sqrt(2)  * 1.0f * rIn );
        //内接正方形的下边坐标
        mRect.right = (int)(mRect.left + Math.sqrt(2)  * 1.0f * rIn);

        int wRect = (int)(Math.sqrt(2) * 1.0 * rIn);

        //如果图片的宽度小于圆的内接正方形的边长，则让该图片放在中间显示
        if(mBackground.getWidth() < wRect){
//            mRect.left = (int) (mRect.left + Math.sqrt(2) * rIn * 1.0f / 2 -
//                    mBackground.getWidth() * 1.0f / 2);
//            mRect.top = (int) (mRect.top + Math.sqrt(2) * rIn * 1.0f / 2 -
//                    mBackground.getHeight() * 1.0f / 2);
//            mRect.right = mRect.left + mBackground.getWidth();
//            mRect.bottom = mRect.top + mBackground.getHeight();
            mRect.left = getWidth() / 2 - mBackground.getWidth() / 2;
            mRect.top = getHeight() / 2 - mBackground.getHeight() / 2;
            mRect.right = getWidth() / 2 + mBackground.getWidth() / 2;
            mRect.bottom = getHeight() / 2 + mBackground.getHeight() / 2;

        }
        canvas.drawBitmap(mBackground,null,mRect,mPaint);

    }

    //根据参数画出每个小块
    private void drawOval(Canvas canvas,int center,int r){
        //根据需要画的个数以及间隙计算每个块块所占的比例*360
        float itemSize = (360 - mCount * mSplitSize) / mCount;
        //用于定义的圆弧的形状和大小的界限
        RectF mRectF = new RectF(center - r,center - r,
                center + r,center + r);
        mPaint.setColor(mFirstColor);
        for (int i = 0; i < mCount; i++){
            canvas.drawArc(mRectF,i * (itemSize + mSplitSize),
                    itemSize,false,mPaint);
        }
        mPaint.setColor(mSencondColor);
        for(int i = 0; i < progress; i++){
            canvas.drawArc(mRectF,i * (itemSize + mSplitSize),
                    itemSize,false,mPaint);
        }
    }

    /**
     * 当前数量+1
     */
    public void up()
    {
        progress++;
        postInvalidate();
    }

    /**
     * 当前数量-1
     */
    public void down()
    {
        progress--;
        postInvalidate();
    }

    private int xDown, xUp;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getY();
                break;

            case MotionEvent.ACTION_UP:
                xUp = (int) event.getY();
                if (xUp > xDown)// 下滑
                {
                    down();
                } else
                {
                    up();
                }
                break;
        }

        return true;
    }
}

