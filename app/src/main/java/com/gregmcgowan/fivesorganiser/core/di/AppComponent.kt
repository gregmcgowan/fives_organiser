package com.gregmcgowan.fivesorganiser.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gregmcgowan.fivesorganiser.FivesOrganiserApp
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.MatchTypesInfo
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
        modules = [AppModule::class]
)
@Singleton
interface AppComponent {

    fun inject(app: FivesOrganiserApp)

    fun coroutineContexts(): CoroutineContexts

    fun firebaseAuth(): FirebaseAuth

    fun fireStore(): FirebaseFirestore

    fun authentication(): Authentication

    fun strings(): Strings

    fun matchTypesInfo(): MatchTypesInfo

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: FivesOrganiserApp): Builder

        fun build(): AppComponent
    }


}