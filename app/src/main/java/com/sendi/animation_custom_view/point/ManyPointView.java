package com.sendi.animation_custom_view.point;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ManyPointView extends View {

    private static final int COUNT = 1;//共多少个圆点
    private float radiu = 10;//半径

    List<Integer> radius;//半径的集合
    List<Integer> colors;//颜色
    List<Point> points;//圆心

    Point mPoint;

    Paint mPaint;

    public ManyPointView(Context context) {
        this(context, null);
    }

    public ManyPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManyPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initPoints();
    }

    private void initPoints() {
        points = new ArrayList<>(COUNT);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
    }

    public void startAnim() {

        Point startPoint = new Point(radiu, radiu);
        Point endPoint = new Point(getWidth(), getHeight() - radiu);

        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);

        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setDuration(5000);

        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mPoint = (Point) animation.getAnimatedValue();

                postInvalidate();
            }
        });

        animator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircles(canvas);

        super.onDraw(canvas);

    }


    private void drawCircles(Canvas canvas) {
        if (mPoint != null)
            canvas.drawCircle(mPoint.x, mPoint.y, radiu, mPaint);
    }

    class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            float startX = startValue.x;
            float startY = startValue.y;

            float endX = endValue.x;
            float endY = endValue.y;

            float x = (fraction * (endX - startX) + startX);


            Point point = new Point(x, (float) (Math.sin(x * Math.PI / 180) * 100) + endY / 2);

            return point;
        }
    }
}
