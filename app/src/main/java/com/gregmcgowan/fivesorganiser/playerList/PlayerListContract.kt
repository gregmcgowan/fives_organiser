package com.gregmcgowan.fivesorganiser.playerList

import io.reactivex.Observable

interface PlayerListContract {

    interface Ui {

        fun render(uiModel: PlayerListUiModel)

        fun addPlayerButtonEvents() : Observable<PlayerListUiEvent>

        fun showAddPlayers()
    }


}