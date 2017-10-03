package com.gregmcgowan.fivesorganiser.playerList

import com.gregmcgowan.fivesorganiser.main.MainContract
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class PlayerListUiPresenter(val playerListUi: PlayerListContract.Ui,
                            val playersRepo: PlayerRepo) : MainContract.MainUiPresenter {

    private val disposables = CompositeDisposable()

    override fun startPresenting() {
        disposables.add(model(events(playerListUi), playersRepo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { uiModel -> playerListUi.render(uiModel) })
    }

    override fun stopPresenting() {
        disposables.clear()
    }

    override fun screenName(): MainContract.MainScreen {
        return MainContract.MainScreen.PlayersScreen
    }
}