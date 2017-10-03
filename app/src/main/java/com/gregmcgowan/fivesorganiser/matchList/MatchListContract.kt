package com.gregmcgowan.fivesorganiser.matchList

import com.gregmcgowan.fivesorganiser.main.MainContract
import io.reactivex.Observable


interface MatchListContract {

    interface Ui {
        fun uiEvents(): Observable<MatchListUiEvent>

        fun render(matchListUIModel: MatchListUiModel)

        fun showCreateMatch()
    }

    interface Presenter : MainContract.MainUiPresenter

    sealed class MatchListUiEvent {
        class AddMatchSelected : MatchListUiEvent()
        class UiShown : MatchListUiEvent()
    }

    data class MatchListUiModel(
            val showList: Boolean,
            val showProgressBar: Boolean,
            val showEmptyView: Boolean,
            val emptyMessage: String?,
            val matchList: List<MatchListItemUiModel>,
            val goToMatchScreen : Boolean
    )

    data class MatchListItemUiModel(val location: String,
                                    val dateAndTime: String)

}