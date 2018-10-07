package com.gregmcgowan.fivesorgainser.playerlist

import android.support.v7.util.DiffUtil.calculateDiff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback

class PlayerListAdapter : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    private var playerList: MutableList<PlayerListItemUiModel> = mutableListOf()

    fun setPlayers(newPlayers: MutableList<PlayerListItemUiModel>) {
        val calculateDiff = calculateDiff(
                DiffUtilCallback(
                        oldList = playerList,
                        newList = newPlayers,
                        //TODO change to id
                        itemsAreTheSame = { p1, p2 -> p1.name == p2.name }
                ))
        this.playerList.clear()
        this.playerList.addAll(newPlayers)

        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = playerList.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.playerNameTextView.text = playerList[position].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerNameTextView: TextView by find(R.id.player_name)
    }

}
