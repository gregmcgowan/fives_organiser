package com.gregmcgowan.fivesorganiser.players

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.find

class PlayerListView : FrameLayout, PlayerListContract.View {

    val rootView: ViewGroup
    val progressBar: ProgressBar by find<ProgressBar>(R.id.progress_bar)
    val playerList: RecyclerView by find<RecyclerView>(R.id.player_list)
    val playerListAdapter: PlayerListAdapter = PlayerListAdapter()

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs ) {
        rootView = LayoutInflater.from(context).inflate(R.layout.players_list, this, false) as ViewGroup
        addView(rootView)
        playerList.adapter = playerListAdapter
    }

    override fun showPlayers(players: List<Player>) {
        playerListAdapter.setPlayers(players)
    }

    override fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) VISIBLE else GONE
    }

    override fun showPlayerList(show: Boolean) {
        playerList.visibility = if (show) VISIBLE else GONE
    }
}

