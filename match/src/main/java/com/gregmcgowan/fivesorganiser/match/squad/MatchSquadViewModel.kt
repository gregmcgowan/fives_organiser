package com.gregmcgowan.fivesorganiser.match.squad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.Dispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.data.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadListPlayerStatus.*
import javax.inject.Inject

class MatchSquadViewModel @Inject constructor(
        private val mapper: MatchSquadUiModelMapper,
        private val orchestrator: MatchSquadOrchestrator,
        dispatchers: Dispatchers
) : CoroutinesViewModel(dispatchers) {

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

        launch({
            allPlayers = orchestrator.getPlayerAndMatchStatus().toMutableList()
            mapper.map(allPlayers)
        }, { _matchSquadUiModelLiveData.value = it })
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
        launch(
                backgroundBlock = { orchestrator.updatePlayerAndMatchStatus(allPlayers) },
                uiBlock = { _matchSquadNavEventsLiveData.value = MatchSquadListNavEvent.CloseScreen }
        )
    }


}