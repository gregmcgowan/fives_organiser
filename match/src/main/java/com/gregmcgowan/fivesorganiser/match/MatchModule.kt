package com.gregmcgowan.fivesorganiser.match

import android.app.Activity
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module(includes = [MatchRepoModule::class])
class MatchModule {

    @Provides
    fun matchId(matchActivity: Activity) = (matchActivity as MatchActivity).matchEvent


}