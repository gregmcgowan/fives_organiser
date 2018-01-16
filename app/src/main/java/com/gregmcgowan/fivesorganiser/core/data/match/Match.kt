package com.gregmcgowan.fivesorganiser.core.data.match

import org.threeten.bp.ZonedDateTime

data class Match(val matchId: String,
                 val location: String,
                 val dateTime: ZonedDateTime,
                 val numberOfPlayers : Int)