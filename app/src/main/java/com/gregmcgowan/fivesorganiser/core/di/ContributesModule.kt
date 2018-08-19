package com.gregmcgowan.fivesorganiser.core.di

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsActivity
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsModule
import com.gregmcgowan.fivesorganiser.main.MainActivity
import com.gregmcgowan.fivesorganiser.main.MainModule
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListFragment
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListModule
import com.gregmcgowan.fivesorganiser.main.playerlist.PlayerListFragment
import com.gregmcgowan.fivesorganiser.main.playerlist.PlayerListModule
import com.gregmcgowan.fivesorganiser.match.MatchActivity
import com.gregmcgowan.fivesorganiser.match.MatchModule
import com.gregmcgowan.fivesorganiser.match.inviteplayers.InvitePlayerModule
import com.gregmcgowan.fivesorganiser.match.inviteplayers.InvitePlayersFragment
import com.gregmcgowan.fivesorganiser.match.timelocation.MatchTimeAndLocationFragment
import com.gregmcgowan.fivesorganiser.match.timelocation.MatchTimeAndLocationModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module()
abstract class ContributesModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [PlayerListModule::class])
    abstract fun contributesPlayerListFragment(): PlayerListFragment

    @ContributesAndroidInjector(modules = [MatchListModule::class])
    abstract fun contributesMatchListFragment(): MatchListFragment

    @ContributesAndroidInjector(modules = [ImportContactsModule::class])
    abstract fun contributesImporContactsActivity(): ImportContactsActivity

    @ContributesAndroidInjector(modules = [MatchModule::class])
    abstract fun contributesMatchActivity(): MatchActivity

    @ContributesAndroidInjector(modules = [InvitePlayerModule::class])
    abstract fun contributesInvitePlayersFragment(): InvitePlayersFragment

    @ContributesAndroidInjector(modules = [MatchTimeAndLocationModule::class])
    abstract fun contributesDateTimeLocationFragment(): MatchTimeAndLocationFragment


}