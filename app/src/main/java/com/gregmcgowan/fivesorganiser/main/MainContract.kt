package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.core.ViewPresenter
import com.gregmcgowan.fivesorganiser.core.ViewState
import io.reactivex.Observable

interface MainContract {

    data class MainModel(val menuIdToShow : Int)

    interface ParentView {
        var viewState : ViewState

        fun menuSelected () : Observable<Int>
    }

    interface Presenter : ViewPresenter

    interface MainViewPresenter : ViewPresenter {
        fun menuId() : Int
    }
}