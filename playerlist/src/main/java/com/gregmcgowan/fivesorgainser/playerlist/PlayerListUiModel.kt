package com.gregmcgowan.fivesorgainser.playerlist

import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID

data class PlayerListUiModel(val players: List<PlayerListItemUiModel>,
                             val showPlayers: Boolean,
                             val showLoading: Boolean,
                             val showErrorMessage: Boolean,
                             @StringRes val errorMessage: Int = NO_STRING_RES_ID)

data class PlayerListItemUiModel(
        val id: String,
        val name: String
)

sealed class PlayerListNavigationEvents {
    object Idle : PlayerListNavigationEvents()
    object AddPlayerEvent : PlayerListNavigationEvents()
}


