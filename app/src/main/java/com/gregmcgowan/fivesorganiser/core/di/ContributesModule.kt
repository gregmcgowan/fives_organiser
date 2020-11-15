package com.gregmcgowan.fivesorganiser.core.di

import com.gregmcgowan.fivesorganiser.match.MatchActivity
import com.gregmcgowan.fivesorganiser.match.MatchModule
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsFragment
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsModule
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadFragment
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributesModule {

    @ContributesAndroidInjector(modules = [MatchModule::class, DataModule::class])
    abstract fun contributesMatchActivity(): MatchActivity

    @ContributesAndroidInjector(modules = [MatchSquadModule::class, DataModule::class])
    abstract fun contributesInvitePlayersFragment(): MatchSquadFragment

    @ContributesAndroidInjector(modules = [MatchDetailsModule::class, DataModule::class])
    abstract fun contributesMatchDetailsFragment(): MatchDetailsFragment

}
