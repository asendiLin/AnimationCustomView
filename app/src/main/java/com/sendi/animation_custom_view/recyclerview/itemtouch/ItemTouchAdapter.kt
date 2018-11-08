package com.sendi.itemtouch

import android.support.v7.widget.RecyclerView

/**
 * author: asendi.
 * data: 2018/11/7.
 * description:
 */
abstract class ItemTouchAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var isOpenDrag = true//是否支持拖拽
    var isOpenSwipe = true//是否支持滑动删除

    abstract fun onItemMove(fromPosition: Int, toPosition: Int)
    abstract fun onItemRemove(position: Int)

}