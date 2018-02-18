package com.gregmcgowan.fivesorganiser.match.squad.notinvitedplayers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.match.MatchOrchestrator
import javax.inject.Inject

class NotInvitedPlayersViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchId: String,
        private val matchOrchestrator: MatchOrchestrator
) : CoroutinesViewModel(coroutineContext) {

    private val notInvitedPlayersUiModel = MutableLiveData<NotInvitedPlayersUiModel>()

    init {
        notInvitedPlayersUiModel.value = NotInvitedPlayersUiModel(
                showLoading = true,
                showContent = false,
                notInvitedPlayers = emptyList()
        )
    }

    fun uiModel(): LiveData<NotInvitedPlayersUiModel> {
        return notInvitedPlayersUiModel
    }

    fun onViewShown() {
        runOnBackgroundAndUpdateOnUI(
                { mapToUiModel(matchOrchestrator.getUninvitedPlayers(matchId)) },
                { uiModel -> notInvitedPlayersUiModel.value = uiModel }
        )
    }

    private fun mapToUiModel(noteInvitedPlayers: List<Player>): NotInvitedPlayersUiModel {
        val models = mutableListOf<NotInvitedPlayerItemModel>()
        noteInvitedPlayers.mapTo(models) {
            NotInvitedPlayerItemModel(
                    it.playerId,
                    it.name,
                    false
            )
        }

        return NotInvitedPlayersUiModel(
                showLoading = false,
                showContent = true,
                notInvitedPlayers = models)
    }

    fun saveAndExit() {
        //TODO save and exit

    }
}