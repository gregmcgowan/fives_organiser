package com.gregmcgowan.fivesorganiser.matchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.MatchNavigator
import com.gregmcgowan.fivesorganiser.matchlist.MatchListAdapter.MatchListInteraction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.match_list.*
import javax.inject.Inject

@AndroidEntryPoint
class MatchListFragment : BaseFragment() {

    companion object {
        const val MATCH_LIST_FRAGMENT_TAG = "MATCH_LIST_FRAGMENT_TAG"
    }

    @Inject
    lateinit var matchNavigator: MatchNavigator

    @Inject
    lateinit var matchListAdapter: MatchListAdapter

    private val matchListViewModel: MatchListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.match_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        match_list.adapter = matchListAdapter

        matchListViewModel
                .matchListUiModelLiveData
                .observeNonNull(this, this@MatchListFragment::render)
        matchListViewModel
                .navigationLiveData
                .observeNonNull(this, this@MatchListFragment::handleNavigationEvent)

        match_list_fab.setOnClickListener { matchListViewModel.addMatchButtonPressed() }

        matchListAdapter.matchListInteraction = object : MatchListInteraction {

            override fun editMatchDetails(matchId: String) {
                matchListViewModel.editMatchDetails(matchId)
            }

            override fun editSquad(matchId: String) {
                matchListViewModel.editSquad(matchId)
            }
        }
    }

    private fun render(matchListUIModel: MatchListUiModel) {
        matchListAdapter.setMatches(matchListUIModel.matches.toMutableList())
        match_list.setVisibleOrGone(matchListUIModel.showList)
        match_list_progress_bar.setVisibleOrGone(matchListUIModel.showProgressBar)
        match_list_empty_view_group.setVisibleOrGone(matchListUIModel.showEmptyView)
        match_list_empty_message.text = matchListUIModel.emptyMessage
    }

    private fun handleNavigationEvent(navEvent: MatchNavigationEvent) {
        matchNavigator.handleEvent(navEvent)
    }

    override fun onResume() {
        super.onResume()
        matchListViewModel.onViewShown()
    }
}