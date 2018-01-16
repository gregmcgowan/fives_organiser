package com.gregmcgowan.fivesorganiser.main.playerList

import com.gregmcgowan.fivesorganiser.core.data.player.Player

typealias PlayerListUiModelReducer = (PlayerListUiModel) -> PlayerListUiModel


internal fun loadingPlayersUiModel(): PlayerListUiModelReducer = { playerListUiModel ->
    playerListUiModel.copy(showLoading = true, showPlayers = false,
            showErrorMessage = false, errorMessage = null)
}

internal fun playersLoadedUiModel(players: List<Player>): PlayerListUiModelReducer = { playerListUiModel ->
    val playerListItemUiModels = players.map { PlayerListItemUiModel(it.name) }

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

fun playersLoadErrorUiModel(): PlayerListUiModelReducer = { _ ->
    PlayerListUiModel(
            emptyList(),
            showLoading = false,
            showPlayers = false,
            showErrorMessage = true,
            errorMessage = "Oops, Something went wrong")
}