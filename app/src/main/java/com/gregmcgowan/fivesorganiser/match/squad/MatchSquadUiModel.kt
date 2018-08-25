package com.gregmcgowan.fivesorganiser.match.squad


data class MatchSquadUiModel(
        val showLoading: Boolean,
        val showContent: Boolean,
        val playersListUi: List<MatchSquadListItemUiModel>
)

data class MatchSquadListItemUiModel(
        val playerId: String,
        val playerName: String,
        var status : MatchSquadListPlayerStatus
)

sealed class MatchSquadListNavEvent {

    object Idle : MatchSquadListNavEvent()

    object CloseScreen : MatchSquadListNavEvent()

}

enum class MatchSquadListPlayerStatus {
    NOT_SELECTED,
    INVITED_SELECTED,
    DECLINED_SELECTED,
    UNKNOWN_SELECTED,
    CONFIRMED_SELECTED
}
