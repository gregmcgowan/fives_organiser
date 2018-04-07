package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.match.database.MatchEntity
import com.gregmcgowan.fivesorganiser.match.database.MatchRepo
import com.gregmcgowan.fivesorganiser.match.database.MatchSquadEntity
import com.gregmcgowan.fivesorganiser.match.database.MatchSquadRepo
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class MatchOrchestrator @Inject constructor(private val matchRepo: MatchRepo,
                                            private val matchSquad: MatchSquadRepo,
                                            private val playersRepo: PlayerRepo) {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    suspend fun createMatch(startTime: ZonedDateTime,
                            endTime: ZonedDateTime,
                            squadSize: Long,
                            location: String): Match {
        val match = matchRepo.createMatch(startTime, endTime, squadSize, location)
        val squad = matchSquad.createMatchSquad(matchId = match.matchId)
        return map(match, squad, emptyMap())
    }

    suspend fun saveMatch(match: Match) {
        matchRepo.saveMatch(map(match))
        matchSquad.saveMatchSquad(mapSquad(match))
    }

    suspend fun getMatch(matchID: String): Match {
        val matchEntity = matchRepo.getMatch(matchID)
        return mapFullMatch(matchEntity, getPlayerMap())
    }

    suspend fun getAllMatches(): List<Match> {
        val playerMap = getPlayerMap()
        return matchRepo.getAllMatches().map { match ->
            mapFullMatch(match, playerMap)
        }
    }

    private suspend fun mapFullMatch(match: MatchEntity, playerMap: Map<String, Player>): Match {
        val matchSquad = matchSquad.getMatchSquad(match.matchId)

        return map(
                matchEntity = match,
                matchSquad = matchSquad,
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

    private fun map(match: Match): MatchEntity {
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

    private fun map(matchEntity: MatchEntity,
                    matchSquad: MatchSquadEntity,
                    players: Map<String, Player>): Match {

        val mutableList = mutableListOf<PlayerAndMatchStatus>()

        populateList(matchSquad.invited, PlayerMatchSquadStatus.INVITED, players, mutableList)
        populateList(matchSquad.confirmed, PlayerMatchSquadStatus.CONFIRMED, players, mutableList)
        populateList(matchSquad.declined, PlayerMatchSquadStatus.DECLINED, players, mutableList)
        populateList(matchSquad.unsure, PlayerMatchSquadStatus.UNSURE, players, mutableList)

        return Match(
                matchId = matchEntity.matchId,
                location = matchEntity.location,
                start = ZonedDateTime.parse(matchEntity.startDateTime),
                end = ZonedDateTime.parse(matchEntity.endDateTime),
                squad = Squad(
                        expectedSize = matchEntity.numberOfPlayers,
                        playerAndStatuses = mutableList
                )

        )
    }

    private fun populateList(playerIds: List<String>,
                             playerMatchSquadStatus: PlayerMatchSquadStatus,
                             players: Map<String, Player>,
                             mutableList: MutableList<PlayerAndMatchStatus>) {
        for (playerId in playerIds) {
            val player = players[playerId]
            player?.let {
                mutableList.add(PlayerAndMatchStatus(it, playerMatchSquadStatus))
            }
        }
    }


}