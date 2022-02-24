package com.gregmcgowan.fivesorganiser.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gregmcgowan.fivesorgainser.playerlist.PlayerList
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsScreen


object Destinations {
    // Main tabs
    const val PLAYER_LIST_ROUTE = "player_list"
    const val MATCHES_LIST_ROUTE = "matches_list"
    const val RESULTS_LIST_ROUTE = "results_list"

    // player tab nested screens
    const val IMPORT_CONTACTS_ROUTE = "import_contacts"
}

class NavigationActions(private val navController: NavHostController,
                        private val nestedScreenShown : (Boolean) -> Unit) {


    val navigateToMainScreenTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            nestedScreenShown(false)
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToNestedScreen: (String) -> Unit = { route ->
        navController.navigate(route)
        nestedScreenShown(true)
        // TODO do we need anything else here
    }

    val navigateBack: () -> Unit = {
        // TODO fix
        nestedScreenShown(false)
        navController.popBackStack()
    }

}


@Composable
fun NavigationGraph(modifier: Modifier = Modifier,
                    navController: NavHostController,
                    startDestination: String,
                    navigationActions: NavigationActions) {
    NavHost(navController,
            startDestination = startDestination,
            modifier = modifier
    ) {
        composable(Destinations.PLAYER_LIST_ROUTE) {
            PlayerList(openImportContacts = {
                navigationActions.navigateToNestedScreen(Destinations.IMPORT_CONTACTS_ROUTE)
            })
        }
        composable(Destinations.IMPORT_CONTACTS_ROUTE) {
            ImportContactsScreen(navigationActions.navigateBack)
        }
        composable(Destinations.MATCHES_LIST_ROUTE) { Matches() }
        composable(Destinations.RESULTS_LIST_ROUTE) { Results() }
    }
}

// TODO move to modules once created
@Composable
fun Matches() {
    Text("Matches")
}

@Composable
fun Results() {
    Text("Results")
}
