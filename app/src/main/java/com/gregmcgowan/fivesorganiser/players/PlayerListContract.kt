package com.gregmcgowan.fivesorganiser.players

import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.main.MainContract

interface PlayerListContract {

    data class PlayerListModel(val players : List<Player>)

    interface View {
        var viewState : ViewState
    }

    interface Presenter : MainContract.BaseViewPresenter
}