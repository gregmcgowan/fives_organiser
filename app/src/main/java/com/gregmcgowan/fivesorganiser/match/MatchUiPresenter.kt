package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.gregmcgowan.fivesorganiser.match.MatchContract.*
import com.gregmcgowan.fivesorganiser.match.MatchContract.MatchUiEvent.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

typealias CreateMatchModelReducer = (MatchUiModel) -> MatchUiModel

class MatchUiPresenter(private val ui: MatchContract.Ui,
                       private val reducers: Reducers,
                       private val matchStore: MatchStore) : MatchContract.Presenter {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun startPresenting() {

        val initialUiModel = MatchUiModel(
                showContent = false,
                loading = true,
                success = false,
                errorMessage = null,
                date = "",
                time = "",
                location = "",
                timePickerUiModel = TimePickerUiModel.Idle,
                datePickerUiModel = DatePickerUiModel.Idle
        )

        val events = Observable.merge(
                listOf(
                        Observable.just(UiShownEvent()),
                        ui.timeSelected(),
                        ui.timeUpdated(),
                        ui.dateSelected(),
                        ui.dateUpdated(),
                        ui.locationUpdated(),
                        ui.backPressed(),
                        ui.savePressed()
                ))
                .doAfterNext { event -> Timber.d("view sending event [${event.javaClass.name}]") }
                .share()

        disposables.add(
                process(events, initialUiModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { uiModel -> ui.render(uiModel) }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun stopPresenting() {
        disposables.clear()
    }

    private fun process(events: Observable<MatchUiEvent>,
                        initialUiModel: MatchUiModel): Observable<MatchUiModel> {

        return events
                .flatMap { event ->
                    when (event) {
                        is UiShownEvent -> {
                            matchStore.loadMatch()
                                    .map { match -> reducers.displayMatchReducer(match) }
                                    .toObservable()
                        }
                        is TimeSelected -> {
                            matchStore.getMatch()
                                    .map { match -> reducers.timeSelectedReducer(match) }
                                    .toObservable()
                        }
                        is DateSelected -> {
                            matchStore.getMatch()
                                    .map { match -> reducers.dateSelectedReducer(match) }
                                    .toObservable()
                        }
                        is DateUpdatedEvent -> {
                            matchStore.updateDate(event)
                                    .map { match -> reducers.dateUpdatedReducer(match) }
                                    .toObservable()
                        }
                        is TimeUpdatedEvent -> {
                            matchStore.updateTime(event)
                                    .map { match -> reducers.timeUpdatedReducer(match) }
                                    .toObservable()
                        }
                        is LocationUpdatedEvent -> {
                            matchStore.updateLocation(event)
                                    .map { match -> reducers.locationUpdatedReducer(match) }
                                    .toObservable()
                        }
                        is BackArrowPressedEvent -> {
                            Observable.just(reducers.closeScreenReducer())
                        }
                        is SaveButtonPressedEvent -> {
                            matchStore.saveMatch()
                                    .andThen(Observable.just<MatchEventResults?>(MatchEventResults.SaveFinishedResult()))
                                    .startWith(Observable.just<MatchEventResults?>(MatchEventResults.SaveStartedResult()))
                                    .map {
                                        when (it) {
                                            is MatchEventResults.SaveStartedResult -> reducers.savingReducer()
                                            is MatchEventResults.SaveFinishedResult -> reducers.closeScreenReducer()
                                        }
                                    }
                        }
                    }
                }
                .scan(initialUiModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering $model") })
    }


}