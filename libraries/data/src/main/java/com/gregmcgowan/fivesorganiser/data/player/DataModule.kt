package com.gregmcgowan.fivesorganiser.data.player

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.gregmcgowan.fivesorganiser.data.FivesOrganiserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideFivesDatabase(
        @ApplicationContext context: Context,
    ): FivesOrganiserDatabase =
        FivesOrganiserDatabase(
            driver =
                AndroidSqliteDriver(
                    schema = FivesOrganiserDatabase.Schema,
                    context = context,
                    name = "fives.db",
                ),
        )
}
