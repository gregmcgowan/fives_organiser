package com.gregmcgowan.fivesorganiser.playerList

import android.support.v7.util.DiffUtil.calculateDiff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListItemUiModel

class PlayerListAdapter : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    private var playerList: List<PlayerListItemUiModel> = mutableListOf()

    fun setPlayers(newPlayers: List<PlayerListItemUiModel>) {
        this.playerList = newPlayers
        val calculateDiff = calculateDiff(
                DiffUtilCallback(
                        oldList = playerList,
                        newList = newPlayers
                ))
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = playerList.size

    override fun onBindViewHolder(holder: PlayerViewHolder?, position: Int) {
        holder?.let { holder.playerNameTextView.text = playerList[position].name }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    class PlayerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val playerNameTextView: TextView by find(R.id.player_name)
    }

}
