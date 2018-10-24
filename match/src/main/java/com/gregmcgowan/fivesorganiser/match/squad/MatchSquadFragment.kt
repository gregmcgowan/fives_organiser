package com.gregmcgowan.fivesorganiser.match.squad

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchFragment
import com.gregmcgowan.fivesorganiser.match.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.match_squad_layout.*
import javax.inject.Inject

class MatchSquadFragment : MatchFragment, MatchSquadListInteractions, BaseFragment() {

    companion object {
        fun newInstance(matchId: String): MatchSquadFragment =
                MatchSquadFragment().apply {
                    val args = Bundle()
                    args.putString(MATCH_ID_INTENT_EXTRA, matchId)
                    arguments = args
                }
    }

    val matchId: String
        get() = arguments?.getString(MATCH_ID_INTENT_EXTRA) ?: throw IllegalArgumentException()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var matchSquadListAdapter: MatchSquadListAdapter

    private lateinit var matchSquadViewModel: MatchSquadViewModel
    private lateinit var navigationViewModel: MatchActivityViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.match_squad_layout, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppCompatActivity().setSupportActionBar(match_squad_toolbar)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AndroidSupportInjection.inject(this)

        match_squad_player_list.adapter = matchSquadListAdapter

        matchSquadViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MatchSquadViewModel::class.java)

        navigationViewModel = ViewModelProviders
                .of(requireActivity())
                .get(MatchActivityViewModel::class.java)

        matchSquadViewModel
                .matchSquadUiModelLiveData
                .observeNonNull(this, this::render)

        matchSquadViewModel
                .matchSquadNavEventsLiveData
                .observeNonNull(this, this::handleNavEvent)
    }

    private fun render(uninvitedPlayersUiModel: MatchSquadUiModel) {
        match_squad_progress_bar.setVisibleOrGone(uninvitedPlayersUiModel.showLoading)
        match_squad_content.setVisibleOrGone(uninvitedPlayersUiModel.showContent)
        matchSquadListAdapter.setUninvitedPlayers(uninvitedPlayersUiModel.playersListUi)
    }

    private fun handleNavEvent(navEvent: MatchSquadListNavEvent) {
        when (navEvent) {
            MatchSquadListNavEvent.Idle -> {
                // Do nothing
            }
            MatchSquadListNavEvent.CloseScreen -> {
                navigationViewModel.backButtonPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.add_edit_match, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun consumeBackPress(): Boolean {
        matchSquadViewModel.handleBackPressed()
        return true
    }

    override fun playerUpdated(playerId: String, status: MatchSquadListPlayerStatus) {
        matchSquadViewModel.handlePlayerStatusChanged(playerId, status)
    }
}


