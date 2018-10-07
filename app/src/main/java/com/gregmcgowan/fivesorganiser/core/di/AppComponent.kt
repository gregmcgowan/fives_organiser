package com.gregmcgowan.fivesorganiser.core.di

import com.gregmcgowan.fivesorganiser.FivesOrganiserApp
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.MatchTypesInfo
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
        modules = [
            AppModule::class,
            ContributesModule::class,
            AndroidSupportInjectionModule::class
        ]
)
@Singleton
interface AppComponent : AndroidInjector<FivesOrganiserApp> {

    fun coroutineContexts(): CoroutineContexts

    fun authentication(): Authentication

    fun strings(): Strings

    fun matchTypesInfo(): MatchTypesInfo

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: FivesOrganiserApp): Builder

        fun build(): AppComponent
    }


}