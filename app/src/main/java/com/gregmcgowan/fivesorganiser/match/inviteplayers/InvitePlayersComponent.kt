package com.gregmcgowan.fivesorganiser.match.inviteplayers

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
            InvitePlayerModule::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
@FragmentScope
interface InvitePlayersComponent : ViewModelComponent {

    fun inject(invitePlayersFragment: InvitePlayersFragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun matchId(matchId: String): Builder

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): InvitePlayersComponent
    }

}