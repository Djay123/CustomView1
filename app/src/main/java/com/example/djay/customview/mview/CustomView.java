package com.example.djay.customview.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.icu.util.Measure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.djay.customview.R;

import java.util.Random;


/**
 * Created by DJAY on 2017/3/19.
 */

public class CustomView extends View {
    private static final String TAG = CustomView.class.getSimpleName();
    private String text;
    private float textSize;
    private int textColor;
    private Paint mPain;
    private Rect mRect;


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                text = "value:" + new Random().nextInt(10);
                postInvalidate();
            }
        });
    }
    private void init(Context context, AttributeSet attrs){
        mPain = new Paint();
        mRect = new Rect();
        initAttrs(context,attrs);
        /**
         * 功能：通过这个方法来获取尺寸的时候，你可以得到能够包裹文字的最小矩形
         * 参数1：需要测量的字符串
         * 参数2：从哪个字符串开始
         * 参数3：结束位置
         * 参数4：得到的最小矩形
         * 这里的Rect对象坐标并不是以左上角为准的，而是相对于左边中间靠下位置的一个点
         * mRect.bottom == 1
         * mRect.left == 0
         * mRect.right == 53
         * mRect.top == -9
         */
        mPain.getTextBounds(text,0,text.length(),mRect);
    }
    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.test);
        text = a.getString(R.styleable.test_titleText);
        textColor = a.getColor(R.styleable.test_titleTextColor, Color.BLUE);
        //textSize的类型必须设置成float
        textSize = a.getDimension(R.styleable.test_titleTextSize,25);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heigthtSize = MeasureSpec.getSize(heightMeasureSpec);
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int heigth;

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthMeasureSpec;
        }else{
            mPain.setTextSize(textSize);
            mPain.getTextBounds(text,0,text.length(),mRect);
            width = getPaddingLeft() + getPaddingRight() + mRect.width();

        }
        if(heigthMode == MeasureSpec.EXACTLY){
            heigth = widthMeasureSpec;
        }else{
            mPain.setTextSize(textSize);
            mPain.getTextBounds(text,0,text.length(),mRect);
            heigth = getPaddingTop() + getPaddingBottom() + mRect.height();

        }
        setMeasuredDimension(width,heigth);
    }

    /**
     *  getWidth(): View在设定好布局后整个View的宽度。
        getMeasuredWidth(): 对View上的内容进行测量后得到的View内容佔据的宽度，
        前提是你必须在父布局的onLayout()方法或者此View的onDraw()
        方法里调用measure(0,0);(measure 参数的值你可以自己定义)，
        否则你得到的结果和getWidth()得到的结果一样。
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int measWidth = getMeasuredWidth();
        int measHeight = getMeasuredHeight();
        int width = getWidth();
        int height = getHeight();

        Log.i(TAG,"measWidth:" + measWidth +
                  "measHeight:" + measHeight +
                  "width:" + width +
                  "height:" + height +
                  "mRect.width:" + mRect.width() +
                  "mRect.height:" + mRect.height());

        mPain.setColor(Color.GREEN);
        //用mPain画出来一个矩形
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPain);

        mPain.setColor(textColor);
        mPain.setTextSize(textSize);
        //在画布上用mPain写字
        canvas.drawText(text,getWidth() / 2 - mRect.width() / 2, getHeight() / 2 + mRect.height() / 2, mPain);

    }
}
