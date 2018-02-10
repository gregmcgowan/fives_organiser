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
        return map(
                matchRepo.createMatch(startTime, endTime, squadSize, location),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyMap()
        )
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

    suspend fun getUninvitedPlayers(matchId: String): List<Player> {
        val uninvitedPlayers = mutableListOf<Player>()
        val matchSquad = matchSquad.getMatchSquad(matchId)
        val playerMap = getPlayerMap()
        for (playerId in playerMap.keys) {
            if (!matchSquad.confirmed.contains(playerId)
                    || !matchSquad.invited.contains(playerId)
                    || !matchSquad.declined.contains(playerId)
                    || !matchSquad.unsure.contains(playerId)) {
                playerMap[playerId]?.let {
                    uninvitedPlayers.add(it)
                }
            }
        }
        return uninvitedPlayers
    }

    private suspend fun mapFullMatch(match: MatchEntity, playerMap: Map<String, Player>): Match {
        val matchSquad = matchSquad.getMatchSquad(match.matchId)

        return map(
                matchEntity = match,
                confirmedPlayersIds = matchSquad.confirmed,
                invitedPlayersIds = matchSquad.invited,
                declinedPlayersIds = matchSquad.declined,
                unknownPlayersIds = matchSquad.unsure,
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
                numberOfPlayers = match.squad.size
        )
    }

    private fun mapSquad(match: Match): MatchSquadEntity {
        return MatchSquadEntity(
                matchId = match.matchId,
                invited = match.squad.invited.map(Player::playerId),
                confirmed = match.squad.confirmed.map(Player::playerId),
                declined = match.squad.declined.map(Player::playerId),
                unsure = match.squad.unsure.map(Player::playerId)
        )
    }

    private fun map(matchEntity: MatchEntity,
                    confirmedPlayersIds: List<String>,
                    invitedPlayersIds: List<String>,
                    declinedPlayersIds: List<String>,
                    unknownPlayersIds: List<String>,
                    players: Map<String, Player>): Match {
        //TODO map ids to players
        val confirmedPlayers: List<Player> = mutableListOf()
        val invitedPlayers: List<Player> = mutableListOf()
        val declinedPlayers: List<Player> = mutableListOf()
        val unknownPlayers: List<Player> = mutableListOf()

        return Match(
                matchId = matchEntity.matchId,
                location = matchEntity.location,
                start = ZonedDateTime.parse(matchEntity.startDateTime),
                end = ZonedDateTime.parse(matchEntity.endDateTime),
                squad = Squad(
                        size = matchEntity.numberOfPlayers,
                        invited = invitedPlayers,
                        confirmed = confirmedPlayers,
                        unsure = unknownPlayers,
                        declined = declinedPlayers
                )

        )
    }


}