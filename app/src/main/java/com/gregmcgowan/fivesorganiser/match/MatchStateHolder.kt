package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

const val DEFAULT_NO_OF_PLAYERS = 10L

class MatchStateHolder @Inject constructor() {

    lateinit var match: Match

    fun dateUpdated(year: Int,
                    month: Int,
                    date: Int) {
        val dateTime = ZonedDateTime.of(
                year,
                month + 1,
                date,
                match.start.hour,
                match.start.minute,
                match.start.second,
                match.start.nano,
                match.start.zone
        )
        match = match.copy(start = dateTime)
    }

    fun startTimeUpdated(hour: Int,
                         minute: Int) {
        match = match.copy(
                start = ZonedDateTime.of(
                        match.start.year,
                        match.start.month.value,
                        match.start.dayOfMonth,
                        hour,
                        minute,
                        match.start.second,
                        match.start.nano,
                        match.start.zone
                )
        )
    }

    fun endTimeUpdated(hour: Int, minute: Int) {
        match = match.copy(
                end = ZonedDateTime.of(
                        match.end.year,
                        match.end.month.value,
                        match.end.dayOfMonth,
                        hour,
                        minute,
                        match.end.second,
                        match.end.nano,
                        match.end.zone
                )
        )
    }

    fun locationUpdated(location: String) {
        match = match.copy(location = location)
    }

    fun squadSizeUpdated(numberOfPlayers: Int) {
        match = match.copy(squad = match.squad.copy(expectedSize = numberOfPlayers.toLong()))
    }

    fun createOrRestoreMatch() {
        // TODO we should check if there is a stored match to restore from
        match = Match(
                matchId = "",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now().plusHours(1),
                location = "",
                squad = Squad(DEFAULT_NO_OF_PLAYERS)

        )
    }

    fun updatePlayerStatus(player: Player,
                           status: PlayerMatchSquadStatus) {
        match = match.copy(squad = match.squad.updatePlayerStatus(player, status))
    }

}