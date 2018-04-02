package com.gregmcgowan.fivesorganiser.match.summary

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.match.DEFAULT_NO_OF_PLAYERS
import com.gregmcgowan.fivesorganiser.match.Match
import com.gregmcgowan.fivesorganiser.match.MatchTypeHelper
import javax.inject.Inject

class MatchSummaryUiModelReducers @Inject constructor(
        private val instantFormatter: ZonedDateTimeFormatter,
        private val matchTypeHelper: MatchTypeHelper

) {

    internal fun savingUiModel(): MatchUiModelReducer = { model ->
        model.copy(loading = true, showContent = false)
    }

    internal fun dateUpdatedReducer(match: Match): MatchUiModelReducer = { model ->
        model.copy(date = instantFormatter.formatDate(match.start))
    }

    internal fun startTimeUpdated(match: Match): MatchUiModelReducer = { model ->
        model.copy(startTime = instantFormatter.formatTime(match.start))
    }

    internal fun locationUpdatedReducer(match: Match): MatchUiModelReducer = { model ->
        model.copy(location = match.location)
    }

    internal fun matchTypeUpdated(match: Match): MatchUiModelReducer = { model ->
        val matchTypes = matchTypeHelper.getAllMatchTypes()
        val selectedMatchType = getSelectedMatchTypeIndex(match, matchTypes)
        model.copy(
                selectedMatchTypeIndex = selectedMatchType
        )
    }

    internal fun displayMatchReducer(match: Match): MatchUiModelReducer = { model ->
        val matchTypes = matchTypeHelper.getAllMatchTypes()
        val selectedMatchType = getSelectedMatchTypeIndex(match, matchTypes)

        model.copy(
                loading = false,
                showContent = true,
                success = true,
                location = match.location,
                date = instantFormatter.formatDate(match.start),
                startTime = instantFormatter.formatTime(match.start),
                endTime = instantFormatter.formatTime(match.end),
                matchTypeOptions = matchTypes,
                selectedMatchTypeIndex = selectedMatchType

        )
    }

    private fun getSelectedMatchTypeIndex(match: Match, matchTypes: List<String>): Int {
        val numberOfPlayers: Int
        // TODO maybe default to last selected size
        if (match.squad.size == 0L) {
            numberOfPlayers = DEFAULT_NO_OF_PLAYERS.toInt()
        } else {
            numberOfPlayers = match.squad.size.toInt()
        }
        val matchType = matchTypeHelper.getMatchType(numberOfPlayers)
        return matchTypes.indexOf(matchType)
    }

    fun endTimeUpdated(match: Match): MatchUiModelReducer = { model ->
        model.copy(endTime = instantFormatter.formatTime(match.end))
    }

}


