package com.gregmcgowan.fivesorganiser.data.match

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
interface MatchRepoModule {

    @Binds
    fun matchRepo(matchFirebaseRepo: MatchFirebaseRepo): MatchRepo

    @Binds
    fun matchSquadRepo(matchSquadFirebaseRepo: MatchSquadFirebaseRepo): MatchSquadRepo
}