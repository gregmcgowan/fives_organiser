package com.gregmcgowan.fivesorganiser.main.playerlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.core.requireValue
import javax.inject.Inject

class PlayerListViewModel @Inject constructor(
        private val playerListUiModelMapper: PlayerListUiModelMapper,
        private val playersRepo: PlayerRepo,
        coroutineContext: CoroutineContexts
) : CoroutinesViewModel(coroutineContext) {

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

        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { playerListUiModelMapper.map(playersRepo.getPlayers()) },
                uiBlock = { uiModel -> _playerUiModelLiveData.value = uiModel }
        )
    }

}
