package com.gregmcgowan.fivesorganiser.core.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import javax.inject.Inject
import kotlin.coroutines.experimental.suspendCoroutine


class FirestoreHelper @Inject constructor(private val authentication: Authentication,
                                          private val firebaseFirestore: FirebaseFirestore) {


    fun getUserDocRef(): DocumentReference {
        return firebaseFirestore
                .collection(authentication.getUserId())
                .document("User ${authentication.getUserId()}")
    }

    suspend fun getData(
            documentReference: DocumentReference
    ): Map<String, Any> = suspendCoroutine { cont ->
        documentReference.get()
                .addOnCompleteListener { docSnapshot ->
                    if (docSnapshot.result.exists()) {
                        val data = docSnapshot.result.data
                        data[ID_KEY] = docSnapshot.result.id
                        cont.resume(data)
                    } else {
                        cont.resumeWithException(
                                Exception("Document reference ${documentReference.path} " +
                                        "does not exist"))
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
    }

    suspend fun setData(collectionReference: CollectionReference,
                        documentId: String,
                        data: Map<String, Any>): Unit = suspendCoroutine { cont ->
        collectionReference
                .document(documentId)
                .set(data)
                .addOnSuccessListener { _ ->
                    cont.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
    }

    suspend fun addData(collectionReference: CollectionReference,
                        data: Map<String, Any>): String = suspendCoroutine { cont ->
        collectionReference
                .add(data)
                .addOnSuccessListener { docReference ->
                    cont.resume(docReference.id)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
    }

    suspend fun setData(documentReference: DocumentReference,
                        data: Map<String, Any>,
                        setOptions: SetOptions? = null): Unit = suspendCoroutine { cont ->
        val setRef: Task<Void>
        if (setOptions != null) {
            setRef = documentReference.set(data, setOptions)
        } else {
            setRef = documentReference.set(data)
        }

        setRef
                .addOnCompleteListener {
                    cont.resume(Unit)
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