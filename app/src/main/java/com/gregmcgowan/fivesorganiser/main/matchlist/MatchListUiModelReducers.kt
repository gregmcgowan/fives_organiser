package com.gregmcgowan.fivesorganiser.main.matchlist

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.match.Match
import com.gregmcgowan.fivesorganiser.match.MatchTypeHelper
import javax.inject.Inject

typealias MatchListUiModelReducer = (MatchListUiModel) -> MatchListUiModel

class MatchListUiModelReducers @Inject constructor(
        private val strings: Strings,
        private val dateTimeFormatter: ZonedDateTimeFormatter,
        private val matchTypeHelper: MatchTypeHelper
) {

    internal fun loadingMatchListUiModel(): MatchListUiModelReducer = { uiModel ->
        uiModel.copy(
                showEmptyView = false,
                showProgressBar = true,
                showList = false,
                emptyMessage = null
        )
    }

    internal fun matchListUiModel(matches: List<Match>): MatchListUiModelReducer = { uiModel ->
        val matchesUiModels = mutableListOf<MatchListItemUiModel>()
        for (match in matches) {
            val matchType = matchTypeHelper.getMatchType(match.squad.size.toInt())
            val summary = strings.getString(R.string.match_list_summary_format, matchType, match.location)

            matchesUiModels += MatchListItemUiModel(
                    matchId = match.matchId,
                    heading = dateTimeFormatter.formatDate(match.start),
                    summary = summary)
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


}



