package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
        private val uiModelMapper: PlayerListUiModelMapper,
        getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase
) : ViewModel() {

    var uiModel: UiModel<PlayerListUiModel> by mutableStateOf(value = LoadingUiModel())
        private set

    val playerListUiEvents: Flow<PlayerListUiEvents>
        get() = _playerListUiEvents.asSharedFlow()

    private val _playerListUiEvents = MutableSharedFlow<PlayerListUiEvents>()

    init {
        emitEvent(PlayerListUiEvents.Idle)

        viewModelScope.launch {
            getPlayerListUpdatesUseCase
                    .execute()
                    .collect {
                        it.either(
                                { exception -> handleError(exception) },
                                { update -> handleUpdate(update) }
                        )
                    }
        }
    }

    private fun emitEvent(playerListUiEvents1: PlayerListUiEvents) {
        viewModelScope.launch {
            _playerListUiEvents.emit(playerListUiEvents1)
        }
    }
    private fun handleUpdate(update: DataUpdate<Player>) {
        uiModel =  uiModelMapper.map(uiModel, update)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        uiModel = UiModel.ErrorUiModel(string = R.string.generic_error_message)
    }

    fun addPlayerButtonPressed() {
        emitEvent(PlayerListUiEvents.ShowAddPlayerScreenEvent)
    }

}
