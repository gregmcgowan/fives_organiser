package com.gregmcgowan.fivesorganiser.match.squad

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadListPlayerStatus.*
import javax.inject.Inject

class MatchSquadViewModel @Inject constructor(
        private val uiModelMapper: MatchSquadUiModelMapper,
        private val orchestrator: MatchSquadOrchestrator,
        coroutineContext: CoroutineContexts
) : CoroutinesViewModel(coroutineContext) {

    val matchSquadUiModelLiveData: MutableLiveData<MatchSquadUiModel>
        get() = _matchSquadUiModelLiveData
    private val _matchSquadUiModelLiveData = MutableLiveData<MatchSquadUiModel>()

    val matchSquadNavEventsLiveData: LiveData<MatchSquadListNavEvent>
        get() = _matchSquadNavEventsLiveData
    private val _matchSquadNavEventsLiveData = MutableLiveData<MatchSquadListNavEvent>()

    private lateinit var allPlayers: MutableList<PlayerAndMatchStatus>

    init {
        _matchSquadUiModelLiveData.value = MatchSquadUiModel(
                showLoading = true,
                showContent = false,
                playersListUi = emptyList()
        )

        _matchSquadNavEventsLiveData.value = MatchSquadListNavEvent.Idle

        runOnBackgroundAndUpdateOnUI({
            allPlayers = orchestrator.getPlayerAndMatchStatus().toMutableList()
            uiModelMapper.map(allPlayers)
        }, { uiModel -> _matchSquadUiModelLiveData.value = uiModel })
    }

    fun handlePlayerStatusChanged(playerId: String, matchSquadlistPlayerStatus: MatchSquadListPlayerStatus) {
        val indexOfFirst = allPlayers.indexOfFirst { it.player.playerId == playerId }
        if (indexOfFirst != -1) {
            val playerAndMatchStatus = allPlayers[indexOfFirst]
            val matchSquadStatus = map(matchSquadlistPlayerStatus)

            allPlayers[indexOfFirst] = playerAndMatchStatus.copy(matchSquadStatus = matchSquadStatus)
        }
    }

    private fun map(matchSquadListPlayerStatus: MatchSquadListPlayerStatus): PlayerMatchSquadStatus {
        return when (matchSquadListPlayerStatus) {
            NOT_SELECTED -> PlayerMatchSquadStatus.NO_STATUS
            INVITED_SELECTED -> PlayerMatchSquadStatus.INVITED
            DECLINED_SELECTED -> PlayerMatchSquadStatus.DECLINED
            UNKNOWN_SELECTED -> PlayerMatchSquadStatus.UNSURE
            CONFIRMED_SELECTED -> PlayerMatchSquadStatus.CONFIRMED
        }
    }

    fun handleBackPressed() {
        _matchSquadUiModelLiveData.value = _matchSquadUiModelLiveData.requireValue().copy(
                showContent = false, showLoading = true
        )
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { orchestrator.updatePlayerAndMatchStatus(allPlayers) },
                uiBlock = { _matchSquadNavEventsLiveData.value = MatchSquadListNavEvent.CloseScreen }
        )
    }


}