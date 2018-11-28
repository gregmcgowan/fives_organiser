package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.*
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import javax.inject.Inject

class PlayerListUiModelMapper @Inject constructor() {

    fun map(existingModel: PlayerListUiModel, update: DataUpdate<Player>): PlayerListUiModel {

        val updatedPlayerUiModels = updatePlayers(existingModel.players.toMutableList(), update.changes)
        val playersExist = updatedPlayerUiModels.isNotEmpty()

        return existingModel.copy(
                showLoading = false,
                showPlayers = playersExist,
                showErrorMessage = !playersExist,
                errorMessage = getErrorMessage(playersExist),
                players = updatedPlayerUiModels
        )
    }


    private fun updatePlayers(existingPlayerUiModels: MutableList<PlayerListItemUiModel>,
                              updates: List<DataChange<Player>>): List<PlayerListItemUiModel> {
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

    private fun getErrorMessage(playersExist: Boolean) =
            if (playersExist) NO_STRING_RES_ID else R.string.player_list_no_players_message


}