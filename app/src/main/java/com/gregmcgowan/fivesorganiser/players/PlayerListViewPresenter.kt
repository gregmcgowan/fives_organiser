package com.gregmcgowan.fivesorganiser.players

import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlayerListViewPresenter(val playerListView: PlayerListContract.View,
                              val playersRepo: PlayerRepo) : PlayerListContract.Presenter {

    var subscription: Subscription? = null

    override fun startPresenting() {
        loadPlayers()
    }

    fun loadPlayers() {
        playerListView.showProgressBar(true)
        playerListView.showPlayerList(false)

        subscription = playersRepo.getPlayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ players -> handlePlayers(players) },
                        { error -> handleError(error) })
    }

    private fun handlePlayers(players: List<Player>) {
        playerListView.showPlayerList(true)
        playerListView.showProgressBar(false)
        playerListView.showPlayers(players)
    }

    private fun handleError(e: Throwable?) {
        //TODO implement
    }

    override fun stopPresenting() {
        subscription?.unsubscribe()
        subscription = null
    }

}