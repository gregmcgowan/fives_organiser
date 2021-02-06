package com.gregmcgowan.fivesorgainser.playerlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setTextIfValidRes
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerListFragment : BaseFragment() {

    companion object {
        const val PLAYER_LIST_FRAGMENT_TAG = "PlayerListFragment"
    }

    @Inject
    lateinit var navigator: ImportContactsNavigator

    @Inject
    lateinit var playerListAdapter: PlayerListAdapter

    private val playerListViewModel: PlayerListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.players_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.player_list).adapter = playerListAdapter

        playerListViewModel
                .playerUiModelLiveData
                .observeNonNull(this, this@PlayerListFragment::render)

        playerListViewModel
                .playerListNavigationLiveData
                .observeNonNull(this, this@PlayerListFragment::handleNavEvent)

        view.findViewById<FloatingActionButton>(R.id.player_list_fab).setOnClickListener { playerListViewModel.addPlayerButtonPressed() }
    }

    private fun handleNavEvent(navEvent: PlayerListNavigationEvents) {
        when (navEvent) {
            PlayerListNavigationEvents.AddPlayerEvent -> {
                navigator.goToImportContacts()
            }
            PlayerListNavigationEvents.Idle -> {
                // Do nothing
            }
        }
    }

    private fun render(uiModel: PlayerListUiModel) {
        requireView().findViewById<View>(R.id.progress_bar).setVisibleOrGone(uiModel.showLoading)
        requireView().findViewById<TextView>(R.id.player_list_empty_message).setTextIfValidRes(uiModel.errorMessage)
        requireView().findViewById<View>(R.id.player_list_empty_view_group).setVisibleOrGone(uiModel.showErrorMessage)
        requireView().findViewById<View>(R.id.player_list).setVisibleOrGone(uiModel.showPlayers)
        playerListAdapter.setPlayers(uiModel.players.toMutableList())
    }


}