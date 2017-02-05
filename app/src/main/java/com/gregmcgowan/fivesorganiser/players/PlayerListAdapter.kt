package com.gregmcgowan.fivesorganiser.players

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import java.util.*


class PlayerListAdapter : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    var playerList: List<Player> = ArrayList()

    fun setPlayers(players: List<Player>) {
        this.playerList = players
        this.notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder?, position: Int) {
        val player = playerList.get(position)
        holder?.playerNameTextView?.text = player.name
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    class PlayerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var playerNameTextView: TextView? = null

        init {
            playerNameTextView = itemView?.findViewById(R.id.player_name) as TextView?
        }

    }

}
