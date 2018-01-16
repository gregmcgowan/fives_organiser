package com.gregmcgowan.fivesorganiser.main.playerList

import android.arch.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.core.ui.NonNullMutableLiveData
import kotlin.coroutines.experimental.CoroutineContext

class PlayerListViewModel(
        ui: CoroutineContext,
        background: CoroutineContext,
        private val playersRepo: PlayerRepo) : CoroutinesViewModel(ui, background) {

    private val playerUiModelLiveData = NonNullMutableLiveData(
            PlayerListUiModel(
                    players = emptyList(),
                    showLoading = true,
                    showErrorMessage = false,
                    showPlayers = false,
                    errorMessage = null
            )
    )
    private val playerListNavigationLiveData = NonNullMutableLiveData<PlayerListNavigationEvents>(
            PlayerListNavigationEvents.Idle
    )

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
        playerUiModelLiveData.value = reducer.invoke(playerUiModelLiveData.getNonNullValue())
    }

}
