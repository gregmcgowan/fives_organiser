package com.gregmcgowan.fivesorgainser.playerlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
interface PlayerModule {

    @Binds
    fun bindGetPlayersUseCase(impl: GetPlayerListUpdatesUseCaseImpl): GetPlayerListUpdatesUseCase

    @Binds
    fun bindPLayerListUiStateMapper(impl : PlayerListUiStateMapperImpl) : PlayerListUiStateMapper

}
