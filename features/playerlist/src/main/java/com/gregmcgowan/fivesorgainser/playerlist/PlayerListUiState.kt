package com.gregmcgowan.fivesorgainser.playerlist

data class PlayerListUiState(
    val players: List<PlayerListItemUiState>,
)

data class PlayerListItemUiState(
    val id: String,
    val name: String,
)

sealed class PlayerListUserEvent {
    object AddPlayerSelectedEvent : PlayerListUserEvent()
}
