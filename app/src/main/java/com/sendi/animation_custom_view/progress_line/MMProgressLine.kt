package com.sendi.commentdemo

import android.animation.Animator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import com.sendi.animation_custom_view.R

/**
 * 创建时间：2018/12/4
 * 作者：asendi
 * 功能描述：进度条
 */
class MMProgressLine @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val TAG = "MMProgressLine"


    private var mLinePaint: Paint? = null
    private var mProgressPaint: Paint? = null
    private var mTextPaint: Paint? = null

    private var mTextBound: Rect? = null

    private var mTagDrawable: Drawable? = null
    private var mTagBound: Rect? = null

    private var mProgressDrawable: Drawable? = null
    private var mProgressBound: Rect? = null

    private var mTagLeft = 0
    private var mTagRight = 0
    private var mTagTop = 0
    private var mTagBottom = 0

    private val mProgressText = "100%"
    private var mCurrentProgress: String? = null
    private var mCurrentProgressValue = 0f
    private var mTextWidth = 0

    private var mProgressAnimation: ValueAnimator? = null

    private var mTagHeight = DEFAULT_TAG_HEIGHT
    private var mTagWidth = DEFAULT_TAG_WIDTH
    private var mLineStroke = DEFAULT_LINE_STROKE.toFloat()

    private var mLineColor: Int = 0
    private var mProgressColor: Int = 0
    private var mTextColor: Int = 0

    private var mTextSize: Int = 0

    private var mEndListerner: OnAnimationEndListener? = null

    init {

        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MMProgressLine, defStyleAttr, 0)

        mTagHeight = typedArray.getDimensionPixelSize(R.styleable.MMProgressLine_pl_tag_height, DEFAULT_TAG_HEIGHT)
        mTagWidth = typedArray.getDimensionPixelSize(R.styleable.MMProgressLine_pl_tag_width, DEFAULT_TAG_WIDTH)
        mLineStroke = typedArray.getDimensionPixelSize(
            R.styleable.MMProgressLine_pl_line_stroke,
            dp2Px(context, DEFAULT_LINE_STROKE).toInt()
        ).toFloat()

        mLineColor = typedArray.getColor(R.styleable.MMProgressLine_pl_line_color, DEFAULT_LINE_COLOR)
        mProgressColor = typedArray.getColor(R.styleable.MMProgressLine_pl_progress_color, DEFAULT_PROGRESS_COLOR)
        mTextColor = typedArray.getColor(R.styleable.MMProgressLine_pl_text_color, DEFAULT_TEXT_COLOR)

        mTextSize = typedArray.getDimensionPixelSize(
            R.styleable.MMProgressLine_pl_text_size,
            sp2Px(context, DEFAULT_TEXT_SIZE).toInt()
        )

        typedArray.recycle()

        mLinePaint = Paint()
        mLinePaint!!.style = Paint.Style.FILL_AND_STROKE
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.strokeWidth = mLineStroke
        mLinePaint!!.strokeCap = Paint.Cap.ROUND
        mLinePaint!!.strokeJoin = Paint.Join.ROUND
        mLinePaint!!.color = mLineColor

        mProgressPaint = Paint()
        mProgressPaint!!.style = Paint.Style.FILL_AND_STROKE
        mProgressPaint!!.isAntiAlias = true
        mProgressPaint!!.strokeWidth = mLineStroke
        mProgressPaint!!.strokeCap = Paint.Cap.ROUND
        mProgressPaint!!.strokeJoin = Paint.Join.ROUND
        mProgressPaint!!.color = mProgressColor

        mTextPaint = Paint()
        mTextPaint!!.style = Paint.Style.FILL_AND_STROKE
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = mTextColor
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextBound = Rect()
        mTextPaint!!.getTextBounds(mProgressText, 0, mProgressText.length, mTextBound)

        mTextWidth = mTextBound!!.width()

        mTagDrawable = resources.getDrawable(R.drawable.bg_tag_progress)

        mTagLeft = 0
        mTagRight = (mTagLeft + dp2Px(context, mTagWidth)).toInt()
        mTagTop = 0
        mTagBottom = dp2Px(context, mTagHeight).toInt()
        mTagBound = Rect(mTagLeft, mTagTop, mTagRight, mTagBottom)

        mTagDrawable!!.bounds = mTagBound!!

        mProgressDrawable = resources.getDrawable(R.drawable.bg_line_progress)
        mProgressBound = Rect()
    }

    fun setEndListerner(mEndListerner: OnAnimationEndListener) {
        this.mEndListerner = mEndListerner
    }

    /**
     * 开始动画
     */
    fun startProgressAnimation(finalValue: Float) {
        if (finalValue > 1f || finalValue < 0f) return

        mProgressAnimation = ValueAnimator.ofFloat(0f, finalValue)
        mProgressAnimation!!.addUpdateListener { animation ->
            Log.d(TAG, "startProgressAnimation: " + animation.animatedFraction)
            mCurrentProgressValue = animation.animatedValue as Float
            postInvalidate()
        }
        mProgressAnimation!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                if (mEndListerner != null)
                    mEndListerner!!.onEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (mEndListerner != null)
                    mEndListerner!!.onEnd()
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        mProgressAnimation!!.interpolator = LineInterceptor()
        mProgressAnimation!!.setEvaluator(FloatEvaluator())

        mProgressAnimation!!.duration = ANIMATION_DURATION.toLong()
        mProgressAnimation!!.start()
    }

    /**
     * 释放资源
     */
    fun stop() {
        if (mProgressAnimation != null) {
            mProgressAnimation!!.removeAllUpdateListeners()
            mProgressAnimation!!.cancel()
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height

        //draw background line
        canvas.drawLine(
            dp2Px(context, 2) + paddingLeft,
            height / 2f,
            width - dp2Px(context, 2),
            height / 2f,
            mLinePaint!!
        )

        var currentWidth =
            mCurrentProgressValue * (width.toFloat() - dp2Px(context, mTagWidth) - paddingRight.toFloat())

        if (currentWidth == width - dp2Px(context, 2)) {
            currentWidth -= dp2Px(context, 2)
        }
        // draw progress line
        canvas.drawLine(dp2Px(context, 2) + paddingLeft, height / 2f, currentWidth, height / 2f, mProgressPaint!!)

        mProgressBound!!.set(
            dp2Px(context, 2).toInt() + paddingLeft, height / 2 - dp2Px(context, 2).toInt(),
            currentWidth.toInt(), height / 2 + dp2Px(context, 2).toInt()
        )
        mProgressDrawable!!.bounds = mProgressBound!!
        mProgressDrawable!!.draw(canvas)

        var tagLeft = currentWidth

        if (tagLeft > width.toFloat() - dp2Px(context, mTagWidth) - dp2Px(context, 2) - paddingRight.toFloat()) {
            tagLeft = width.toFloat() - dp2Px(context, mTagWidth) - dp2Px(context, 2) - paddingRight.toFloat()
        }

        if (tagLeft < 0f) {
            tagLeft = 0f
        }

        mTagBound!!.set(
            tagLeft.toInt(),
            0,
            (tagLeft + dp2Px(context, mTagWidth)).toInt(),
            dp2Px(context, mTagHeight).toInt()
        )
        mTagDrawable!!.bounds = mTagBound!!
        mTagDrawable!!.draw(canvas)

        mCurrentProgress = (mCurrentProgressValue * 100).toInt().toString() + "%"
        //draw text
        mTextPaint!!.getTextBounds(mCurrentProgress, 0, mCurrentProgress!!.length, mTextBound)
        val fontMetricsInt = mTextPaint!!.fontMetricsInt

        //        float startX = getPaddingLeft() + tagLeft + dp2Px(getContext(), 12) + (mTextWidth - mTextBound.width()) / 2f;
        val startX = paddingLeft.toFloat() + tagLeft + mTextSize.toFloat() + (mTextWidth - mTextBound!!.width()) / 2f
        val startY = height / 2 + (Math.abs(fontMetricsInt.ascent) - Math.abs(fontMetricsInt.descent)) / 2f
        canvas.drawText(mCurrentProgress!!, startX, startY, mTextPaint!!)
    }

    private fun dp2Px(context: Context, dpVal: Int): Float {
        val density = context.resources.displayMetrics.density
        return dpVal * density + 0.5f
    }

    private fun sp2Px(context: Context, spVal: Int): Float {

        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return spVal * scaledDensity + 0.5f
    }


    private inner class LineInterceptor : Interpolator {

        override fun getInterpolation(input: Float): Float {

            return 1f - (1f - input) * (1f - input)

        }
    }

    interface OnAnimationEndListener {
        fun onEnd()
    }

    companion object {

        private val ANIMATION_DURATION = 2500

        private val DEFAULT_TEXT_SIZE = 12

        private val DEFAULT_TAG_HEIGHT = 24
        private val DEFAULT_TAG_WIDTH = 50
        private val DEFAULT_LINE_STROKE = 4

        private val DEFAULT_LINE_COLOR = Color.parseColor("#D2E8E4")
        private val DEFAULT_PROGRESS_COLOR = Color.parseColor("#23B396")
        private val DEFAULT_TEXT_COLOR = Color.parseColor("#37B49D")
    }
}
