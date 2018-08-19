package com.gregmcgowan.fivesorganiser.main.matchlist

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListAdapter.MatchListInteraction
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.createMatchIntent
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MatchListFragment : BaseFragment() {

    companion object {
        const val MATCH_LIST_FRAGMENT_TAG = "MATCH_LIST_FRAGMENT_TAG"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var matchListViewModel: MatchListViewModel

    private lateinit var addMatchButton: FloatingActionButton
    private lateinit var matchList: RecyclerView
    private lateinit var progressView: View
    private lateinit var emptyView: View
    private lateinit var emptyMessage: TextView

    private val matchListAdapter = MatchListAdapter()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.match_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMatchButton = find<FloatingActionButton>(R.id.match_list_fab, view).value
        matchList = find<RecyclerView>(R.id.match_list, view).value
        progressView = find<View>(R.id.match_list_progress_bar, view).value
        emptyView = find<View>(R.id.match_list_empty_view_group, view).value
        emptyMessage = find<TextView>(R.id.match_list_empty_message, view).value
        matchList.adapter = matchListAdapter

        AndroidSupportInjection.inject(this)

        matchListViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MatchListViewModel::class.java)

        matchListViewModel
                .matchListUiModelLiveData
                .observeNonNull(this, this@MatchListFragment::render)
        matchListViewModel
                .navigationLiveData
                .observeNonNull(this, this@MatchListFragment::handleNavigationEvent)

        addMatchButton.setOnClickListener { matchListViewModel.addMatchButtonPressed() }

        matchListAdapter.matchListInteraction = object : MatchListInteraction {

            override fun editDateTimeAndLocation(matchId: String) {
                matchListViewModel.editMatchDateTimeAndLocation(matchId)
            }

            override fun editSquad(matchId: String) {
                matchListViewModel.editSquad(matchId)
            }
        }
    }

    private fun render(matchListUIModel: MatchListUiModel) {
        matchListAdapter.setMatches(matchListUIModel.matches.toMutableList())
        matchList.setVisibleOrGone(matchListUIModel.showList)
        progressView.setVisibleOrGone(matchListUIModel.showProgressBar)
        emptyView.setVisibleOrGone(matchListUIModel.showEmptyView)
        emptyMessage.text = matchListUIModel.emptyMessage
    }

    private fun handleNavigationEvent(navEvent: MatchNavigationEvent) {
        when (navEvent) {
            is MatchNavigationEvent.Idle -> {
                //Do nothing
            }
            else -> {
                requireStartActivity(requireContext()
                        .createMatchIntent(navEvent))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        matchListViewModel.onViewShown()
    }
}