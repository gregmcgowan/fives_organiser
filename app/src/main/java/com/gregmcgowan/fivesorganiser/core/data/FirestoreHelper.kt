package com.gregmcgowan.fivesorganiser.core.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import kotlin.coroutines.experimental.suspendCoroutine

class FirestoreHelper(private val authentication: Authentication,
                      private val firebaseFirestore: FirebaseFirestore) {


    fun getUserDocRef(): DocumentReference {
        return firebaseFirestore
                .collection(authentication.getUserId())
                .document("User ${authentication.getUserId()}")
    }

    suspend fun getData(documentReference: DocumentReference): Map<String, Any> = suspendCoroutine { cont ->
        documentReference.get()
                .addOnCompleteListener { docSnapshot ->
                    val data = docSnapshot.result.data
                    data.put(ID_KEY, docSnapshot.result.id)
                    cont.resume(data)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
    }


    suspend fun setData(documentReference: DocumentReference,
                        data: Map<String, Any>,
                        setOptions: SetOptions? = null): Any = suspendCoroutine { cont ->
        val setRef: Task<Void>
        if (setOptions != null) {
            setRef = documentReference.set(data, setOptions)
        } else {
            setRef = documentReference.set(data)
        }

        setRef
                .addOnCompleteListener {
                    cont.resume(1)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
    }

    suspend fun runQuery(query: Query): QuerySnapshot = suspendCoroutine { cont ->
        query.get()
                .addOnSuccessListener { querySnapshot ->
                    cont.resume(querySnapshot)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }

    }
}