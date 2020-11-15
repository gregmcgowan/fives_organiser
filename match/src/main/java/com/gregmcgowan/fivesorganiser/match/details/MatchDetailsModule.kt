package com.gregmcgowan.fivesorganiser.match.details

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.match.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.IntoMap
import javax.inject.Named

const val MATCH_DETAILS_ID = "MATCH_DETAILS_ID"

@InstallIn(FragmentComponent::class)
@Module(
        includes = [
            MatchDetailsModule.Bindings::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
class MatchDetailsModule {

    @Provides
    @Named(MATCH_DETAILS_ID)
    fun matchId(matchDetailsFragment: Fragment): String? =
            (matchDetailsFragment as MatchDetailsFragment).matchId

    @InstallIn(FragmentComponent::class)
    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(MatchDetailsViewModel::class)
        fun bindViewModel(matchDetailsViewModel: MatchDetailsViewModel): ViewModel

    }


}