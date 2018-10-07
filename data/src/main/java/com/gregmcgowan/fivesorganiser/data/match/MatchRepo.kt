package com.gregmcgowan.fivesorganiser.data.match

import org.threeten.bp.ZonedDateTime

interface MatchRepo {

    suspend fun createMatch(startTime: ZonedDateTime,
                            endTime: ZonedDateTime,
                            squadSize: Long,
                            location: String) : MatchEntity

    suspend fun saveMatch(match: MatchEntity)

    suspend fun getMatch(matchID: String): MatchEntity

    suspend fun getAllMatches(): List<MatchEntity>
}