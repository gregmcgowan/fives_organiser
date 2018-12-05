package com.gregmcgowan.fivesorganiser.data.match

import androidx.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import org.threeten.bp.ZonedDateTime
import java.lang.Exception

interface MatchRepo {

    suspend fun createMatch(startTime: ZonedDateTime,
                            endTime: ZonedDateTime,
                            squadSize: Long,
                            location: String): MatchEntity

    suspend fun saveMatch(match: MatchEntity)

    suspend fun getMatch(matchID: String): MatchEntity

    suspend fun getAllMatches(): List<MatchEntity>

    fun getMatchUpdates(): LiveData<Either<Exception, DataUpdate<MatchEntity>>>
}