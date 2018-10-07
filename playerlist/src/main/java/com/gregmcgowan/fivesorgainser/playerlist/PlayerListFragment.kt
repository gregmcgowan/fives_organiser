package com.gregmcgowan.fivesorgainser.playerlist

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PlayerListFragment : BaseFragment() {

    companion object {
        const val PLAYER_LIST_FRAGMENT_TAG = "PlayerListFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var importContactsNavigator: ImportContactsNavigator

    private lateinit var playerListViewModel: PlayerListViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var playerList: RecyclerView
    private lateinit var emptyState: View
    private lateinit var emptyStateMessage: TextView
    private lateinit var addPlayerButton: FloatingActionButton

    private val playerListAdapter = PlayerListAdapter()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.players_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AndroidSupportInjection.inject(this)

        progressBar = find<ProgressBar>(R.id.progress_bar, view).value
        playerList = find<RecyclerView>(R.id.player_list, view).value
        emptyState = find<View>(R.id.player_list_empty_view_group, view).value
        emptyStateMessage = find<TextView>(R.id.player_list_empty_message, view).value
        addPlayerButton = find<FloatingActionButton>(R.id.player_list_fab, view).value
        playerList.adapter = playerListAdapter

        playerListViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(PlayerListViewModel::class.java)

        playerListViewModel
                .playerUiModelLiveData
                .observeNonNull(this, this@PlayerListFragment::render)

        playerListViewModel
                .playerListNavigationLiveData
                .observeNonNull(this, this@PlayerListFragment::handleNavEvent)

        addPlayerButton.setOnClickListener { playerListViewModel.addPlayerButtonPressed() }
    }

    override fun onResume() {
        super.onResume()
        playerListViewModel.onViewShown()
    }

    private fun handleNavEvent(navEvent: PlayerListNavigationEvents) {
        when (navEvent) {
            PlayerListNavigationEvents.AddPlayerEvent -> {
                showAddPlayers()
            }
            PlayerListNavigationEvents.Idle -> {
                // Do nothing
            }
        }
    }

    private fun showAddPlayers() {
        importContactsNavigator.goToImportContacts()
    }

    private fun render(uiModel: PlayerListUiModel) {
        progressBar.setVisibleOrGone(uiModel.showLoading)
        emptyStateMessage.text = uiModel.errorMessage
        emptyState.setVisibleOrGone(uiModel.showErrorMessage)
        playerList.setVisibleOrGone(uiModel.showPlayers)
        playerListAdapter.setPlayers(uiModel.players.toMutableList())
    }


}