package com.sendi.itemtouch

import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * author: asendi.
 * data: 2018/11/7.
 * description:拖动排序和拖拽
 */
class ItemTouchCallbakImpl<VH : RecyclerView.ViewHolder>
constructor(private val mAdapter: ItemTouchAdapter<VH>) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {

        var dragFlags = 0
        var swipeFlags = 0

        val manager = recyclerView?.layoutManager

        if (manager is GridLayoutManager || manager is StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        } else {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END //左右滑动删除

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * 移动回调
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemViewType == target.itemViewType) {
            var fromPosition = viewHolder.adapterPosition
            var toPosition = target.adapterPosition

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    mAdapter.onItemMove(i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    mAdapter.onItemMove(i, i - 1)
                }
            }

            mAdapter.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        return false
    }

    /**
     * 删除回调
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        mAdapter.onItemRemove(viewHolder?.adapterPosition!!)
        mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //toDo:滑动时改变背景等
        }
    }

    /**
     * item被选中
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * item取消长按
     */
    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return mAdapter.isOpenDrag
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return mAdapter.isOpenSwipe
    }

}