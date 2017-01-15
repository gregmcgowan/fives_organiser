package com.gregmcgowan.fivesorganiser.authenication


interface Authentication {

    fun addAuthenticationStateListener (listener: AuthenticationStateListener)

    fun removeAuthenticationStateListener(listener : AuthenticationStateListener)

    fun signInAnonymously(listener: Authentication.AuthenticationStateListener)

    fun getCurrentUserId() : String?

    interface AuthenticationStateListener {
        fun authStateChanged()
    }
}