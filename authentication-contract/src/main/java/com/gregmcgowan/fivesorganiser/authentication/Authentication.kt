package com.gregmcgowan.fivesorganiser.authentication

interface Authentication {

    suspend fun initialise()

    fun isInitialised(): Boolean

    fun getUserId(): String
}