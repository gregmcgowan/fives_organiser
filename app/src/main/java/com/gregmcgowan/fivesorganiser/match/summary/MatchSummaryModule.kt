package com.gregmcgowan.fivesorganiser.match.summary

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MatchSummaryModule {

    @Binds
    @IntoMap
    @ViewModelKey(MatchSummaryViewModel::class)
    abstract fun bindViewModel(viewModel: MatchSummaryViewModel): ViewModel

}