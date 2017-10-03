package com.gregmcgowan.fivesorganiser.core.data.player

import io.reactivex.Single


interface PlayerRepo {

    fun getPlayers(): Single<List<PlayerEntity>>

    fun addPlayer(name: String,
                  email: String,
                  phoneNumber: String,
                  contactId: Int)

}