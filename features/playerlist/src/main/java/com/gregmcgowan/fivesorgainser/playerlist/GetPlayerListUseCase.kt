package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

interface GetPlayerListUseCase {
    suspend fun execute(): List<Player>
}

// Eventually will add in extra player data about reliability and more stats so there will be more
// of a need for this to be a UseCase.
class GetPlayerListUseCaseImpl @Inject constructor(
    private val playerRepo: PlayerRepo,
) : GetPlayerListUseCase {
    override suspend fun execute(): List<Player> = playerRepo.getPlayers()
}
