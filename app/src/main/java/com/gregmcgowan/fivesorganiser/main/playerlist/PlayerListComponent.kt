package com.gregmcgowan.fivesorganiser.main.playerlist

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepoModule
import com.gregmcgowan.fivesorganiser.core.di.ActivityScope
import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelComponent
import dagger.Component

@Component(
        dependencies = [AppComponent::class],
        modules = [
            PlayerListModule::class,
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ]
)
@ActivityScope
interface PlayerListComponent : ViewModelComponent {

    fun inject(playerListFragment: PlayerListFragment);

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): PlayerListComponent
    }

}