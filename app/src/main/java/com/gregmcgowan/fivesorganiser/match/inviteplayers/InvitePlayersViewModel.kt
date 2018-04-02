package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.match.MatchOrchestrator
import com.gregmcgowan.fivesorganiser.match.MatchStateHolder
import javax.inject.Inject

class InvitePlayersViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchStateHolder: MatchStateHolder,
        private val matchId: String?,
        private val matchOrchestrator: MatchOrchestrator
) : CoroutinesViewModel(coroutineContext) {

    private val notInvitedPlayersUiModel = MutableLiveData<InvitePlayersUiModel>()

    init {
        notInvitedPlayersUiModel.value = InvitePlayersUiModel(
                showLoading = true,
                showContent = false,
                invitePlayersList = emptyList()
        )
    }

    fun uiModel(): LiveData<InvitePlayersUiModel> {
        return notInvitedPlayersUiModel
    }

    fun onViewShown() {
        runOnBackgroundAndUpdateOnUI(
                { mapToUiModel(matchOrchestrator.getAllPlayers()) },
                { uiModel -> notInvitedPlayersUiModel.value = uiModel }
        )
    }

    private fun mapToUiModel(noteInvitedPlayers: List<Player>): InvitePlayersUiModel {
        val models = mutableListOf<InvitePlayerListItemModel>()
        noteInvitedPlayers.mapTo(models) {
            InvitePlayerListItemModel(
                    it.playerId,
                    it.name,
                    false
            )
        }

        return InvitePlayersUiModel(
                showLoading = false,
                showContent = true,
                invitePlayersList = models)
    }

    fun saveAndExit() {
        //TODO save and exit

    }
}