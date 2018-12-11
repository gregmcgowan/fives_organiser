package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.Player
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class MatchMapper @Inject constructor() {

    fun map(match: MatchEntity,
            allPlayers: Map<String, Player>): Match {

        val playerAndMatchStatuses = mutableListOf<PlayerAndMatchStatus>()

        populateList(match.invited, PlayerMatchSquadStatus.INVITED, allPlayers, playerAndMatchStatuses)
        populateList(match.confirmed, PlayerMatchSquadStatus.CONFIRMED, allPlayers, playerAndMatchStatuses)
        populateList(match.declined, PlayerMatchSquadStatus.DECLINED, allPlayers, playerAndMatchStatuses)
        populateList(match.unsure, PlayerMatchSquadStatus.UNSURE, allPlayers, playerAndMatchStatuses)

        return Match(
                matchId = match.matchId,
                location = match.location,
                start = ZonedDateTime.parse(match.startDateTime),
                end = ZonedDateTime.parse(match.endDateTime),
                squad = Squad(
                        expectedSize = match.numberOfPlayers,
                        playerAndStatuses = playerAndMatchStatuses
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