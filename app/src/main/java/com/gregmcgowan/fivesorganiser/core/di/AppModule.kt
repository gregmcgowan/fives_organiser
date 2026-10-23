package com.gregmcgowan.fivesorganiser.core.di

import android.content.Context
import android.content.res.Resources
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

@ContributesTo(AppScope::class)
@BindingContainer
object AppModule {
    @Provides
    @SingleIn(AppScope::class)
    fun dispatchers(): CoroutineDispatchers = CoroutineDispatchers(Main, IO)

    @Provides
    fun resources(context: Context): Resources = context.resources


}
