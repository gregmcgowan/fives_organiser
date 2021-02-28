package com.gregmcgowan.fivesorgainser.playerlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListUserEvent.AddPlayerSelectedEvent
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PlayerListFragment : BaseFragment() {

    companion object {
        const val PLAYER_LIST_FRAGMENT_TAG = "PlayerListFragment"
    }

    @Inject
    lateinit var navigator: ImportContactsNavigator

    private val playerListViewModel: PlayerListViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                PlayerListScreen(
                        uiModel = playerListViewModel.uiModel,
                        eventHandler = { playerListUserEvent ->
                            when (playerListUserEvent) {
                                AddPlayerSelectedEvent -> playerListViewModel.addPlayerButtonPressed()
                            }
                        }
                )
            }
        }
        lifecycleScope.launchWhenStarted {
            playerListViewModel.playerListUiEvents.collect { event ->
                when (event) {
                    PlayerListUiEvents.ShowAddPlayerScreenEvent -> {
                        navigator.goToImportContacts()
                    }
                    PlayerListUiEvents.Idle -> {
                        // Do nothing
                    }
                }
            }
        }

    }


}