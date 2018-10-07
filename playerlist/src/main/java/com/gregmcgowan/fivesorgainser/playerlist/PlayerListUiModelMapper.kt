package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.data.player.Player
import javax.inject.Inject

class PlayerListUiModelMapper @Inject constructor(
        private val strings: Strings
) {

    fun map(players: List<Player>): PlayerListUiModel = PlayerListUiModel(
            players = players.map { PlayerListItemUiModel(it.name) },
            showLoading = false,
            showPlayers = players.isNotEmpty(),
            showErrorMessage = players.isEmpty() ,
            errorMessage = getErrorMessage(players))

    private fun getErrorMessage(players: List<Player>): String? {
        return if (players.isEmpty()) {
            strings.getString(R.string.player_list_no_players_message)
        } else null
    }

}

