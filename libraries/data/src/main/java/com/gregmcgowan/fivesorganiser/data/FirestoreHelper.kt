package com.gregmcgowan.fivesorganiser.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.gregmcgowan.fivesorganiser.authentication.Authentication
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreHelper @Inject constructor(
    private val authentication: Authentication,
    private val firebaseFirestore: FirebaseFirestore,
) {
    fun getUserDocRef(): DocumentReference {
        return firebaseFirestore
            .collection(authentication.getUserId())
            .document("User ${authentication.getUserId()}")
    }

    suspend fun runQuery(query: Query): QuerySnapshot =
        suspendCoroutine { cont ->
            query.get()
                .addOnSuccessListener { querySnapshot -> cont.resume(querySnapshot) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
        }
}
