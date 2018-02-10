package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.core.di.ActivityScope
import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelComponent
import dagger.Component

@Component(
        dependencies = [AppComponent::class],
        modules = [
            MainModule::class,
            ViewModelBuilder::class
        ]
)
@ActivityScope
interface MainComponent : ViewModelComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): MainComponent
    }

}