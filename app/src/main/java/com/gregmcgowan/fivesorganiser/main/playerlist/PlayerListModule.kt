package com.gregmcgowan.fivesorganiser.main.playerlist

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepoModule
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
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