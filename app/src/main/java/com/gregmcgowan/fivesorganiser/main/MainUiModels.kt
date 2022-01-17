package com.gregmcgowan.fivesorganiser.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.main.MainScreen.PlayersScreen
import com.gregmcgowan.fivesorganiser.main.MainScreen.MatchesScreen
import com.gregmcgowan.fivesorganiser.main.MainScreen.ResultsScreen

data class MainScreenUiModel(
        val screenToShow: MainScreen,
        val mainTabScreens: List<MainScreen> = listOf(PlayersScreen, MatchesScreen, ResultsScreen)
)


sealed class MainScreen(val route: String,
                        @StringRes val resourceId: Int,
                        @DrawableRes val iconRes: Int) {

    object PlayersScreen : MainScreen(
            "players",
            R.string.players_title,
            R.drawable.ic_people_black_24dp
    )

    object MatchesScreen : MainScreen(
            "matches",
            R.string.matches_title,
            R.drawable.ic_timeline_black_24dp
    )

    object ResultsScreen : MainScreen(
            "results",
            R.string.results_title,
            R.drawable.ic_results_black_24dp
    )
}

sealed class MainScreenUiEvents {
    class ShowMainTabScreen(val route: String) : MainScreenUiEvents()
}
