package com.example.djay.customview.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.djay.customview.R;

/**
 * Created by DJAY on 2017/3/21.
 */

public class CustomImageView extends View {
    private int mTextColor;
    private float mTextSize;
    private String mText;
    private Bitmap mImage;
    private int mImageScale;

    private Rect mRect;
    private Rect mTextRect;
    private Paint mPaint;

    private int width = 0;
    private int height = 0;

    private final static int IMAGE_SCALE_FILLXY = 0;
    private final static int IMAGE_SCALE_CENTER = 1;


    /**
     * 在构造函数中得到该控件所拥有的所有属性
     * @param context
     * @param attrs
     */
    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomImageView);
        mText = a.getString(R.styleable.CustomImageView_titleText);
        mTextSize = a.getDimension(
                R.styleable.CustomImageView_titleTextSize,15);
        mTextColor = a.getColor(
                R.styleable.CustomImageView_titleTextColor, Color.CYAN);
        mImage = BitmapFactory.decodeResource(
                getResources(),
                a.getResourceId(R.styleable.CustomImageView_image,0));

        mImageScale = a.getInt(R.styleable.CustomImageView_imageScaleType,0);
        a.recycle();

        //这个矩形是为了计算图片的位置
        mRect = new Rect();

        mTextRect = new Rect();
        mPaint = new Paint();
        //设置画笔的大小
        mPaint.setTextSize(mTextSize);
        //用这个画笔测量Text需要的范围
        mPaint.getTextBounds(mText,0,mText.length(),mTextRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        /**
         * 如果高度的模式是填充父控件或者指明数值，那么实际的宽度就是测量宽度
         * 如果是内容包裹，则该控件的宽度由最宽值(图片或者文字)和测量值决定
         */
        //布局中的宽度设置成Match_parent或者精确的数值
        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else{
            //由图片决定的宽
            int widthPicture = getPaddingLeft() + getPaddingRight() + mImage.getWidth();
            //由字体决定的宽
            int widthText = getPaddingLeft() + getPaddingRight() + mTextRect.width();

            //如果宽的布局设置的是内容包裹
            if (widthMode == MeasureSpec.AT_MOST){
                width = Math.min(Math.max(widthPicture,widthText),widthSize);
            }
        }

        /**
         * 如果高度设置成match_patch或者设置成精确值，那么高度就是测量值
         * 如果是内容包裹那么该控件的高度是图片+文字高度 和测量高度中的较大者
         */
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;

        }else{
            if(heightMode == MeasureSpec.AT_MOST){
                int desire = getPaddingTop() + getPaddingBottom()
                        + mImage.getHeight() + mTextRect.height();
                height = Math.min(desire,heightSize);
            }
        }
        setMeasuredDimension(width,height);


    }


    @Override
    protected void onDraw(Canvas canvas) {
      //  super.onDraw(canvas);

        //画图片和文字的边框
        mPaint.setStrokeWidth(4);//设置笔画的宽度，并不是字体大小
        mPaint.setStyle(Paint.Style.STROKE);//设置笔画为空心
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0,0,width,height,mPaint);

        //写字
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.STROKE);
        if(mTextRect.width() > width){
            TextPaint textPaint = new TextPaint(mPaint);
            //如果字体显示不下设置成在xxx...格式
            String msg = TextUtils.ellipsize(mText,textPaint,(float)width -
                    getPaddingRight() - getPaddingLeft(),
                    TextUtils.TruncateAt.END).toString();
            //开始画字
            canvas.drawText(msg,getPaddingRight(),height - getPaddingBottom(),mPaint);
        }else{
            //正常情况，将字体居中
            canvas.drawText(mText, width / 2 - mTextRect.width() * 1.0f / 2,
                    height - getPaddingBottom(), mPaint);
        }


        mRect.left = getPaddingLeft();
        mRect.top = getTop();
        mRect.right = width - getPaddingRight();
        mRect.bottom = height - getPaddingBottom() - mTextRect.height();
        //把原图按照指定的大小在View中显示，拉伸显示图片，不保持原比例，填满ImageView
        if(mImageScale == IMAGE_SCALE_FILLXY){
            canvas.drawBitmap(mImage,null,mRect,mPaint);
        }else{
            /*保持原图的大小，显示在ImageView的中心。
              当原图的size大于ImageView的size，超过部分裁剪处理。
            */
            //计算居中的矩形范围
            mRect.left = width / 2 - mImage.getWidth() / 2;
            mRect.right = width / 2 + mImage.getWidth() / 2;
            mRect.top = (height - mTextRect.height()) / 2 - mImage.getHeight() / 2;
            mRect.bottom = (height - mTextRect.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, mRect, mPaint);
        }


    }
}
