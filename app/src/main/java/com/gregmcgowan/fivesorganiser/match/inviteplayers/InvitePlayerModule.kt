package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.ViewModel
import android.support.annotation.Nullable
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
        includes = [
            InvitePlayerModule.Bindings::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
class InvitePlayerModule {

    @Provides @Nullable
    fun matchId(invitePlayersFragment: InvitePlayersFragment): String? {
        return invitePlayersFragment.matchId
    }

    @Module
    interface Bindings {
        @Binds
        @IntoMap
        @ViewModelKey(InvitePlayersViewModel::class)
        fun bindViewModel(viewModel: InvitePlayersViewModel): ViewModel

    }


}