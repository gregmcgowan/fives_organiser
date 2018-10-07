package com.gregmcgowan.fivesorganiser.data.player

interface PlayerRepo {

    suspend fun getPlayers(): List<Player>

    suspend fun addPlayer(name: String,
                  email: String,
                  phoneNumber: String,
                  contactId: Long)

}