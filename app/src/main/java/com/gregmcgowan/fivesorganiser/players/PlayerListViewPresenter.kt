package com.gregmcgowan.fivesorganiser.players

import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlayerListViewPresenter(val playerListView: PlayerListContract.View,
                              val playersRepo: PlayerRepo) : PlayerListContract.Presenter {

    override fun loadPlayers() {
        playerListView.showProgressBar(false)
        playersRepo.getPlayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Player>> {
                    override fun onError(e: Throwable?) {

                    }

                    override fun onCompleted() {

                    }

                    override fun onNext(players: List<Player>) {
                        playerListView.showPlayerList(true)
                        playerListView.showPlayers(players)
                    }
                })
    }

    override fun startPresenting() {
        loadPlayers()
    }

    override fun handleAddPlayerSelected() {
        //TODO start add player activity
        val player = Player("Player " + (System.currentTimeMillis()), "123r3243", "email")
        playersRepo.addPlayer(player)
        loadPlayers()
    }
}