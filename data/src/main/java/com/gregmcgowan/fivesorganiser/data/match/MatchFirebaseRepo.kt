package com.gregmcgowan.fivesorganiser.data.match

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.data.ID_KEY
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

private const val MATCHES_KEY = "Matches"
private const val START_DATE_TIME_KEY = "startDateTime"
private const val END_DATE_TIME_KEY = "endDateTime"
private const val LOCATION_KEY = "location"
private const val NUMBER_OF_PLAYERS_KEY = "numberOfPlayers"
private const val TIMESTAMP_KEY = "timestamp"
private const val INVITED_PLAYER_IDS_KEY = "invitedPlayerIds"
private const val CONFIRMED_PLAYER_IDS_KEY = "confirmedPlayerIds"
private const val UNSURE_PLAYER_IDS_KEY = "unsurePlayerIds"
private const val DECLINED_PLAYER_IDS_KEY = "declinedPlayerIds"

class MatchFirebaseRepo @Inject constructor(
        private val firestoreHelper: FirestoreHelper
) : MatchRepo {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override suspend fun createMatch(startTime: ZonedDateTime,
                                     endTime: ZonedDateTime,
                                     squadSize: Long,
                                     location: String): MatchEntity {
        val map = mutableMapOf<String, Any>()
        map[START_DATE_TIME_KEY] = startTime.format(dateTimeFormatter)
        map[END_DATE_TIME_KEY] = endTime.format(dateTimeFormatter)
        map[LOCATION_KEY] = location
        map[NUMBER_OF_PLAYERS_KEY] = squadSize
        map[TIMESTAMP_KEY] = System.currentTimeMillis()

        val id = firestoreHelper.addData(matches(), map)
        map[ID_KEY] = id

        return mapToMatch(map)
    }

    override suspend fun saveMatch(match: MatchEntity) {
        val map = mapOf(
                START_DATE_TIME_KEY to match.startDateTime,
                END_DATE_TIME_KEY to match.endDateTime,
                LOCATION_KEY to match.location,
                NUMBER_OF_PLAYERS_KEY to match.numberOfPlayers,
                INVITED_PLAYER_IDS_KEY to match.invited,
                CONFIRMED_PLAYER_IDS_KEY to match.confirmed,
                UNSURE_PLAYER_IDS_KEY to match.unsure,
                DECLINED_PLAYER_IDS_KEY to match.declined
        )
        firestoreHelper.setData(matches().document(match.matchId), map, SetOptions.merge())
    }

    override fun getMatchUpdates(): LiveData<Either<Exception, DataUpdate<MatchEntity>>> {
        return firestoreHelper.changesForCollection(matches(), ::mapToMatch)
    }

    private fun matches(): CollectionReference =
            firestoreHelper.getUserDocRef().collection(MATCHES_KEY)

    override suspend fun getMatch(matchID: String): MatchEntity =
            mapToMatch(firestoreHelper.getData(matches().document(matchID)))

    override suspend fun getAllMatches(): List<MatchEntity> {
        return firestoreHelper.runQuery(matches().orderBy(TIMESTAMP_KEY))
                .documents
                .mapNotNullTo(mutableListOf()) { snapShot ->
                    snapShot.data?.let {
                        it[ID_KEY] = snapShot.id
                        mapToMatch(it)
                    }
                }
    }

    private fun mapToMatch(map: Map<String, Any>): MatchEntity {
        return MatchEntity(
                matchId = map[ID_KEY] as String,
                location = map[LOCATION_KEY] as String,
                startDateTime = map[START_DATE_TIME_KEY] as String,
                endDateTime = map[END_DATE_TIME_KEY] as String,
                numberOfPlayers = (map[NUMBER_OF_PLAYERS_KEY] as Long),
                invited = getList(map, INVITED_PLAYER_IDS_KEY),
                confirmed = getList(map, CONFIRMED_PLAYER_IDS_KEY),
                unsure = getList(map, UNSURE_PLAYER_IDS_KEY),
                declined = getList(map, DECLINED_PLAYER_IDS_KEY)

        )
    }

    private fun getList(map: Map<String, Any>, key: String): List<String> {
        if (map.containsKey(key)) {
            return map[key] as List<String>
        }
        return emptyList()
    }


}


