package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.ViewState
import rx.Subscription

class MainPresenter(val mainParentView: MainContract.ParentView,
                    val mainViewPresenters : List<MainContract.MainViewPresenter>,
                    val authentication: Authentication) : MainContract.Presenter {

    private var currentSelection: Int = -1
    private var subscription: Subscription? = null

    override fun startPresenting() {
        if (authentication.isInitialised()) {
            subscription = mainParentView.menuSelected()
                    .subscribe({ menuID -> handleMenuSelection(menuID) })

            if (currentSelection == -1) {
                currentSelection = R.id.main_players_menu_item;
            }
            handleMenuSelection(currentSelection)
        } else {
            mainParentView.viewState = ViewState.Loading()
            authentication.initialise({ startPresenting() })
        }
    }

    override fun stopPresenting() {
        subscription?.unsubscribe()
    }

    fun handleMenuSelection(menuItemID: Int) {
        this.currentSelection = menuItemID
        mainParentView.viewState = ViewState.Success(MainContract.MainModel(menuItemID))
        for(mainViewPresenter : MainContract.MainViewPresenter in mainViewPresenters) {
            if(mainViewPresenter.menuId() == menuItemID) {
                mainViewPresenter.startPresenting()
            } else {
                mainViewPresenter.stopPresenting()
            }
        }
    }

}