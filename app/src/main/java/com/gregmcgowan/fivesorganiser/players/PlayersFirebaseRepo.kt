package com.gregmcgowan.fivesorganiser.players

import com.google.firebase.database.*
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import rx.Single
import rx.functions.Func1
import java.util.*
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

    private fun getPlayersReference() = getCurrentUserDatabase()
            .child(PLAYERS_KEY)

    private fun getCurrentUserDatabase() =
            databaseReference
                    .child(USERS_KEY)
                    .child(authentication.getUserId())

    private fun marshallPlayers(): Func1<DataSnapshot, List<Player>> {
        return Func1 { dataSnapshot ->
            val children = if (dataSnapshot != null) {
                dataSnapshot.children
            } else ArrayList<DataSnapshot>()

            val players = mutableListOf<Player>()
            children.map { it.getValue(Player::class.java) }
                    .forEach {
                        it?.let {
                            players.add(it)
                        }
                    }
            players.toList()
        }
    }

    override fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Int) {
        val playerId = getPlayersReference().push().key

        getPlayersReference()
                .child(playerId)
                .setValue(Player(playerId, name, phoneNumber, email, contactId))
    }

    //TODO move to common database class
    private fun <T> getSingleValue(databaseReference: DatabaseReference,
                                   dataMarshaller: Func1<DataSnapshot, T>): Single<T> {
        return Single.fromEmitter<T>({ emitter ->
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(databaseError: DatabaseError?) {
                    emitter.onError(databaseError?.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    emitter.onSuccess(dataMarshaller.call(dataSnapshot))
                }
            })
        }).timeout(10, TimeUnit.SECONDS)
    }

}