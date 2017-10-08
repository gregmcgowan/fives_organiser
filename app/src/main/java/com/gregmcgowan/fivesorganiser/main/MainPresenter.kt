package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenActions.AuthenticationFinished
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiEvent
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiEvent.MainScreenShown
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiEvent.MenuSelectedEvent
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.merge
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MainPresenter(val mainParentUi: MainContract.ParentUi,
                    val mainViewPresenters: List<MainContract.MainUiPresenter>,
                    val authentication: Authentication,
                    val mainScreenStore: MainScreenStateStore) : MainContract.Presenter {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startPresenting() {
        val disposable = process(events(mainParentUi), mainScreenStore, authentication)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { uiModel ->
                    mainParentUi.render(uiModel)
                    updateMainUiPresenters(uiModel)
                }
        disposables.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopPresenting() {
        disposables.clear()
    }

    private fun events(mainParentUi: MainContract.ParentUi): Observable<MainScreenUiEvent> {
        return merge(listOf(just(MainScreenShown()), mainParentUi.menuSelected()))
                .doAfterNext { event ->
                    Timber.d("View sending event [${event.javaClass.name}]")
                }
                .share()
    }

    private fun process(events: Observable<MainScreenUiEvent>,
                        mainScreenStore: MainScreenStateStore,
                        authentication: Authentication): Observable<MainScreenUiModel> {
        val initialUilModel = MainScreenUiModel(
                mainScreenStore.currentScreen,
                showMatchesView = false,
                showPlayersView = false,
                showResultsView = false,
                showContent = false,
                showLoading = true
        )

        return events
                .flatMap { event ->
                    when (event) {
                        is MainScreenShown ->
                            if (authentication.isInitialised()) {
                                Completable.complete()
                                        .toSingleDefault(AuthenticationFinished())
                                        .toObservable()
                                        .map { authCompleteReducer(mainScreenStore.currentScreen) }
                            } else {
                                authentication.initialise()
                                        .toSingleDefault(AuthenticationFinished())
                                        .toObservable()
                                        .map { authCompleteReducer(mainScreenStore.currentScreen) }
                            }
                        is MenuSelectedEvent -> mainScreenStore.setCurrentScreen(event.selectedScreen)
                                .map { menuSelectedReducer(it) }
                    }
                }
                .scan(initialUilModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering  $model") })
    }

    //TODO should we pass the MainScreenStateStore to the subpresenters and the observe that
    //instead?
    private fun updateMainUiPresenters(uiModel: MainScreenUiModel) {
        if (!uiModel.showLoading && uiModel.showContent) {
            for (presenter in mainViewPresenters) {
                if (presenter.screenName() == uiModel.selectedScreen) {
                    presenter.startPresenting()
                } else {
                    presenter.stopPresenting()
                }
            }
        }
    }



}