package com.example.djay.customview.mview;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.djay.customview.R;
import com.example.djay.customview.tools.Tools;

import java.util.Random;

/**
 * Created by DJAY on 2017/5/7.
 */

public class CustomDialProgress extends View{

    public static final String TAG = "CustomCircleProgress";

    private SweepGradient mSweepGradient;
    //起始角度
    private int startAngles;
    //默认的起始角度为0度,结束角度为360度。以三点钟方向为0度，顺时针为正，逆时针为负
    public static final int DEFAULTSTART = 0;
    public static final int DEFAULTEND = 360;

    //结束的角度
    private int endAngles;

    //最大值。默认为10000
    private float mMaxValue;
    public static final int DEFAULTMAXVALUE= 300;
    public static final int DEFAULTRADIUS = 100;
    //圆的宽度
    private float widthCircle;

    public static final int DEFAULTWIDTH = 5;


    //提示文字的画笔
    private Paint hitPaint;

    //值得画笔
    private Paint valuePaint;

    //单位的画笔
    private Paint unitPaint;

    //未达到的画笔
    private Paint firstPaint;

    //进度值得画笔
    private Paint secondePaint;

    //当前的进度值
    private float value;

    //圆心的坐标
    private Point centerPoint;

    //圆的半径
    private int radius;

    //圆的外接矩形
    private RectF mRectFCircle;

    //刻度的数量
    private int mCountDial;
    public static final int DEFAULTCOUNT = 20;


    private float offsetHit;
    private float offsetValue;
    private float offsetUnit;

    //字体之间的间距是半径的mTextOffsetPercentInRadius倍
    public static final float mTextOffsetPercentInRadius = 0.33f;

    //字体的大小是半径的mTextPercentInradius倍
    public static final float mTextPercentInradius = 0.2f;

    //圆环的宽度是半径的mWidthCirclePercentInradius倍
    public static final float mWidthCirclePercentInradius = 0.15f;
    //动画默认持续的时间
    public static final int DEFAULT_ANIM_TIME = 1000;

    //当前的值是最大值的百分比，取值范围[0.1f,1.0f]
    private float mPercent;

    //属性动画
    private ValueAnimator mAnimator;


    //画刻度的画笔
    private Paint mDialPaint;
    //画刻度盘上的小刻度的画笔的宽度
    public static final int dialPaintWidth = 4;
    //刻度之间的度数
    public static final int dialAngle = 5;

    private Random mRandom;
    public CustomDialProgress(Context context) {
        super(context);
    }

    public CustomDialProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomDialProgress);
        startAngles = a.getInteger(R.styleable.CustomDialProgress_startAngles,
                DEFAULTEND);
        endAngles = a.getInteger(R.styleable.CustomDialProgress_endAngles,
                DEFAULTEND);
        widthCircle = (int) a.getDimension(R.styleable.CustomDialProgress_circleWidth,
                DEFAULTWIDTH);

        radius = (int)a.getDimension(R.styleable.CustomDialProgress_radius,
                DEFAULTRADIUS);
        mMaxValue = a.getFloat(R.styleable.CustomDialProgress_maxValue,DEFAULTMAXVALUE);

        mCountDial = a.getInteger(R.styleable.CustomDialProgress_count,DEFAULTCOUNT);
        a.recycle();





        widthCircle = radius * mWidthCirclePercentInradius;
        initTools();
        setValue(0);
        initPaint();
        mRandom = new Random();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                float radm = mRandom.nextFloat() * mMaxValue;
                setValue(radm);
            }
        });
    }
    private void initTools() {
        centerPoint = new Point();
        mRectFCircle = new RectF();
    }

    private void initPaint() {
        hitPaint = new Paint();
        //设置抗锯齿
        hitPaint.setAntiAlias(true);
        hitPaint.setTextSize(mTextPercentInradius * radius);
        //设置画笔宽度
        hitPaint.setStrokeWidth(5f);
        hitPaint.setColor(Color.BLACK);
        hitPaint.setStyle(Paint.Style.FILL);
        // 从中间向两边绘制，不需要再次计算文字
        hitPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint = new Paint();
        valuePaint.setTextSize(mTextPercentInradius * radius);
        //设置抗锯齿
        valuePaint.setAntiAlias(true);
        //设置画笔宽度
        valuePaint.setStrokeWidth(5f);
        valuePaint.setColor(Color.RED);
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        // 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
        valuePaint.setTypeface(Typeface.DEFAULT_BOLD);



        unitPaint = new Paint();
        unitPaint.setTextSize(mTextPercentInradius * radius);
        //设置抗锯齿
        unitPaint.setAntiAlias(true);
        //设置画笔宽度
        unitPaint.setStrokeWidth(5f);
        unitPaint.setColor(Color.GREEN);
        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setTextAlign(Paint.Align.CENTER);

        firstPaint = new Paint();
        firstPaint.setAntiAlias(true);
        firstPaint.setStrokeWidth(widthCircle);
        firstPaint.setColor(Color.parseColor("#F0808080"));
        firstPaint.setStyle(Paint.Style.STROKE);

        secondePaint = new Paint();
        secondePaint.setAntiAlias(true);
        secondePaint.setStrokeWidth(widthCircle);
        secondePaint.setColor(Color.parseColor("#F0808080"));
        secondePaint.setStyle(Paint.Style.STROKE);

        //刻度的画笔
        mDialPaint = new Paint();
        mDialPaint.setAntiAlias(true);
        mDialPaint.setStrokeWidth(dialPaintWidth);
        mDialPaint.setColor(Color.WHITE);


    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerPoint.x = w/2;
        centerPoint.y = h/2;

        mRectFCircle.left = centerPoint.x - radius - widthCircle/2;
        mRectFCircle.top = centerPoint.y - radius - widthCircle/2;
        mRectFCircle.right = centerPoint.x + radius + widthCircle/2;
        mRectFCircle.bottom = centerPoint.y + radius + widthCircle/2;

        /**
         * 计算文字的偏移
         */
        offsetValue = centerPoint.y + Tools.measureTextHeight(valuePaint)/2;
        offsetHit = centerPoint.y - radius * mTextOffsetPercentInRadius +  Tools.measureTextHeight(hitPaint) / 2;
        offsetUnit = centerPoint.y + radius * mTextOffsetPercentInRadius + Tools.measureTextHeight(unitPaint) / 2;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
        drawdial(canvas);

    }

    private void drawdial(Canvas canvas) {
        int total = endAngles / dialAngle;
        canvas.save();
        canvas.rotate(startAngles,centerPoint.x,centerPoint.y);
        for (int i = 0; i <= total; i++){
            canvas.drawLine(
                    centerPoint.x + radius - widthCircle / 2,
                    centerPoint.y,
                    centerPoint.x + radius + widthCircle,
                    centerPoint.y,
                    mDialPaint);
            canvas.rotate(dialAngle,centerPoint.x,centerPoint.y);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        if(hitPaint != null){
            canvas.drawText("当前速度",centerPoint.x,offsetHit,hitPaint);
        }
        if(valuePaint != null){
            canvas.drawText((int)(value) + "",centerPoint.x,offsetValue,valuePaint);
        }
        if (unitPaint != null){
            canvas.drawText("km/h",centerPoint.x,offsetUnit,unitPaint);
        }
    }
    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.rotate(startAngles,centerPoint.x,centerPoint.y);
        //当前的角度
        float currentAngles = mPercent * endAngles;
        canvas.drawArc(mRectFCircle,currentAngles,endAngles - currentAngles + 2,false,firstPaint);
        updateArcPaint();
        canvas.drawArc(mRectFCircle,2,currentAngles,false,secondePaint);
        canvas.restore();
    }

    private void setValue(float value){
        if(value > DEFAULTMAXVALUE){
            this.value = DEFAULTMAXVALUE;
        }else{
            this.value = value;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start,end,DEFAULT_ANIM_TIME);

    }

    private void startAnimator(float start,float end,long animTime){
        mAnimator = ValueAnimator.ofFloat(start,end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float)mAnimator.getAnimatedValue();
                value = mPercent * mMaxValue;
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 更新圆弧画笔
     */
    private void updateArcPaint() {
        // 设置渐变
        int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
        // 围绕点(mCenterPoint.x, mCenterPoint.y)扫描梯度渲染
        mSweepGradient = new SweepGradient(centerPoint.x, centerPoint.y, mGradientColors, null);
        secondePaint.setShader(mSweepGradient);
    }


}
