package com.gregmcgowan.fivesorganiser.main

typealias MainScreenUiModelReducer = (MainContract.MainScreenUiModel) -> MainContract.MainScreenUiModel

internal fun authCompleteReducer(selectedScreen: MainContract.MainScreen)
        : MainScreenUiModelReducer = { model ->
    mainScreenUiModel(selectedScreen, model.copy(showLoading = false, showContent = true))
}

internal fun menuSelectedReducer(selectedScreen: MainContract.MainScreen)
        : MainScreenUiModelReducer = { model ->
    mainScreenUiModel(selectedScreen, model)
}

internal fun mainScreenUiModel(selectedScreen: MainContract.MainScreen,
                              model: MainContract.MainScreenUiModel): MainContract.MainScreenUiModel {
    return when (selectedScreen) {
        is MainContract.MainScreen.MatchesScreen -> model.copy(
                selectedScreen = selectedScreen,
                showMatchesView = true,
                showPlayersView = false,
                showResultsView = false
        )
        is MainContract.MainScreen.PlayersScreen -> model.copy(
                selectedScreen = selectedScreen,
                showMatchesView = false,
                showPlayersView = true,
                showResultsView = false)
        is MainContract.MainScreen.ResultsScreen -> model.copy(
                selectedScreen = selectedScreen,
                showMatchesView = false,
                showPlayersView = false,
                showResultsView = true
        )
    }
}