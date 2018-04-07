package com.gregmcgowan.fivesorganiser.match.summary

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
        includes = [
            MatchSummaryModule.Bindings::class,
            ViewModelBuilder::class
        ]
)
class MatchSummaryModule {

    @Provides
    fun matchId(matchSummaryFragment: MatchSummaryFragment): String? {
        return matchSummaryFragment.matchId
    }

    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchSummaryViewModel::class)
        fun bindViewModel(viewModel: MatchSummaryViewModel): ViewModel

    }


}