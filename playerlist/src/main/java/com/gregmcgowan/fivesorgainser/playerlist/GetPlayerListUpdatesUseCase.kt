package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetPlayerListUpdatesUseCase {
    suspend fun execute(): Flow<DataUpdate<Player>>
}

class GetPlayerListUpdatesUseCaseImpl @Inject constructor(
        private val playerRepo: PlayerRepo
) : GetPlayerListUpdatesUseCase {

    override suspend fun execute(): Flow<DataUpdate<Player>> =
            playerRepo.playersUpdates()

}