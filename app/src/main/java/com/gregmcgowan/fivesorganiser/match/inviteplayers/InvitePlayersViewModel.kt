package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.match.MatchStateHolder
import com.gregmcgowan.fivesorganiser.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.match.getPlayerIndex
import com.gregmcgowan.fivesorganiser.match.toPlayerMatchSquadStatus
import timber.log.Timber
import javax.inject.Inject

class InvitePlayersViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchStateHolder: MatchStateHolder,
        private val invitePlayersUiModelMapper: InvitePlayersUiModelMapper,
        private val invitePlayersOrchestrator: InvitePlayersOrchestrator

) : CoroutinesViewModel(coroutineContext) {

    private val notInvitedPlayersUiModel = MutableLiveData<InvitePlayersUiModel>()
    private lateinit var allPlayers: List<PlayerAndMatchStatus>

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
        runOnBackgroundAndUpdateOnUI({
            allPlayers = invitePlayersOrchestrator.getPlayersAndStatus(matchStateHolder.match.squad)
            mapToUiModel()
        }, { uiModel -> notInvitedPlayersUiModel.value = uiModel })
    }

    private fun mapToUiModel(): InvitePlayersUiModel {

        return InvitePlayersUiModel(
                showLoading = false,
                showContent = true,
                invitePlayersList = invitePlayersUiModelMapper.map(allPlayers))
    }


    fun handlePlayerStatusChanged(playerId: String, status: String) {
        Timber.d("$playerId changed to $status")

        runOnBackgroundAndUpdateOnUI({
            val indexOfPlayer = allPlayers.getPlayerIndex(playerId)
            val player = allPlayers[indexOfPlayer]
            matchStateHolder.updatePlayerStatus(player.player, status.toPlayerMatchSquadStatus())
        }, {})
    }
}