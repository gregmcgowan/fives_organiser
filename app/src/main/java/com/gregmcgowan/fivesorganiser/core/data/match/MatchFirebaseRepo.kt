package com.gregmcgowan.fivesorganiser.core.data.match

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.gregmcgowan.fivesorganiser.core.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.core.data.ID_KEY
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val MATCHES_KEY = "Matches"
private const val DATE_TIME_KEY = "dateTime"
private const val LOCATION_KEY = "location"
private const val NUMBER_OF_PLAYERS_KEY = "numberOfPlayers"
private const val TIMESTAMP_KEY = "timestamp"

class MatchFirebaseRepo(private val firestoreHelper: FirestoreHelper) : MatchRepo {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override suspend fun createMatch(dateTime: ZonedDateTime,
                                     location: String,
                                     numberOfPlayers: Int) {
        val map = mutableMapOf<String, Any>()
        map.put(DATE_TIME_KEY, dateTime.format(dateTimeFormatter))
        map.put(LOCATION_KEY, location)
        map.put(NUMBER_OF_PLAYERS_KEY, numberOfPlayers)
        map.put(TIMESTAMP_KEY, System.currentTimeMillis())

        matches().add(map)
    }

    override suspend fun saveMatch(match: Match) {
        val map = mutableMapOf<String, Any>()
        map.put(DATE_TIME_KEY, match.dateTime.format(dateTimeFormatter))
        map.put(LOCATION_KEY, match.location)
        map.put(NUMBER_OF_PLAYERS_KEY, match.numberOfPlayers)

        firestoreHelper.setData(matches().document(match.matchId), map, SetOptions.merge())
    }

    private fun matches(): CollectionReference {
        return firestoreHelper.getUserDocRef().collection(MATCHES_KEY)
    }

    suspend override fun getMatch(matchID: String): Match {
        val data = firestoreHelper.getData(matches().document(matchID))
        return mapToMatch(data)
    }

    suspend override fun getAllMatches(): List<Match> {
        return firestoreHelper.runQuery(matches().orderBy(TIMESTAMP_KEY))
                .documents
                .mapTo(mutableListOf()) {
                    val data = it.data
                    data.put(ID_KEY, it.id)
                    mapToMatch(data)
                }
    }

    private fun mapToMatch(map: Map<String, Any>): Match {
        return Match(
                matchId = map[ID_KEY] as String,
                location = map[LOCATION_KEY] as String,
                dateTime = ZonedDateTime.parse(map[DATE_TIME_KEY] as String),
                numberOfPlayers = (map[NUMBER_OF_PLAYERS_KEY] as Long).toInt()
        )
    }

}


