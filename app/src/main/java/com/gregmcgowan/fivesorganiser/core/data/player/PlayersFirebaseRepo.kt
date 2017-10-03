package com.gregmcgowan.fivesorganiser.core.data.player

import com.google.firebase.database.*
import com.gregmcgowan.fivesorganiser.core.data.FirebaseDatabaseHelper
import io.reactivex.Single
import io.reactivex.functions.Function

class PlayersFirebaseRepo(private val firebaseDatabaseHelper: FirebaseDatabaseHelper) : PlayerRepo {
    private val PLAYERS_KEY = "Players"


    override fun getPlayers(): Single<List<PlayerEntity>>
            = firebaseDatabaseHelper.getSingleValue(getPlayersReference(), marshallPlayers())

    override fun addPlayer(name: String, email: String, phoneNumber: String, contactId: Int) {
        val playerId = getPlayersReference().push().key

        getPlayersReference()
                .child(playerId)
                .setValue(PlayerEntity(playerId, name, phoneNumber, email, contactId))
    }

    private fun getPlayersReference() = firebaseDatabaseHelper.getCurrentUserDatabase()
            .child(PLAYERS_KEY)

    private fun marshallPlayers(): Function<DataSnapshot, List<PlayerEntity>> {
        return Function({ dataSnapshot ->
            val players: MutableList<PlayerEntity> = mutableListOf()
            dataSnapshot.children.map { it.getValue(PlayerEntity::class.java) }
                    .forEach {
                        it?.let {
                            players.add(it)
                        }
                    }
            players.toList()
        })
    }


}