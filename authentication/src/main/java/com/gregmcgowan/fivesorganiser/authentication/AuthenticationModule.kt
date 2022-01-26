package com.gregmcgowan.fivesorganiser.authentication

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [AuthenticationModule.Bindings::class])
class AuthenticationModule {

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @InstallIn(SingletonComponent::class)
    @Module
    interface Bindings {

        @Binds
        @Singleton
        fun auth(firebaseAuth: FirebaseAuthentication): Authentication

    }
}