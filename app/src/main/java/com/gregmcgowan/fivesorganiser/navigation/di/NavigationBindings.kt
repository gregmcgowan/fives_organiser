package com.gregmcgowan.fivesorganiser.navigation.di

import android.app.Activity
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListFragment
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import com.gregmcgowan.fivesorganiser.matchlist.MatchListFragment
import com.gregmcgowan.fivesorganiser.match.MatchNavigator
import com.gregmcgowan.fivesorganiser.navigation.ImportContactsActivityNavigator
import com.gregmcgowan.fivesorganiser.navigation.MatchActivityNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [MatchNavigationModule.Bindings::class])
class MatchNavigationModule {

    @Provides
    fun provideActivity(matchListFragment: MatchListFragment) =
            matchListFragment.requireActivity() as Activity

    @Module
    interface Bindings {

        @Binds
        fun bindNavigator(navigator: MatchActivityNavigator): MatchNavigator

    }
}

@Module(includes = [InvitePlayersNavigationModule.Bindings::class])
class InvitePlayersNavigationModule {

    @Provides
    fun provideActivity(playerListFragment: PlayerListFragment) =
            playerListFragment.requireActivity() as Activity

    @Module
    interface Bindings {

        @Binds
        fun bindNavigator(navigator: ImportContactsActivityNavigator): ImportContactsNavigator

    }


}