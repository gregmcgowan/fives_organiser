package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

//TODO check scope
@InstallIn(ApplicationComponent::class)
@Module(includes = [PlayerRepoModule::class])
interface MatchRepoModule {

    @Binds
    @Reusable
    fun matchRepo(matchFirebaseRepo: MatchFirebaseRepo): MatchRepo

    @Binds
    @Reusable
    fun matchSquadRepo(matchSquadFirebaseRepo: MatchSquadFirebaseRepo): MatchSquadRepo
}