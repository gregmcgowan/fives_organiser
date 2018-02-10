package com.gregmcgowan.fivesorganiser.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.authenication.FirebaseAuthentication
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import javax.inject.Singleton

@Module(
        includes = [AppModule.Bindings::class]
)
class AppModule {

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun fireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun coroutines(): CoroutineContexts {
        return CoroutineContexts(UI, CommonPool)
    }

    @Module
    interface Bindings {

        @Binds
        @Singleton
        fun auth(firebaseAuth: FirebaseAuthentication): Authentication

    }


}