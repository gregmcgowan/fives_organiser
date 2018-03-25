package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find

class InvitePlayersListAdapter : RecyclerView.Adapter<NotInvitedPlayerViewHolder>() {

    private var notInvitedPlayers: MutableList<InvitePlayerListItemModel> = mutableListOf()

    fun setUninvitedPlayers(notInvitedPlayers: List<InvitePlayerListItemModel>) {
        this.notInvitedPlayers.clear()
        this.notInvitedPlayers.addAll(notInvitedPlayers)
        // Do we need diff util?
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotInvitedPlayerViewHolder {
        return NotInvitedPlayerViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.match_squad_invite_players_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: NotInvitedPlayerViewHolder, position: Int) {
        holder.playerName.text = notInvitedPlayers[position].playerName
    }

    override fun getItemCount(): Int = notInvitedPlayers.size
}


class NotInvitedPlayerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val playerName: TextView by find(R.id.uninvited_player_name)
    val invitedStatus: Spinner by find(R.id.uninvited_player_invited_status_checkbox)

}