package com.digitaldream.winskool.interfaces

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int, )
    fun onItemDismiss(recyclerView: RecyclerView)

}