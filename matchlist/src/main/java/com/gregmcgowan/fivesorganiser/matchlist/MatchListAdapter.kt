package com.gregmcgowan.fivesorganiser.matchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.match_list_item_content.*
import javax.inject.Inject

class MatchListAdapter @Inject constructor() : RecyclerView.Adapter<MatchListAdapter.MatchViewHolder>() {

    private val matches: MutableList<MatchListItemUiModel> = mutableListOf()

    var matchListInteraction: MatchListInteraction? = null

    fun setMatches(newMatches: MutableList<MatchListItemUiModel>) {
        val calculateDiff = DiffUtil.calculateDiff(
                DiffUtilCallback(
                        oldList = matches,
                        newList = newMatches,
                        itemsAreTheSame = { m1, m2 -> m1.matchId == m2.matchId }
                ))
        matches.clear()
        matches.addAll(newMatches)

        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = matches.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        //TODO presenter or databinding
        val matchUiModel = matches[position]
        holder.itemView.findViewById<TextView>(R.id.match_list_item_match_type).text = matchUiModel.matchType
        holder.itemView.findViewById<TextView>(R.id.match_list_item_match_location).text = matchUiModel.location
        holder.itemView.findViewById<TextView>(R.id.match_list_item_date_and_time).text = matchUiModel.dateAndTime
        holder.itemView.findViewById<TextView>(R.id.match_list_item_squad_text).text = matchUiModel.squadStatus

        holder.itemView.findViewById<View>(R.id.match_list_item_date_time_location_background).setOnClickListener {
            matchListInteraction?.editMatchDetails(matchUiModel.matchId)

        }
        holder.itemView.findViewById<View>(R.id .match_list_item_squad_background).setOnClickListener {
            matchListInteraction?.editSquad(matchUiModel.matchId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder =
            MatchViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.match_list_item, parent, false)
            )

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView
    }

    interface MatchListInteraction {

        fun editMatchDetails(matchId: String)

        fun editSquad(matchId: String)
    }

}