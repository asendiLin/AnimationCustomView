package com.sendi.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * author: asendi.
 * data: 2018/11/7.
 * description:自定义下划线
 */
class ItemDivider : RecyclerView.ItemDecoration() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mLayoutManager: RecyclerView.LayoutManager? = null

    var mDividerWidth = 1

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.color = 0xff000000.toInt()
    }

    /**
     * 指定分割线的大小
     */
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        if (mLayoutManager == null) {
            mLayoutManager = parent?.layoutManager
        }

        if (mLayoutManager is LinearLayoutManager) {

            val orientation = (mLayoutManager as LinearLayoutManager).orientation

            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect?.bottom = mDividerWidth
            } else {
                outRect?.right = mDividerWidth
            }

            if (mLayoutManager is GridLayoutManager) {

                val layoutParams: GridLayoutManager.LayoutParams? = view?.layoutParams as GridLayoutManager.LayoutParams

                if (orientation == LinearLayoutManager.VERTICAL && layoutParams?.spanIndex!! > 0) {
                    outRect?.left = mDividerWidth
                } else if (orientation == LinearLayoutManager.HORIZONTAL && layoutParams?.spanIndex!! > 0) {
                    outRect?.top = mDividerWidth
                }

            }
        }

    }

    /**
     * 在ItemView绘制之前调用
     */
    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)

        val childCount = parent?.childCount

        for (i in 0 until childCount!!) {
            val child = parent.getChildAt(i)
            /*竖直分割线*/
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left1 = child.right + params.rightMargin
            val right1 = left1 + mDividerWidth
            val top1 = child.top - params.topMargin
            val bottom1 = child.bottom + params.bottomMargin
            c?.drawRect(left1.toFloat(), top1.toFloat(), right1.toFloat(), bottom1.toFloat(), mPaint)

            /*水平分割线*/
            val left2 = child.left - params.leftMargin
            val right2 = child.right + params.rightMargin
            val top2 = child.bottom + params.bottomMargin
            val bottom2 = top2 + mDividerWidth
            c?.drawRect(left2.toFloat(), top2.toFloat(), right2.toFloat(), bottom2.toFloat(), mPaint)
        }
    }

    /**
     * 在ItemView绘制后调用
     */
    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)
    }

}