package com.gregmcgowan.fivesorganiser.core.data.match

import com.google.firebase.database.IgnoreExtraProperties
import org.threeten.bp.ZonedDateTime


@IgnoreExtraProperties
data class MatchEntity(
        val matchId: String = "",
        //ISO_ZONED_DATE_TIME
        val dateTime: String = "",
        val location: String = "")

data class Match(val matchId: String,
                 val location: String,
                 val dateTime: ZonedDateTime)