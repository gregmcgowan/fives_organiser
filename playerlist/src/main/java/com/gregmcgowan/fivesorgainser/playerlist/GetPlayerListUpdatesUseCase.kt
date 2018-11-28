package com.gregmcgowan.fivesorgainser.playerlist

import androidx.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class GetPlayerListUpdatesUseCase @Inject constructor(
        private val playerRepo: PlayerRepo
) {

    fun execute(): LiveData<Either<Exception, DataUpdate<Player>>> {
        return playerRepo.playerUpdateLiveData()
    }

}