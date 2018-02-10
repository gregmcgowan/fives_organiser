package com.gregmcgowan.fivesorganiser.main.matchList

import com.gregmcgowan.fivesorganiser.core.di.ActivityScope
import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelComponent
import com.gregmcgowan.fivesorganiser.match.database.MatchRepoModule
import dagger.Component

@Component(
        dependencies = [AppComponent::class],
        modules = [
            MatchListModule::class,
            MatchRepoModule::class,
            ViewModelBuilder::class
        ]
)
@ActivityScope
interface MatchListComponent : ViewModelComponent {

    fun inject(matchListFragment: MatchListFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): MatchListComponent
    }

}