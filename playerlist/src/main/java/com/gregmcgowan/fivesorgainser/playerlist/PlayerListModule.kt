package com.gregmcgowan.fivesorgainser.playerlist

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
        includes = [
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ]
)
abstract class PlayerListModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerListViewModel::class)
    abstract fun bindViewModel(viewModel: PlayerListViewModel): ViewModel


}