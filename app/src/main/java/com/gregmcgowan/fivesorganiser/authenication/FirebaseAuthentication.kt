package com.gregmcgowan.fivesorganiser.authenication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FirebaseAuthentication(val firebaseAuth: FirebaseAuth) : Authentication {

    var currentUser : FirebaseUser? = null
    var listener : Authentication.AuthenticationStateListener? = null

    override fun addAuthenticationStateListener(listener: Authentication.AuthenticationStateListener) {
        this.listener = listener;

    }

    override fun removeAuthenticationStateListener(listener: Authentication.AuthenticationStateListener) {
        this.listener = null;
    }

    override fun getCurrentUserId(): String? {
        return currentUser?.uid
    }

    override fun signInAnonymously(listener: Authentication.AuthenticationStateListener) {
        firebaseAuth.signInAnonymously().addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                this.currentUser = firebaseAuth.currentUser
            }
            listener.authStateChanged()
        }
    }
}




