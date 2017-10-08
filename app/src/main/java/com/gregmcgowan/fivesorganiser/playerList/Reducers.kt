package com.gregmcgowan.fivesorganiser.playerList

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerEntity
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiModel

typealias PlayerListUiModelReducer = (PlayerListUiModel) -> PlayerListUiModel

internal fun addPlayersModelReducer(): PlayerListUiModelReducer = { playerListUiModel ->
    playerListUiModel.copy(
            showAddPlayers = true
    )
}

internal fun playersLoadedModelReducer(players: List<PlayerEntity>): PlayerListUiModelReducer = { playerListUiModel ->
    val playerListItemUiModels = players.map { PlayerListContract.PlayerListItemUiModel(it.name) }

    if (players.isNotEmpty()) {
        playerListUiModel.copy(
                players = playerListItemUiModels,
                showLoading = false,
                showPlayers = true,
                showErrorMessage = false,
                errorMessage = null)
    } else {
        PlayerListUiModel(
                emptyList(),
                showLoading = false,
                showPlayers = false,
                showErrorMessage = true,
                errorMessage = "No players, press + to add")
    }
}

fun playersLoadErrorModelReducer(): PlayerListUiModelReducer = { _ ->
    PlayerListUiModel(
            emptyList(),
            showLoading = false,
            showPlayers = false,
            showErrorMessage = true,
            errorMessage = "Oops, Something went wrong")
}