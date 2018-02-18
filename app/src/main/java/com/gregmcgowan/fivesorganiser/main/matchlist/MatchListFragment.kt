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
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListAdapter.MatchListInteraction
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListNavigationEvents.AddMatchEvent
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListNavigationEvents.MatchSelected
import com.gregmcgowan.fivesorganiser.match.createMatchIntent
import com.gregmcgowan.fivesorganiser.match.editMatchIntent
import javax.inject.Inject

class MatchListFragment : BaseFragment() {

    private lateinit var addMatchButton: FloatingActionButton
    private lateinit var matchList: RecyclerView
    private lateinit var progressView: View
    private lateinit var emptyView: View
    private lateinit var emptyMessage: TextView

    private val matchListAdapter = MatchListAdapter()

    private lateinit var matchListViewModel: MatchListViewModel

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        const val MATCH_LIST_FRAGMENT_TAG = "MATCH_LIST_FRAGMENT_TAG"
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.match_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMatchButton = find<FloatingActionButton>(R.id.match_list_fab, view).value
        matchList = find<RecyclerView>(R.id.match_list, view).value
        progressView = find<View>(R.id.match_list_progress_bar, view).value
        emptyView = find<View>(R.id.match_list_empty_view_group, view).value
        emptyMessage = find<TextView>(R.id.match_list_empty_message, view).value
        matchList.adapter = matchListAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        matchListAdapter.matchListInteraction = object : MatchListInteraction {
            override fun matchSelected(matchId: String) {
                matchListViewModel.matchSelected(matchId)
            }
        }

        DaggerMatchListComponent
                .builder()
                .appComponent(appComponent)
                .build().inject(this)

        activity?.let {
            matchListViewModel = ViewModelProviders
                    .of(this, viewModelFactory)
                    .get(MatchListViewModel::class.java)

            matchListViewModel
                    .uiModelLiveData()
                    .observeNonNull(this, this@MatchListFragment::render)
            matchListViewModel
                    .navigationLiveData()
                    .observeNonNull(this, this@MatchListFragment::handleNavigationEvent)

            addMatchButton.setOnClickListener({
                matchListViewModel.addMatchButtonPressed()
            })
        }

    }

    private fun handleNavigationEvent(navEvent: MatchListNavigationEvents) {
        when (navEvent) {
            is MatchSelected -> {
                activity?.startActivity(context?.editMatchIntent(navEvent.matchId))
            }
            AddMatchEvent -> {
                activity?.startActivity(context?.createMatchIntent())
            }
            MatchListNavigationEvents.Idle -> {
                //Do nothing
            }
        }
    }

    override fun onResume() {
        super.onResume()
        matchListViewModel.onViewShown()
    }


    private fun render(matchListUIModel: MatchListUiModel) {
        matchListAdapter.setMatches(matchListUIModel.matches.toMutableList())
        matchList.setVisible(matchListUIModel.showList)
        progressView.setVisible(matchListUIModel.showProgressBar)
        emptyView.setVisible(matchListUIModel.showEmptyView)
        emptyMessage.text = matchListUIModel.emptyMessage
    }

}