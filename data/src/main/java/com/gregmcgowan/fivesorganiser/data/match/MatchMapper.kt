package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.Player
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class MatchMapper @Inject constructor() {

    fun map(match: MatchEntity,
            squad: MatchSquadEntity,
            players: Map<String, Player>): Match {

        val playerAndMatchStatuses = mutableListOf<PlayerAndMatchStatus>()

        populateList(squad.invited, PlayerMatchSquadStatus.INVITED, players, playerAndMatchStatuses)
        populateList(squad.confirmed, PlayerMatchSquadStatus.CONFIRMED, players, playerAndMatchStatuses)
        populateList(squad.declined, PlayerMatchSquadStatus.DECLINED, players, playerAndMatchStatuses)
        populateList(squad.unsure, PlayerMatchSquadStatus.UNSURE, players, playerAndMatchStatuses)

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