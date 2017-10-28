package com.gregmcgowan.fivesorganiser.matchList

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.Match
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListItemUiModel
import com.gregmcgowan.fivesorganiser.matchList.MatchListContract.MatchListUiModel

typealias MatchListUiModelReducer = (MatchListUiModel) -> MatchListUiModel

internal fun showMatchScreenReducer(): MatchListUiModelReducer = { uiModel ->
    uiModel.copy(goToMatchScreen = true)
}

internal fun loadingReducer() : MatchListUiModelReducer = { uiModel ->
    uiModel.copy(
            showEmptyView = false,
            showProgressBar = true,
            showList = false,
            emptyMessage = null
    )
}

internal fun loadMatchesReducer(matches: List<Match>,
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


internal fun showEditMatchScreenReducer(matchId: String): MatchListUiModelReducer = { uiModel ->
    uiModel.copy(goToMatchScreen = true, matchIdSelected = matchId)
}