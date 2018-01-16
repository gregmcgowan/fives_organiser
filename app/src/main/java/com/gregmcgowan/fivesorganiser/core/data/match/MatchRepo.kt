package com.gregmcgowan.fivesorganiser.core.data.match

import org.threeten.bp.ZonedDateTime

interface MatchRepo {

    suspend fun createMatch(dateTime: ZonedDateTime,
                            location: String,
                            numberOfPlayers: Int)

    suspend fun saveMatch(match: Match)

    suspend fun getMatch(matchID: String): Match

    suspend fun getAllMatches(): List<Match>
}