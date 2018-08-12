package com.gregmcgowan.fivesorganiser.match.inviteplayers

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.updatePlayerStatus
import javax.inject.Inject

class InvitePlayersOrchestrator @Inject constructor(
        private val matchId: String,
        private val matchInteractor: MatchInteractor,
        private val playersRepo: PlayerRepo
) {

    suspend fun getUninvitedPlayers(): List<Player> {
        val match = matchInteractor.getMatch(matchId)

        return playersRepo.getPlayers()
                .filter { player ->
                    !match.squad.playerAndStatuses.any { playerAndMatchStatus ->
                        playerAndMatchStatus.player.playerId == player.playerId
                    }
                }
    }

    suspend fun invitePlayers(playersToInvite: List<Player>) {
        var match = matchInteractor.getMatch(matchId)
        for (player in playersToInvite) {
            match = match.copy(squad = match.squad.updatePlayerStatus(player, PlayerMatchSquadStatus.INVITED))
        }
        matchInteractor.saveMatch(match)
    }


    //        return mutableListOf<PlayerAndMatchStatus>()
//                .apply {
//                    // show the players that have been some kind of status first then everything
//                    // else afterwards
//                    addAll(squad.playerAndStatuses)
//                    addAll(playersRepo.getPlayers()
//                            .filter { player ->
//                                squad.getPlayerIndex(player.playerId) == -1
//                            }
//                            .map { player ->
//                                PlayerAndMatchStatus(player, PlayerMatchSquadStatus.NO_STATUS)
//                            }
//                    )
//                }

}