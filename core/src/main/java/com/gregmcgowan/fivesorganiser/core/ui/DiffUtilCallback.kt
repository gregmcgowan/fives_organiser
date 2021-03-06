package com.gregmcgowan.fivesorganiser.core.ui

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<out T>(private val oldList: List<T>,
                              private val newList: List<T>,
                              private val itemsAreTheSame: (T, T) -> Boolean) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return itemsAreTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldListPosition: Int, newListPosition: Int): Boolean =
            oldList[oldListPosition] == newList[newListPosition]
}