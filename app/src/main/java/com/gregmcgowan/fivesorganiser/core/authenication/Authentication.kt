package com.gregmcgowan.fivesorganiser.core.authenication

interface Authentication {

    suspend fun initialise()

    fun isInitialised(): Boolean

    fun getUserId(): String
}