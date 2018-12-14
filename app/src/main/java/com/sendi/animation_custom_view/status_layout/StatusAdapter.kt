package com.maimemo.statusrecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * 创建时间：2018/12/3
 * 作者：asendi
 * 功能描述：
 */
class StatusAdapter(private val mContext: Context) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {


    private val mItemData = ArrayList<String>()

    init {

        for (i in 1..10) {
            mItemData.add("item-->$i")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val textView = TextView(mContext)
        return StatusViewHolder(textView)
    }

    override fun getItemCount(): Int = mItemData.size

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {

        val textView = holder.itemView as TextView
        textView.text = mItemData[position]
    }


    inner class StatusViewHolder(private val itemView: TextView) : RecyclerView.ViewHolder(itemView) {

    }
}