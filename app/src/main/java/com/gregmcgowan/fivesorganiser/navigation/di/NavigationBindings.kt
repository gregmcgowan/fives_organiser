package com.gregmcgowan.fivesorganiser.navigation.di

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import com.gregmcgowan.fivesorganiser.navigation.ImportContactsActivityNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
interface InvitePlayersNavigationModule {

    @Binds
    fun bindNavigator(navigator: ImportContactsActivityNavigator): ImportContactsNavigator


}