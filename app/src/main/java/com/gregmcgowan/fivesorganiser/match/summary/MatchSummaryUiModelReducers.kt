package com.gregmcgowan.fivesorganiser.match.summary

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.match.Match
import javax.inject.Inject

class MatchSummaryUiModelReducers @Inject constructor(private val instantFormatter: ZonedDateTimeFormatter) {

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

    internal fun numberOfPlayersUpdatedReduce(match: Match): MatchUiModelReducer = { model ->
        if (match.squad.size > 0) {
            model.copy(numberOfPLayers = match.squad.size.toString())
        } else {
            model
        }
    }

    internal fun displayMatchReducer(match: Match, new: Boolean): MatchUiModelReducer = { model ->
        val numberOfPlayers: String
        if (match.squad.size > 0) {
            numberOfPlayers = match.squad.size.toString()
        } else {
            numberOfPlayers = ""
        }

        // TODO get from string resources
        val title: String = if (new) {
            "New match"
        } else {
            "Update match"
        }

        model.copy(
                title = title,
                loading = false,
                showContent = true,
                success = true,
                location = match.location,
                date = instantFormatter.formatDate(match.start),
                startTime = instantFormatter.formatTime(match.start),
                endTime = instantFormatter.formatTime(match.end),
                numberOfPLayers = numberOfPlayers
        )
    }

    fun endTimeUpdated(match: Match): MatchUiModelReducer = { model ->
        model.copy(endTime = instantFormatter.formatTime(match.end))
    }

}


