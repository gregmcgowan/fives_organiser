package com.gregmcgowan.fivesorganiser.match.squad

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.data.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.data.match.PlayerMatchSquadStatus
import javax.inject.Inject
import javax.inject.Named

class MatchSquadOrchestrator @Inject constructor(
        @Named(MATCH_SQUAD_ID) private val matchId: String,
        private val matchInteractor: MatchInteractor,
        private val playersRepo: PlayerRepo
) {

    suspend fun getPlayerAndMatchStatus(): List<PlayerAndMatchStatus> {
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

    suspend fun updatePlayerAndMatchStatus(playersAndMatchStatus: List<PlayerAndMatchStatus>) {
        val match = matchInteractor.getMatch(matchId)
        matchInteractor.saveMatch(match.copy(squad = match.squad.copy(playerAndStatuses = playersAndMatchStatus)))
    }


}