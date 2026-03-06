package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo

class FakePlayerRepo : PlayerRepo {
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
        email: String,
        phoneNumber: String,
        contactId: Long,
    ) {
        if (exception != null) {
            throw exception!!
        } else {
            players.add(Player(players.size.toString(), name, phoneNumber, email, contactId))
        }
    }
}

class FakeContactsImporter : ContactImporter {
    var contacts: List<Contact> = listOf()

    var exception: RuntimeException? = null

    override suspend fun getAllContacts(): List<Contact> {
        if (exception != null) {
            throw exception!!
        } else {
            return contacts
        }
    }
}
