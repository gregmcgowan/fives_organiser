package com.gregmcgowan.fivesorganiser.main.playerlist

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
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.importcontacts.importContactsIntent
import javax.inject.Inject

const val IMPORT_CONTACTS = 1

class PlayerListFragment : BaseFragment() {

    companion object {
        const val PLAYER_LIST_FRAGMENT_TAG = "PlayerListFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var playerListViewModel: PlayerListViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var playerList: RecyclerView
    private lateinit var emptyState: View
    private lateinit var emptyStateMessage: TextView
    private lateinit var addPlayerButton: FloatingActionButton
    private val playerListAdapter: PlayerListAdapter = PlayerListAdapter()

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

        DaggerPlayerListComponent
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)

        activity?.let { _ ->
            playerListViewModel = ViewModelProviders
                    .of(this, viewModelFactory)
                    .get(PlayerListViewModel::class.java)

            playerListViewModel
                    .uiModel()
                    .observeNonNull(this, this@PlayerListFragment::render)

            playerListViewModel
                    .navigationEvents()
                    .observeNonNull(this, this@PlayerListFragment::handleNavEvent)

            addPlayerButton.setOnClickListener { playerListViewModel.addPlayerButtonPressed() }
        }
    }

    override fun onResume() {
        super.onResume()
        playerListViewModel.onViewShown()
    }

    private fun handleNavEvent(navEvent: PlayerListNavigationEvents) {
        when (navEvent) {
            PlayerListNavigationEvents.AddPlayerEvent ->
                showAddPlayers()
            PlayerListNavigationEvents.Idle -> {

            }
        }
    }

    private fun showAddPlayers() {
        requireStartActivityForResult(requireContext()
                .importContactsIntent(), IMPORT_CONTACTS)
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
        progressBar.setVisibleOrGone(show)
    }

    private fun showPlayerList(show: Boolean) {
        playerList.setVisibleOrGone(show)
    }

    private fun setEmptyState(message: String?) {
        emptyStateMessage.text = message
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.setVisibleOrGone(show)
    }


}