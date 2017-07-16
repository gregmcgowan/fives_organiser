package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.core.ViewState
import rx.Observable

interface MainContract {

    data class MainModel(val menuIdToShow : Int)

    interface View {
        var viewState : ViewState

        fun menuSelected () : Observable<Int>
    }

    interface BaseViewPresenter {
        fun startPresenting()
        fun stopPresenting()
    }

    interface Presenter : BaseViewPresenter
}