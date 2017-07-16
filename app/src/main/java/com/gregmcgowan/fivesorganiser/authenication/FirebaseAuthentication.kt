package com.gregmcgowan.fivesorganiser.authenication

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*


class FirebaseAuthentication(val firebaseAuth: FirebaseAuth) : Authentication {

    val tag = "FirebaseAuthentication"
    var addOnCompleteListenerTask: Task<AuthResult>? = null
    var currentUserId: String? = null
    var currentUser: FirebaseUser? = null
    //TODO persist on start up for devices without internet connection
    val fakeUserId = UUID.randomUUID().toString()

    override fun getUserId(): String? {
        return currentUserId
    }

    override fun isInitialised(): Boolean {
        val result = currentUserId != null
        Log.d(tag, if (result) "Initialised " else "Not Initialised")
        return result
    }

    override fun initialise(onInitAction: () -> Unit) {
        if (addOnCompleteListenerTask == null
                || taskCompletedAndFailed()) {
            addOnCompleteListenerTask = firebaseAuth
                    .signInAnonymously()
                    .addOnCompleteListener {
                        task ->
                        handleCompletedResult(task)
                        onInitAction()
                        addOnCompleteListenerTask = null
                    }
        } else {
            addOnCompleteListenerTask?.let {
                if(it.isComplete) {
                    handleCompletedResult(it)
                }
            }
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




