package com.gregmcgowan.fivesorganiser.match.squad

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ProgressBar
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchFragment
import com.gregmcgowan.fivesorganiser.match.R
import dagger.android.support.AndroidSupportInjection
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

    private lateinit var matchSquadList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var content: View

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.match_squad_layout, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppCompatActivity().setSupportActionBar(find<Toolbar>(R.id.match_squad_toolbar).value)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        matchSquadList = find<RecyclerView>(R.id.match_squad_player_list).value
        progressBar = find<ProgressBar>(R.id.match_squad_progress_bar).value
        content = find<View>(R.id.match_squad_content).value

        AndroidSupportInjection.inject(this)

        matchSquadList.adapter = matchSquadListAdapter

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
        progressBar.setVisibleOrGone(uninvitedPlayersUiModel.showLoading)
        content.setVisibleOrGone(uninvitedPlayersUiModel.showContent)
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


