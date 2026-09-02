package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

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

    override suspend fun addPlayer(
        name: String,
        email: String?,
        phoneNumber: String?,
        contactId: Long?,
    ) {
        if (exception != null) {
            throw exception!!
        } else {
            players.add(Player(players.size.toString(), name, phoneNumber, email, contactId))
        }
    }
}
