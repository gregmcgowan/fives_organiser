package com.gregmcgowan.fivesorganiser.players

import android.support.v7.util.DiffUtil
import android.support.v7.util.DiffUtil.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.find


class PlayerListAdapter : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    var playerList: List<Player> = mutableListOf()

    fun setPlayers(newPlayers: List<Player>) {
        this.playerList = newPlayers
        val calculateDiff = calculateDiff(DiffUtilCallback(oldList = playerList,
                                                            newList = newPlayers))
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder?, position: Int) {
        holder?.let { holder.playerNameTextView.text = playerList[position].name }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    class PlayerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val playerNameTextView: TextView by find<TextView>(R.id.player_name)
    }

    class DiffUtilCallback(val oldList: List<Player>,
                           val newList: List<Player>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldListPosition: Int, newListPosition: Int): Boolean =
                oldList[oldListPosition] === newList[newListPosition]

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldListPosition: Int, newListPosition: Int): Boolean {
            return oldList[oldListPosition] == (newList[newListPosition])
        }
    }

}
