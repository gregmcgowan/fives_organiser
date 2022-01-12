package com.gregmcgowan.fivesorganiser.matchlist


data class MatchListUiModel(
        val showList: Boolean,
        val showProgressBar: Boolean,
        val showEmptyView: Boolean,
        val emptyMessage: String?,
        val matches: List<MatchListItemUiModel>
)

data class MatchListItemUiModel(
        val matchId: String,
        val matchType: String,
        val dateAndTime: String,
        val location: String,
        val squadStatus: String
)


