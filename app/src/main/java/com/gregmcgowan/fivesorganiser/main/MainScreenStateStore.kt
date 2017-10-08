package com.gregmcgowan.fivesorganiser.main

import io.reactivex.Observable

class MainScreenStateStore {
    //default to players
    var currentScreen: MainContract.MainScreen = MainContract.MainScreen.PlayersScreen

    fun setCurrentScreen(mainScreen: MainContract.MainScreen): Observable<MainContract.MainScreen> {
        this.currentScreen = mainScreen
        return Observable.just(mainScreen)
    }
}

