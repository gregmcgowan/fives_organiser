package com.gregmcgowan.fivesorganiser.match.squad

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchFragment
import com.gregmcgowan.fivesorganiser.match.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.match_squad_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class MatchSquadFragment : MatchFragment, MatchSquadListInteractions, BaseFragment() {

    companion object {
        fun newInstance(matchId: String): MatchSquadFragment =
                MatchSquadFragment().apply {
                    val args = Bundle()
                    args.putString(MATCH_ID_INTENT_EXTRA, matchId)
                    arguments = args
                }
    }


    @Inject
    lateinit var matchSquadListAdapter: MatchSquadListAdapter

    private val matchSquadViewModel: MatchSquadViewModel by viewModels()
    private val navigationViewModel: MatchActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.match_squad_layout, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppCompatActivity().setSupportActionBar(match_squad_toolbar)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        match_squad_player_list.adapter = matchSquadListAdapter

        matchSquadViewModel
                .matchSquadUiModelLiveData
                .observeNonNull(this, this::render)

        matchSquadViewModel
                .matchSquadNavEventsLiveData
                .observeNonNull(this, this::handleNavEvent)

        val matchId = arguments?.getString(MATCH_ID_INTENT_EXTRA) ?: throw IllegalArgumentException()
        matchSquadViewModel.start(matchId)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_edit_match, menu)
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


