package com.gregmcgowan.fivesorganiser.data.match

import androidx.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import java.lang.Exception

interface MatchSquadRepo {

    suspend fun createMatchSquad(matchId: String,
                                 invited: List<String> = emptyList(),
                                 confirmed: List<String> = emptyList(),
                                 unsure: List<String> = emptyList(),
                                 declined: List<String> = emptyList()): MatchSquadEntity


    suspend fun saveMatchSquad(matchSquad: MatchSquadEntity)


    suspend fun getMatchSquad(matchId: String): MatchSquadEntity

    fun getSquadUpdates(): LiveData<Either<Exception, DataUpdate<MatchSquadEntity>>>

}