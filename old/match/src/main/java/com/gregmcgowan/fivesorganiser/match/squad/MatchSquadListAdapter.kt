package com.gregmcgowan.fivesorganiser.match.squad

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.match.R
import javax.inject.Inject

class MatchSquadListAdapter @Inject constructor(
        private val matchSquadListFactory: MatchSquadListFactory.Builder
) : RecyclerView.Adapter<MatchSquadListPlayerViewHolder>() {

    private var players: MutableList<MatchSquadListItemUiModel> = mutableListOf()

    fun setUninvitedPlayers(notInvitedPlayers: List<MatchSquadListItemUiModel>) {
        this.players.clear()
        this.players.addAll(notInvitedPlayers)
        // Do we need diff util?
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchSquadListPlayerViewHolder =
            matchSquadListFactory.itemView(LayoutInflater.from(parent.context)
                    .inflate(R.layout.match_squad_list_item, parent, false)!!)
                    .build()
                    .matchSquadPlayerListViewHolder()

    override fun onBindViewHolder(holder: MatchSquadListPlayerViewHolder, position: Int) {
        holder.presenter.bindModel(players[position])

    }

    override fun getItemCount(): Int = players.size
}

class MatchSquadListPlayerViewHolder @Inject constructor(
        val presenter: MatchSquadListPlayerContract.Presenter,
        itemView: View
) : RecyclerView.ViewHolder(itemView)


