package com.gregmcgowan.fivesorganiser.main


data class MainScreenUiModel(
        val screenToShow: MainScreen,
        val showLoading: Boolean,
        val showContent: Boolean)


sealed class MainScreen {
    object PlayersScreen : MainScreen()
    object MatchesScreen : MainScreen()
    object ResultsScreen : MainScreen()
}
