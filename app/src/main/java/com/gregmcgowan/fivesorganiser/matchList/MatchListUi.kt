package com.gregmcgowan.fivesorganiser.matchList

import android.app.Activity
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.match.createMatchIntent
import com.gregmcgowan.fivesorganiser.match.editMatchIntent
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent.AddMatchSelected
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiModel
import io.reactivex.Observable

class MatchListUi(rootView: View,
                  val context: Context) : MatchListContract.Ui {

    private val addMatchButton: FloatingActionButton by find(R.id.match_list_fab, rootView)
    private val matchList: RecyclerView by find(R.id.match_list, rootView)
    private val progressView: View by find(R.id.match_list_progress_bar, rootView)
    private val emptyView: View by find(R.id.match_list_empty_view_group, rootView)
    private val emptyMessage: TextView by find(R.id.match_list_empty_message, rootView)

    private val userActionsObservable: Observable<MatchListUiEvent>
    private val matchListAdapter = MatchListAdapter()

    init {
        userActionsObservable = Observable.create { emitter ->
            addMatchButton.setOnClickListener({ emitter.onNext(AddMatchSelected()) })
            emitter.setCancellable { addMatchButton.setOnClickListener(null) }
        }
        matchList.adapter = matchListAdapter
    }

    override fun uiEvents(): Observable<MatchListUiEvent> = userActionsObservable

    override fun render(matchListUIModel: MatchListUiModel) {
        if(matchListUIModel.goToMatchScreen) {
            val activity = context as Activity
            if(matchListUIModel.matchIdSelected != null) {
                activity.startActivity(context.editMatchIntent(matchListUIModel.matchIdSelected))
            } else {
                activity.startActivity(context.createMatchIntent())
            }

            return
        }

        matchListAdapter.setMatches(matchListUIModel.matches.toMutableList())
        matchList.setVisible(matchListUIModel.showList)
        progressView.setVisible(matchListUIModel.showProgressBar)
        emptyView.setVisible(matchListUIModel.showEmptyView)
        emptyMessage.text = matchListUIModel.emptyMessage
    }

    override fun matchSelected(): Observable<MatchListUiEvent> {
        return matchListAdapter.matchSelectedObservable as Observable<MatchListUiEvent>
    }
}
