package com.gregmcgowan.fivesorganiser.data.player

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.gregmcgowan.fivesorganiser.data.FivesOrganiserDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DataModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideFivesDatabase(context: Context): FivesOrganiserDatabase =
        FivesOrganiserDatabase(
            driver =
                AndroidSqliteDriver(
                    schema = FivesOrganiserDatabase.Schema,
                    context = context,
                    name = "fives.db",
                ),
        )
}
