package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import org.threeten.bp.ZonedDateTime

data class Match(val matchId: String,
                 val location: String,
                 val start: ZonedDateTime,
                 val end: ZonedDateTime,
                 val squad: Squad
)

data class Squad(
        val expectedSize: Long,
        val playerAndStatuses: List<PlayerAndMatchStatus> = emptyList()
)

data class PlayerAndMatchStatus(
        val player: Player,
        val matchSquadStatus: PlayerMatchSquadStatus
)

fun Squad.getPlayersWithStatus(matchSquadStatus: PlayerMatchSquadStatus): List<String> {
    return this.playerAndStatuses
            .filter { playerAndMatchStatus ->
                playerAndMatchStatus.matchSquadStatus == matchSquadStatus
            }.map { playerAndMatchStatus ->
                playerAndMatchStatus.player.playerId
            }
}


enum class PlayerMatchSquadStatus {
    NO_STATUS,
    INVITED,
    DECLINED,
    UNSURE,
    CONFIRMED
}





