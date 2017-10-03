package com.gregmcgowan.fivesorganiser.core.data

import com.google.firebase.database.*
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

private const val USERS_KEY = "users"

class FirebaseDatabaseHelper(private val authentication: Authentication,
                             private val firebaseDatabase: FirebaseDatabase) {

    private var databaseReference: DatabaseReference

    init {
        firebaseDatabase.setPersistenceEnabled(true)
        databaseReference = firebaseDatabase.reference
    }

    fun getCurrentUserDatabase() : DatabaseReference  {
        return  databaseReference
                .child(USERS_KEY)
                .child(authentication.getUserId())
    }

    fun <T> getSingleValue(databaseReference: DatabaseReference,
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