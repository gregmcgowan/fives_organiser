package com.gregmcgowan.fivesorganiser.playerList

import io.reactivex.Observable

interface PlayerListContract {

    interface Ui {

        fun render(uiModel: PlayerListUiModel)

        fun addPlayerButtonEvents() : Observable<PlayerListUiEvent>

        fun showAddPlayers()
    }

    data class PlayerListUiModel(val players: List<PlayerListItemUiModel>,
                                 val showPlayers: Boolean,
                                 val showLoading: Boolean,
                                 val showErrorMessage: Boolean,
                                 val errorMessage: String?,
                                 val showAddPlayers: Boolean = false)

    data class PlayerListItemUiModel(val name : String)

    sealed class PlayerListUiEvent {
        class AddPlayerEvent : PlayerListUiEvent()
        class ViewShownEvent : PlayerListUiEvent()
    }


}