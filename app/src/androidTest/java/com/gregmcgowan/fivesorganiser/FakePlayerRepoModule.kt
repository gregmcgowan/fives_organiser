package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
object FakePlayerRepoModule {
    val fakePlayerRepo: FakePlayerRepo = FakePlayerRepo()

    @Provides
    @SingleIn(AppScope::class)
    fun playerRepo(): PlayerRepo = fakePlayerRepo
}
