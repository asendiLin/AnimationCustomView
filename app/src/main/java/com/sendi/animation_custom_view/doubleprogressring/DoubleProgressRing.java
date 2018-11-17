package com.sendi.animation_custom_view.doubleprogressring;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sendi.animation_custom_view.R;

/**
 * Created by asendi on 2018/3/10.
 */

public class DoubleProgressRing extends View {
    private final String TAG = "ASENDI";

    private static final int ANIMATION_DURACTION = 3000;

    private Paint mPaint;
    private Paint mShadowPaint;
    private Paint mTextPaint;

    private int mProgressBorderWidth = 20;  //进度条厚度的默认大小
    private int mProgressShadowWidth = 5;     //底部圆环的厚度
    private int mProgressBorderColor; //进度颜色
    private int mProgressShadowColor; //背景颜色
    private float mProgressCurrent;// 目标进度
    private float mProgressTotal;//总进度
    private float mProgress;
    private float mTextSize = 30;

    private final float mStartProgress = -90f;  //从最顶端开始
    private final int mTotalProgressAngle = 360;     //一整个的圆弧的完整角度
    private float mCurrentProgressAngle = 0f; //记录当前进度的角度

    private RectF mBigOval;

    public DoubleProgressRing(Context context) {
        this(context, null);
    }

    public DoubleProgressRing(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleProgressRing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.ProgressCircle, defStyleAttr, 0);
        mProgressBorderWidth = typeArray.getDimensionPixelSize(R.styleable.ProgressCircle_progress_border_width
                , mProgressBorderWidth);
        mProgressBorderColor = typeArray.getColor(R.styleable.ProgressCircle_progress_border_color,
                getResources().getColor(R.color.colorWhite));
        mProgressShadowColor = typeArray.getColor(R.styleable.ProgressCircle_progress_shadow_color,
                getResources().getColor(R.color.colorWhite));
        mProgressCurrent = typeArray.getFloat(R.styleable.ProgressCircle_progress_current, 0f);
        mProgressTotal = typeArray.getFloat(R.styleable.ProgressCircle_progress_total, 360f);
        mTextSize = typeArray.getFloat(R.styleable.ProgressCircle_text_size, mTextSize);
        typeArray.recycle();

        initPaint();

        initRect();
    }

    private void initRect() {
        mBigOval=new RectF();
    }


    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        float percent = mProgress / mProgressTotal;//获得比例
        mCurrentProgressAngle = percent * mTotalProgressAngle;//获得相对应的角度
        postInvalidate();//刷新
    }

    private ObjectAnimator mAnimator;

    public void startAni() {
        mProgressCurrent = getProgressCurrent();
        mProgressTotal = getProgressTotal();

        Keyframe kStart = Keyframe.ofFloat(0, 0);
        Keyframe kCenter = Keyframe.ofFloat(0.5f, mProgressTotal);
        Keyframe kEnd = Keyframe.ofFloat(1f, mProgressCurrent);

        PropertyValuesHolder holder = PropertyValuesHolder.
                ofKeyframe("progress", kStart, kCenter, kEnd);

        mAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holder);

        mAnimator.setDuration(ANIMATION_DURACTION).start();

    }

    private void stopAnimation() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        this.clearAnimation();
    }

    /**
     * 圆环的厚度
     *
     * @return
     */
    public int getProgressBorderWidth() {
        return mProgressBorderWidth;
    }

    public void setProgressBorderWidth(int progressBorderWidth) {
        mProgressBorderWidth = progressBorderWidth;
    }

    /**
     * 圆环的颜色
     *
     * @return
     */
    public int getProgressBorderColor() {
        return mProgressBorderColor;
    }

    public void setProgressBorderColor(int progressBorderColor) {
        mProgressBorderColor = progressBorderColor;
    }


    /**
     * 整个圆弧颜色
     */
    public int getProgressShadowColor() {
        return mProgressShadowColor;
    }

    public void setProgressShadowColor(int progressShadowColor) {
        mProgressShadowColor = progressShadowColor;
    }

    /**
     * 进度圆弧的目标进度
     *
     * @return
     */
    public float getProgressCurrent() {
        return mProgressCurrent;
    }

    public void setProgressCurrent(float progressCurrent) {
        mProgressCurrent = progressCurrent;
    }

    /**
     * 整个圆弧的进度
     *
     * @return
     */
    public float getProgressTotal() {
        return mProgressTotal;
    }

    public void setProgressTotal(float progressTotal) {
        mProgressTotal = progressTotal;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);

        //为了呈现方形
        int sideLength = Math.min(widthSpaceSize, heightSpaceSize);

        setMeasuredDimension(sideLength, sideLength);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        mBigOval.set(mProgressBorderWidth / 2
                , mProgressBorderWidth / 2
                , getWidth() - (mProgressBorderWidth - 1) / 2
                , getHeight() - (mProgressBorderWidth - 1) / 2);

        canvas.drawArc(mBigOval, mStartProgress, mTotalProgressAngle, false, mShadowPaint);
        canvas.drawArc(mBigOval, mStartProgress, mCurrentProgressAngle, false, mPaint);

        int length = ("" + mProgress).length();
        canvas.drawText("" + (int) this.mProgress, (getWidth() - length * mTextSize) / 2 + mProgressBorderWidth,
                (getHeight() - mTextSize) / 2 + mProgressBorderWidth / 2, mTextPaint);

        Log.i("TAG", "onDraw: ");
        super.onDraw(canvas);
    }

    private void initPaint() {
        //绘制进度条的
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mProgressBorderColor);//设置颜色
        mPaint.setStrokeWidth(mProgressBorderWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置圆角

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);//消除锯齿
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setColor(mProgressShadowColor);//设置颜色
        mShadowPaint.setStrokeWidth(mProgressShadowWidth);
        mShadowPaint.setStrokeJoin(Paint.Join.ROUND);
        mShadowPaint.setStrokeCap(Paint.Cap.ROUND);//设置圆角

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);//消除锯齿
        mTextPaint.setColor(mProgressShadowColor);//设置颜色
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);//设置圆角
        mTextPaint.setTextSize(mTextSize);
    }

}
