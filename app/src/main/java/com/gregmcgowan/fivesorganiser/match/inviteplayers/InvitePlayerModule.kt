package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class InvitePlayerModule {

    @Binds
    @IntoMap
    @ViewModelKey(InvitePlayersViewModel::class)
    abstract fun bindViewModel(viewModel: InvitePlayersViewModel): ViewModel

}