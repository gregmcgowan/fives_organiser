package com.gregmcgowan.fivesorganiser.core.di

import com.gregmcgowan.fivesorgainser.playerlist.PlayerListFragment
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListModule
import com.gregmcgowan.fivesorganiser.match.MatchActivity
import com.gregmcgowan.fivesorganiser.match.MatchModule
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsFragment
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsModule
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadFragment
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadModule
import com.gregmcgowan.fivesorganiser.matchlist.MatchListFragment
import com.gregmcgowan.fivesorganiser.matchlist.MatchListModule
import com.gregmcgowan.fivesorganiser.navigation.di.InvitePlayersNavigationModule
import com.gregmcgowan.fivesorganiser.navigation.di.MatchNavigationModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributesModule {

    @ContributesAndroidInjector(modules = [PlayerListModule::class, DataModule::class, InvitePlayersNavigationModule::class])
    abstract fun contributesPlayerListFragment(): PlayerListFragment

    @ContributesAndroidInjector(modules = [MatchListModule::class, DataModule::class, MatchNavigationModule::class])
    abstract fun contributesMatchListFragment(): MatchListFragment

    @ContributesAndroidInjector(modules = [MatchModule::class, DataModule::class])
    abstract fun contributesMatchActivity(): MatchActivity

    @ContributesAndroidInjector(modules = [MatchSquadModule::class, DataModule::class])
    abstract fun contributesInvitePlayersFragment(): MatchSquadFragment

    @ContributesAndroidInjector(modules = [MatchDetailsModule::class, DataModule::class])
    abstract fun contributesMatchDetailsFragment(): MatchDetailsFragment

}

