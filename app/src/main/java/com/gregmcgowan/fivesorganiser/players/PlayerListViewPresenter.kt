package com.gregmcgowan.fivesorganiser.players

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.main.MainContract
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlayerListViewPresenter(val playerListView: PlayerListContract.View,
                              val playersRepo: PlayerRepo) : MainContract.MainViewPresenter {

    var subscription: Subscription? = null

    override fun startPresenting() {
        loadPlayers()
    }

    fun loadPlayers() {
        playerListView.viewState = ViewState.Loading()

        subscription = playersRepo.getPlayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { players -> handlePlayers(players) },
                        { error -> handleError(error) }
                )
    }

    private fun handlePlayers(players: List<Player>) {
        if (!players.isEmpty()) {
            playerListView.viewState = ViewState.Success(PlayerListContract.PlayerListModel(players))
        } else {
            playerListView.viewState = ViewState.Error("No players, press + to add")
        }
    }

    private fun handleError(e: Throwable?) {
        playerListView.viewState = ViewState.Error("Oops, Something went wrong")
    }

    override fun stopPresenting() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun menuId(): Int = R.id.main_players_menu_item
}