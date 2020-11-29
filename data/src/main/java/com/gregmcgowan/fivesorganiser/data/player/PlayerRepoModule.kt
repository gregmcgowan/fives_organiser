package com.gregmcgowan.fivesorganiser.data.player

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

//TODO check scope
@InstallIn(ApplicationComponent::class)
@Module
interface PlayerRepoModule {

    @Binds
    @Reusable
    fun playerRepo(firebasePlayerRepo: PlayersFirebaseRepo): PlayerRepo

}