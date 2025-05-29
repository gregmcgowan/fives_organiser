package com.gregmcgowan.fivesorganiser.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
        private val useCase: MainInitialiseUseCase
) : ViewModel() {

    private var _uiState: MutableStateFlow<UiState<MainScreenUiState>> =
            MutableStateFlow(value = UiState.LoadingUiState())

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.execute()
            updateUiState(MainScreen.PlayersListScreen)
        }
    }

    fun nestedScreenShown(nestedScreenShown: Boolean) {
        if (_uiState.value is ContentUiState) {
            val content = _uiState.value as ContentUiState
            _uiState.update {
                ContentUiState(content.content.copy(showBottomNavigation = nestedScreenShown))
            }

        }
    }

    private fun updateUiState(selectedScreen: MainScreen) {
        _uiState.update {
            ContentUiState(MainScreenUiState(selectedScreen))
        }
    }

}
