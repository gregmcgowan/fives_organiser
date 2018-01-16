package com.gregmcgowan.fivesorganiser.main.playerList


data class PlayerListUiModel(val players: List<PlayerListItemUiModel>,
                             val showPlayers: Boolean,
                             val showLoading: Boolean,
                             val showErrorMessage: Boolean,
                             val errorMessage: String?)

data class PlayerListItemUiModel(val name: String)


sealed class PlayerListNavigationEvents{
    object Idle : PlayerListNavigationEvents()
    object AddPlayerEvent : PlayerListNavigationEvents()
}


