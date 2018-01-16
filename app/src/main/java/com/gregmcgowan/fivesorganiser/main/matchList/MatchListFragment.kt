package com.gregmcgowan.fivesorganiser.main.matchList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.getApp
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.main.MainViewModelFactory
import com.gregmcgowan.fivesorganiser.main.matchList.MatchListNavigationEvents.AddMatchEvent
import com.gregmcgowan.fivesorganiser.main.matchList.MatchListNavigationEvents.MatchSelected
import com.gregmcgowan.fivesorganiser.match.createMatchIntent
import com.gregmcgowan.fivesorganiser.match.editMatchIntent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

class MatchListFragment : Fragment() {

    private lateinit var addMatchButton: FloatingActionButton
    private lateinit var matchList: RecyclerView
    private lateinit var progressView: View
    private lateinit var emptyView: View
    private lateinit var emptyMessage: TextView

    private val matchListAdapter = MatchListAdapter()

    private lateinit var matchListViewModel: MatchListViewModel

    companion object {
        val MATCH_LIST_FRAGMENT_TAG = "MATCH_LIST_FRAGMENT_TAG"
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

        matchListAdapter.matchListInteraction = object : MatchListAdapter.MatchListInteraction {
            override fun matchSelected(matchId: String) {
                matchListViewModel.matchSelected(matchId)
            }
        }

        activity?.let {
            matchListViewModel = ViewModelProviders
                    .of(this, MainViewModelFactory(it.getApp().dependencies, UI, CommonPool))
                    .get(MatchListViewModel::class.java)

            matchListViewModel
                    .uiModelLiveData()
                    .observe(this,
                            Observer<MatchListUiModel?> { uiModel ->
                                uiModel?.let {
                                    render(it)
                                }
                            })
            matchListViewModel
                    .navigationLiveData()
                    .observe(this,
                            Observer<MatchListNavigationEvents?> { navModel ->
                                navModel?.let {
                                    handleNavigationEvent(it)
                                }
                            })

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
            is AddMatchEvent -> {
                activity?.startActivity(context?.createMatchIntent())
            }
            is MatchListNavigationEvents.Idle -> {
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