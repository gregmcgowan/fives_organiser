package com.gregmcgowan.fivesorganiser.playerList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsActivity
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListItemUiModel
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiEvent
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiEvent.*
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable

class PlayerListUi(rootView: View,
                   val context: Context) : PlayerListContract.Ui {

    companion object {
        const val IMPORT_CONTACTS = 3
    }

    private val progressBar: ProgressBar by find(R.id.progress_bar, rootView)
    private val playerList: RecyclerView by find(R.id.player_list, rootView)
    private val playerListAdapter: PlayerListAdapter = PlayerListAdapter()
    private val emptyState: View by find(R.id.player_list_empty_view_group, rootView)
    private val emptyStateMessage: TextView by find(R.id.player_list_empty_message, rootView)
    private val fab: FloatingActionButton by find(R.id.player_list_fab, rootView)
    private val addPlayerButtonClicks : Observable<PlayerListUiEvent>

    init {
        playerList.adapter = playerListAdapter
        addPlayerButtonClicks = RxView.clicks(fab)
                .map { _ -> AddPlayerEvent() }
    }

    override fun render(uiModel: PlayerListContract.PlayerListUiModel) {
        if(!uiModel.showAddPlayers) {
            showProgressBar(uiModel.showLoading)
            setEmptyState(uiModel.errorMessage)
            showEmptyState(uiModel.showErrorMessage)
            showPlayerList(uiModel.showPlayers)
            showPlayers(uiModel.players)
        } else {
            showAddPlayers()
        }
    }

    override fun addPlayerButtonEvents(): Observable<PlayerListUiEvent> {
        return addPlayerButtonClicks
    }

    override fun showAddPlayers() {
        val activity = context as Activity
        activity.startActivityForResult(Intent(activity, ImportContactsActivity::class.java),
                IMPORT_CONTACTS)
    }

    private fun showPlayers(players: List<PlayerListItemUiModel>) {
        playerListAdapter.setPlayers(players)
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showPlayerList(show: Boolean) {
        playerList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setEmptyState(message: String?) {
        emptyStateMessage.text = message
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
    }
}

