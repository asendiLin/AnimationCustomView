package com.maimemo.statusrecyclerview

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlin.IllegalStateException

/**
 * 创建时间：2018/12/3
 * 作者：asendi
 * 功能描述：状态切换布局
 */
class StatusLayout : RelativeLayout {

    private var mRecyclerView: RecyclerView? = null
    private lateinit var mStatusView: TextView

    var mEmptyText: String = ""
    var mErrorText: String = ""


    private var mStatus = Companion.SUCCESS_STATU

    constructor(context: Context?) : super(context) {
//        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        init()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    private fun init() {

        if (checkChildView()) {
            if (getChildAt(0) is RecyclerView) {
                mRecyclerView = getChildAt(0) as RecyclerView
            } else {
                val swipeRefreshLayout = getChildAt(0) as SwipeRefreshLayout
                mRecyclerView = swipeRefreshLayout.getChildAt(1) as RecyclerView
            }
        }

        mStatusView = TextView(context)

        addView(mStatusView)

        val params = mStatusView.layoutParams as RelativeLayout.LayoutParams

        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        mStatusView.layoutParams = params

        mStatusView.visibility = View.INVISIBLE
    }

    private fun checkChildView(): Boolean {
        if (getChildAt(0) is RecyclerView) {
            return true
        }
        if (getChildAt(0) is SwipeRefreshLayout) {
            val refreshView = getChildAt(0) as SwipeRefreshLayout

            if (refreshView.getChildAt(1) is RecyclerView) {
                return true
            }
        }

        throw IllegalStateException("the first child must be RecyclerView.")
    }

    fun setEmptyTextId(textId: Int) {
        setEmptyText(resources.getString(textId))
    }

    fun setEmptyText(text: String) {
        mEmptyText = text
    }

    fun setFailTextId(textId: Int) {
        setFailText(resources.getString(textId))
    }

    fun setFailText(text: String) {
        mErrorText = text
    }

    /**
     * 更新状态
     */
    fun setStatus(status: Int) {

        if (status == mStatus)
            return

        changeStatus(status)
        mStatus = status
    }

    private fun changeStatus(status: Int) {

        when (status) {
            SUCCESS_STATU -> {
                success()
            }
            FAILD_STATU -> {
                fails()
            }
            EMPTY_STATU -> {
                empty()
            }
        }

    }

    private fun empty() {
        mRecyclerView?.visibility = View.INVISIBLE
        mStatusView.text = mEmptyText
        mStatusView.visibility = View.VISIBLE
    }

    private fun fails() {
        mRecyclerView?.visibility = View.INVISIBLE
        mStatusView.text = mErrorText
        mStatusView.visibility = View.VISIBLE
    }

    private fun success() {

        mRecyclerView?.visibility = View.VISIBLE

        mStatusView.visibility = View.GONE
    }


    companion object {
        const val SUCCESS_STATU = 1
        const val FAILD_STATU = 2
        const val EMPTY_STATU = 3
    }
}

