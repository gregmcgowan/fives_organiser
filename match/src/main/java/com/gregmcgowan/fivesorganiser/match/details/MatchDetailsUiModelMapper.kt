package com.gregmcgowan.fivesorganiser.match.details

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.data.match.Match
import com.gregmcgowan.fivesorganiser.data.match.MatchTypeHelper
import javax.inject.Inject

class MatchDetailsUiModelMapper @Inject constructor(
        private val instantFormatter: ZonedDateTimeFormatter,
        private val matchTypeHelper: MatchTypeHelper
){

    fun map(match: Match): MatchDetailsUiModel = MatchDetailsUiModel(
            loading = false,
            showContent = true,
            showErrorState = false,
            location = match.location,
            showCreateSquadButton = match.matchId == NEW_MATCH_ID,
            date = instantFormatter.formatDate(match.start),
            startTime = instantFormatter.formatTime(match.start),
            endTime = instantFormatter.formatTime(match.end),
            matchTypeOptions = matchTypeHelper.getAllMatchTypes(),
            selectedMatchTypeIndex = getSelectedMatchTypeIndex(match)
    )

    private fun getSelectedMatchTypeIndex(match: Match): Int {
        val allMatchTypes = matchTypeHelper.getAllMatchTypes()
        // TODO maybe default to last selected size
        val numberOfPlayers: Int = if (match.squad.expectedSize == 0L) {
            10
        } else {
            match.squad.expectedSize.toInt()
        }

        val matchType = matchTypeHelper.getMatchType(numberOfPlayers)
        return allMatchTypes.indexOf(matchType)
    }



}