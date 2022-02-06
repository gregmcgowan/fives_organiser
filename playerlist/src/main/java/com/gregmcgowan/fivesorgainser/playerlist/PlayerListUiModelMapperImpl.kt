package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ErrorUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ContentUiModel
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Added
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Modified
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Removed
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import javax.inject.Inject

interface PlayerListUiModelMapper {
    fun map(existingModel: UiModel<PlayerListUiModel>, updates: DataUpdate<Player>): UiModel<PlayerListUiModel>
}

class PlayerListUiModelMapperImpl @Inject constructor() : PlayerListUiModelMapper {

    override fun map(existingModel: UiModel<PlayerListUiModel>, updates: DataUpdate<Player>) =
            when (existingModel) {
                is LoadingUiModel,
                is ErrorUiModel -> ContentUiModel(PlayerListUiModel(mapPlayerList(updates.changes)))
                is ContentUiModel -> mapFromExistingContent(existingModel.content, updates)
            }

    private fun mapFromExistingContent(
            existingModel: PlayerListUiModel,
            update: DataUpdate<Player>): UiModel<PlayerListUiModel> {
        val updatedPlayerUiModels = mapPlayerList(update.changes, existingModel.players.toMutableList())

        return ContentUiModel(existingModel.copy(players = updatedPlayerUiModels))
    }


    private fun mapPlayerList(
            updates: List<DataChange<Player>>,
            existingPlayerUiModels: MutableList<PlayerListItemUiModel> = mutableListOf(),
    ): List<PlayerListItemUiModel> {
        updates.forEach { update ->
            val player = update.data
            val findPlayerIndex = existingPlayerUiModels.indexOfFirst { it.id == player.playerId }
            when (update.type) {
                Added -> {
                    if (findPlayerIndex == -1) {
                        existingPlayerUiModels.add(map(player))
                    }
                }
                Modified -> {
                    if (findPlayerIndex != -1) {
                        existingPlayerUiModels[findPlayerIndex] = map(player)
                    }
                }
                Removed -> {
                    if (findPlayerIndex != -1) {
                        existingPlayerUiModels.removeAt(findPlayerIndex)
                    }
                }
            }
        }
        existingPlayerUiModels.sortBy { it.name }
        return existingPlayerUiModels
    }

    private fun map(player: Player) = PlayerListItemUiModel(player.playerId, player.name)
}
