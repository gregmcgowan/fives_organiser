package com.gregmcgowan.fivesorganiser.match.details

import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@InstallIn(FragmentComponent::class)
@Module(includes = [MatchRepoModule::class])
class MatchDetailsModule {

}