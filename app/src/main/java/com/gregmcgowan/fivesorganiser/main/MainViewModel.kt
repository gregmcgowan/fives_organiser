package com.gregmcgowan.fivesorganiser.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        private val useCase: MainInitialiseUseCase
) : ViewModel() {

    var uiModel: UiModel<MainScreenUiModel> by mutableStateOf(value = UiModel.LoadingUiModel())
        private set

    val mainUiEvents: Flow<MainScreenUiEvents>
        get() = _mainUiEvents.asSharedFlow()

    private val _mainUiEvents = MutableSharedFlow<MainScreenUiEvents>()

    init {
        viewModelScope.launch {
            useCase.execute()
            updateUiModel(MainScreen.PlayersScreen)
        }
    }

    private fun updateUiModel(selectedScreen: MainScreen) {
        uiModel = UiModel.ContentUiModel(MainScreenUiModel(selectedScreen))
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        updateUiModel(selectedScreen)
        viewModelScope.launch {
            _mainUiEvents.emit(MainScreenUiEvents.ShowMainTabScreen(selectedScreen.route))
        }
    }

}
