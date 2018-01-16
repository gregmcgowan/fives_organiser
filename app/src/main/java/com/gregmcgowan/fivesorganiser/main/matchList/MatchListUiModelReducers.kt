package com.gregmcgowan.fivesorganiser.main.matchList

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.Match

typealias MatchListUiModelReducer = (MatchListUiModel) -> MatchListUiModel


internal fun loadingMatchListUiModel() : MatchListUiModelReducer = { uiModel ->
    uiModel.copy(
            showEmptyView = false,
            showProgressBar = true,
            showList = false,
            emptyMessage = null
    )
}

internal fun matchListUiModel(matches: List<Match>,
                              dateTimeFormatter: ZonedDateTimeFormatter): MatchListUiModelReducer = { uiModel ->
    val matchesUiModels = mutableListOf<MatchListItemUiModel>()
    for (match in matches) {
        matchesUiModels += MatchListItemUiModel(
                matchId = match.matchId,
                location = match.location,
                dateAndTime = dateTimeFormatter.formatDate(match.dateTime))
    }
    var errorMessage: String? = null
    var showEmptyView = false
    if (matches.isEmpty()) {
        showEmptyView = true
        errorMessage = "No matches press + to add"
    }

    uiModel.copy(
            showEmptyView = showEmptyView,
            showProgressBar = false,
            showList = true,
            matches = matchesUiModels,
            emptyMessage = errorMessage
    )
}

