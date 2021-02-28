package com.gregmcgowan.fivesorgainser.playerlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@InstallIn(FragmentComponent::class)
@Module
interface PlayerModule {

    @Binds
    fun bindGetPlayersUseCase(impl : GetPlayerListUpdatesUseCaseImpl) : GetPlayerListUpdatesUseCase
}