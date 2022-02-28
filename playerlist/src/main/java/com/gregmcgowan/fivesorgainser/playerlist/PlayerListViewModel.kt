package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
        private val uiStateMapper: PlayerListUiStateMapper,
        getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase
) : ViewModel() {

    var uiState: UiState<PlayerListUiState> by mutableStateOf(value = LoadingUiState())
        private set

    init {
        viewModelScope.launch {
            getPlayerListUpdatesUseCase
                    .execute()
                    .catch { exception -> handleError(exception) }
                    .collect { uiState = uiStateMapper.map(uiState, it) }
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        uiState = UiState.ErrorUiState(string = R.string.generic_error_message)
    }

}
