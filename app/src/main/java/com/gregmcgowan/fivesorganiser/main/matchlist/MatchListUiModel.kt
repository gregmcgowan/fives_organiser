package com.gregmcgowan.fivesorganiser.main.matchlist


data class MatchListUiModel(
        val showList: Boolean,
        val showProgressBar: Boolean,
        val showEmptyView: Boolean,
        val emptyMessage: String?,
        val matches: List<MatchListItemUiModel>
)

data class MatchListItemUiModel(val matchId: String,
                                val summary: String,
                                val heading: String)

sealed class MatchListNavigationEvents {

    object Idle : MatchListNavigationEvents()

    object AddMatchEvent : MatchListNavigationEvents()

    class MatchSelected(val matchId: String) : MatchListNavigationEvents()
}

