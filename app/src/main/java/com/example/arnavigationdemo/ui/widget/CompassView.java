package com.example.arnavigationdemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.arnavigationdemo.R;

/**
 * Created by ming on 2018/8/23.
 * 指南针控件，类似刺激战场的方位控件
 * 传入方向角度 0～360
 * 360/0表示正北，90表示正东，180表示正南，270表示正西
 */

public class CompassView extends View {
    private int indicatorColor = Color.WHITE;
    private int showDegree = 60; //页面一共展示方位的度数，默认60
    private int textSize;
    private int scaleWidth = 4; //刻度宽度,默认4px
    private Paint mPaint;
    private int widthPerDegree;
    private int mWidth; //控件的宽
    private int mHeight; //控件的高
    private int mCurrentDegree; //当前手机正对方向的度数
    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CompassView, defStyleAttr, 0);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CompassView_indicator_color:
                    indicatorColor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.CompassView_show_degree:
                    showDegree = a.getInteger(attr, 60);
                    break;
                case R.styleable.CompassView_text_size:
                    textSize = a.getDimensionPixelOffset(attr, 30);
                    break;
                case R.styleable.CompassView_scale_width:
                    scaleWidth = a.getDimensionPixelOffset(attr, 4);
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
        mPaint.setColor(indicatorColor);
        if(textSize >0 ){
            mPaint.setTextSize(textSize);
        }
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = widthSize / 2;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = heightSize / 2;
        }
        mWidth = widthSize;
        mHeight = heightSize;
        widthPerDegree = widthSize / showDegree;
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offset = mCurrentDegree % 5;
        //向左绘制
        for(int i=0; (i*5+offset)<=showDegree/2; i++){
            int templeDegree = mCurrentDegree - (i*5 + offset);
            if(templeDegree > 360){
                templeDegree = templeDegree - 360;
            }
            if(templeDegree < 0){
                templeDegree = templeDegree + 360;
            }
            int paintPosition = mWidth / 2 - (i*5 + offset) * widthPerDegree;
            if(getDirectionName(templeDegree) != null){
                canvas.drawRect(paintPosition-scaleWidth/2, 0, paintPosition+scaleWidth/2, getHeight() / 3, mPaint);
                canvas.drawText(getDirectionName(templeDegree),paintPosition, mHeight, mPaint);
            }else {
                canvas.drawRect(paintPosition-scaleWidth/2, 0, paintPosition+scaleWidth/2, getHeight() / 5, mPaint);
                canvas.drawText(String.valueOf(templeDegree),paintPosition, mHeight, mPaint);
            }
        }
        //向右绘制
        for(int i=0; (i*5+5-offset)<=showDegree/2; i++){
            int templeDegree = mCurrentDegree + (i*5+5-offset);
            if(templeDegree > 360){
                templeDegree = templeDegree - 360;
            }
            if(templeDegree < 0){
                templeDegree = templeDegree + 360;
            }
            int paintPosition = mWidth / 2 + (i*5+5-offset) * widthPerDegree;
            if(getDirectionName(templeDegree) != null){
                canvas.drawRect(paintPosition-scaleWidth/2, 0, paintPosition+scaleWidth/2, getHeight() / 3, mPaint);
                canvas.drawText(getDirectionName(templeDegree),paintPosition, mHeight, mPaint);
            }else {
                canvas.drawRect(paintPosition-scaleWidth/2, 0, paintPosition+scaleWidth/2, getHeight() / 5, mPaint);
                canvas.drawText(String.valueOf(templeDegree),paintPosition, mHeight, mPaint);
            }
        }
    }

    private String getDirectionName(int degree){
        String directionName = null;
        if(degree == 0 || degree == 360){
            directionName = "正北";
        }else if(degree == 45){
            directionName = "东北";
        }else if(degree == 90){
            directionName = "正东";
        }else if(degree == 135){
            directionName = "东南";
        }else if(degree == 180){
            directionName = "正南";
        }else if(degree == 90){
            directionName = "西南";
        }else if(degree == 270){
            directionName = "正西";
        }else if(degree == 315){
            directionName = "西北";
        }
        return directionName;
    }

    public void setCurrentDegree(int degree){
        this.mCurrentDegree = degree;
        postInvalidate();
    }
}
