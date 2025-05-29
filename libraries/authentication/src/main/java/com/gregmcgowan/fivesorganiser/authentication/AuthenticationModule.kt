package com.gregmcgowan.fivesorganiser.authentication

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthenticationModule {
    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@InstallIn(SingletonComponent::class)
@Module
interface AuthenticationBindings {
    @Binds
    @Singleton
    fun bind(impl: FirebaseAuthentication): Authentication
}
