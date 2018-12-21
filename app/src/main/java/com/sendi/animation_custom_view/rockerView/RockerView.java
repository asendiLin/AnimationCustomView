package com.sendi.animation_custom_view.rockerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lizheng on 2018/11/1.
 * 添加了回调和判断的遥感View
 */

public class RockerView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread thread;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint paint;
    private boolean flag;
    private OnRadListener mOnRadListener;

    public void setmOnRadListener(OnRadListener onRadListener) {
        this.mOnRadListener = onRadListener;
    }


    //遥感背景图片X，Y,以及半径R
    private int RockerCircleX = 0;
    private int RockerCircleY = 0;
    private int RockerCircleR = 0;
    //遥感半径X，Y，以及半径R
    private float SmallRockerCircleX = 0;
    private float SmallRockerCircleY = 0;
    private float SmallRockerCircleR = 0;


    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置为透明
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        this.setKeepScreenOn(true);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = getWidth();
        int height = getHeight();
        RockerCircleX = width / 2;
        RockerCircleY = height / 2;
        RockerCircleR = width / 4;
        SmallRockerCircleX = width / 2;
        SmallRockerCircleY = height / 2;
        SmallRockerCircleR = width / 8;

        thread = new Thread(this);
        flag = true;
        thread.start();

    }

    /***
     * 得到两点之间的弧度
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        double x = px2 - px1;
        double y = py1 - py2;

        double len = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double cosAngle = x / len;
        double rad = Math.acos(cosAngle);

        if (py1 > py2) {
            rad = -rad;
        }
        return rad;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // 当触屏区域不在活动范围内
            if (Math.sqrt(Math.pow(RockerCircleX - (int) event.getX(), 2) + Math.pow(RockerCircleY - (int) event.getY(), 2)) >= RockerCircleR) {
                double tempRad = getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY());
                getXY(RockerCircleX, RockerCircleY, RockerCircleR, tempRad);
                mOnRadListener.getOnRad(tempRad);
                directionController(tempRad);

            } else {
                //如果小球中心点小于活动区域则随着用户触屏点移动即可
                SmallRockerCircleX = event.getX();
                SmallRockerCircleY = event.getY();
//                mOnRadListener.getOnRad(getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY()));
//                directionController(getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY()));
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当释放按键时摇杆要恢复摇杆的位置为初始位置
            SmallRockerCircleX = RockerCircleX;
            SmallRockerCircleY = RockerCircleY;
            mOnRadListener.stop();
        }
        return true;
    }

    private void directionController(double rad) {
        double degrees = Math.toDegrees(rad);
        if (-45.0 < degrees && degrees < 45.0) {
            //右
            mOnRadListener.turnRight();

        } else if (45.0 < degrees && degrees < 135.0) {
            //下
            mOnRadListener.turnBackward();
        } else if (135.0 < degrees && degrees < 180.0 || -180.0 < degrees && degrees < -135.0) {
            //左
            mOnRadListener.turnLeft();
        } else if (-135.0 < degrees && degrees < -45.0) {
            //上
            mOnRadListener.turnForward();
        } else {

        }
    }

    public void getXY(double centerX, double centerY, double R, double rad) {
        //获取圆周运动的X坐标
        SmallRockerCircleX = (float) ((R * Math.cos(rad)) + centerX);
        //获取圆周运动的Y坐标
        SmallRockerCircleY = (float) ((R * Math.sin(rad)) + centerY);
    }

    public void draw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas == null)
                return;
            //设置为透明
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            //设置透明度
            paint.setColor(0xB3888888);
            //绘制摇杆背景
            canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, paint);
            paint.setColor(Color.WHITE);
            //绘制摇杆
            canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY,
                    SmallRockerCircleR, paint);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (canvas != null)
                    sfh.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void run() {
        while (flag) {
            draw();
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
            }
        }

    }

    public interface OnRadListener {
        void getOnRad(double rad);

        void turnLeft();

        void turnRight();

        void turnForward();

        void turnBackward();

        void stop();
    }
}
