package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.players.PlayerListViewPresenter
import rx.Subscription


class MainPresenter(val mainView: MainContract.View,
                    val playerListViewPresenter: PlayerListViewPresenter,
                    val authentication: Authentication) : MainContract.Presenter {

    private var currentSelection: Int = -1
    private var subscription: Subscription? = null

    override fun startPresenting() {
        if (authentication.isInitialised()) {
            subscription = mainView.menuSelected()
                    .subscribe({ menuID -> handleMenuSelection(menuID) })

            if (currentSelection == -1) {
                currentSelection = R.id.main_players_menu_item;
            }
            handleMenuSelection(currentSelection)
        } else {
            mainView.viewState = ViewState.Loading()
            authentication.initialise({ startPresenting() })
        }
    }

    override fun stopPresenting() {
        subscription?.unsubscribe()
    }

    fun handleMenuSelection(menuItemID: Int) {
        this.currentSelection = menuItemID
        mainView.viewState = ViewState.Success(MainContract.MainModel(menuItemID))
        //TODO generalise this for other sub presenters
        if(menuItemID == R.id.main_players_menu_item) {
            playerListViewPresenter.startPresenting()
        } else {
            playerListViewPresenter.stopPresenting()
        }
    }

}