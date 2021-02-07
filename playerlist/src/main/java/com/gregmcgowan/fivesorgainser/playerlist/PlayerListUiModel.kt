package com.gregmcgowan.fivesorgainser.playerlist


data class PlayerListUiModel(
        val players: List<PlayerListItemUiModel>
)

data class PlayerListItemUiModel(
        val id: String,
        val name: String
)

sealed class PlayerListUiEvents {
    object Idle : PlayerListUiEvents()
    object ShowAddPlayerScreenEvent : PlayerListUiEvents()
}

sealed class PlayerListUserEvent {

    object AddPlayerSelectedEvent : PlayerListUserEvent()
}


