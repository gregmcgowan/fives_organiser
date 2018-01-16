package com.gregmcgowan.fivesorganiser.main.playerList

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.getApp
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsActivity
import com.gregmcgowan.fivesorganiser.main.MainViewModelFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

const val IMPORT_CONTACTS = 1

class PlayerListFragment : Fragment() {

    companion object {
        val PLAYER_LIST_FRAGMENT_TAG = "PlayerListFragment"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var playerList: RecyclerView
    private lateinit var emptyState: View
    private lateinit var emptyStateMessage: TextView
    private lateinit var addPlayerButton: FloatingActionButton

    private val playerListAdapter: PlayerListAdapter = PlayerListAdapter()
    private lateinit var playerListViewModel: PlayerListViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.players_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = find<ProgressBar>(R.id.progress_bar, view).value
        playerList = find<RecyclerView>(R.id.player_list, view).value
        emptyState = find<View>(R.id.player_list_empty_view_group, view).value
        emptyStateMessage = find<TextView>(R.id.player_list_empty_message, view).value
        addPlayerButton = find<FloatingActionButton>(R.id.player_list_fab, view).value

        playerList.adapter = playerListAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            playerListViewModel = ViewModelProviders
                    .of(this, MainViewModelFactory(it.getApp().dependencies, UI, CommonPool))
                    .get(PlayerListViewModel::class.java)

            playerListViewModel.uiModel().observe(this,
                    Observer<PlayerListUiModel?> { uiModel ->
                        uiModel?.let {
                            render(it)
                        }
                    })
            playerListViewModel.navigationEvents().observe(this,
                    Observer<PlayerListNavigationEvents?> { navEvent ->
                        navEvent?.let {
                            handleNavEvent(navEvent)
                        }
                    })
            addPlayerButton.setOnClickListener({
                playerListViewModel.addPlayerButtonPressed()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        playerListViewModel.onViewShown()
    }

    private fun handleNavEvent(navEvent: PlayerListNavigationEvents) {
        when (navEvent) {
            is PlayerListNavigationEvents.AddPlayerEvent ->
                showAddPlayers()
            is PlayerListNavigationEvents.Idle -> {

            }
        }
    }

    private fun showAddPlayers() {
        val activity = context as Activity
        activity.startActivityForResult(Intent(activity, ImportContactsActivity::class.java),
                IMPORT_CONTACTS)
    }

    private fun render(uiModel: PlayerListUiModel) {
        showProgressBar(uiModel.showLoading)
        setEmptyState(uiModel.errorMessage)
        showEmptyState(uiModel.showErrorMessage)
        showPlayerList(uiModel.showPlayers)
        showPlayers(uiModel.players)
    }

    private fun showPlayers(players: List<PlayerListItemUiModel>) {
        playerListAdapter.setPlayers(players.toMutableList())
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.setVisible(show)
    }

    private fun showPlayerList(show: Boolean) {
        playerList.setVisible(show)
    }

    private fun setEmptyState(message: String?) {
        emptyStateMessage.text = message
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.setVisible(show)
    }


}