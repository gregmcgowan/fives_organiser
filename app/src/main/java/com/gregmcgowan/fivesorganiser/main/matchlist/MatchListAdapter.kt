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
        holder.locationTextView.text = matches[position].summary
        holder.dateAndTimeTextView.text = matches[position].heading
        holder.itemView.setOnClickListener {
            matchListInteraction?.matchSelected(matchId = matches[position].matchId)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.match_list_item, parent, false)
        return MatchViewHolder(view)
    }

    class MatchViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val locationTextView: TextView by find(R.id.match_list_item_match_location)
        val dateAndTimeTextView: TextView by find(R.id.match_list_item_date_and_time)
    }

    interface MatchListInteraction {

        fun matchSelected(matchId: String)
    }

}