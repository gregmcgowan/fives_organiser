package com.gregmcgowan.fivesorganiser.players

import io.reactivex.Single


interface PlayerRepo {

    fun getPlayers(): Single<List<Player>>

    fun addPlayer(name: String,
                  email: String,
                  phoneNumber: String,
                  contactId: Int)

}