package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.LifecycleObserver
import io.reactivex.Observable


interface MainContract {

    data class MainScreenUiModel(
            val selectedScreen: MainScreen,
            val showMatchesView: Boolean,
            val showPlayersView: Boolean,
            val showResultsView: Boolean,
            val showLoading: Boolean,
            val showContent: Boolean)

    interface ParentUi {

        fun menuSelected(): Observable<MainScreenUiEvent>

        fun render(mainScreenUiModel: MainScreenUiModel);
    }

    interface Presenter : LifecycleObserver {
        fun startPresenting()
        fun stopPresenting()
    }

    interface MainUiPresenter {
        fun screenName(): MainScreen
        fun startPresenting()
        fun stopPresenting()
    }

    sealed class MainScreenUiEvent {
        class MenuSelectedEvent(val selectedScreen : MainScreen) : MainScreenUiEvent()
        class MainScreenShown : MainScreenUiEvent()
    }

    sealed class MainScreen {
        object PlayersScreen : MainScreen()
        object MatchesScreen : MainScreen()
        object ResultsScreen : MainScreen()
    }

    sealed class MainScreenActions {
        class AuthenticationFinished : MainScreenActions()
    }
}