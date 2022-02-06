package com.gregmcgowan.fivesorganiser.authentication

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TIMEOUT = 10L

class FirebaseAuthentication @Inject constructor(
        private val firebaseAuth: FirebaseAuth
) : Authentication {

    private var currentUserId: String? = null
    private lateinit var currentUser: FirebaseUser

    override fun getUserId(): String {
        return currentUserId!!
    }

    override fun isInitialised(): Boolean {
        val result = currentUserId != null
        Timber.d(if (result) "Initialised " else "Not Initialised")
        return result
    }

    override suspend fun initialise() {
        withTimeout(TimeUnit.SECONDS.toMillis(TIMEOUT)) {
            handleCompletedResult(signInAnonymously())
        }
    }

    private suspend fun signInAnonymously(): AuthResult {
        return suspendCoroutine { cont ->
            firebaseAuth
                    .signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(task.result!!)
                        } else {
                            cont.resumeWithException(task.exception as Throwable)
                        }
                    }
                    .addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
        }
    }

    private fun handleCompletedResult(authResult: AuthResult) {
        authResult.user?.let {
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




