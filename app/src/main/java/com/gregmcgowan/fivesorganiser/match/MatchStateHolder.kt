package com.gregmcgowan.fivesorganiser.match

import org.threeten.bp.ZonedDateTime

class MatchStateHolder(var match: Match) {

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
                ))
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
                ))
    }

    fun locationUpdated(location: String) {
        match = match.copy(location = location)
    }

    fun squadSizeUpdated(numberOfPlayers: Int) {
        match = match.copy(squad = match.squad.copy(size = numberOfPlayers.toLong()))
    }
}