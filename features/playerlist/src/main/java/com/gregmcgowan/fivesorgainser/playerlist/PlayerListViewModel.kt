package com.gregmcgowan.fivesorgainser.playerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorgainser.playerlist.R.string.generic_error_message
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber.e
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val uiStateMapper: PlayerListUiStateMapper,
    private val getPlayerListUseCase: GetPlayerListUseCase,
) : ViewModel() {
    private val mutableUiStateFlow: MutableStateFlow<UiState<PlayerListUiState>> =
        MutableStateFlow(LoadingUiState())

    val uiStateFlow: StateFlow<UiState<PlayerListUiState>> =
        mutableUiStateFlow
            .asStateFlow()

    fun init() {
        viewModelScope.launch {
            mutableUiStateFlow.update { LoadingUiState() }
            runCatching { uiStateMapper.map(getPlayerListUseCase.execute()) }
                .onSuccess { uiState -> mutableUiStateFlow.update { uiState } }
                .onFailure { error ->
                    e(error)
                    mutableUiStateFlow.update { ErrorUiState(string = generic_error_message) }
                }
        }
    }
}
