package com.gregmcgowan.fivesorganiser.match.details

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
        includes = [
            MatchDetailsModule.Bindings::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
class MatchDetailsModule {

    @Provides
    fun matchId(matchDetailsFragment: MatchDetailsFragment): String? {
        return matchDetailsFragment.matchId
    }

    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchDetailsViewModel::class)
        fun bindViewModel(matchDetailsViewModel: MatchDetailsViewModel): ViewModel

    }


}