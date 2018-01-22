package com.gregmcgowan.fivesorganiser.core.data.match

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.gregmcgowan.fivesorganiser.core.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.core.data.ID_KEY
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val MATCHES_KEY = "Matches"
private const val START_DATE_TIME_KEY = "startDateTime"
private const val END_DATE_TIME_KEY = "endDateTime"
private const val LOCATION_KEY = "location"
private const val NUMBER_OF_PLAYERS_KEY = "numberOfPlayers"
private const val TIMESTAMP_KEY = "timestamp"

class MatchFirebaseRepo(private val firestoreHelper: FirestoreHelper) : MatchRepo {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override suspend fun createMatch(startTime: ZonedDateTime,
                                     endTime: ZonedDateTime,
                                     squadSize: Int,
                                     location: String) {
        val map = mutableMapOf<String, Any>()
        map[START_DATE_TIME_KEY] = startTime.format(dateTimeFormatter)
        map[END_DATE_TIME_KEY] = endTime.format(dateTimeFormatter)
        map[LOCATION_KEY] = location
        map[NUMBER_OF_PLAYERS_KEY] = squadSize
        map[TIMESTAMP_KEY] = System.currentTimeMillis()

        matches().add(map)
    }

    override suspend fun saveMatch(match: Match) {
        val map = mutableMapOf<String, Any>()
        map[START_DATE_TIME_KEY] = match.start.format(dateTimeFormatter)
        map[END_DATE_TIME_KEY] = match.end.format(dateTimeFormatter)
        map[LOCATION_KEY] = match.location
        map[NUMBER_OF_PLAYERS_KEY] = match.squadSize

        firestoreHelper.setData(matches().document(match.matchId), map, SetOptions.merge())
    }

    private fun matches(): CollectionReference {
        return firestoreHelper.getUserDocRef().collection(MATCHES_KEY)
    }

    override suspend fun getMatch(matchID: String): Match {
        val data = firestoreHelper.getData(matches().document(matchID))
        return mapToMatch(data)
    }

    override suspend fun getAllMatches(): List<Match> {
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
                start = ZonedDateTime.parse(map[START_DATE_TIME_KEY] as String),
                end = ZonedDateTime.parse(map[END_DATE_TIME_KEY] as String),
                squadSize = (map[NUMBER_OF_PLAYERS_KEY] as Long).toInt()
        )
    }

}


