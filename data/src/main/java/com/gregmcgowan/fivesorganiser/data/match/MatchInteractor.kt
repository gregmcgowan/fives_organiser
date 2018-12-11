package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class MatchInteractor @Inject constructor(private val matchRepo: MatchRepo,
                                          private val playersRepo: PlayerRepo,
                                          private val matchToEntityMapper: MatchToEntityMapper,
                                          private val matchMapper: MatchMapper) {

    suspend fun createMatch(startTime: ZonedDateTime,
                            endTime: ZonedDateTime,
                            squadSize: Long,
                            location: String): Match {
        return matchMapper.map(
                matchRepo.createMatch(startTime, endTime, squadSize, location),
                emptyMap()
        )
    }

    suspend fun saveMatch(match: Match) {
        matchRepo.saveMatch(matchToEntityMapper.map(match))
    }

    suspend fun getMatch(matchID: String): Match {
        return matchMapper.map(
                match = matchRepo.getMatch(matchID),
                allPlayers = playersRepo.getPlayers().associateBy { it.playerId }
        )
    }


}