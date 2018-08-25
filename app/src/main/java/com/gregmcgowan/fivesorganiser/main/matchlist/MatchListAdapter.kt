package com.gregmcgowan.fivesorganiser.main.matchlist

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback

class MatchListAdapter : RecyclerView.Adapter<MatchListAdapter.MatchViewHolder>() {

    private var matches: MutableList<MatchListItemUiModel> = mutableListOf()

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
        holder.matchTypeTextView.text = matchUiModel.matchType
        holder.locationTextView.text = matchUiModel.location
        holder.dateAndTimeTextView.text = matchUiModel.dateAndTime
        holder.squadTextView.text = matchUiModel.squadStatus

        holder.dateTimeLocationBackground.setOnClickListener {
            matchListInteraction?.editMatchDetails(matchId = matchUiModel.matchId)

        }
        holder.squadBackground.setOnClickListener {
            matchListInteraction?.editSquad(matchUiModel.matchId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        return MatchViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.match_list_item, parent, false)
        )
    }

    class MatchViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val matchTypeTextView: TextView by find(R.id.match_list_item_match_type)
        val dateAndTimeTextView: TextView by find(R.id.match_list_item_date_and_time)
        val dateTimeLocationBackground : View by find(R.id.match_list_item_date_time_location_background)
        val locationTextView: TextView by find(R.id.match_list_item_match_location)
        val squadTextView: TextView by find(R.id.match_list_item_squad_text)
        val squadBackground : View by find(R.id.match_list_item_squad_background)
    }

    interface MatchListInteraction {

        fun editMatchDetails(matchId: String)

        fun editSquad(matchId : String)
    }

}