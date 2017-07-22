package com.gregmcgowan.fivesorganiser.core

import android.support.v7.util.DiffUtil

class DiffUtilCallback<out T>(val oldList: List<T>,
                              val newList: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldListPosition: Int, newListPosition: Int): Boolean =
            oldList[oldListPosition] === newList[newListPosition]

    override fun areContentsTheSame(oldListPosition: Int, newListPosition: Int): Boolean =
            oldList[oldListPosition] == newList[newListPosition]
}