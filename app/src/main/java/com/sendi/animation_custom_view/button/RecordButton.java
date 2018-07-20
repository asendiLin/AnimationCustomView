package com.sendi.animation_custom_view.button;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sendi.animation_custom_view.R;


public class RecordButton extends View {

    private static final String TAG = "RecordButton";

    private final static float FINAL_SIZE_RATE = 0.4f;

    /*正在录制中的状态*/
    public static final int STATE_RECORDING = 1;
    /*停止录制的状态*/
    public static final int STATE_STOPPING = 2;

    private static final float RATE_2 = 2f;

    private static final float RATE_4 = 4f;

    private float mCurrentRate = RATE_2;

    private int mCurrentState = STATE_STOPPING;

    private Paint outCirclePaint;//白色圆环的画笔

    private Paint innerRectPaint;//红色圆角方形的画笔

    private static final int DURATION = 1000;

    private static final int OUT_COLOR = R.color.color_rb_out_white;

    private static final int INNER_COLOR = R.color.color_rb_inner_red;
    /*外环距离边缘的距离*/
    private static final int OUT_DISTANCE = 5;
    /*外环的厚度*/
    private static final float OUT_STROCK = 10.0f;
    /*内圆角方形*/
    private RectF mInnerRectF;
    /*内圆角方形的边长*/
    private float mLength;
    /*内圆角方形的最后边长，最小为最开始的0.4*/
    private float mFinalLength;
    /*圆内角方形的开始边长*/
    private float mStartLength;

    private float radius;

    private int centerX;

    private int centerY;

    private ValueAnimator mAnimator;

    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();

        initGraph();

        initAnim();
    }

    private void initAnim() {
        mAnimator = new ValueAnimator();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mCurrentState == STATE_STOPPING) {
                    mCurrentState = STATE_RECORDING;
                } else {
                    mCurrentState = STATE_STOPPING;
                }
                setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void initGraph() {
        mInnerRectF = new RectF();
    }

    private void initPaint() {
        outCirclePaint = new Paint();
        outCirclePaint.setColor(getResources().getColor(OUT_COLOR));
        outCirclePaint.setStrokeWidth(OUT_STROCK);
        outCirclePaint.setAntiAlias(true);
        outCirclePaint.setStyle(Paint.Style.STROKE);

        innerRectPaint = new Paint();
        innerRectPaint.setColor(getResources().getColor(INNER_COLOR));
        innerRectPaint.setAntiAlias(true);
        innerRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        float maxRadius = (float) getWidth()/2;
        if (width != height) {
            if (width > height) {
                maxRadius = (float) height / 2;
            } else {
                maxRadius = (float)width / 2;
            }
        }
        radius = maxRadius - OUT_DISTANCE;

        centerX = width / 2;
        centerY = height / 2;

        mStartLength = mLength = (float) (radius * Math.sqrt(2));

        mFinalLength = FINAL_SIZE_RATE * mLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //1.画外圆圈--空心、白色
        canvas.drawCircle(centerX, centerY, radius, outCirclePaint);

        //2.画圆角方形--实心、红色

        float left = centerX - mLength / 2;
        float top = centerY - mLength / 2;
        float right = left + mLength;
        float bottom = top + mLength;
        mInnerRectF.set(left, top, right, bottom);

        canvas.drawRoundRect(mInnerRectF, mLength / mCurrentRate, mLength / mCurrentRate, innerRectPaint);
    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mAnimator != null && !mAnimator.isRunning()) {
                        setClickable(false);
                        startAnimByState();
                    }
                    break;
            }
            return true;
        }


    public void setCurrentState(int mCurrentState) {
        this.mCurrentState = mCurrentState;
    }

    public void startAnim() {

        if (mAnimator != null && !mAnimator.isRunning()) {
            setClickable(false);
            startAnimByState();
        }
    }


    /**
     * 根据状态进行做动画
     */
    private void startAnimByState() {
        if (mCurrentState == STATE_STOPPING) {//由大到小
            mAnimator.setFloatValues(mStartLength, mFinalLength);
//            mAnimator = ValueAnimator.ofFloat(mStartLength, mFinalLength);
        } else {//恢复原状
            mAnimator.setFloatValues(mFinalLength, mStartLength);
//            mAnimator = ValueAnimator.ofFloat(mFinalLength, mStartLength);
        }

        mAnimator.setDuration(DURATION);
        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float fraction = valueAnimator.getAnimatedFraction();
                if (mCurrentState == STATE_STOPPING) {
                    mCurrentRate = (RATE_4 - RATE_2) * fraction + RATE_2;
                } else {
                    mCurrentRate = RATE_4 - (RATE_4 - RATE_2) * fraction;
                }
                mLength = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });


        mAnimator.start();

    }

}
