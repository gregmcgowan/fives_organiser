package com.gregmcgowan.fivesorganiser.main

import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class MainViewModel : ViewModel() {
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
