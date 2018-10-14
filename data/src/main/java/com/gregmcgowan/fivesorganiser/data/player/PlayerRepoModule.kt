package com.gregmcgowan.fivesorganiser.data.player

import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module()
interface PlayerRepoModule {

    @Binds
    @Reusable
    fun playerRepo(firebasePlayerRepo: PlayersFirebaseRepo): PlayerRepo

}