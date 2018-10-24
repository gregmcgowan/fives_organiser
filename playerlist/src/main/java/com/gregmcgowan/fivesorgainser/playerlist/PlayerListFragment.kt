package com.gregmcgowan.fivesorgainser.playerlist

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.players_list.*
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
    private val playerListAdapter = PlayerListAdapter()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.players_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AndroidSupportInjection.inject(this)

        player_list.adapter = playerListAdapter

        playerListViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(PlayerListViewModel::class.java)

        playerListViewModel
                .playerUiModelLiveData
                .observeNonNull(this, this@PlayerListFragment::render)

        playerListViewModel
                .playerListNavigationLiveData
                .observeNonNull(this, this@PlayerListFragment::handleNavEvent)

        player_list_fab.setOnClickListener { playerListViewModel.addPlayerButtonPressed() }
    }

    override fun onResume() {
        super.onResume()
        playerListViewModel.onViewShown()
    }

    private fun handleNavEvent(navEvent: PlayerListNavigationEvents) {
        when (navEvent) {
            PlayerListNavigationEvents.AddPlayerEvent -> {
                importContactsNavigator.goToImportContacts()
            }
            PlayerListNavigationEvents.Idle -> {
                // Do nothing
            }
        }
    }

    private fun render(uiModel: PlayerListUiModel) {
        progress_bar.setVisibleOrGone(uiModel.showLoading)
        player_list_empty_message.text = uiModel.errorMessage
        player_list_empty_view_group.setVisibleOrGone(uiModel.showErrorMessage)
        player_list.setVisibleOrGone(uiModel.showPlayers)
        playerListAdapter.setPlayers(uiModel.players.toMutableList())
    }


}