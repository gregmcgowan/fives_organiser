package com.gregmcgowan.fivesorganiser.players

import rx.Observable


interface PlayerRepo {

    fun getPlayers(): Observable<List<Player>>

    fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Int)

}