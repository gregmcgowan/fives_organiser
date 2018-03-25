package com.gregmcgowan.fivesorganiser.main.playerlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.core.requireValue
import javax.inject.Inject

class PlayerListViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val playersRepo: PlayerRepo
) : CoroutinesViewModel(coroutineContext) {

    private val playerUiModelLiveData = MutableLiveData<PlayerListUiModel>()
    private val playerListNavigationLiveData = MutableLiveData<PlayerListNavigationEvents>()

    init {
        playerUiModelLiveData.value = PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = false,
                showPlayers = false,
                errorMessage = null
        )

        playerListNavigationLiveData.value = PlayerListNavigationEvents.Idle
    }

    fun uiModel(): LiveData<PlayerListUiModel> {
        return playerUiModelLiveData
    }

    fun navigationEvents(): LiveData<PlayerListNavigationEvents> {
        return playerListNavigationLiveData
    }

    fun addPlayerButtonPressed() {
        playerListNavigationLiveData.value = PlayerListNavigationEvents.AddPlayerEvent
    }

    fun onViewShown() {
        playerListNavigationLiveData.value = PlayerListNavigationEvents.Idle
        updateUiModel(loadingPlayersUiModel())
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { playersLoadedUiModel(playersRepo.getPlayers()) },
                uiBlock = { uiModel -> updateUiModel(uiModel) }
        )
    }

    private fun updateUiModel(reducer: PlayerListUiModelReducer) {
        playerUiModelLiveData.value = reducer.invoke(playerUiModelLiveData.requireValue())
    }

}
