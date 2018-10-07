package com.gregmcgowan.fivesorganiser.match

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
            ViewModelBuilder::class,
            MatchModule.Bindings::class,
            MatchRepoModule::class
        ]
)
class MatchModule {

    @Provides
    fun matchId(matchActivity: MatchActivity) = matchActivity.matchEvent

    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchActivityViewModel::class)
        abstract fun bindViewModel(viewModel: MatchActivityViewModel): ViewModel

    }

}