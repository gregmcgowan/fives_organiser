package com.gregmcgowan.fivesorganiser.core.data.match

import io.reactivex.Single
import org.threeten.bp.ZonedDateTime

interface MatchRepo {

    fun createMatch(dateTime: ZonedDateTime,
                    location: String)

    fun saveMatch(match: Match)

    fun getMatch(matchID: String): Single<Match>

    fun getAllMatches(): Single<List<Match>>
}