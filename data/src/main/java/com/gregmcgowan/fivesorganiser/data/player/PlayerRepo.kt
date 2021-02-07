package com.gregmcgowan.fivesorganiser.data.player

import androidx.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import kotlinx.coroutines.flow.Flow

interface PlayerRepo {

    suspend fun getPlayers(): List<Player>

    suspend fun addPlayer(name: String,
                          email: String,
                          phoneNumber: String,
                          contactId: Long)

    fun playerUpdateLiveData(): LiveData<Either<Exception, DataUpdate<Player>>>

    suspend fun playersUpdates() : Flow<DataUpdate<Player>>
}