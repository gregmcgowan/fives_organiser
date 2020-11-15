package com.gregmcgowan.fivesorganiser.match.squad

import android.view.View
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

@Subcomponent(modules = [MatchSquadListFactory.Bindings::class])
interface MatchSquadListFactory {

    fun matchSquadPlayerListViewHolder(): MatchSquadListPlayerViewHolder

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun itemView(view: View): Builder

        fun build(): MatchSquadListFactory

    }

    @InstallIn(ViewComponent::class)
    @Module
    interface Bindings {

        @Binds
        fun bindView(impl: MatchSquadListPlayerView): MatchSquadListPlayerContract.View

        @Binds
        fun bindPresenter(impl: MatchSquadListPlayerPresenter): MatchSquadListPlayerContract.Presenter

    }

}