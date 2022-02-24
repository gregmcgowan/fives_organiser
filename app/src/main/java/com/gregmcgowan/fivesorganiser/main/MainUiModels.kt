package com.gregmcgowan.fivesorganiser.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.main.MainScreen.PlayersListScreen
import com.gregmcgowan.fivesorganiser.main.MainScreen.MatchesListScreen
import com.gregmcgowan.fivesorganiser.main.MainScreen.ResultsListScreen
import com.gregmcgowan.fivesorganiser.navigation.Destinations

data class MainScreenUiModel(
        val screenToShow: MainScreen,
        val showBottomNavigation : Boolean = true,
        val mainTabScreens: List<MainScreen> = listOf(PlayersListScreen, MatchesListScreen, ResultsListScreen)
)


sealed class MainScreen(val route: String,
                        @StringRes val resourceId: Int,
                        @DrawableRes val iconRes: Int) {

    object PlayersListScreen : MainScreen(
            Destinations.PLAYER_LIST_ROUTE,
            R.string.players_title,
            R.drawable.ic_people_black_24dp
    )

    object MatchesListScreen : MainScreen(
            Destinations.MATCHES_LIST_ROUTE,
            R.string.matches_title,
            R.drawable.ic_timeline_black_24dp
    )

    object ResultsListScreen : MainScreen(
            Destinations.MATCHES_LIST_ROUTE,
            R.string.results_title,
            R.drawable.ic_results_black_24dp
    )
}

