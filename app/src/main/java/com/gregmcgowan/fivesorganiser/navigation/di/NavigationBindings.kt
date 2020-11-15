package com.gregmcgowan.fivesorganiser.navigation.di

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import com.gregmcgowan.fivesorganiser.match.MatchNavigator
import com.gregmcgowan.fivesorganiser.navigation.ImportContactsActivityNavigator
import com.gregmcgowan.fivesorganiser.navigation.MatchActivityNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
interface MatchNavigationModule {
    @Binds
    fun bindNavigator(navigator: MatchActivityNavigator): MatchNavigator

}

@InstallIn(ActivityComponent::class)
@Module
interface InvitePlayersNavigationModule {

    @Binds
    fun bindNavigator(navigator: ImportContactsActivityNavigator): ImportContactsNavigator


}