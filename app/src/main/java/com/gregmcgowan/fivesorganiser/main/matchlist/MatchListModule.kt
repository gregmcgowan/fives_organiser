package com.gregmcgowan.fivesorganiser.main.matchlist

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey

@Module
abstract class MatchListModule {

    @Binds
    @IntoMap
    @ViewModelKey(MatchListViewModel::class)
    abstract fun bindViewModel(viewModel: MatchListViewModel): ViewModel
}