package com.gregmcgowan.fivesorganiser.match.inviteplayers


data class InvitePlayersUiModel(
        val showLoading: Boolean,
        val showContent: Boolean,
//        val matchTypeOptions : List<String> = emptyList(),
//        val selectedMatchTypeIndex : Int = -1,
//        val confirmedPlayersCount: Int = 0,
//        val unknownPlayersCount: Int = 0,
//        val declinedPlayersCount: Int = 0,
        val invitePlayersList: List<InvitePlayerListItemModel>
)

data class InvitePlayerListItemModel(
        val playerId: String,
        val playerName: String,
        val invite : Boolean
)
