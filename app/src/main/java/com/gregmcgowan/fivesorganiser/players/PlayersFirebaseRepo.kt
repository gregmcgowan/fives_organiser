package com.gregmcgowan.fivesorganiser.players

import com.google.firebase.database.*
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

class PlayersFirebaseRepo(val authentication: Authentication,
                          val firebaseDatabase: FirebaseDatabase) : PlayerRepo {
    val PLAYERS_KEY = "Players"
    val USERS_KEY = "users"

    var databaseReference: DatabaseReference

    init {
        firebaseDatabase.setPersistenceEnabled(true)
        databaseReference = firebaseDatabase.reference
    }

    override fun getPlayers(): Single<List<Player>>
            = getSingleValue(getPlayersReference(), marshallPlayers())

    override fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Int) {
        val playerId = getPlayersReference().push().key

        getPlayersReference()
                .child(playerId)
                .setValue(Player(playerId, name, phoneNumber, email, contactId))
    }

    private fun getPlayersReference() = getCurrentUserDatabase()
            .child(PLAYERS_KEY)

    private fun getCurrentUserDatabase() =
            databaseReference
                    .child(USERS_KEY)
                    .child(authentication.getUserId())

    private fun marshallPlayers(): Function<DataSnapshot, List<Player>> {
        return Function({ dataSnapshot ->
            val players: MutableList<Player> = mutableListOf()
            dataSnapshot.children.map { it.getValue(Player::class.java) }
                    .forEach {
                        it?.let {
                            players.add(it)
                        }
                    }
            players.toList()
        })
    }
    //TODO move to common database class
    private fun <T> getSingleValue(databaseReference: DatabaseReference,
                                   dataMarshaller: Function<DataSnapshot, T>): Single<T> {
        return Single.create({ emitter: SingleEmitter<T> ->
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(databaseError: DatabaseError?) {
                    databaseError?.let {
                        emitter.onError(databaseError.toException())
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.let {
                        emitter.onSuccess(dataMarshaller.apply(it))
                    }
                }
            })
        }).timeout(10, TimeUnit.SECONDS)

    }

}