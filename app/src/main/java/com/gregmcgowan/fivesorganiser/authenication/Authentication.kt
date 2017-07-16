package com.gregmcgowan.fivesorganiser.authenication


interface Authentication {

    fun isInitialised(): Boolean
    fun initialise(onInitAction: () -> Unit)
    fun getUserId(): String?

}