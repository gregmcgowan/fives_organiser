package com.gregmcgowan.fivesorganiser.match

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@InstallIn(ActivityComponent::class)
@Module(
        includes = [
            ViewModelBuilder::class,
            MatchModule.Bindings::class,
            MatchRepoModule::class
        ]
)
class MatchModule {

    @Provides
    fun matchId(matchActivity: Activity) = (matchActivity as MatchActivity).matchEvent

    @InstallIn(ActivityComponent::class)
    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchActivityViewModel::class)
        abstract fun bindViewModel(viewModel: MatchActivityViewModel): ViewModel

    }

}