package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.di.AppGraph
import dev.zacsweers.metro.createDynamicGraphFactory

class TestFivesOrganiserApp : FivesOrganiserApp() {
    override fun createGraph(): AppGraph =
        createDynamicGraphFactory<AppGraph.Factory>(
            FakePlayerRepoModule,
        ).create(context = this, app = this)
}
