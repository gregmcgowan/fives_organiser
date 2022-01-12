package com.gregmcgowan.fivesorganiser.match.squad

import com.gregmcgowan.fivesorganiser.data.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.data.match.PlayerMatchSquadStatus
import javax.inject.Inject

class MatchSquadUiModelMapper @Inject constructor() {

    fun map(playerAndMatchStatus: List<PlayerAndMatchStatus>): MatchSquadUiModel {
        return MatchSquadUiModel(
                showLoading = false,
                showContent = true,
                playersListUi = mapPlayerList(playerAndMatchStatus))
    }

    private fun mapPlayerList(playerList: List<PlayerAndMatchStatus>): List<MatchSquadListItemUiModel> =
            playerList.mapTo(mutableListOf()) { playerAndMatchStatus ->
                MatchSquadListItemUiModel(
                        playerAndMatchStatus.player.playerId,
                        playerAndMatchStatus.player.name,
                        mapPlayerList(playerAndMatchStatus.matchSquadStatus)
                )
            }

    private fun mapPlayerList(status: PlayerMatchSquadStatus): MatchSquadListPlayerStatus = when (status) {
        PlayerMatchSquadStatus.NO_STATUS -> MatchSquadListPlayerStatus.NOT_SELECTED
        PlayerMatchSquadStatus.INVITED -> MatchSquadListPlayerStatus.INVITED_SELECTED
        PlayerMatchSquadStatus.DECLINED -> MatchSquadListPlayerStatus.DECLINED_SELECTED
        PlayerMatchSquadStatus.UNSURE -> MatchSquadListPlayerStatus.UNKNOWN_SELECTED
        PlayerMatchSquadStatus.CONFIRMED -> MatchSquadListPlayerStatus.CONFIRMED_SELECTED
    }


}