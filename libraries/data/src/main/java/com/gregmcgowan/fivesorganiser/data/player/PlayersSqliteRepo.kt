package com.gregmcgowan.fivesorganiser.data.player

import com.gregmcgowan.fivesorganiser.data.FivesOrganiserDatabase
import javax.inject.Inject

class PlayersSqliteRepo @Inject constructor(
    private val database: FivesOrganiserDatabase,
) : PlayerRepo {
    override suspend fun getPlayers(): List<Player> =
        database.playerQueries
            .selectAll()
            .executeAsList()
            .map {
                Player(
                    playerId = it.playerId.toString(),
                    name = it.name,
                    phoneNumber = it.phoneNumber,
                    email = it.email,
                    contactId = it.contactId,
                )
            }

    override suspend fun addPlayer(
        name: String,
        email: String?,
        phoneNumber: String?,
        contactId: Long?,
    ) {
        database.playerQueries.insert(
            contactId = contactId,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
        )
    }
}
