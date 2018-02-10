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
        val size: Long,
        val invited : List<Player> = emptyList(),
        val confirmed: List<Player> = emptyList(),
        val unsure: List<Player> = emptyList(),
        val declined: List<Player> = emptyList()
)

enum class MatchSquadStatus {
    NOT_INVITED,
    INVITED,
    DECLINED,
    UNSURE,
    ACCEPTED
}


