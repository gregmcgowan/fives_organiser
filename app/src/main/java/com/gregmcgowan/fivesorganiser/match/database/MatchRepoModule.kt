package com.gregmcgowan.fivesorganiser.match.database

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepoModule
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module(includes = [PlayerRepoModule::class])
interface MatchRepoModule {

    @Binds
    @Reusable
    fun matchRepo(matchFirebaseRepo: MatchFirebaseRepo): MatchRepo

    @Binds
    @Reusable
    fun matchSquadRepo(matchSquadFirebaseRepo: MatchSquadFirebaseRepo): MatchSquadRepo
}