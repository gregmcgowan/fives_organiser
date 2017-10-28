package com.gregmcgowan.fivesorganiser.matchList

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.main.MainContract
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.*
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiResult.FinishLoading
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiResult.StartLoading
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiEvent.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
                matches = emptyList(),
                goToMatchScreen = false,
                matchIdSelected = null
        )

        val events = Observable.merge(
                Observable.just(UiShown()),
                matchListUi.uiEvents(),
                matchListUi.matchSelected()
        )
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
                                    .flatMap { matches -> Observable.just<MatchListUiResult?>(FinishLoading(matches)) }
                                    .startWith(Observable.just<MatchListUiResult?>(StartLoading()))
                                    .map {
                                        when (it) {
                                            is StartLoading -> loadingReducer()
                                            is FinishLoading -> loadMatchesReducer(it.matches, dateTimeFormatter)
                                        }
                                    }
                                    .subscribeOn(Schedulers.io())

                        }
                        is AddMatchSelected -> {
                            Observable.just(showMatchScreenReducer())
                        }
                        is MatchSelectedEvent -> {
                            Observable.just(showEditMatchScreenReducer(event.matchId))
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