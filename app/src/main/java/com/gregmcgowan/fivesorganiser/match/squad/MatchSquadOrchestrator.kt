package com.gregmcgowan.fivesorganiser.match.squad

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import javax.inject.Inject

class MatchSquadOrchestrator @Inject constructor(
        private val matchId: String,
        private val matchInteractor: MatchInteractor,
        private val playersRepo: PlayerRepo
) {

    suspend fun getPlayerAndMatchStatus(): List<PlayerAndMatchStatus> {
        val playersAndStatus = matchInteractor.getMatch(matchId)
                .squad
                .playerAndStatuses.toMutableList()

        playersRepo
                .getPlayers()
                .forEach { player ->
                    if (playersAndStatus.firstOrNull { it.player.playerId == player.playerId } == null) {
                        playersAndStatus.add(PlayerAndMatchStatus(player, PlayerMatchSquadStatus.NO_STATUS))
                    }
                }
        return playersAndStatus
    }

    suspend fun updatePlayerAndMatchStatus(playersAndMatchStatus: List<PlayerAndMatchStatus>) {
        val match = matchInteractor.getMatch(matchId)
        matchInteractor.saveMatch(match.copy(squad = match.squad.copy(playerAndStatuses = playersAndMatchStatus)))
    }


}