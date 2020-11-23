package com.gregmcgowan.fivesorganiser.match.squad

import androidx.fragment.app.Fragment
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module(
        includes = [
            MatchSquadModule.Bindings::class,
            MatchRepoModule::class
        ],
        subcomponents = [MatchSquadListFactory::class]

)
class MatchSquadModule {

    @Provides
    fun matchDetailsFragment(matchSquadFragment: Fragment): MatchSquadFragment = matchSquadFragment as MatchSquadFragment


    @InstallIn(FragmentComponent::class)
    @Module
    interface Bindings {

        @Binds
        fun interactions(impl: MatchSquadFragment): MatchSquadListInteractions

    }

}