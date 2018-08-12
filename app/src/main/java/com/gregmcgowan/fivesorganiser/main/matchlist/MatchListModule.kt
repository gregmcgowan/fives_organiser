package com.gregmcgowan.fivesorganiser.main.matchlist

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule

@Module(
        includes = [
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
abstract class MatchListModule {

    @Binds
    @IntoMap
    @ViewModelKey(MatchListViewModel::class)
    abstract fun bindViewModel(viewModel: MatchListViewModel): ViewModel
}