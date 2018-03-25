package com.gregmcgowan.fivesorganiser.match.inviteplayers


data class InvitePlayersUiModel(
        val showLoading: Boolean,
        val showContent: Boolean,
        val invitePlayersList: List<InvitePlayerListItemModel>
)

data class InvitePlayerListItemModel(
        val playerId: String,
        val playerName: String,
        val selected: Boolean
)

enum class InvitedPlayerStatus {
    INVITED,
    UNSURE,
    CONFIRMED,
    DECLINED

}