package com.gregmcgowan.fivesorganiser.matchList

import com.gregmcgowan.fivesorganiser.core.data.match.Match
import com.gregmcgowan.fivesorganiser.main.MainContract
import io.reactivex.Observable


interface MatchListContract {

    interface Ui {
        fun uiEvents(): Observable<MatchListUiEvent>

        fun render(matchListUIModel: MatchListUiModel)

        fun matchSelected() : Observable<MatchListUiEvent>
    }

    interface Presenter : MainContract.MainUiPresenter

    sealed class MatchListUiEvent {
        class AddMatchSelected : MatchListUiEvent()
        class UiShown : MatchListUiEvent()
        class MatchSelectedEvent(val matchId : String) : MatchListUiEvent()
    }


    sealed class MatchListUiResult {
        class StartLoading : MatchListUiResult()
        class FinishLoading(val matches : List<Match>) : MatchListUiResult()
    }

    data class MatchListUiModel(
            val showList: Boolean,
            val showProgressBar: Boolean,
            val showEmptyView: Boolean,
            val emptyMessage: String?,
            val matches: List<MatchListItemUiModel>,
            val goToMatchScreen : Boolean,
            val matchIdSelected : String?
    )

    data class MatchListItemUiModel(val matchId : String,
                                    val location: String,
                                    val dateAndTime: String)

}