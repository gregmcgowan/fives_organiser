package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
        includes = [
            ViewModelBuilder::class
        ]
)
abstract class MatchModule {

    @Binds
    @IntoMap
    @ViewModelKey(MatchActivityViewModel::class)
    abstract fun bindViewModel(viewModel: MatchActivityViewModel): ViewModel


}