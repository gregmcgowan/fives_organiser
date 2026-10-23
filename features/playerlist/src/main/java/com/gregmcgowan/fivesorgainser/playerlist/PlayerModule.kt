package com.gregmcgowan.fivesorgainser.playerlist

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
@BindingContainer
interface PlayerModule {
    @Binds
    fun bindGetPlayerListUseCase(impl: GetPlayerListUseCaseImpl): GetPlayerListUseCase

    @Binds
    fun bindPLayerListUiStateMapper(impl: PlayerListUiStateMapperImpl): PlayerListUiStateMapper
}
