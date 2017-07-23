package com.gregmcgowan.fivesorganiser.players

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.main.MainContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class PlayerListViewPresenter(val playerListView: PlayerListContract.View,
                              val playersRepo: PlayerRepo) : MainContract.MainViewPresenter {

    var subscription: Disposable? = null

    override fun startPresenting() {
        loadPlayers()
    }

    fun loadPlayers() {
        playerListView.viewState = ViewState.Loading()

        subscription = playersRepo.getPlayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handlePlayers,
                        this::handleError
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
        subscription?.dispose()
        subscription = null
    }

    override fun menuId(): Int = R.id.main_players_menu_item
}