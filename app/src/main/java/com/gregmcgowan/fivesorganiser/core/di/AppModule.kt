package com.gregmcgowan.fivesorganiser.core.di

import android.content.Context
import android.content.res.Resources
import com.google.firebase.auth.FirebaseAuth
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.AndroidStrings
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.MatchTypesInfo
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.authenication.FirebaseAuthentication
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module(includes = [AppModule.Bindings::class])
class AppModule {

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun disptchers(): CoroutineDisptachersAndContext = CoroutineDisptachersAndContext(Main, IO)

    @Provides
    @Reusable
    fun resources(@ApplicationContext application: Context): Resources = application.resources

    @Provides
    @Reusable
    fun matchTypesInfo(resources: Resources): MatchTypesInfo {
        return MatchTypesInfo(
                resources.getInteger(R.integer.match_type_min_players),
                resources.getInteger(R.integer.match_type_max_players)
        )
    }

    @InstallIn(ApplicationComponent::class)
    @Module
    interface Bindings {

        @Binds
        @Singleton
        fun auth(firebaseAuth: FirebaseAuthentication): Authentication


        @Binds
        @Reusable
        fun strings(androidStrings: AndroidStrings): Strings

    }


}