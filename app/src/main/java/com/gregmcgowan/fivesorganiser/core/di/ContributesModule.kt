package com.gregmcgowan.fivesorganiser.core.di

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsActivity
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsModule
import com.gregmcgowan.fivesorganiser.main.MainActivity
import com.gregmcgowan.fivesorganiser.main.MainModule
import com.gregmcgowan.fivesorganiser.match.MatchActivity
import com.gregmcgowan.fivesorganiser.match.MatchModule
import com.gregmcgowan.fivesorganiser.match.inviteplayers.InvitePlayerModule
import com.gregmcgowan.fivesorganiser.match.inviteplayers.InvitePlayersFragment
import com.gregmcgowan.fivesorganiser.match.summary.MatchSummaryFragment
import com.gregmcgowan.fivesorganiser.match.summary.MatchSummaryModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module()
abstract class ContributesModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ImportContactsModule::class])
    abstract fun contributesImporContactsActivity(): ImportContactsActivity

    @ContributesAndroidInjector(modules = [MatchModule::class])
    abstract fun contributesMatchActivity(): MatchActivity

    @ContributesAndroidInjector(modules = [InvitePlayerModule::class])
    abstract fun contributesInvitePlayersModule() : InvitePlayersFragment

    @ContributesAndroidInjector(modules = [MatchSummaryModule::class])
    abstract fun contributesMatchSummarysModule() : MatchSummaryFragment

}