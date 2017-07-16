package com.gregmcgowan.fivesorganiser.players

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ViewBinder
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.find
import com.gregmcgowan.fivesorganiser.players.import.ImportContactsActivity

class PlayerListView(rootView: View,
                     val context: Context) : PlayerListContract.View {

    companion object {
        const val IMPORT_CONTACTS = 3
    }

    val progressBar: ProgressBar by find<ProgressBar>(R.id.progress_bar, rootView)
    val playerList: RecyclerView by find<RecyclerView>(R.id.player_list, rootView)
    val playerListAdapter: PlayerListAdapter = PlayerListAdapter()

    val emptyState: View by find<View>(R.id.player_list_empty_state, rootView)
    val emptyStateMessage: TextView by find<TextView>(R.id.player_list_empty_message, rootView)

    val fab: FloatingActionButton by find<FloatingActionButton>(R.id.fab, rootView)

    init {
        playerList.adapter = playerListAdapter
        fab.setOnClickListener({
            getContacts()
        })
    }

    override var viewState: ViewState by ViewBinder {
        when (it) {
            is ViewState.Loading -> {
                showProgressBar(true)
                showPlayerList(false)
                showEmptyState(false)
            }
            is ViewState.Error -> {
                showProgressBar(false)
                showPlayerList(false)
                showEmptyState(true)
                setEmptyState(it.errorMessage)
            }
            is ViewState.Success<*> -> {
                if (it.item is PlayerListContract.PlayerListModel) {
                    showEmptyState(false)
                    showProgressBar(false)
                    showPlayers(it.item.players)
                    showPlayerList(true)
                }
            }
        }
    }

    private fun showPlayers(players: List<Player>) {
        playerListAdapter.setPlayers(players)
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showPlayerList(show: Boolean) {
        playerList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun getContacts() {
        val activity = context as Activity
        activity.startActivityForResult(Intent(activity, ImportContactsActivity::class.java),
                IMPORT_CONTACTS)
    }

    private fun setEmptyState(message: String?) {
        emptyStateMessage.text = message
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
    }
}

