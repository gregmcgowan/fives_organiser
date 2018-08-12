package com.gregmcgowan.fivesorganiser.match.timelocation

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.match.Match
import javax.inject.Inject

class MatchTimeAndLocationUiModelMapper @Inject constructor(
        private val instantFormatter: ZonedDateTimeFormatter
){

    fun map(match: Match): MatchTimeAndLocationUiModel {
        return MatchTimeAndLocationUiModel(
                loading = false,
                showContent = true,
                showErrorState = false,
                location = match.location,
                showCreateSquadButton = match.matchId == NEW_MATCH_ID,
                date = instantFormatter.formatDate(match.start),
                startTime = instantFormatter.formatTime(match.start),
                endTime = instantFormatter.formatTime(match.end)
        )
    }

}