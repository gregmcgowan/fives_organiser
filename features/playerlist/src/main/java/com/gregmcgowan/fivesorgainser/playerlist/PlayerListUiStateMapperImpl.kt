package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.data.player.Player
import javax.inject.Inject

interface PlayerListUiStateMapper {
    fun map(players: List<Player>): UiState<PlayerListUiState>
}

class PlayerListUiStateMapperImpl @Inject constructor() : PlayerListUiStateMapper {
    override fun map(players: List<Player>): UiState<PlayerListUiState> =
        ContentUiState(
            PlayerListUiState(players.map { map(it) }),
        )

    private fun map(player: Player) =
        PlayerListItemUiState(
            id = player.playerId,
            name = player.name,
        )
}
