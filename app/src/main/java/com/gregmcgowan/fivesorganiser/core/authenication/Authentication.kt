package com.gregmcgowan.fivesorganiser.core.authenication

interface Authentication {

    fun isInitialised(): Boolean
    suspend fun initialise()
    fun getUserId(): String
}