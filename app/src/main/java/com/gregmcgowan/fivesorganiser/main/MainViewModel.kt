package com.gregmcgowan.fivesorganiser.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ContentUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
        private val useCase: MainInitialiseUseCase
) : ViewModel() {

    var uiModel: UiModel<MainScreenUiModel> by mutableStateOf(value = UiModel.LoadingUiModel())
        private set

    init {
        viewModelScope.launch {
            useCase.execute()
            updateUiModel(MainScreen.PlayersListScreen)
        }
    }

    fun nestedScreenShown(nestedScreenShown: Boolean) {
        if (uiModel is ContentUiModel) {
            uiModel = ContentUiModel((uiModel as ContentUiModel<MainScreenUiModel>)
                    .content.copy(showBottomNavigation = !nestedScreenShown))
        }

    }

    private fun updateUiModel(selectedScreen: MainScreen) {
        uiModel = ContentUiModel(MainScreenUiModel(selectedScreen))
    }

}
