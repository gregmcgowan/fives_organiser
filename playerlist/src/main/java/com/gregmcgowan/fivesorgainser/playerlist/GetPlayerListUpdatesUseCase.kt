package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.asEither
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerListUpdatesUseCase @Inject constructor(
        private val playerRepo: PlayerRepo
) {

    suspend fun execute(): Flow<Either<Throwable, DataUpdate<Player>>> =
            playerRepo.playersUpdates().asEither()

}