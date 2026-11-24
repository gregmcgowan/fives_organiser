package com.gregmcgowan.fivesorganiser.data.player

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@BindingContainer
@ContributesTo(AppScope::class)
interface PlayerRepoModule {
    @Binds
    fun playerRepo(impl: PlayersSqliteRepo): PlayerRepo
}
