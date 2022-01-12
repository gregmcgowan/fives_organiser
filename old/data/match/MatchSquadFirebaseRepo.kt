package com.gregmcgowan.fivesorganiser.data.match

import com.google.firebase.firestore.CollectionReference
import com.gregmcgowan.fivesorganiser.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.data.ID_KEY
import javax.inject.Inject

private const val MATCHES_SQUAD_KEY = "MatchSquad"
private const val INVITED_PLAYER_IDS_KEY = "invitedPlayerIds"
private const val CONFIRMED_PLAYER_IDS_KEY = "confirmedPlayerIds"
private const val UNSURE_PLAYER_IDS_KEY = "unsurePlayerIds"
private const val DECLINED_PLAYER_IDS_KEY = "declinedPlayerIds"

class MatchSquadFirebaseRepo @Inject constructor(
        private val firestoreHelper: FirestoreHelper
) : MatchSquadRepo {

    override suspend fun createMatchSquad(matchId: String,
                                          invited: List<String>,
                                          confirmed: List<String>,
                                          unsure: List<String>,
                                          declined: List<String>): MatchSquadEntity {
        val map = mutableMapOf<String, Any>()
        map[INVITED_PLAYER_IDS_KEY] = invited
        map[CONFIRMED_PLAYER_IDS_KEY] = confirmed
        map[UNSURE_PLAYER_IDS_KEY] = unsure
        map[DECLINED_PLAYER_IDS_KEY] = declined

        firestoreHelper.setData(matchSquad(), matchId, map)
        val newMap = map.toMutableMap()
        newMap[ID_KEY] = matchId

        return mapToSquadEntity(newMap)
    }

    override suspend fun saveMatchSquad(matchSquad: MatchSquadEntity) {
        val map = mutableMapOf<String, Any>()
        map[INVITED_PLAYER_IDS_KEY] = matchSquad.invited
        map[CONFIRMED_PLAYER_IDS_KEY] = matchSquad.confirmed
        map[UNSURE_PLAYER_IDS_KEY] = matchSquad.unsure
        map[DECLINED_PLAYER_IDS_KEY] = matchSquad.declined

        firestoreHelper.setData(matchSquad(), matchSquad.matchId, map)
    }

    override suspend fun getMatchSquad(matchId: String): MatchSquadEntity {
        val data = firestoreHelper.getData(matchSquad().document(matchId))
        return mapToSquadEntity(data)
    }

    private fun matchSquad(): CollectionReference {
        return firestoreHelper.getUserDocRef().collection(MATCHES_SQUAD_KEY)
    }

    private fun mapToSquadEntity(map: Map<String, Any>): MatchSquadEntity {
        return MatchSquadEntity(
                matchId = map[ID_KEY] as String,
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