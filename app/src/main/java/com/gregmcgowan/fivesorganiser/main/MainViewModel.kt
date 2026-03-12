package com.gregmcgowan.fivesorganiser.main

import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private var _uiState: MutableStateFlow<UiState<MainScreenUiState>> =
        MutableStateFlow(
            value =
                ContentUiState(
                    MainScreenUiState(screenToShow = MainScreen.PlayersListScreen),
                ),
        )

    val uiState = _uiState.asStateFlow()

    fun nestedScreenShown(nestedScreenShown: Boolean) {
        if (_uiState.value is ContentUiState) {
            val content = _uiState.value as ContentUiState
            _uiState.update {
                ContentUiState(content.content.copy(showBottomNavigation = !nestedScreenShown))
            }
        }
    }
}
