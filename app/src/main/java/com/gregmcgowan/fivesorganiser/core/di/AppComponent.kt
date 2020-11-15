package com.gregmcgowan.fivesorganiser.core.di

import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

// Will be removed once all the modules are moved over to hilt
@InstallIn(ApplicationComponent::class)
@Module(includes = [
    ContributesModule::class,
    AndroidSupportInjectionModule::class]
)
class AggregatorModule {

}

