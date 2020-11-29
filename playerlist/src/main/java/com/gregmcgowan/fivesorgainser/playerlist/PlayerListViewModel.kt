package com.gregmcgowan.fivesorgainser.playerlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player

class PlayerListViewModel @ViewModelInject constructor(
        private val uiModelMapper: PlayerListUiModelMapper,
        getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase,
        coroutineDisptachersAndContext: CoroutineDisptachersAndContext
) : CoroutinesViewModel(coroutineDisptachersAndContext) {

    val playerUiModelLiveData: LiveData<PlayerListUiModel>
        get() = _playerUiModelLiveData

    private val _playerUiModelLiveData = MediatorLiveData<PlayerListUiModel>()

    val playerListNavigationLiveData: LiveData<PlayerListNavigationEvents>
        get() = _playerListNavigationLiveData

    private val _playerListNavigationLiveData = MutableLiveData<PlayerListNavigationEvents>()

    private val playerUpdatesLiveData: LiveData<Either<Exception, DataUpdate<Player>>>

    init {
        _playerUiModelLiveData.value = PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = false,
                showPlayers = false)

        _playerListNavigationLiveData.value = PlayerListNavigationEvents.Idle

        playerUpdatesLiveData = getPlayerListUpdatesUseCase.execute()

        _playerUiModelLiveData.addSource(playerUpdatesLiveData) {
            it.either(
                    { exception -> handleError(exception) },
                    { update -> handleUpdate(update) }
            )
        }
    }

    private fun handleUpdate(update: DataUpdate<Player>) {
        _playerUiModelLiveData.postValue(
                uiModelMapper.map(_playerUiModelLiveData.requireValue(), update)
        )
    }

    private fun handleError(exception: Exception) {
        // TODO log error
        _playerUiModelLiveData.value = PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = true,
                showPlayers = false,
                errorMessage = R.string.generic_error_message
        )
    }

    fun addPlayerButtonPressed() {
        _playerListNavigationLiveData.value = PlayerListNavigationEvents.AddPlayerEvent
    }

    override fun onCleared() {
        super.onCleared()
        _playerUiModelLiveData.removeSource(playerUpdatesLiveData)
    }
}
