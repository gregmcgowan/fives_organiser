package com.gregmcgowan.fivesorganiser.playerList

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.main.MainContract
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiEvent
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiEvent.AddPlayerEvent
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiEvent.ViewShownEvent
import com.gregmcgowan.fivesorganiser.playerList.PlayerListContract.PlayerListUiModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PlayerListUiPresenter(val playerListUi: PlayerListContract.Ui,
                            val playersRepo: PlayerRepo) : MainContract.MainUiPresenter {

    private val disposables = CompositeDisposable()

    override fun startPresenting() {
        val initialUiModel = PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = false,
                showPlayers = false,
                errorMessage = null,
                showAddPlayers = false
        )

        val events = Observable.merge(
                listOf(Observable.just(ViewShownEvent()), playerListUi.addPlayerButtonEvents()))
                .doAfterNext { event ->
                    Timber.d("View sending event [${event.javaClass.name}]")
                }
                .share()

        disposables.add(process(initialUiModel, events, playersRepo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { uiModel -> playerListUi.render(uiModel) })
    }

    override fun stopPresenting() {
        disposables.clear()
    }

    override fun screenName(): MainContract.MainScreen {
        return MainContract.MainScreen.PlayersScreen
    }

    private fun process(initialUiModel: PlayerListUiModel,
                        events: Observable<PlayerListUiEvent>,
                        playersRepo: PlayerRepo): Observable<PlayerListUiModel> {
        return events
                .flatMap { event ->
                    when (event) {
                        is AddPlayerEvent -> {
                            Observable.just(addPlayersModelReducer())
                        }
                        is PlayerListContract.PlayerListUiEvent.ViewShownEvent -> {
                            playersRepo.getPlayers()
                                    .toObservable()
                                    .subscribeOn(Schedulers.io())
                                    .map { playersLoadedModelReducer(it) }
                                    .onErrorResumeNext { t: Throwable ->
                                        Timber.e("error loading players ${t.message}", t)
                                        Observable.just(playersLoadErrorModelReducer())
                                    }

                        }
                    }
                }
                .scan(initialUiModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering  $model") })

    }
}