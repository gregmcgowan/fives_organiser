package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class, replaces = [PlayerRepoModule::class])
object FakePlayerRepoModule {
    val fakePlayerRepo: FakePlayerRepo = FakePlayerRepo()

    @Provides
    @SingleIn(AppScope::class)
    fun playerRepo(): PlayerRepo = fakePlayerRepo
}
