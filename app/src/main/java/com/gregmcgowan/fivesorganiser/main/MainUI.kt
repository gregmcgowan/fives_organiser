package com.gregmcgowan.fivesorganiser.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ContentUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ErrorUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import com.gregmcgowan.fivesorganiser.navigation.NavigationActions
import com.gregmcgowan.fivesorganiser.navigation.NavigationGraph

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()
    val navigationActions = NavigationActions(navController)
    val mainViewModel: MainViewModel = hiltViewModel()

    when (val uiModel = mainViewModel.uiModel) {
        is LoadingUiModel -> Loading()
        is ErrorUiModel -> ErrorMessage(uiModel)
        is ContentUiModel -> MainContent(uiModel.content, navController, navigationActions)
    }
}

@Composable
fun MainContent(mainScreenUiModel: MainScreenUiModel,
                navController: NavHostController,
                navigationActions: NavigationActions) {

    Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    mainScreenUiModel.mainTabScreens.forEach { screen ->
                        BottomNavigationItem(
                                icon = {
                                    Icon(painter = painterResource(id = screen.iconRes),
                                            contentDescription = null)
                                },
                                label = { Text(stringResource(screen.resourceId)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navigationActions.navigateToMainScreenTab(screen.route)
                                }
                        )
                    }
                }
            }
    ) { innerPadding ->
        NavigationGraph(
                startDestination = mainScreenUiModel.screenToShow.route,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                navigationActions = navigationActions
        )
    }
}




