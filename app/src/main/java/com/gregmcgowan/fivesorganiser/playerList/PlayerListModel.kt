package com.gregmcgowan.fivesorganiser.playerList

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerEntity
import com.gregmcgowan.fivesorganiser.playerList.PlayerListUiEvent.AddPlayerEvent
import com.gregmcgowan.fivesorganiser.playerList.PlayerListUiEvent.ViewShownEvent
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

typealias PlayerListUiModelReducer = (PlayerListUiModel) -> PlayerListUiModel

data class PlayerListUiModel(val players: List<PlayerEntity>,
                             val showPlayers: Boolean,
                             val showLoading: Boolean,
                             val showErrorMessage: Boolean,
                             val errorMessage: String?,
                             val showAddPlayers: Boolean = false)

sealed class PlayerListUiEvent {
    class AddPlayerEvent : PlayerListUiEvent()
    class ViewShownEvent : PlayerListUiEvent()
}

fun events(playerListUi: PlayerListContract.Ui): Observable<PlayerListUiEvent> {
    return Observable.merge(
            listOf(just(ViewShownEvent()), playerListUi.addPlayerButtonEvents()))
            .doAfterNext { event ->
                Timber.d("View sending event [${event.javaClass.name}]")
            }
            .share()
}


fun model(events: Observable<PlayerListUiEvent>,
          playersRepo: PlayerRepo): Observable<PlayerListUiModel> {

    val viewShown = events
            .ofType(ViewShownEvent::class.java)
            .flatMapSingle {
                playersRepo.getPlayers()
                        .subscribeOn(Schedulers.io())
            }
            .map { playersLoadedModelReducer(it) }
            .onErrorResumeNext { t: Throwable ->
                Timber.e("error loading players ${t.message}", t)
                Observable.just(playersLoadErrorModelReducer())
            }

    val addPlayer = events
            .ofType(AddPlayerEvent::class.java)
            .map { addPlayersModelReducer() }

    val initialUiModel = PlayerListUiModel(
            players = emptyList(),
            showLoading = true,
            showErrorMessage = false,
            showPlayers = false,
            errorMessage = null,
            showAddPlayers = false
    )

    return Observable.merge(listOf(viewShown, addPlayer))
            .scan(initialUiModel, { previousUiModel, reducer -> reducer(previousUiModel) })
            .distinctUntilChanged()
            .doAfterNext({ model -> Timber.d("Rendering  $model") })

}

fun addPlayersModelReducer(): PlayerListUiModelReducer = { playerListUiModel ->
    playerListUiModel.copy(
            showAddPlayers = true
    )
}

fun playersLoadedModelReducer(players: List<PlayerEntity>): PlayerListUiModelReducer = { playerListUiModel ->
    if (players.isNotEmpty()) {
        playerListUiModel.copy(
                players = players,
                showLoading = false,
                showPlayers = true,
                showErrorMessage = false,
                errorMessage = null)
    } else {
        PlayerListUiModel(
                emptyList(),
                showLoading = false,
                showPlayers = false,
                showErrorMessage = true,
                errorMessage = "No players, press + to add")
    }
}

fun playersLoadErrorModelReducer(): PlayerListUiModelReducer = { _ ->
    PlayerListUiModel(
            emptyList(),
            showLoading = false,
            showPlayers = false,
            showErrorMessage = true,
            errorMessage = "Oops, Something went wrong")
}