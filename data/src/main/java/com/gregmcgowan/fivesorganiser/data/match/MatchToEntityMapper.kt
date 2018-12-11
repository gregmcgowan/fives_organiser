package com.gregmcgowan.fivesorganiser.data.match

import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class MatchToEntityMapper @Inject constructor() {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    fun map(match: Match): MatchEntity {
        return MatchEntity(
                matchId = match.matchId,
                location = match.location,
                startDateTime = match.start.format(dateTimeFormatter),
                endDateTime = match.end.format(dateTimeFormatter),
                numberOfPlayers = match.squad.expectedSize,
                invited = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.INVITED),
                confirmed = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.CONFIRMED),
                declined = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.DECLINED),
                unsure = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.UNSURE)
        )
    }

}