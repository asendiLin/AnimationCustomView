package com.sendi.animation_custom_view.text;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sendi.animation_custom_view.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * author: asendi.
 * data: 2021/6/25.
 * description:
 */
public class ScrollTextView extends View {
    private final String TAG = "NumberFlipView";
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private Paint mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private int mFlipNumber = 0;
    private int mOuterFlipNumber = mFlipNumber;
    private Rect textRect = new Rect();
    private final float mMaxMoveHeight;
    private float mCurrentMoveHeight;
    private float mOuterMoveHeight;
    private float mCurrentAlphaValue;
    private int mTargetNumber;
    private ValueAnimator animator = ValueAnimator.ofFloat(1F);
    private List<String> flipNumbers = new ArrayList<>();
    private List<String> flipOuterNumbers = new ArrayList<>();
    private float lastWidth;
    private float lastHeight;
    private Map<String, Float> widthMap = new HashMap<>();

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ScrollTextView,
                defStyleAttr, 0);
        int textColor = arr.getColor(R.styleable.ScrollTextView_android_textColor, DEFAULT_TEXT_COLOR);
        float textSize = arr.getDimension(R.styleable.ScrollTextView_android_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_TEXT_SIZE, context.getResources().getDisplayMetrics()));
        int textStyle = arr.getInt(R.styleable.ScrollTextView_android_textStyle, 0);
        arr.recycle();
        mNumberPaint.setColor(textColor);
        Typeface typeface = mNumberPaint.getTypeface();
        if (textStyle == Typeface.BOLD_ITALIC) {
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        } else if (textStyle == Typeface.BOLD) {
            typeface = Typeface.create(typeface, Typeface.BOLD);
        } else if (textStyle == Typeface.ITALIC) {
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        }
        mNumberPaint.setTypeface(typeface);
        mNumberPaint.setTextSize(textSize);
        mMaxMoveHeight = textSize;
        initAnimator();
    }

    private void initAnimator() {
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mCurrentMoveHeight = mMaxMoveHeight * (1 - fraction);
                mCurrentAlphaValue = fraction;
                mOuterMoveHeight = -mMaxMoveHeight * fraction;
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                checkForLayout();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                nextNumber();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure");
        int desiredWidth = resolveSize((int) lastWidth, widthMeasureSpec);
        int desiredHeight = resolveSize((int) lastHeight, heightMeasureSpec);
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        flipNumbers.clear();
        flipOuterNumbers.clear();


        String flipNumberString = String.valueOf(mFlipNumber);
        for (int i = 0; i < flipNumberString.length(); i++) {
            flipNumbers.add(String.valueOf(flipNumberString.charAt(i)));
        }

        String flipOuterNumberString = String.valueOf(mOuterFlipNumber);
        for (int i = 0; i < flipOuterNumberString.length(); i++) {
            flipOuterNumbers.add(String.valueOf(flipOuterNumberString.charAt(i)));
        }
        int outerSize = flipOuterNumbers.size();
        mNumberPaint.getTextBounds(String.valueOf(mFlipNumber), 0, String.valueOf(mFlipNumber).length(), textRect);

        float height = getMeasuredHeight();
        float textHeight = textRect.height();

        for (int i = 0; i < flipNumbers.size(); i++) {
            String num = flipNumbers.get(i);
            float width;
            Float w = widthMap.get(num);
            if (w != null) {
                width = w;
            } else {
                width = mNumberPaint.measureText(num);
                widthMap.put(num, width);
            }
            if (outerSize > i && flipNumbers.get(i).equals(flipOuterNumbers.get(i))) {
                mNumberPaint.setAlpha(255);
                canvas.drawText(num, 0, height / 2 + textHeight / 2, mNumberPaint);
            } else {
                mNumberPaint.setAlpha((int) (255 * (1 - mCurrentAlphaValue)));
                if (outerSize > i) {
                    String outNumber = flipOuterNumbers.get(i);
                    Float outerW = widthMap.get(outNumber);
                    if (outerW == null) {
                        float ow = mNumberPaint.measureText(outNumber);
                        outerW = ow;
                        widthMap.put(outNumber, ow);
                    }
                    if (outerW > width) {
                        width = outerW;
                    }
                    canvas.drawText(flipOuterNumbers.get(i), 0, mOuterMoveHeight + height / 2 + textHeight / 2, mNumberPaint);
                }
                mNumberPaint.setAlpha((int) (255 * mCurrentAlphaValue));
                canvas.drawText(num, 0, mCurrentMoveHeight + height / 2 + textHeight / 2, mNumberPaint);
            }
            canvas.translate(width, 0f);
        }
    }

    /**
     * 如果要滚动，则必须输入一个大于等于@mTargetNumber的数字，否则不会有任何效果
     *
     * @param targetNumber 输入一个目标数字
     * @param animate      要不要做滚动
     */
    public void setNumber(int targetNumber, boolean animate) {
        Log.i(TAG, "setNumber, targetNumber:" + targetNumber + " animate:" + animate);
        if (animate && targetNumber <= mTargetNumber) {
            return;
        }
        mTargetNumber = targetNumber;
        if (animate) {
            if (animator.isRunning()) {
                animator.cancel();
            }
            int gav = targetNumber - mFlipNumber;
            if (gav > 0) {
                int duration = 3000 / gav;
                animator.setDuration(duration);
            }
            nextNumber();
        } else {
            mOuterFlipNumber = targetNumber;
            mFlipNumber = targetNumber;
            checkForLayout();
            invalidate();
        }
    }

    private void nextNumber() {
        if (mFlipNumber < mTargetNumber) {
            mOuterFlipNumber = mFlipNumber;
            ++mFlipNumber;
            Log.i(TAG, "nextNumber, mFlipNumber:" + mFlipNumber);
            animator.start();
        }
    }

    private void checkForLayout() {
        Log.i(TAG, "checkForLayout");
        final float currentWidth = mNumberPaint.measureText(String.valueOf(mFlipNumber));
        if (currentWidth != lastWidth) {
            lastWidth = currentWidth;
            final Paint.FontMetrics fm = mNumberPaint.getFontMetrics();
            lastHeight = fm.bottom - fm.top;
            requestLayout();
        }
    }
}
