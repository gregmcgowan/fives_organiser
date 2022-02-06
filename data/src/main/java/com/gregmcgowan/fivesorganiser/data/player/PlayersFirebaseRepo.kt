package com.gregmcgowan.fivesorganiser.data.player

import com.google.firebase.firestore.CollectionReference
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.data.ID_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val PLAYERS_KEY = "Players"
private const val NAME_KEY = "Name"
private const val EMAIL_KEY = "Email"
private const val PHONE_NUMBER = "PhoneNumber"
private const val CONTACT_ID = "ContactId"
private const val TIMESTAMP_KEY = "timestamp"

class PlayersFirebaseRepo @Inject constructor(
        private val firestoreHelper: FirestoreHelper
) : PlayerRepo {


    override suspend fun playersUpdates(): Flow<DataUpdate<Player>> {
        return firestoreHelper.flowOfDataUpdates(getPlayersRef(), ::mapToPlayer)
    }

    override suspend fun getPlayers(): List<Player> {
        return firestoreHelper
                .runQuery(getPlayersRef().orderBy(NAME_KEY))
                .documents
                .mapNotNullTo(mutableListOf()) { docSnapshot ->
                    docSnapshot.data?.let { data ->
                        data[ID_KEY] = docSnapshot.id
                        mapToPlayer(data)
                    }
                }
    }

    override suspend fun addPlayer(name: String,
                                   email: String,
                                   phoneNumber: String,
                                   contactId: Long) {
        val map = mutableMapOf<String, Any>().apply {
            this[NAME_KEY] = name
            this[EMAIL_KEY] = email
            this[PHONE_NUMBER] = phoneNumber
            this[CONTACT_ID] = contactId
            this[TIMESTAMP_KEY] = System.currentTimeMillis()
        }

        runBlocking { getPlayersRef().add(map) }
    }

    private fun getPlayersRef(): CollectionReference {
        return firestoreHelper.getUserDocRef().collection(PLAYERS_KEY)
    }

    private fun mapToPlayer(map: Map<String, Any>) = Player(
            playerId = map[ID_KEY] as String,
            name = map[NAME_KEY] as String,
            phoneNumber = map[PHONE_NUMBER] as String,
            email = map[EMAIL_KEY] as String,
            contactId = map[CONTACT_ID] as Long
    )
}

