package com.gregmcgowan.fivesorganiser.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ContentUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ErrorUiModel
import com.gregmcgowan.fivesorganiser.main.MainScreen.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    // TODO understand this more
    LaunchedEffect(navController) {
        coroutineScope.launch {
            mainViewModel.mainUiEvents.collect { event ->
                when (event) {
                    is MainScreenUiEvents.ShowMainTabScreen -> {
                        navController.navigate(event.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }


    when (val uiModel = mainViewModel.uiModel) {
        is LoadingUiModel -> Loading()
        is ErrorUiModel -> ErrorMessage(uiModel)
        is ContentUiModel -> MainContent(uiModel.content, navController, mainViewModel::handleMenuSelection)
    }
}

@Composable
fun MainContent(mainScreenUiModel: MainScreenUiModel,
                navController: NavHostController,
                onBottomTabSelected: (MainScreen) -> Unit) {

    Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    mainScreenUiModel.mainTabScreens.forEach { screen ->
                        BottomNavigationItem(
                                icon = { Icon(painter = painterResource(id = screen.iconRes), contentDescription = null) },
                                label = { Text(stringResource(screen.resourceId)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    onBottomTabSelected.invoke(screen)
                                }
                        )
                    }
                }
            }
    ) { innerPadding ->
        NavHost(navController,
                startDestination = mainScreenUiModel.screenToShow.route,
                modifier = Modifier.padding(innerPadding)) {
            composable(PlayersScreen.route) { Players() }
            composable(MatchesScreen.route) { Matches() }
            composable(ResultsScreen.route) { Results() }
        }
    }
}

@Composable
fun Players() {
    Text("Players")
}

@Composable
fun Matches() {
    Text("Matches")
}

@Composable
fun Results() {
    Text("Results")
}

