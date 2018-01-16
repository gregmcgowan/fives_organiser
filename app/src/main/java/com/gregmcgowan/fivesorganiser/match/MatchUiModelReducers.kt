package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.Match

class MatchUiModelReducers(private val instantFormatter: ZonedDateTimeFormatter) {

    internal fun savingUiModel(): MatchUiModelReducer = { model ->
        model.copy(loading = true, showContent = false)
    }

    internal fun dateUpdatedReducer(match: Match): MatchUiModelReducer = { model ->
        model.copy(date = instantFormatter.formatDate(match.dateTime))
    }

    internal fun timeUpdatedReducer(match: Match): MatchUiModelReducer = { model ->
        model.copy(time = instantFormatter.formatTime(match.dateTime))
    }

    internal fun locationUpdatedReducer(match: Match): MatchUiModelReducer = { model ->
        model.copy(location = match.location)
    }

    internal fun numberOfPlayersUpdatedReduce(match: Match): MatchUiModelReducer = { model ->
        if (match.numberOfPlayers > 0) {
            model.copy(numberOfPLayers = match.numberOfPlayers.toString())
        } else {
            model
        }
    }

    internal fun displayMatchReducer(match: Match): MatchUiModelReducer = { model ->
        val numberOfPlayers: String
        if (match.numberOfPlayers > 0) {
            numberOfPlayers = match.numberOfPlayers.toString()
        } else {
            numberOfPlayers = ""
        }

        model.copy(
                loading = false,
                showContent = true,
                success = true,
                location = match.location,
                date = instantFormatter.formatDate(match.dateTime),
                time = instantFormatter.formatTime(match.dateTime),
                numberOfPLayers = numberOfPlayers
        )
    }

}


