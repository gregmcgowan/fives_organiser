package com.gregmcgowan.fivesorganiser.data.player

import com.gregmcgowan.fivesorganiser.data.DataUpdate
import kotlinx.coroutines.flow.Flow

interface PlayerRepo {

    suspend fun getPlayers(): List<Player>

    suspend fun addPlayer(name: String,
                          email: String,
                          phoneNumber: String,
                          contactId: Long)

    fun playersUpdates(): Flow<DataUpdate<Player>>
}

