package com.gregmcgowan.fivesorganiser.core.authenication

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import java.util.*

class FirebaseAuthentication(private val firebaseAuth: FirebaseAuth) : Authentication {

    private val tag = "FirebaseAuthentication"
    private var addOnCompleteListenerTask: Task<AuthResult>? = null
    private var currentUserId: String? = null
    private var currentUser: FirebaseUser? = null
    //TODO persist on start up for devices without internet connection
    private val fakeUserId = UUID.randomUUID().toString()

    override fun getUserId(): String? {
        return currentUserId
    }

    override fun isInitialised(): Boolean {
        val result = currentUserId != null
        Log.d(tag, if (result) "Initialised " else "Not Initialised")
        return result
    }

    override fun initialise(): Completable {
        if (addOnCompleteListenerTask == null || taskCompletedAndFailed()) {
            return Completable.create({ e: CompletableEmitter ->
                addOnCompleteListenerTask = firebaseAuth
                        .signInAnonymously()
                        .addOnCompleteListener({ task ->
                            handleCompletedResult(task)
                            addOnCompleteListenerTask = null
                            e.onComplete()
                        })
                        .addOnFailureListener { e.onError(Exception()) }
            })
        } else {
            addOnCompleteListenerTask?.let {
                if (it.isComplete) {
                    handleCompletedResult(it)
                }
            }
            return Completable.complete()
        }
    }


    private fun taskCompletedAndFailed(): Boolean {
        addOnCompleteListenerTask?.let {
            return it.isComplete && !it.isSuccessful
        }
        return false
    }

    private fun handleCompletedResult(task: Task<AuthResult>?) {
        task?.let {
            if (task.isSuccessful) {
                this.currentUser = firebaseAuth.currentUser
                this.currentUserId = currentUser?.uid
                //TODO move data stored under fake ID to real Id store
                Log.d(tag, "Initialise with currentUser $currentUserId")
            } else {
                currentUserId = fakeUserId
                Log.d(tag, "Initialise with fakeUserID $currentUserId taskException " +
                        "${task.exception?.message}")
            }
        }

    }
}




