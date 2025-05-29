package com.gregmcgowan.fivesorgainser.playerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val uiStateMapper: PlayerListUiStateMapper,
    getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase,
) : ViewModel() {
    private val mutableUiStateFlow: MutableStateFlow<UiState<PlayerListUiState>> =
        MutableStateFlow(LoadingUiState())

    val uiStateFlow: StateFlow<UiState<PlayerListUiState>> =
        mutableUiStateFlow
            .asStateFlow()

    init {
        viewModelScope.launch {
            getPlayerListUpdatesUseCase
                .execute()
                .catch { exception -> handleError(exception) }
                .collect { dataUpdate ->
                    mutableUiStateFlow.update { prev ->
                        uiStateMapper.map(prev, dataUpdate)
                    }
                }
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        mutableUiStateFlow.update {
            UiState.ErrorUiState(string = R.string.generic_error_message)
        }
    }
}
