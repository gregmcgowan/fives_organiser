package com.gregmcgowan.fivesorganiser.matchlist

import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
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