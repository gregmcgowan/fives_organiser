package com.gregmcgowan.fivesorganiser.core.data.match

import com.google.firebase.database.DataSnapshot
import com.gregmcgowan.fivesorganiser.core.data.FirebaseDatabaseHelper
import io.reactivex.Single
import io.reactivex.functions.Function
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val MATCHES_KEY = "Matches"

class MatchFirebaseRepo(private val firebaseDatabaseHelper: FirebaseDatabaseHelper) : MatchRepo {

    private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override fun createMatch(dateTime: ZonedDateTime,
                             location: String) {
        val matchID = getMatchesReference().push().key

        val formattedDateTime = dateTime.format(dateTimeFormatter)

        getMatchesReference()
                .child(matchID)
                .setValue(MatchEntity(matchID, formattedDateTime, location))

    }

    override fun saveMatch(match: Match) {
        getMatchesReference()
                .child(match.matchId)
                .setValue(MatchEntity(match.matchId,
                        match.dateTime.format(dateTimeFormatter),
                        match.location))
    }

    override fun getMatch(matchID: String): Single<Match> {
        return firebaseDatabaseHelper.getSingleValue(getMatchesReference().child(matchID), marshallMatch())
                .map { matchEntity ->
                    map(matchEntity)
                }
    }

    override fun getAllMatches(): Single<List<Match>> {
        return firebaseDatabaseHelper.getSingleValue(getMatchesReference(), marshallMatches())
                .flatMap { matchEntities ->
                    val matches = mutableListOf<Match>()
                    for (matchEntity in matchEntities) {
                        matches.add(map(matchEntity))
                    }
                    Single.just(matches.toList())
                }
    }

    private fun map(matchEntity: MatchEntity) =
            Match(matchEntity.matchId, matchEntity.location,
                    ZonedDateTime.parse(matchEntity.dateTime))

    private fun marshallMatch(): Function<DataSnapshot, MatchEntity> {
        return Function { dataSnapshot ->
            dataSnapshot.getValue<MatchEntity>(MatchEntity::class.java) ?:
                    throw IllegalStateException()
        }
    }

    private fun marshallMatches(): Function<DataSnapshot, List<MatchEntity>> {
        return Function({ dataSnapshot ->
            val players: MutableList<MatchEntity> = mutableListOf()
            dataSnapshot.children.map { it.getValue(MatchEntity::class.java) }
                    .forEach {
                        it?.let {
                            players.add(it)
                        }
                    }
            players.toList()
        })
    }

    private fun getMatchesReference() = firebaseDatabaseHelper.getCurrentUserDatabase().child(MATCHES_KEY)
}