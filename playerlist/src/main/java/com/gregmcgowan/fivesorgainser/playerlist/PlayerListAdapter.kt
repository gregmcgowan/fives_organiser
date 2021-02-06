package com.gregmcgowan.fivesorgainser.playerlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback
import kotlinx.android.extensions.LayoutContainer
import javax.inject.Inject

class PlayerListAdapter @Inject constructor() : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    private var playerList: MutableList<PlayerListItemUiModel> = mutableListOf()

    fun setPlayers(newPlayers: MutableList<PlayerListItemUiModel>) {
        val calculateDiff = calculateDiff(
                DiffUtilCallback(
                        oldList = playerList,
                        newList = newPlayers,
                        itemsAreTheSame = { p1, p2 -> p1.id == p2.id }
                ))
        this.playerList.clear()
        this.playerList.addAll(newPlayers)

        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = playerList.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.player_list_name).text = playerList[position].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView
    }

}
