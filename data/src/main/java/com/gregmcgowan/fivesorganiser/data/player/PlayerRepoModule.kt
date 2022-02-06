package com.gregmcgowan.fivesorganiser.data.player

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
interface PlayerRepoModule {

    @Binds
    fun playerRepo(firebasePlayerRepo: PlayersFirebaseRepo): PlayerRepo

}
