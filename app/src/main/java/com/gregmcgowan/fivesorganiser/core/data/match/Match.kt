package com.gregmcgowan.fivesorganiser.core.data.match

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import org.threeten.bp.ZonedDateTime

data class Match(val matchId: String,
                 val location: String,
                 val start: ZonedDateTime,
                 val end: ZonedDateTime,
                 val squadSize: Int,
                 val confirmed: List<Player> = emptyList(),
                 val unknown: List<Player> = emptyList(),
                 val declined: List<Player> = emptyList())


