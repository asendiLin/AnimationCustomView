package com.sendi.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sendi.animation_custom_view.R
import com.sendi.itemtouch.ItemTouchAdapter
import java.util.*

/**
 * author: asendi.
 * data: 2018/11/7.
 * description:
 */
class TouchAdapter : ItemTouchAdapter<TouchAdapter.GridViewHolder>() {

    val mDatas = ArrayList<String>(32)

    init {
        (1..30).mapTo(mDatas) { "item-$it" }
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Log.d("Adapter","f=$fromPosition t=$toPosition")
        Collections.swap(mDatas, fromPosition, toPosition)
    }

    override fun onItemRemove(position: Int) {
        mDatas.removeAt(position)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.mTextView.text = mDatas[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)

        return GridViewHolder(itemView)
    }


    override fun getItemCount(): Int = mDatas.size


    inner class GridViewHolder : RecyclerView.ViewHolder {

        val mTextView: TextView

        constructor(itemView: View) : super(itemView) {
            mTextView = itemView.findViewById(R.id.grid_tv)
        }

    }
}