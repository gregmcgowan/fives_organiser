package com.gregmcgowan.fivesorganiser.match.squad

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.IntoMap
import javax.inject.Named

const val MATCH_SQUAD_ID: String = "MATCH_SQUAD_ID"

@InstallIn(FragmentComponent::class)
@Module(
        includes = [
            MatchSquadModule.Bindings::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ],
        subcomponents = [MatchSquadListFactory::class]

)
class MatchSquadModule {

    @Provides
    fun matchDetailsFragment(matchSquadFragment: Fragment): MatchSquadFragment
            = matchSquadFragment as MatchSquadFragment

    @Provides
    @Named(MATCH_SQUAD_ID)
    fun matchId(matchSquadFragment: MatchSquadFragment): String
            = matchSquadFragment.matchId

    @InstallIn(FragmentComponent::class)
    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchSquadViewModel::class)
        fun bindViewModel(viewModel: MatchSquadViewModel): ViewModel


        @Binds
        fun interactions(impl: MatchSquadFragment): MatchSquadListInteractions

    }

}