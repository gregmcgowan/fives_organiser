package com.gregmcgowan.fivesorganiser.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.navigation.NavigationActions
import com.gregmcgowan.fivesorganiser.navigation.NavigationGraph

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val navigationActions =
        NavigationActions(
            navController = navController,
            nestedScreenShown = mainViewModel::nestedScreenShown,
        )

    when (val uiModel = uiState) {
        is LoadingUiState -> {
            Loading(modifier = modifier)
        }

        is ErrorUiState -> {
            ErrorMessage(errorUiState = uiModel, modifier = modifier)
        }

        is ContentUiState -> {
            MainContent(
                mainScreenUiState = uiModel.content,
                navController = navController,
                navigationActions = navigationActions,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun MainContent(
    mainScreenUiState: MainScreenUiState,
    navController: NavHostController,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = mainScreenUiState.showBottomNavigation,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column {
                    HorizontalDivider()
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        mainScreenUiState.mainTabScreens.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = screen.iconRes),
                                        contentDescription = null,
                                    )
                                },
                                colors =
                                    NavigationBarItemDefaults
                                        .colors()
                                        .copy(
                                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                        ),
                                label = { Text(stringResource(screen.resourceId)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navigationActions.navigateToMainScreenTab(screen.route)
                                },
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        NavigationGraph(
            startDestination = mainScreenUiState.screenToShow.route,
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            navigationActions = navigationActions,
        )
    }
}
