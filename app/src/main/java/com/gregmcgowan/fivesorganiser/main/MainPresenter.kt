package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreen.*
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
                    val authentication: Authentication) : MainContract.Presenter {

    private val disposables = CompositeDisposable()
    private val mainScreenStore = MainScreenStateStore()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startPresenting() {
        val disposable = model(events(mainParentUi), mainScreenStore, authentication)
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

    private fun model(events: Observable<MainScreenUiEvent>,
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
                        is MenuSelectedEvent -> setCurrentScreen(event.selectedScreen)
                                .map { menuSelectedReducer(it) }
                    }
                }
                .scan(initialUilModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering  $model") })
    }

    private fun setCurrentScreen(mainScreen: MainContract.MainScreen): Observable<MainContract.MainScreen> {
        this.mainScreenStore.currentScreen = mainScreen
        return just(mainScreen)
    }

    private fun authCompleteReducer(selectedScreen: MainContract.MainScreen)
            : MainScreenUiModelReducer = { model ->
        mainScreenUiModel(selectedScreen, model.copy(showLoading = false, showContent = true))
    }

    private fun menuSelectedReducer(selectedScreen: MainContract.MainScreen)
            : MainScreenUiModelReducer = { model ->
        mainScreenUiModel(selectedScreen, model)
    }

    private fun mainScreenUiModel(selectedScreen: MainContract.MainScreen,
                                  model: MainScreenUiModel): MainScreenUiModel {
        return when (selectedScreen) {
            is MatchesScreen -> model.copy(
                    selectedScreen = selectedScreen,
                    showMatchesView = true,
                    showPlayersView = false,
                    showResultsView = false
            )
            is PlayersScreen -> model.copy(
                    selectedScreen = selectedScreen,
                    showMatchesView = false,
                    showPlayersView = true,
                    showResultsView = false)
            is ResultsScreen -> model.copy(
                    selectedScreen = selectedScreen,
                    showMatchesView = false,
                    showPlayersView = false,
                    showResultsView = true
            )
        }
    }

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

    private class MainScreenStateStore {
        //default to players
        var currentScreen: MainContract.MainScreen = MainContract.MainScreen.PlayersScreen
    }

}