package com.gregmcgowan.fivesorganiser.players

import com.google.firebase.database.*
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import rx.Emitter
import rx.Observable
import rx.functions.Func1
import java.util.*

class PlayersFirebaseRepo(firebaseDatabase: FirebaseDatabase,
                          val authentication: Authentication) : PlayerRepo {
    val PLAYERS_KEY = "Players"
    val USERS_KEY = "users"

    val databaseReference: DatabaseReference = firebaseDatabase.reference

    override fun getPlayers(): Observable<List<Player>> {
        return getSinglValueObserable(getPlayersReference(), marhsallPlayers())
    }

    private fun getPlayersReference() = getCurrentUserDatabase()
            .child(PLAYERS_KEY)

    private fun getCurrentUserDatabase() =
            databaseReference.child(USERS_KEY)
                    .child(authentication.getCurrentUserId())

    private fun marhsallPlayers(): Func1<DataSnapshot, List<Player>> {

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
    private fun <T> getSinglValueObserable(databaseReference: DatabaseReference, dataMarshaller: Func1<DataSnapshot, T>): Observable<T> {
        return Observable.fromEmitter<T>({ emitter ->
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(databaseError: DatabaseError?) {
                    emitter.onError(databaseError?.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    emitter.onNext(dataMarshaller.call(dataSnapshot))
                }
            })
        }, Emitter.BackpressureMode.DROP)
    }

}