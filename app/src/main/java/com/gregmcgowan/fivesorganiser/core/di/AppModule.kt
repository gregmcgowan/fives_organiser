package com.gregmcgowan.fivesorganiser.core.di

import android.content.Context
import android.content.res.Resources
import com.gregmcgowan.fivesorganiser.core.AndroidStrings
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.core.Strings
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [AppModule.Bindings::class])
class AppModule {
    @Provides
    @Singleton
    fun dispatchers(): CoroutineDispatchers = CoroutineDispatchers(Main, IO)

    @Provides
    @Reusable
    fun resources(
        @ApplicationContext application: Context,
    ): Resources = application.resources

    @InstallIn(SingletonComponent::class)
    @Module
    interface Bindings {
        @Binds
        @Reusable
        fun strings(androidStrings: AndroidStrings): Strings
    }
}
