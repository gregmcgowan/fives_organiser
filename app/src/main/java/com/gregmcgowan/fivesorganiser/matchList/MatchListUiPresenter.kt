package com.gregmcgowan.fivesorganiser.matchList

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.main.MainContract
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent.AddMatchSelected
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent.UiShown
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MatchListUiPresenter(val matchListUi: MatchListContract.Ui,
                           val matchRepo: MatchRepo,
                           val dateTimeFormatter: ZonedDateTimeFormatter) : MatchListContract.Presenter {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun startPresenting() {
        val initialUiModel = MatchListUiModel(
                showList = false,
                showProgressBar = true,
                showEmptyView = false,
                emptyMessage = null,
                matchList = emptyList(),
                goToMatchScreen = false
        )

        val events = Observable.merge(
                Observable.just(UiShown()), matchListUi.uiEvents())
                .share()

        disposables.add(
                processEvents(initialUiModel, events)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { uiModel -> matchListUi.render(uiModel) })
    }

    private fun processEvents(initialUiModel: MatchListUiModel,
                              events: Observable<MatchListUiEvent>): Observable<MatchListUiModel> {
        return events
                .flatMap { event ->
                    when (event) {
                        is UiShown -> {
                            matchRepo.getAllMatches()
                                    .toObservable()
                                    .map { matches -> loadMatchesReducer(matches, dateTimeFormatter) }
                        }
                        is AddMatchSelected -> {
                            Observable.just(showMatchScreenReducer())
                        }
                    }
                }
                .scan(initialUiModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering $model") })
    }


    override fun stopPresenting() {
        disposables.clear()
    }

    override fun screenName(): MainContract.MainScreen {
        return MainContract.MainScreen.MatchesScreen
    }
}