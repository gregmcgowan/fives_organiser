package com.gregmcgowan.fivesorganiser.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreHelper @Inject constructor(private val authentication: Authentication,
                                          private val firebaseFirestore: FirebaseFirestore) {
    fun getUserDocRef(): DocumentReference {
        return firebaseFirestore
                .collection(authentication.getUserId())
                .document("User ${authentication.getUserId()}")
    }


    fun <T> flowOfDataUpdates(collectionReference: CollectionReference,
                              map: (Map<String, Any>) -> T): Flow<DataUpdate<T>> = callbackFlow {
        val subscription = collectionReference.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) { return@addSnapshotListener }
            // Sends events to the flow! Consumers will get the new events
            try {
                this.trySend(getDataChangeList(snapshot, map)).isSuccess
            } catch (e: Throwable) {
                // Event couldn't be sent to the flow
                // TODO log
            }
        }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from Firestore
        awaitClose { subscription.remove() }
    }

    private fun <T> getDataChangeList(querySnapshot: QuerySnapshot,
                                      map: (Map<String, Any>) -> T): DataUpdate<T> {
        val listOfChanges = querySnapshot.documentChanges.mapNotNull { changes ->
            changes?.let { documentChange ->
                val data = documentChange.document.data
                data[ID_KEY] = documentChange.document.id
                DataChange(mapToDataChange(documentChange.type), map(data))
            }
        }
        return DataUpdate(listOfChanges)
    }

    private fun mapToDataChange(documentChange: DocumentChange.Type): DataChangeType {
        return when (documentChange) {
            DocumentChange.Type.ADDED -> DataChangeType.Added
            DocumentChange.Type.MODIFIED -> DataChangeType.Modified
            DocumentChange.Type.REMOVED -> DataChangeType.Removed
        }
    }

    suspend fun getData(
            docRef: DocumentReference
    ): Map<String, Any> = suspendCoroutine { cont ->
        docRef.get()
                .addOnCompleteListener { docSnapshot ->
                    val data = docSnapshot.result?.data
                    if (data != null) {
                        data[ID_KEY] = docSnapshot.result?.id
                        cont.resume(data)
                    } else {
                        cont.resumeWithException(
                                Exception("Document reference ${docRef.path} " +
                                        "does not exist"))
                    }
                }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    suspend fun setData(collectionReference: CollectionReference,
                        documentId: String,
                        data: Map<String, Any>): Unit = suspendCoroutine { cont ->
        collectionReference
                .document(documentId)
                .set(data)
                .addOnSuccessListener { cont.resume(Unit) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    suspend fun addData(collectionReference: CollectionReference,
                        data: Map<String, Any>): String = suspendCoroutine { cont ->
        collectionReference
                .add(data)
                .addOnSuccessListener { docReference -> cont.resume(docReference.id) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    suspend fun setData(documentReference: DocumentReference,
                        data: Map<String, Any>,
                        setOptions: SetOptions? = null): Unit = suspendCoroutine { cont ->
        val setRef: Task<Void> = if (setOptions != null) {
            documentReference.set(data, setOptions)
        } else {
            documentReference.set(data)
        }

        setRef
                .addOnCompleteListener { cont.resume(Unit) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    suspend fun runQuery(query: Query): QuerySnapshot = suspendCoroutine { cont ->
        query.get()
                .addOnSuccessListener { querySnapshot -> cont.resume(querySnapshot) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }
}