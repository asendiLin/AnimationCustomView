package com.maimemo.statusrecyclerview

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.sendi.animation_custom_view.R

/**
 * 创建时间：2018/12/3
 * 作者：asendi
 * 功能描述：PK统计进度条
 */
class ProgrssLine : View {
    private lateinit var mLinePaint: Paint
    private lateinit var mProgressPaint: Paint
    private lateinit var mTextPaint: Paint

    private lateinit var mTextBound: Rect

    private lateinit var mTagDrawable: Drawable
    private lateinit var mTagBound: Rect

    private var mTagLeft = 0
    private var mTagRight = 0
    private var mTagTop = 0
    private var mTagBottom = 0

    private var mProgressText = "100%"
    private var mCurrentProgress = ""
    private var mCurrentProgressValue = 0f
    private var mTextWidth: Int = 0
    private var mTextHeight: Int = 0

    private lateinit var mProgressAniamtion: ValueAnimator

    constructor(context: Context) : super(context) {
        init(context, null)
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        mLinePaint = Paint()
        mLinePaint.style = Paint.Style.FILL_AND_STROKE
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = dp2Px(context, 4)
        mLinePaint.strokeCap = Paint.Cap.ROUND
        mLinePaint.strokeJoin = Paint.Join.ROUND
        mLinePaint.color = resources.getColor(R.color.colorLine)

        mProgressPaint = Paint()
        mProgressPaint.style = Paint.Style.FILL_AND_STROKE
        mProgressPaint.isAntiAlias = true
        mProgressPaint.strokeWidth = dp2Px(context, 4)
        mProgressPaint.strokeCap = Paint.Cap.ROUND
        mProgressPaint.strokeJoin = Paint.Join.ROUND
        mProgressPaint.color = resources.getColor(R.color.colorProgress)


        mTextPaint = Paint()
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = sp2Px(context, 12)
        mTextPaint.color = resources.getColor(R.color.colorProgressText)
        mTextBound = Rect()
        mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length, mTextBound)

        mTextWidth = mTextBound.width()
        mTextHeight = mTextBound.height()

        mTagDrawable = resources.getDrawable(R.drawable.bg_tag_bg)

        mTagLeft = 0
        mTagRight = mTagLeft + dp2Px(context, 50).toInt()
        mTagTop = 0
        mTagBottom = dp2Px(context, 24).toInt()
        mTagBound = Rect(mTagLeft, mTagTop, mTagRight, mTagBottom)

        mTagDrawable.bounds = mTagBound
    }

    /**
     * 开始动画
     */
    fun startProgressAnimation(finalValue: Float) {
        if (finalValue > 1f) return

        mProgressAniamtion = ValueAnimator.ofFloat(0f, finalValue)
        mProgressAniamtion.addUpdateListener {
            mCurrentProgressValue = it.animatedValue as Float
            invalidate()
        }
        mProgressAniamtion.interpolator = AccelerateDecelerateInterpolator()
        mProgressAniamtion.setEvaluator(FloatEvaluator())

        mProgressAniamtion.duration = 2000
        mProgressAniamtion.start()
    }

    /**
     * 释放资源
     */
    fun stop() {
        mProgressAniamtion.removeAllUpdateListeners()
        mProgressAniamtion.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawLine(dp2Px(context, 2), height / 2f, width.toFloat() - dp2Px(context, 2), height / 2f, mLinePaint)

        var currentWidth = mCurrentProgressValue * (width.toFloat() - dp2Px(context, 50))

        if (currentWidth == width - dp2Px(context, 2)) {
            currentWidth -= dp2Px(context, 2)
        }
        canvas?.drawLine(dp2Px(context, 2), height / 2f, currentWidth, height / 2f, mProgressPaint)

        var tagLeft = currentWidth

        if (tagLeft > (width - dp2Px(context, 50) - dp2Px(context, 2))) {
            tagLeft = width - dp2Px(context, 50)
        }

        if (tagLeft < 0f) {
            tagLeft = 0f
        }

        mTagBound.set(tagLeft.toInt(), 0, (tagLeft + dp2Px(context, 50)).toInt(), dp2Px(context, 24).toInt())
        mTagDrawable.bounds = mTagBound
        mTagDrawable.draw(canvas)

        mCurrentProgress = "${(mCurrentProgressValue * 100).toInt()}%"
        //draw text
        mTextPaint.getTextBounds(mCurrentProgress, 0, mCurrentProgress.length, mTextBound)
        val fontMetricsInt = mTextPaint.fontMetricsInt

        val startX = paddingLeft + tagLeft + dp2Px(context, 12) + (mTextWidth - mTextBound.width()) / 2f
        val startY = height / 2 + (Math.abs(fontMetricsInt.ascent) - Math.abs(fontMetricsInt.descent)) / 2f
        canvas?.drawText(mCurrentProgress, startX, startY, mTextPaint)
    }
}

fun dp2Px(context: Context?, dpVal: Int): Float {
    var density = context?.resources?.displayMetrics?.density
    return dpVal * density!! + 0.5f
}

fun sp2Px(context: Context?, spVal: Int): Float {

    var scaledDensity = context?.resources?.displayMetrics?.scaledDensity
    return spVal * scaledDensity!! + 0.5f

}
