package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Added
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Modified
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Removed
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import javax.inject.Inject

interface PlayerListUiStateMapper {
    fun map(existingState: UiState<PlayerListUiState>, updates: DataUpdate<Player>): UiState<PlayerListUiState>
}

class PlayerListUiStateMapperImpl @Inject constructor() : PlayerListUiStateMapper {

    override fun map(existingState: UiState<PlayerListUiState>, updates: DataUpdate<Player>) =
            when (existingState) {
                is LoadingUiState,
                is ErrorUiState -> ContentUiState(PlayerListUiState(mapPlayerList(updates.changes)))
                is ContentUiState -> mapFromExistingContent(existingState.content, updates)
            }

    private fun mapFromExistingContent(
            existingState: PlayerListUiState,
            update: DataUpdate<Player>): UiState<PlayerListUiState> {
        val updatedPlayerUiModels = mapPlayerList(update.changes, existingState.players.toMutableList())

        return ContentUiState(existingState.copy(players = updatedPlayerUiModels))
    }


    private fun mapPlayerList(
            updates: List<DataChange<Player>>,
            existingPlayerUiStates: MutableList<PlayerListItemUiState> = mutableListOf(),
    ): List<PlayerListItemUiState> {
        updates.forEach { update ->
            val player = update.data
            val findPlayerIndex = existingPlayerUiStates.indexOfFirst { it.id == player.playerId }
            when (update.type) {
                Added -> {
                    if (findPlayerIndex == -1) {
                        existingPlayerUiStates.add(map(player))
                    }
                }
                Modified -> {
                    if (findPlayerIndex != -1) {
                        existingPlayerUiStates[findPlayerIndex] = map(player)
                    }
                }
                Removed -> {
                    if (findPlayerIndex != -1) {
                        existingPlayerUiStates.removeAt(findPlayerIndex)
                    }
                }
            }
        }
        existingPlayerUiStates.sortBy { it.name }
        return existingPlayerUiStates
    }

    private fun map(player: Player) = PlayerListItemUiState(player.playerId, player.name)
}
