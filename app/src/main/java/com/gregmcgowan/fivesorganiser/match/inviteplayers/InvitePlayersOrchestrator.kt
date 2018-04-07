package com.gregmcgowan.fivesorganiser.match.inviteplayers

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.Squad
import com.gregmcgowan.fivesorganiser.match.getPlayerIndex
import javax.inject.Inject

class InvitePlayersOrchestrator @Inject constructor(private val playersRepo: PlayerRepo) {

    suspend fun getPlayersAndStatus(squad: Squad): List<PlayerAndMatchStatus> {
        return mutableListOf<PlayerAndMatchStatus>()
                .apply {
                    // show the players that have been some kind of status first then everything
                    // else afterwards
                    addAll(squad.playerAndStatuses)
                    addAll(playersRepo.getPlayers()
                            .filter { player ->
                                squad.getPlayerIndex(player.playerId) == -1
                            }
                            .map { player ->
                                PlayerAndMatchStatus(player, PlayerMatchSquadStatus.NO_STATUS)
                            }
                    )
                }
    }

}