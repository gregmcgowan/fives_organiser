package com.gregmcgowan.fivesorganiser.di

import android.app.Application
import android.content.Context
import com.gregmcgowan.fivesorganiser.core.AndroidStrings
import com.gregmcgowan.fivesorganiser.core.Strings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    @Binds
    val AndroidStrings.bind: Strings

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides context: Context,
            @Provides app: Application,
        ): AppGraph
    }
}
