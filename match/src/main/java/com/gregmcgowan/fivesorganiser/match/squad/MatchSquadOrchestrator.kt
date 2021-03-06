package com.gregmcgowan.fivesorganiser.match.squad

import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.data.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.data.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class MatchSquadOrchestrator @Inject constructor(
        private val matchInteractor: MatchInteractor,
        private val playersRepo: PlayerRepo
) {

    suspend fun getPlayerAndMatchStatus(matchId: String): List<PlayerAndMatchStatus> {
        val playerAndMatchStatuses = matchInteractor.getMatch(matchId)
                .squad
                .playerAndStatuses
                .associateBy { it.player.playerId }

        return playersRepo
                .getPlayers()
                .mapNotNull {
                    if (playerAndMatchStatuses.containsKey(it.playerId)) {
                        playerAndMatchStatuses[it.playerId]
                    } else {
                        PlayerAndMatchStatus(it, PlayerMatchSquadStatus.NO_STATUS)
                    }
                }

    }

    suspend fun updatePlayerAndMatchStatus(
            matchId: String,
            playersAndMatchStatus: List<PlayerAndMatchStatus>
    ) {
        val match = matchInteractor.getMatch(matchId)
        matchInteractor.saveMatch(match.copy(squad = match.squad.copy(playerAndStatuses = playersAndMatchStatus)))
    }


}