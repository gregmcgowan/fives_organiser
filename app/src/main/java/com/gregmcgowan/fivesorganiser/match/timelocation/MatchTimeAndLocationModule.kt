package com.gregmcgowan.fivesorganiser.match.timelocation

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap


@Module(
        includes = [
            MatchTimeAndLocationModule.Bindings::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
class MatchTimeAndLocationModule {

    @Provides
    fun matchId(matchTimeAndLocationFragment: MatchTimeAndLocationFragment): String? {
        return matchTimeAndLocationFragment.matchId
    }

    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchTimeAndLocationViewModel::class)
        fun bindViewModel(matchTimeAndLocationViewModel: MatchTimeAndLocationViewModel): ViewModel

    }


}