package com.gregmcgowan.fivesorgainser.playerlist

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import dev.zacsweers.metro.Inject

interface GetPlayerListUseCase {
    suspend fun execute(): List<Player>
}

// Eventually will add in extra player data about reliability and more stats so there will be more
// of a need for this to be a UseCase.
@Inject
class GetPlayerListUseCaseImpl(
    private val playerRepo: PlayerRepo,
) : GetPlayerListUseCase {
    override suspend fun execute(): List<Player> = playerRepo.getPlayers()
}
