package com.gregmcgowan.fivesorganiser.data.match

data class MatchEntity(
        val matchId: String = "",
        //ISO_ZONED_DATE_TIME
        val startDateTime: String = "",
        val endDateTime: String = "",
        val location: String = "",
        val numberOfPlayers: Long = 0
)

data class MatchSquadEntity(
        val matchId : String,
        val invited: List<String> = emptyList(),
        val confirmed: List<String> = emptyList(),
        val unsure: List<String> = emptyList(),
        val declined: List<String> = emptyList()
)