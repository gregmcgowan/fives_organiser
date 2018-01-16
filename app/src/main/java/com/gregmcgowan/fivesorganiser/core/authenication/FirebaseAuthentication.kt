package com.gregmcgowan.fivesorganiser.core.authenication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.experimental.withTimeout
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.suspendCoroutine

class FirebaseAuthentication(private val firebaseAuth: FirebaseAuth) : Authentication {

    private val tag = "FirebaseAuthentication"
    private var addOnCompleteListenerTask: Task<AuthResult>? = null

    private lateinit var currentUserId: String
    private lateinit var currentUser: FirebaseUser

    //TODO persist on start up for devices without internet connection
    private val fakeUserId = UUID.randomUUID().toString()

    override fun getUserId(): String {
        return currentUserId
    }

    override fun isInitialised(): Boolean {
        val result = currentUserId != null
        Timber.d(if (result) "Initialised " else "Not Initialised")
        return result
    }


    suspend override fun initialise() {
        withTimeout(10, TimeUnit.SECONDS, {
            handleCompletedResult(signInAnonymously())
        })
    }

    private suspend fun signInAnonymously(): AuthResult {
        return suspendCoroutine { cont ->
            firebaseAuth
                    .signInAnonymously()
                    .addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            cont.resume(task.result)
                        } else {
                            cont.resumeWithException(task.exception as Throwable)
                        }
                    })
                    .addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
        }
    }

    private fun taskCompletedAndFailed(): Boolean {
        addOnCompleteListenerTask?.let {
            return it.isComplete && !it.isSuccessful
        }
        return false
    }

    private fun handleCompletedResult(authResult : AuthResult) {
                firebaseAuth.currentUser?.let {
                    this.currentUser = it
                    this.currentUserId = it.uid
                    //TODO move data stored under fake ID to real Id store
                    Timber.d("Initialise with currentUser $currentUserId")
//                }
//            } else {
//                currentUserId = fakeUserId
//                Timber.d("Initialise with fakeUserID $currentUserId taskException ${task.exception?.message}")
//            }
        }

    }
}




