package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.authentication.Authentication
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class FakeAuthentication @Inject constructor() : Authentication {
    override suspend fun initialise() {
        // No-op
    }

    override fun isInitialised(): Boolean = true

    override fun getUserId(): String = "1"

}

class FakePlayerRepo @Inject constructor() : PlayerRepo {

    var players: MutableList<Player> = mutableListOf()

    var exception: RuntimeException? = null

    override suspend fun getPlayers(): List<Player> {
        if (exception != null) {
            throw exception!!
        } else {
            return players
        }
    }

    override suspend fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Long) {
        if (exception != null) {
            throw exception!!
        } else {
            players.add(Player(players.size.toString(), name, phoneNumber, email, contactId))
        }
    }

    override fun playersUpdates(): Flow<DataUpdate<Player>> {
        return emitPlayers()
    }

    fun emitPlayers() =
            listOf(DataUpdate(players.map { DataChange(DataChangeType.Added, it) })).asFlow()
}

