package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class MatchInteractor @Inject constructor(private val matchRepo: MatchRepo,
                                          private val matchSquad: MatchSquadRepo,
                                          private val playersRepo: PlayerRepo,
                                          private val matchMapper: MatchMapper) {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    suspend fun createMatch(startTime: ZonedDateTime,
                            endTime: ZonedDateTime,
                            squadSize: Long,
                            location: String): Match {
        val match = matchRepo.createMatch(startTime, endTime, squadSize, location)
        return matchMapper.map(match, matchSquad.createMatchSquad(match.matchId), emptyMap())
    }

    suspend fun saveMatch(match: Match) {
        matchRepo.saveMatch(combineMatchAndSquad(match))
        matchSquad.saveMatchSquad(mapSquad(match))
    }

    suspend fun getMatch(matchID: String): Match {
        val matchEntity = matchRepo.getMatch(matchID)
        return mapFullMatch(matchEntity, getPlayerMap())
    }

    private suspend fun mapFullMatch(match: MatchEntity, playerMap: Map<String, Player>): Match {
        val matchSquad = matchSquad.getMatchSquad(match.matchId)

        return matchMapper.map(
                match = match,
                squad = matchSquad,
                players = playerMap
        )
    }

    private suspend fun getPlayerMap(): Map<String, Player> {
        val map: MutableMap<String, Player> = mutableMapOf()
        val players = playersRepo.getPlayers()
        for (player in players) {
            map[player.playerId] = player
        }
        return map
    }

    private fun combineMatchAndSquad(match: Match): MatchEntity {
        return MatchEntity(
                matchId = match.matchId,
                location = match.location,
                startDateTime = match.start.format(dateTimeFormatter),
                endDateTime = match.end.format(dateTimeFormatter),
                numberOfPlayers = match.squad.expectedSize
        )
    }

    private fun mapSquad(match: Match): MatchSquadEntity {
        return MatchSquadEntity(
                matchId = match.matchId,
                invited = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.INVITED),
                confirmed = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.CONFIRMED),
                declined = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.DECLINED),
                unsure = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.UNSURE)
        )
    }


}