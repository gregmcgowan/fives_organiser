package com.gregmcgowan.fivesorganiser.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
        private val useCase: MainInitialiseUseCase
) : ViewModel() {

    var uiState: UiState<MainScreenUiState> by mutableStateOf(value = UiState.LoadingUiState())
        private set

    init {
        viewModelScope.launch {
            useCase.execute()
            updateUiState(MainScreen.PlayersListScreen)
        }
    }

    fun nestedScreenShown(nestedScreenShown: Boolean) {
        if (uiState is ContentUiState) {
            uiState = ContentUiState((uiState as ContentUiState<MainScreenUiState>)
                    .content.copy(showBottomNavigation = !nestedScreenShown))
        }

    }

    private fun updateUiState(selectedScreen: MainScreen) {
        uiState = ContentUiState(MainScreenUiState(selectedScreen))
    }

}
