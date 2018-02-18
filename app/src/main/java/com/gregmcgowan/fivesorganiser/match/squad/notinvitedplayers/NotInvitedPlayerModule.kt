package com.gregmcgowan.fivesorganiser.match.squad.notinvitedplayers

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NotInvitedPlayerModule {

    @Binds
    @IntoMap
    @ViewModelKey(NotInvitedPlayersViewModel::class)
    abstract fun bindViewModel(viewModel: NotInvitedPlayersViewModel): ViewModel

}