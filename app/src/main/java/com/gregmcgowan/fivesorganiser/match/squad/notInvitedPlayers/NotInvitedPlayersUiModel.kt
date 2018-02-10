package com.gregmcgowan.fivesorganiser.match.squad.notInvitedPlayers


data class NotInvitedPlayersUiModel(
        val showLoading: Boolean,
        val showContent: Boolean,
        val notInvitedPlayers: List<NotInvitedPlayerItemModel>
)

data class NotInvitedPlayerItemModel(
        val playerId: String,
        val playerName: String,
        val selected: Boolean
)