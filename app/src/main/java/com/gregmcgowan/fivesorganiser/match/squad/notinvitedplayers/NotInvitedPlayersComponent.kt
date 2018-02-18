package com.gregmcgowan.fivesorganiser.match.squad.notinvitedplayers

import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.FragmentScope
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelComponent
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule
import dagger.BindsInstance
import dagger.Component

@Component(
        dependencies = [AppComponent::class],
        modules = [
            NotInvitedPlayerModule::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
@FragmentScope
interface NotInvitedPlayersComponent : ViewModelComponent {

    fun inject(notInvitedPlayersListFragment: NotInvitedPlayersListFragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun matchId(matchId: String): Builder

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): NotInvitedPlayersComponent
    }

}