package com.gregmcgowan.fivesorgainser.playerlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class PlayerListViewModel @Inject constructor(
        private val mapper: PlayerListUiModelMapper,
        private val playersRepo: PlayerRepo,
        coroutineDisptachersAndContext: CoroutineDisptachersAndContext
) : CoroutinesViewModel(coroutineDisptachersAndContext) {

    val playerUiModelLiveData: LiveData<PlayerListUiModel>
        get() = _playerUiModelLiveData

    private val _playerUiModelLiveData = MutableLiveData<PlayerListUiModel>()

    val playerListNavigationLiveData: LiveData<PlayerListNavigationEvents>
        get() = _playerListNavigationLiveData

    private val _playerListNavigationLiveData = MutableLiveData<PlayerListNavigationEvents>()

    init {
        _playerUiModelLiveData.value = PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = false,
                showPlayers = false,
                errorMessage = null
        )

        _playerListNavigationLiveData.value = PlayerListNavigationEvents.Idle
    }

    fun addPlayerButtonPressed() {
        _playerListNavigationLiveData.value = PlayerListNavigationEvents.AddPlayerEvent
    }

    fun onViewShown() {
        _playerListNavigationLiveData.value = PlayerListNavigationEvents.Idle
        _playerUiModelLiveData.value = playerUiModelLiveData.requireValue()
                .copy(showLoading = true, showPlayers = false, showErrorMessage = false)

        launch(
                backgroundBlock = { mapper.map(playersRepo.getPlayers()) },
                uiBlock = { _playerUiModelLiveData.value = it }
        )
    }

}
