package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerListViewModel @ViewModelInject constructor(
        private val uiModelMapper: PlayerListUiModelMapper,
        getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase
) : ViewModel() {

    var uiModel: UiModel<PlayerListUiModel> by mutableStateOf(
            value = LoadingUiModel()
    )
        private set

    val playerListUiLiveData: LiveData<PlayerListUiEvents>
        get() = _playerListNavigationLiveData

    private val _playerListNavigationLiveData = MutableLiveData<PlayerListUiEvents>()

    init {

        _playerListNavigationLiveData.value = PlayerListUiEvents.Idle

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

    private fun handleUpdate(update: DataUpdate<Player>) {
        uiModel =  uiModelMapper.map(uiModel, update)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        uiModel = UiModel.ErrorUiModel(string = R.string.generic_error_message)
    }

    fun addPlayerButtonPressed() {
        _playerListNavigationLiveData.value = PlayerListUiEvents.ShowAddPlayerScreenEvent
    }

}
