package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetPlayerListUpdatesUseCase {
    fun execute(): Flow<DataUpdate<Player>>
}

// Eventually will add in extra player data about reliability and more stats so there will be more
// of a need for this to be a UseCase.
class GetPlayerListUpdatesUseCaseImpl @Inject constructor(
    private val playerRepo: PlayerRepo,
) : GetPlayerListUpdatesUseCase {
    override fun execute(): Flow<DataUpdate<Player>> = playerRepo.playersUpdates()
}
