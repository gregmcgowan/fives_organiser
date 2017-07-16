package com.gregmcgowan.fivesorganiser.players

import rx.Observable
import rx.Single


interface PlayerRepo {

    fun getPlayers(): Single<List<Player>>

    fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Int)

}