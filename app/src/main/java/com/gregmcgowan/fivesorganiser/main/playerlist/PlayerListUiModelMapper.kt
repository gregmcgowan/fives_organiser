package com.gregmcgowan.fivesorganiser.main.playerlist

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.data.player.Player
import javax.inject.Inject

class PlayerListUiModelMapper @Inject constructor(
        private val strings: Strings
) {

    fun map(players: List<Player>): PlayerListUiModel {
        val playerListItemUiModels = players.map { PlayerListItemUiModel(it.name) }

        return if (players.isNotEmpty()) {
            PlayerListUiModel(
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
                    errorMessage = strings.getString(R.string.player_list_no_players_message))
        }
    }
}

