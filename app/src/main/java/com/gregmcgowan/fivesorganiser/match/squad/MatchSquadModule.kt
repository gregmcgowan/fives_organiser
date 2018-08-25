package com.gregmcgowan.fivesorganiser.match.squad

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepoModule
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
        includes = [
            MatchSquadModule.Bindings::class,
            MatchRepoModule::class,
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ],
        subcomponents = [MatchSquadListFactory::class]

)
class MatchSquadModule {

    @Provides
    fun matchId(matchSquadFragment: MatchSquadFragment): String {
        return matchSquadFragment.matchId
    }

    @Module
    interface Bindings {
        @Binds
        @IntoMap
        @ViewModelKey(MatchSquadViewModel::class)
        fun bindViewModel(viewModel: MatchSquadViewModel): ViewModel


        @Binds
        fun interactions(impl :MatchSquadFragment) : MatchSquadListInteractions

    }

}